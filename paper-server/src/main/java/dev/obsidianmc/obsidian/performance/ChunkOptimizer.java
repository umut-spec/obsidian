package dev.obsidianmc.obsidian.performance;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.logging.Logger;

/**
 * Obsidian Chunk Optimizer
 *
 * Optimizes chunk loading, saving, and sending behavior
 * for better server performance.
 */
public class ChunkOptimizer {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-ChunkOptimizer");

    /**
     * Get the maximum number of chunks to auto-save per tick
     */
    public static int getMaxAutoSavePerTick() {
        return ObsidianConfig.maxAutoSaveChunksPerTick;
    }

    /**
     * Check if optimized chunk loading is enabled
     */
    public static boolean isOptimizedLoading() {
        return ObsidianConfig.optimizeChunkLoading;
    }

    /**
     * Check if players should be prevented from moving into unloaded chunks
     */
    public static boolean shouldPreventUnloadedChunkMovement() {
        return ObsidianConfig.preventMovingIntoUnloadedChunks;
    }

    /**
     * Get the maximum number of chunk packets to send per tick
     */
    public static int getMaxChunkSendsPerTick() {
        return ObsidianConfig.maxChunkSendsPerTick;
    }

    /**
     * Check if async chunk generation is enabled
     */
    public static boolean isAsyncChunkGeneration() {
        return ObsidianConfig.asyncChunkGeneration;
    }

    /**
     * Check if map item data updates should be skipped when not tracking
     */
    public static boolean shouldSkipMapUpdates() {
        return ObsidianConfig.skipMapItemDataUpdatesIfNotTracking;
    }
}
