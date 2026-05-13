package dev.obsidianmc.obsidian.security;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Chat & Command Rate Limiter
 * Prevents chat spam and command abuse.
 *
 * Violation escalation:
 *   1st violation  -> Warning message
 *   2nd violation  -> Kick from server
 *   3rd violation  -> 1 hour temp-ban (blocks reconnection)
 */
public class ChatGuard {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-ChatGuard");
    private static final Map<UUID, RateData> CHAT_DATA = new ConcurrentHashMap<>();
    private static final Map<UUID, RateData> COMMAND_DATA = new ConcurrentHashMap<>();
    private static final Map<UUID, ViolationData> VIOLATIONS = new ConcurrentHashMap<>();

    // IP-based temp-ban tracking (prevents reconnection bypass)
    private static final Map<String, Long> IP_BANS = new ConcurrentHashMap<>();

    public enum Action {
        ALLOW,
        WARN,
        KICK,
        TEMPBAN
    }

    private static class RateData {
        int count = 0;
        long windowStart = System.currentTimeMillis();

        synchronized boolean check(int maxCount, int windowMs) {
            long now = System.currentTimeMillis();
            if (now - windowStart > windowMs) {
                count = 0;
                windowStart = now;
            }
            count++;
            return count <= maxCount;
        }
    }

    private static class ViolationData {
        int violations = 0;
        long lastViolation = 0;
        long banExpiry = 0;

        synchronized Action recordViolation() {
            long now = System.currentTimeMillis();

            // If currently banned, check expiry
            if (banExpiry > 0 && now < banExpiry) {
                return Action.TEMPBAN;
            } else if (banExpiry > 0 && now >= banExpiry) {
                banExpiry = 0;
                violations = 0;
            }

            // Reset violations after 10 minutes of clean behavior
            if (now - lastViolation > 600_000) {
                violations = 0;
            }

            violations++;
            lastViolation = now;

            if (violations >= 3) {
                banExpiry = now + 3_600_000; // 1 hour
                return Action.TEMPBAN;
            } else if (violations >= 2) {
                return Action.KICK;
            } else {
                return Action.WARN;
            }
        }

        synchronized boolean isBanned() {
            if (banExpiry > 0 && System.currentTimeMillis() < banExpiry) {
                return true;
            }
            if (banExpiry > 0) {
                banExpiry = 0;
                violations = 0;
            }
            return false;
        }

        synchronized long getBanRemainingSeconds() {
            long remaining = banExpiry - System.currentTimeMillis();
            return remaining > 0 ? remaining / 1000 : 0;
        }
    }

    // ==========================================
    // IP-based temp-ban (checked at login)
    // ==========================================

    private static final java.io.File BANS_FILE = new java.io.File("obsidian-bans.json");

    /**
     * Record an IP temp-ban. Called when a player gets TEMPBAN action.
     */
    public static void banIp(String ip, long durationMs) {
        long expiry = System.currentTimeMillis() + durationMs;
        IP_BANS.put(ip, expiry);
        LOGGER.warning("[Obsidian] IP temp-banned: " + ip + " for " + (durationMs / 60000) + " minutes");
        saveBans();
    }

    /**
     * Check if an IP is temp-banned. Called by ConnectionGuard at login time.
     * Returns remaining seconds, or 0 if not banned.
     */
    public static long isIpBanned(String ip) {
        Long expiry = IP_BANS.get(ip);
        if (expiry == null) return 0;
        long remaining = expiry - System.currentTimeMillis();
        if (remaining <= 0) {
            IP_BANS.remove(ip);
            saveBans();
            return 0;
        }
        return remaining / 1000;
    }

    /**
     * Cleanup expired IP bans
     */
    public static void cleanupIpBans() {
        long now = System.currentTimeMillis();
        boolean changed = IP_BANS.entrySet().removeIf(entry -> entry.getValue() < now);
        if (changed) saveBans();
    }

    /**
     * Save IP bans to obsidian-bans.json
     */
    public static void saveBans() {
        try {
            com.google.gson.JsonObject root = new com.google.gson.JsonObject();
            com.google.gson.JsonObject bans = new com.google.gson.JsonObject();
            for (Map.Entry<String, Long> entry : IP_BANS.entrySet()) {
                bans.addProperty(entry.getKey(), entry.getValue());
            }
            root.add("ip_bans", bans);
            try (java.io.FileWriter writer = new java.io.FileWriter(BANS_FILE)) {
                new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
            }
        } catch (Exception e) {
            LOGGER.warning("[Obsidian] Failed to save bans: " + e.getMessage());
        }
    }

