package dev.obsidianmc.obsidian.feature;

import dev.obsidianmc.obsidian.ObsidianConfig;

import java.util.Set;
import java.util.HashSet;
import java.util.logging.Logger;

/**
 * Obsidian Chat Formatter
 *
 * Built-in chat formatting without requiring plugins.
 * Format placeholders: {player}, {message}, {world}
 */
public class ChatFormatter {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-ChatFormatter");

    private static String chatFormat = "<{player}> {message}";
    private static boolean enabled = false;

    /**
     * Format a chat message using the configured format
     */
    public static String format(String playerName, String worldName, String message) {
        if (!enabled) return null; // Return null to use default formatting

        return chatFormat
                .replace("{player}", playerName)
                .replace("{message}", message)
                .replace("{world}", worldName)
                .replace("&0", "\u00A70").replace("&1", "\u00A71").replace("&2", "\u00A72")
                .replace("&3", "\u00A73").replace("&4", "\u00A74").replace("&5", "\u00A75")
                .replace("&6", "\u00A76").replace("&7", "\u00A77").replace("&8", "\u00A78")
                .replace("&9", "\u00A79").replace("&a", "\u00A7a").replace("&b", "\u00A7b")
                .replace("&c", "\u00A7c").replace("&d", "\u00A7d").replace("&e", "\u00A7e")
                .replace("&f", "\u00A7f").replace("&l", "\u00A7l").replace("&o", "\u00A7o")
                .replace("&n", "\u00A7n").replace("&m", "\u00A7m").replace("&k", "\u00A7k")
                .replace("&r", "\u00A7r");
    }

    public static void setFormat(String format) { chatFormat = format; }
    public static void setEnabled(boolean enable) { enabled = enable; }
    public static boolean isEnabled() { return enabled; }
    public static String getFormat() { return chatFormat; }
}
