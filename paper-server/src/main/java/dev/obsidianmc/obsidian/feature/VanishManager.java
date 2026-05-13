package dev.obsidianmc.obsidian.feature;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Obsidian Vanish System
 *
 * Allows OPs to become invisible to other players without plugins.
 */
public class VanishManager {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-Vanish");
    private static final Set<UUID> VANISHED = new HashSet<>();

    /**
     * Toggle vanish for a player
     */
    public static boolean toggle(Player player) {
        if (VANISHED.contains(player.getUniqueId())) {
            unvanish(player);
            return false;
        } else {
            vanish(player);
            return true;
        }
    }

    /**
     * Make player vanished
     */
    public static void vanish(Player player) {
        VANISHED.add(player.getUniqueId());
        for (Player other : player.getServer().getOnlinePlayers()) {
            if (!other.equals(player) && !other.isOp()) {
                other.hidePlayer(org.bukkit.Bukkit.getPluginManager().getPlugins().length > 0
                        ? org.bukkit.Bukkit.getPluginManager().getPlugins()[0] : null, player);
            }
        }
        player.sendMessage(ChatColor.LIGHT_PURPLE + "⬛ " + ChatColor.GRAY + "You are now vanished.");
        LOGGER.info("[Obsidian] " + player.getName() + " vanished");
    }

    /**
     * Make player visible again
     */
    public static void unvanish(Player player) {
        VANISHED.remove(player.getUniqueId());
        for (Player other : player.getServer().getOnlinePlayers()) {
            if (!other.equals(player)) {
                other.showPlayer(org.bukkit.Bukkit.getPluginManager().getPlugins().length > 0
                        ? org.bukkit.Bukkit.getPluginManager().getPlugins()[0] : null, player);
            }
        }
        player.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.GRAY + "You are now visible.");
        LOGGER.info("[Obsidian] " + player.getName() + " unvanished");
    }

    /**
     * Check if player is vanished
     */
    public static boolean isVanished(UUID playerId) {
        return VANISHED.contains(playerId);
    }

    /**
     * Handle new player join — hide vanished players from them
     */
    public static void onPlayerJoin(Player joiner) {
        if (joiner.isOp()) return;
        for (UUID vanishedId : VANISHED) {
            Player vanished = org.bukkit.Bukkit.getPlayer(vanishedId);
            if (vanished != null && vanished.isOnline()) {
                joiner.hidePlayer(org.bukkit.Bukkit.getPluginManager().getPlugins().length > 0
                        ? org.bukkit.Bukkit.getPluginManager().getPlugins()[0] : null, vanished);
            }
        }
    }

    /**
     * Clean up on player quit
     */
    public static void onPlayerQuit(UUID playerId) {
        VANISHED.remove(playerId);
    }
}
