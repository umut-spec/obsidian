package dev.obsidianmc.obsidian.feature;

import dev.obsidianmc.obsidian.ObsidianConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Obsidian AFK System
 *
 * Tracks player activity and kicks idle players after configurable timeout.
 */
public class AFKManager {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-AFK");

    private static final Map<UUID, Long> LAST_ACTIVITY = new ConcurrentHashMap<>();
    private static final Map<UUID, Boolean> AFK_STATUS = new ConcurrentHashMap<>();

    /**
     * Record player activity
     */
    public static void recordActivity(UUID playerId) {
        LAST_ACTIVITY.put(playerId, System.currentTimeMillis());
        if (AFK_STATUS.getOrDefault(playerId, false)) {
            AFK_STATUS.put(playerId, false);
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.GRAY + "You are no longer AFK.");
            }
        }
    }

    /**
     * Check all players for AFK status. Called periodically.
     */
    public static void checkAFKPlayers(int afkTimeoutMinutes, boolean kickEnabled) {
        long now = System.currentTimeMillis();
        long timeoutMs = afkTimeoutMinutes * 60_000L;

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("obsidian.afk.exempt")) continue;

            Long lastActive = LAST_ACTIVITY.get(player.getUniqueId());
            if (lastActive == null) {
                LAST_ACTIVITY.put(player.getUniqueId(), now);
                continue;
            }

            long idle = now - lastActive;
            if (idle >= timeoutMs) {
                if (kickEnabled) {
                    player.kick(net.kyori.adventure.text.Component.text(
                            "You were kicked for being AFK for " + afkTimeoutMinutes + " minutes."));
                    LOGGER.info("[Obsidian] Kicked " + player.getName() + " for AFK (" + afkTimeoutMinutes + "m)");
                } else if (!AFK_STATUS.getOrDefault(player.getUniqueId(), false)) {
                    AFK_STATUS.put(player.getUniqueId(), true);
                    Bukkit.broadcastMessage(ChatColor.GRAY + "* " + player.getName() + " is now AFK");
                }
            }
        }
    }

    /**
     * Check if player is AFK
     */
    public static boolean isAFK(UUID playerId) {
        return AFK_STATUS.getOrDefault(playerId, false);
    }

    /**
     * Remove player data on quit
     */
    public static void removePlayer(UUID playerId) {
        LAST_ACTIVITY.remove(playerId);
        AFK_STATUS.remove(playerId);
    }
}
