package dev.obsidianmc.obsidian.command;

import dev.obsidianmc.obsidian.ObsidianConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * /obsidian command
 *
 * Provides server administration tools for Obsidian fork.
 * - /obsidian version - Show version information
 * - /obsidian reload - Reload obsidian.yml configuration
 * - /obsidian status - Show performance/security status
 */
public class ObsidianCommand extends Command {

    public ObsidianCommand(String name) {
        super(name);
        this.description = "Obsidian server management command";
        this.usageMessage = "/obsidian <version|reload|status|unban|bans>";
        this.setPermission("obsidian.command");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.DARK_GRAY + "» " + ChatColor.LIGHT_PURPLE + "Obsidian"
                    + ChatColor.GRAY + " - Performance & Security focused Paper fork");
            sender.sendMessage(ChatColor.GRAY + "Usage: " + ChatColor.WHITE + "/obsidian <version|reload|status|unban|bans>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "version", "ver" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ╔══════════════════════════════╗");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ║ " + ChatColor.LIGHT_PURPLE + "⬛ Obsidian Server" + ChatColor.DARK_GRAY + "            ║");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ╠══════════════════════════════╣");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ║ " + ChatColor.GRAY + "Version: "
                        + ChatColor.WHITE + ObsidianConfig.config.getString("obsidianVersion", "1.0.0") + ChatColor.DARK_GRAY + "            ║");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ║ " + ChatColor.GRAY + "MC: "
                        + ChatColor.WHITE + Bukkit.getMinecraftVersion() + ChatColor.DARK_GRAY + "               ║");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ║ " + ChatColor.GRAY + "Based on: "
                        + ChatColor.AQUA + "Paper" + ChatColor.DARK_GRAY + "              ║");
                sender.sendMessage(ChatColor.DARK_GRAY + "  ╚══════════════════════════════╝");
                sender.sendMessage("");
            }
            case "reload" -> {
                if (!sender.hasPermission("obsidian.command.reload")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload the config.");
                    return true;
                }
                try {
                    ObsidianConfig.init(new File("obsidian.yml"));
                    sender.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.GRAY + "Obsidian configuration reloaded successfully.");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "✗ Failed to reload configuration: " + e.getMessage());
                }
            }
            case "status" -> {
                sender.sendMessage("");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "⬛ Obsidian Status");
                sender.sendMessage(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━━━━━━━━━━");

                // Performance status
                sender.sendMessage(ChatColor.GOLD + "⚡ Performance:");
                sender.sendMessage(statusLine("Entity Optimization", ObsidianConfig.optimizeEntityActivation));
                sender.sendMessage(statusLine("Hopper Optimization", ObsidianConfig.optimizeHoppers));
                sender.sendMessage(statusLine("Redstone Optimization", ObsidianConfig.optimizeRedstone));
                sender.sendMessage(statusLine("TNT Optimization", ObsidianConfig.optimizeTNT));
                sender.sendMessage(statusLine("Async Chunk Gen", ObsidianConfig.asyncChunkGeneration));
                sender.sendMessage(statusLine("Async Pathfinding", ObsidianConfig.asyncPathfinding));

                sender.sendMessage("");

                // Security status
                sender.sendMessage(ChatColor.RED + "🛡 Security:");
                sender.sendMessage(statusLine("Packet Limiter", ObsidianConfig.enablePacketLimiter));
                sender.sendMessage(statusLine("Connection Throttle", ObsidianConfig.enableConnectionThrottle));
                sender.sendMessage(statusLine("Anti-Bot", ObsidianConfig.enableAntiBot));
                sender.sendMessage(statusLine("Chat Rate Limit", ObsidianConfig.enableChatRateLimit));
                sender.sendMessage(statusLine("Book Exploit Prot.", ObsidianConfig.preventBookExploit));
                sender.sendMessage(statusLine("Chunk Ban Prot.", ObsidianConfig.preventChunkBan));
                sender.sendMessage(statusLine("Movement Checks", ObsidianConfig.enableMovementChecks));

                sender.sendMessage(ChatColor.DARK_GRAY + "━━━━━━━━━━━━━━━━━━━━━━━━━━");
                sender.sendMessage("");
            }
            case "unban" -> {
                if (!sender.hasPermission("obsidian.command.unban")) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to unban.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(ChatColor.RED + "Usage: /obsidian unban <ip|all>");
                    return true;
                }
                if (args[1].equalsIgnoreCase("all")) {
                    dev.obsidianmc.obsidian.security.ChatGuard.clearAllBans();
                    sender.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.GRAY + "All IP bans cleared.");
                } else {
                    dev.obsidianmc.obsidian.security.ChatGuard.unbanIp(args[1]);
                    sender.sendMessage(ChatColor.GREEN + "✓ " + ChatColor.GRAY + "Unbanned IP: " + args[1]);
                }
            }
            case "bans" -> {
                java.util.Map<String, Long> bans = dev.obsidianmc.obsidian.security.ChatGuard.getActiveBans();
                if (bans.isEmpty()) {
                    sender.sendMessage(ChatColor.GREEN + "No active IP bans.");
                } else {
                    sender.sendMessage(ChatColor.LIGHT_PURPLE + "Active IP Bans (" + bans.size() + "):");
                    for (var entry : bans.entrySet()) {
                        long remaining = (entry.getValue() - System.currentTimeMillis()) / 1000;
                        sender.sendMessage(ChatColor.GRAY + "  " + entry.getKey() + " — " + remaining + "s remaining");
                    }
                }
            }
            default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand. Use: /obsidian <version|reload|status|unban|bans>");
        }

        return true;
    }

    private String statusLine(String name, boolean enabled) {
        return ChatColor.GRAY + "  " + (enabled ? ChatColor.GREEN + "✓" : ChatColor.RED + "✗")
                + ChatColor.GRAY + " " + name;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("version", "reload", "status", "unban", "bans");
        }
        return Collections.emptyList();
    }
}
