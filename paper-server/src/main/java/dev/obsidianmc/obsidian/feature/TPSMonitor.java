package dev.obsidianmc.obsidian.feature;

import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Obsidian TPS Monitor & Lag Spike Alert
 *
 * Tracks server TPS in real-time and alerts OPs when TPS drops below threshold.
 */
public class TPSMonitor {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-TPSMonitor");

    private static final double[] TPS_HISTORY = new double[60]; // Last 60 seconds
    private static int historyIndex = 0;
    private static long lastTickTime = System.nanoTime();
    private static int tickCount = 0;
    private static boolean lagAlerted = false;

    // Lag spike alert settings
    private static double lagAlertThreshold = 15.0;
    private static boolean lagAlertEnabled = true;

    /**
     * Called every tick to track TPS
     */
    public static void onTick() {
        tickCount++;
        long now = System.nanoTime();
        long elapsed = now - lastTickTime;

        // Calculate TPS every second (20 ticks)
        if (elapsed >= 1_000_000_000L) {
            double tps = tickCount / (elapsed / 1_000_000_000.0);
            tps = Math.min(tps, 20.0); // Cap at 20
            TPS_HISTORY[historyIndex % TPS_HISTORY.length] = tps;
            historyIndex++;
            tickCount = 0;
            lastTickTime = now;

            // Lag spike alert
            if (lagAlertEnabled && tps < lagAlertThreshold) {
                if (!lagAlerted) {
                    lagAlerted = true;
                    alertOps(tps);
                }
            } else {
                lagAlerted = false;
            }
        }
    }

    /**
     * Get current TPS (1s average)
     */
    public static double getCurrentTPS() {
        if (historyIndex == 0) return 20.0;
        return TPS_HISTORY[(historyIndex - 1) % TPS_HISTORY.length];
    }

    /**
     * Get average TPS over last N seconds
     */
    public static double getAverageTPS(int seconds) {
        if (historyIndex == 0) return 20.0;
        int count = Math.min(seconds, Math.min(historyIndex, TPS_HISTORY.length));
        double sum = 0;
        for (int i = 0; i < count; i++) {
            sum += TPS_HISTORY[(historyIndex - 1 - i + TPS_HISTORY.length) % TPS_HISTORY.length];
        }
        return sum / count;
    }

    /**
     * Get colored TPS string
     */
    public static String getColoredTPS(double tps) {
        ChatColor color;
        if (tps >= 19.0) color = ChatColor.GREEN;
        else if (tps >= 17.0) color = ChatColor.YELLOW;
        else if (tps >= 15.0) color = ChatColor.GOLD;
        else color = ChatColor.RED;
        return color + String.format("%.1f", tps);
    }

    /**
     * Get MSPT (milliseconds per tick)
     */
    public static double getMSPT() {
        double tps = getCurrentTPS();
        if (tps <= 0) return 50.0;
        return 1000.0 / tps;
    }

    /**
     * Alert OPs about lag spike
     */
    private static void alertOps(double tps) {
        String msg = ChatColor.RED + "⚠ " + ChatColor.GRAY + "[Obsidian] TPS dropped to "
                + getColoredTPS(tps) + ChatColor.GRAY + " — Possible lag spike!";
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) {
                player.sendMessage(msg);
            }
        }
        LOGGER.warning("[Obsidian] TPS dropped to " + String.format("%.1f", tps));
    }

    public static void setLagAlertEnabled(boolean enabled) { lagAlertEnabled = enabled; }
    public static void setLagAlertThreshold(double threshold) { lagAlertThreshold = threshold; }
}
