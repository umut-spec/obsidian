package dev.obsidianmc.obsidian.performance;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.logging.Logger;

/**
 * Obsidian Hopper Optimizer
 *
 * Optimizes hopper behavior by adding transfer cooldowns,
 * skipping checks on full containers, and reducing item search overhead.
 */
public class HopperOptimizer {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-HopperOptimizer");

    /**
     * Check if a hopper should be allowed to transfer items
     * based on the cooldown configuration
     *
     * @param ticksSinceLastTransfer Ticks since the last successful transfer
     * @return true if the hopper should attempt a transfer
     */
    public static boolean shouldAttemptTransfer(int ticksSinceLastTransfer) {
        if (!ObsidianConfig.optimizeHoppers) return true;
        return ticksSinceLastTransfer >= ObsidianConfig.hopperTransferCooldown;
    }

    /**
     * Check if a hopper should skip ticking when its destination is full
     *
     * @param destinationFull Whether the destination container is full
     * @return true if the hopper tick should be skipped
     */
    public static boolean shouldSkipFullDestination(boolean destinationFull) {
        if (!ObsidianConfig.hopperDisableOnFull) return false;
        return destinationFull;
    }

    /**
     * Get the configured hopper transfer cooldown in ticks
     */
    public static int getTransferCooldown() {
        return ObsidianConfig.hopperTransferCooldown;
    }
}
