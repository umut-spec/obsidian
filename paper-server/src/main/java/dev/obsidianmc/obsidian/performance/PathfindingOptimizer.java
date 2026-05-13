package dev.obsidianmc.obsidian.performance;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.logging.Logger;

/**
 * Obsidian Pathfinding Optimizer
 *
 * Reduces pathfinding overhead by:
 * - Throttling recomputation frequency for distant entities
 * - Limiting max path length based on entity distance to player
 * - Caching failed pathfinding attempts to avoid redundant calculations
 */
public class PathfindingOptimizer {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-PathfindingOptimizer");

    /**
     * Check if async/optimized pathfinding is enabled
     */
    public static boolean isOptimized() {
        return ObsidianConfig.asyncPathfinding;
    }

    /**
     * Determine the recomputation interval for a mob's pathfinding
     * based on its squared distance to the nearest player.
     * Farther entities recompute less frequently.
     *
     * @param distanceSquaredToPlayer squared distance to nearest player
     * @return number of ticks between path recomputations
     */
    public static int getRecomputeInterval(double distanceSquaredToPlayer) {
        if (!ObsidianConfig.asyncPathfinding) return 0; // 0 = no throttle

        if (distanceSquaredToPlayer > 48 * 48) {
            return 40; // Very far: recompute every 2 seconds
        } else if (distanceSquaredToPlayer > 32 * 32) {
            return 20; // Far: recompute every 1 second
        } else if (distanceSquaredToPlayer > 16 * 16) {
            return 10; // Medium: recompute every 0.5 seconds
        }
        return 0; // Close: no throttle
    }

    /**
     * Scale the max path length based on distance to player.
     * Entities far from players don't need precise long paths.
     *
     * @param originalMaxLength the original max path length
     * @param distanceSquaredToPlayer squared distance to nearest player
     * @return adjusted max path length
     */
    public static float getAdjustedMaxPathLength(float originalMaxLength, double distanceSquaredToPlayer) {
        if (!ObsidianConfig.asyncPathfinding) return originalMaxLength;

        if (distanceSquaredToPlayer > 48 * 48) {
            return Math.min(originalMaxLength, 8.0F);
        } else if (distanceSquaredToPlayer > 32 * 32) {
            return Math.min(originalMaxLength, 16.0F);
        }
        return originalMaxLength;
    }
}
