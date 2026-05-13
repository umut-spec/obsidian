package dev.obsidianmc.obsidian.performance;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.util.logging.Logger;

/**
 * Obsidian Entity Optimizer
 *
 * Optimizes entity ticking by implementing smarter activation ranges
 * and reducing unnecessary calculations for inactive entities.
 */
public class EntityOptimizer {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-EntityOptimizer");

    /**
     * Check if an entity should be fully ticked based on distance to nearest player
     *
     * @param entityType The type of entity (ANIMAL, MONSTER, MISC, WATER, VILLAGER, FLYING_MONSTER)
     * @param distanceSquared The squared distance to the nearest player
     * @return true if the entity should be fully activated
     */
    public static boolean shouldActivateEntity(EntityCategory entityType, double distanceSquared) {
        if (!ObsidianConfig.optimizeEntityActivation) return true;

        int range = switch (entityType) {
            case ANIMAL -> ObsidianConfig.entityActivationRangeAnimals;
            case MONSTER -> ObsidianConfig.entityActivationRangeMonsters;
            case MISC -> ObsidianConfig.entityActivationRangeMisc;
            case WATER -> ObsidianConfig.entityActivationRangeWater;
            case VILLAGER -> ObsidianConfig.entityActivationRangeVillagers;
            case FLYING_MONSTER -> ObsidianConfig.entityActivationRangeFlyingMonsters;
        };

        return distanceSquared <= (double) range * range;
    }

    /**
     * Determine if an inactive villager should still tick
     * (for breeding, trading restocking, etc.)
     */
    public static boolean shouldTickInactiveVillager() {
        return ObsidianConfig.entityActivationTickInactiveVillagers;
    }

    /**
     * Check if spectators should be considered for entity activation
     */
    public static boolean shouldIgnoreSpectators() {
        return ObsidianConfig.entityActivationIgnoreSpectators;
    }

    /**
     * Check if armor stand ticking should be optimized
     * (skip ticking if no passengers and not moving)
     */
    public static boolean shouldOptimizeArmorStand(boolean hasPassengers, boolean isMoving) {
        if (!ObsidianConfig.optimizeArmorStandTick) return false;
        return !hasPassengers && !isMoving;
    }

    public enum EntityCategory {
        ANIMAL, MONSTER, MISC, WATER, VILLAGER, FLYING_MONSTER
    }
}
