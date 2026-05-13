package dev.obsidianmc.obsidian.performance;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Obsidian Redstone Optimizer
 *
 * Limits excessive redstone neighbor updates per tick to prevent
 * lag machines and cascading redstone from killing TPS.
 * Works alongside Paper's Eigencraft/Alternate Current implementations.
 */
public class RedstoneOptimizer {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-RedstoneOptimizer");

    // Per-tick neighbor update counter
    private static final AtomicInteger UPDATES_THIS_TICK = new AtomicInteger(0);
    private static final int MAX_UPDATES_PER_TICK = 10000;

    /**
     * Check if redstone optimization is enabled
     */
    public static boolean isOptimized() {
        return ObsidianConfig.optimizeRedstone;
    }

    /**
     * Track a neighbor update. Returns false if the update limit
     * has been exceeded for this tick (should skip the update).
     */
    public static boolean allowNeighborUpdate() {
        if (!ObsidianConfig.optimizeRedstone) return true;
        return UPDATES_THIS_TICK.incrementAndGet() <= MAX_UPDATES_PER_TICK;
    }

    /**
     * Reset the per-tick counter. Called at the start of each tick.
     */
    public static void resetTickCounter() {
        UPDATES_THIS_TICK.set(0);
    }

    /**
     * Get current update count for monitoring
     */
    public static int getCurrentUpdateCount() {
        return UPDATES_THIS_TICK.get();
    }
}
