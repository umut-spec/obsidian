package dev.obsidianmc.obsidian.performance;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.logging.Logger;

/**
 * Obsidian TNT Optimizer
 *
 * Reduces lag from TNT explosions by merging nearby TNT entities
 * and optimizing explosion calculations.
 */
public class TNTOptimizer {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-TNTOptimizer");

    /**
     * Check if TNT optimization is enabled
     */
    public static boolean isOptimized() {
        return ObsidianConfig.optimizeTNT;
    }

    /**
     * Check if two TNT entities should be merged based on distance
     *
     * @param distanceSquared Squared distance between two TNT entities
     * @return true if the TNT entities should be merged
     */
    public static boolean shouldMerge(double distanceSquared) {
        if (!ObsidianConfig.optimizeTNT) return false;
        double mergeRadius = ObsidianConfig.tntMergeRadius;
        return distanceSquared <= mergeRadius * mergeRadius;
    }

    /**
     * Get the merge radius for TNT
     */
    public static double getMergeRadius() {
        return ObsidianConfig.tntMergeRadius;
    }
}
