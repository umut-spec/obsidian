package dev.obsidianmc.obsidian.feature;

import dev.obsidianmc.obsidian.ObsidianConfig;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Obsidian MOTD Manager
 *
 * Supports multiple MOTDs that rotate randomly on each server list ping.
 */
public class MOTDManager {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-MOTD");
    private static final Random RANDOM = new Random();

    private static List<String> motdList = List.of("An Obsidian Server");

    /**
     * Get a random MOTD from the list
     */
    public static String getRandomMOTD() {
        if (motdList.isEmpty()) return "An Obsidian Server";
        return motdList.get(RANDOM.nextInt(motdList.size()));
    }

    /**
     * Set the MOTD list from config
     */
    public static void setMotdList(List<String> motds) {
        if (motds != null && !motds.isEmpty()) {
            motdList = motds;
        }
    }

    /**
     * Get all configured MOTDs
     */
    public static List<String> getMotdList() {
        return motdList;
    }
}
