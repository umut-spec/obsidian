package dev.obsidianmc.obsidian.security;

import dev.obsidianmc.obsidian.ObsidianConfig;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Packet Rate Limiter
 *
 * Tracks incoming packets per player and takes action when
 * a player exceeds the configured rate limit. This prevents
 * various crash exploits and lag machines that abuse packet spam.
 *
 * Actions: WARN, KICK, BAN
 */
public class PacketLimiter {

    private static final Logger LOGGER = Logger.getLogger("Obsidian-PacketLimiter");

    private static final Map<UUID, PlayerPacketData> PLAYER_DATA = new ConcurrentHashMap<>();

    public static class PlayerPacketData {
        private final AtomicInteger packetCount = new AtomicInteger(0);
        private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());
        private final AtomicInteger violations = new AtomicInteger(0);

        public void reset() {
            packetCount.set(0);
            windowStart.set(System.currentTimeMillis());
        }

        public int incrementAndGet() {
            long now = System.currentTimeMillis();
            if (now - windowStart.get() > 1000) { // Reset every second
                packetCount.set(0);
                windowStart.set(now);
            }
            return packetCount.incrementAndGet();
        }

        public int getViolations() {
            return violations.get();
        }

        public int incrementViolations() {
            return violations.incrementAndGet();
        }
    }

    /**
     * Check if a packet from a player should be allowed
     *
     * @param playerId The UUID of the player
     * @param playerName The name of the player (for logging)
     * @return The action to take: ALLOW, WARN, KICK, or BAN
     */
    public static PacketAction checkPacket(UUID playerId, String playerName) {
        if (!ObsidianConfig.enablePacketLimiter) {
            return PacketAction.ALLOW;
        }

        PlayerPacketData data = PLAYER_DATA.computeIfAbsent(playerId, k -> new PlayerPacketData());
        int count = data.incrementAndGet();

        if (count > ObsidianConfig.maxPacketsPerSecond) {
            int violations = data.incrementViolations();

            if (ObsidianConfig.logPacketViolations) {
                LOGGER.warning("[Obsidian Security] Player " + playerName + " exceeded packet limit: "
                        + count + "/" + ObsidianConfig.maxPacketsPerSecond
                        + " (violation #" + violations + ")");
            }

            if (violations >= ObsidianConfig.packetSpamBanThreshold) {
                return PacketAction.valueOf(ObsidianConfig.packetSpamAction.toUpperCase());
            }

            return PacketAction.WARN;
        }

        return PacketAction.ALLOW;
    }

    /**
     * Remove a player from tracking (on disconnect)
     */
    public static void removePlayer(UUID playerId) {
        PLAYER_DATA.remove(playerId);
    }

    /**
     * Clear all tracked data
     */
    public static void clear() {
        PLAYER_DATA.clear();
    }

    /**
     * Get the current packet count for a player
     */
    public static int getPacketCount(UUID playerId) {
        PlayerPacketData data = PLAYER_DATA.get(playerId);
        return data != null ? data.packetCount.get() : 0;
    }

    public enum PacketAction {
        ALLOW,
        WARN,
        KICK,
        BAN
    }
}