    /**
     * Load IP bans from obsidian-bans.json on server startup
     */
    public static void loadBans() {
        if (!BANS_FILE.exists()) return;
        try (java.io.FileReader reader = new java.io.FileReader(BANS_FILE)) {
            com.google.gson.JsonObject root = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
            if (root.has("ip_bans")) {
                com.google.gson.JsonObject bans = root.getAsJsonObject("ip_bans");
                long now = System.currentTimeMillis();
                for (Map.Entry<String, com.google.gson.JsonElement> entry : bans.entrySet()) {
                    long expiry = entry.getValue().getAsLong();
                    if (expiry > now) {
                        IP_BANS.put(entry.getKey(), expiry);
                    }
                }
                LOGGER.info("[Obsidian] Loaded " + IP_BANS.size() + " active IP bans from disk.");
            }
        } catch (Exception e) {
            LOGGER.warning("[Obsidian] Failed to load bans: " + e.getMessage());
        }
    }

    /**
     * Unban a specific IP
     */
    public static void unbanIp(String ip) {
        IP_BANS.remove(ip);
        saveBans();
        LOGGER.info("[Obsidian] IP unbanned: " + ip);
    }

    /**
     * Clear all IP bans
     */
    public static void clearAllBans() {
        IP_BANS.clear();
        saveBans();
        LOGGER.info("[Obsidian] All IP bans cleared.");
    }

    /**
     * Get all active bans (for /obsidian bans command)
     */
    public static Map<String, Long> getActiveBans() {
        long now = System.currentTimeMillis();
        IP_BANS.entrySet().removeIf(entry -> entry.getValue() < now);
        return java.util.Collections.unmodifiableMap(IP_BANS);
    }

    // ==========================================
    // Chat & Command checking
    // ==========================================

    public static Action checkChat(UUID playerId, String playerName) {
        if (!ObsidianConfig.enableChatRateLimit) return Action.ALLOW;

        ViolationData vd = VIOLATIONS.computeIfAbsent(playerId, k -> new ViolationData());
        if (vd.isBanned()) {
            return Action.TEMPBAN;
        }

        RateData data = CHAT_DATA.computeIfAbsent(playerId, k -> new RateData());
        boolean allowed = data.check(
                ObsidianConfig.chatRateLimitMaxMessages,
                ObsidianConfig.chatRateLimitTimeWindowMs
        );
        if (!allowed) {
            Action action = vd.recordViolation();
            if (ObsidianConfig.enableSecurityLogging) {
                LOGGER.warning("[Obsidian] Chat rate limit hit by " + playerName + " -> " + action.name()
                        + " (violation #" + vd.violations + ")");
            }
            return action;
        }
        return Action.ALLOW;
    }

    public static Action checkCommand(UUID playerId, String playerName) {
        if (!ObsidianConfig.enableCommandRateLimit) return Action.ALLOW;

        ViolationData vd = VIOLATIONS.computeIfAbsent(playerId, k -> new ViolationData());
        if (vd.isBanned()) {
            return Action.TEMPBAN;
        }

        RateData data = COMMAND_DATA.computeIfAbsent(playerId, k -> new RateData());
        boolean allowed = data.check(
                ObsidianConfig.commandRateLimitMaxCommands,
                ObsidianConfig.commandRateLimitTimeWindowMs
        );
        if (!allowed) {
            Action action = vd.recordViolation();
            if (ObsidianConfig.enableSecurityLogging) {
                LOGGER.warning("[Obsidian] Command rate limit hit by " + playerName + " -> " + action.name()
                        + " (violation #" + vd.violations + ")");
            }
            return action;
        }
        return Action.ALLOW;
    }

    public static long getBanRemaining(UUID playerId) {
        ViolationData vd = VIOLATIONS.get(playerId);
        return vd != null ? vd.getBanRemainingSeconds() : 0;
    }

    // Legacy compatibility
    public static boolean allowChat(UUID playerId, String playerName) {
        return checkChat(playerId, playerName) == Action.ALLOW;
    }

    public static boolean allowCommand(UUID playerId, String playerName) {
        return checkCommand(playerId, playerName) == Action.ALLOW;
    }

    public static void removePlayer(UUID playerId) {
        CHAT_DATA.remove(playerId);
        COMMAND_DATA.remove(playerId);
        // Don't remove violations - they persist across reconnects
    }
}
