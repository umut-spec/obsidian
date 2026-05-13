package dev.obsidianmc.obsidian.feature;

import dev.obsidianmc.obsidian.ObsidianConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.logging.Logger;

/**
 * Obsidian Event Listener
 *
 * Central Bukkit event listener for all Obsidian features:
 * - AFK tracking
 * - Chat formatting
 * - Random MOTD
 * - Vanish on join
 */
public class ObsidianListener implements Listener {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-Listener");

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.hasChangedPosition()) {
            AFKManager.recordActivity(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        AFKManager.recordActivity(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        AFKManager.recordActivity(event.getPlayer().getUniqueId());

        // Chat formatting
        if (ChatFormatter.isEnabled()) {
            String formatted = ChatFormatter.format(
                    event.getPlayer().getName(),
                    event.getPlayer().getWorld().getName(),
                    event.getMessage()
            );
            if (formatted != null) {
                event.setCancelled(true);
                Bukkit.broadcastMessage(formatted);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        AFKManager.recordActivity(event.getPlayer().getUniqueId());
        VanishManager.onPlayerJoin(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AFKManager.removePlayer(event.getPlayer().getUniqueId());
        VanishManager.onPlayerQuit(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        if (ObsidianConfig.randomMotdEnabled) {
            String motd = MOTDManager.getRandomMOTD()
                    .replace("&0", "\u00A70").replace("&1", "\u00A71").replace("&2", "\u00A72")
                    .replace("&3", "\u00A73").replace("&4", "\u00A74").replace("&5", "\u00A75")
                    .replace("&6", "\u00A76").replace("&7", "\u00A77").replace("&8", "\u00A78")
                    .replace("&9", "\u00A79").replace("&a", "\u00A7a").replace("&b", "\u00A7b")
                    .replace("&c", "\u00A7c").replace("&d", "\u00A7d").replace("&e", "\u00A7e")
                    .replace("&f", "\u00A7f").replace("&l", "\u00A7l").replace("&o", "\u00A7o")
                    .replace("&n", "\u00A7n").replace("&r", "\u00A7r");
            event.setMotd(motd);
        }
    }
}
