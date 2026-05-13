package dev.obsidianmc.obsidian;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;

/**
 * Obsidian Configuration System
 *
 * Performance & Security focused Paper fork configuration.
 * All settings are loaded from obsidian.yml in the server root directory.
 *
 * @author ObsidianMC Team
 */
public class ObsidianConfig {

    private static final String HEADER = """
            ============================================================
             Obsidian Server Configuration
             Performance & Security focused Paper fork
            
             Wiki: https://github.com/ObsidianMC/Obsidian/wiki
            ============================================================
            """;

    private static File CONFIG_FILE;
    public static YamlConfiguration config;
    static int version;

    // ==========================================
    // Performance Settings
    // ==========================================

    // Entity Optimization
    public static boolean optimizeEntityActivation = true;
    public static int entityActivationRangeAnimals = 32;
    public static int entityActivationRangeMonsters = 32;
    public static int entityActivationRangeMisc = 16;
    public static int entityActivationRangeWater = 16;
    public static int entityActivationRangeVillagers = 32;
    public static int entityActivationRangeFlyingMonsters = 48;
    public static boolean entityActivationTickInactiveVillagers = true;
    public static boolean entityActivationIgnoreSpectators = true;

    // Chunk Optimization
    public static int maxAutoSaveChunksPerTick = 24;
    public static boolean optimizeChunkLoading = true;
    public static boolean preventMovingIntoUnloadedChunks = true;
    public static int maxChunkSendsPerTick = 81;

    // Tick Optimization
    public static boolean optimizeRedstone = true;
    public static boolean optimizeHoppers = true;
    public static int hopperTransferCooldown = 8;
    public static boolean hopperDisableOnFull = false;
    public static boolean optimizeArmorStandTick = true;
    public static boolean skipMapItemDataUpdatesIfNotTracking = true;

    // TNT Optimization
    public static boolean optimizeTNT = true;
    public static double tntMergeRadius = 0.5;

    // Mob Spawning Optimization
    public static boolean perPlayerMobSpawning = true;
    public static int mobSpawnerTickRate = 2;
    public static boolean countAllMobsForSpawning = false;

    // Item Merging
    public static double itemMergeRadius = 2.5;
    public static double experienceOrbMergeRadius = 3.0;

    // Memory Optimization
    public static boolean flushRegionsOnSave = true;
    public static boolean reduceEntityAllocations = true;
    public static boolean cacheNbtTagString = true;

    // Async Operations
    public static boolean asyncChunkGeneration = true;
    public static boolean asyncPathfinding = true;
    public static int asyncPathfindingMaxThreads = 2;

    // ==========================================
    // Security Settings
    // ==========================================

    // Packet Security
    public static boolean enablePacketLimiter = true;
    public static int maxPacketsPerSecond = 500;
    public static int packetSpamBanThreshold = 3;
    public static String packetSpamAction = "KICK"; // KICK, BAN, WARN

    // Connection Security
    public static boolean enableConnectionThrottle = true;
    public static int connectionThrottleLimit = 3;
    public static int connectionThrottleTimeMs = 4000;
    public static boolean enableProxyProtocol = false;

    // Exploit Protection
    public static boolean preventBookExploit = true;
    public static int maxBookPageSize = 2560;
    public static int maxBookTotalSize = 102400;
    public static boolean preventSignExploit = true;
    public static int maxSignLineLength = 384;
    public static boolean preventCreativeCrash = true;
    public static boolean preventNbtOverflow = true;
    public static int maxNbtSize = 2097152; // 2MB

    // Chat Security
    public static boolean enableChatRateLimit = true;
    public static int chatRateLimitMaxMessages = 5;
    public static int chatRateLimitTimeWindowMs = 5000;
    public static boolean enableCommandRateLimit = true;
    public static int commandRateLimitMaxCommands = 10;
    public static int commandRateLimitTimeWindowMs = 5000;

    // Movement Security
    public static boolean enableMovementChecks = true;
    public static double maxMovementSpeed = 100.0;
    public static boolean preventInvalidMovement = true;
    public static boolean logSuspiciousMovement = true;

    // Entity Interaction Security
    public static boolean preventEntityInteractSpam = true;
    public static int entityInteractCooldownMs = 50;
    public static boolean preventInvalidEntityInteraction = true;

    // World Security
    public static boolean preventChunkBan = true;
    public static boolean preventFallingBlockCrash = true;
    public static int maxFallingBlocksPerChunk = 100;
    public static boolean preventPistonCrash = true;
    public static int maxPistonPushLimit = 12;

    // Anti-Bot
    public static boolean enableAntiBot = true;
    public static int antiBotMaxJoinsPerSecond = 3;
    public static int antiBotCooldownSeconds = 10;
    public static String antiBotAction = "THROTTLE"; // THROTTLE, KICK, BAN

    // ==========================================
    // Logging & Monitoring
    // ==========================================
    public static boolean enableSecurityLogging = true;
    public static boolean logExploitAttempts = true;
    public static boolean logPacketViolations = false;
    public static boolean logConnectionEvents = true;

    // ==========================================
    // Branding
    // ==========================================
    public static String serverBrand = "Obsidian";
    public static String serverMotd = "An Obsidian Server";

    /**
     * Initialize the Obsidian configuration
     */
    public static void init(File configFile) {
        CONFIG_FILE = configFile;
        config = new YamlConfiguration();
        if (configFile.exists()) {
            try {
                config.load(CONFIG_FILE);
            } catch (IOException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not load obsidian.yml", ex);
            } catch (InvalidConfigurationException ex) {
                Bukkit.getLogger().log(Level.SEVERE, "Could not load obsidian.yml, please correct your syntax errors", ex);
                throw new RuntimeException(ex);
            }
            Bukkit.getLogger().info("[Obsidian] Loaded configuration from obsidian.yml");
        } else {
            Bukkit.getLogger().info("[Obsidian] Creating default obsidian.yml...");
        }
        config.options().header(HEADER);
        config.options().copyDefaults(true);

        version = getInt("config-version", 1);
        set("config-version", 1);

        readConfig(ObsidianConfig.class, null);
    }

    protected static void readConfig(Class<?> clazz, Object instance) {
        for (Method method : clazz.getDeclaredMethods()) {
            if (Modifier.isPrivate(method.getModifiers())) {
                if (method.getParameterTypes().length == 0 && method.getReturnType() == Void.TYPE) {
                    try {
                        method.setAccessible(true);
                        method.invoke(instance);
                    } catch (InvocationTargetException ex) {
                        throw new RuntimeException(ex.getCause());
                    } catch (Exception ex) {
                        Bukkit.getLogger().log(Level.SEVERE, "Error invoking " + method, ex);
                    }
                }
            }
        }

        try {
            config.save(CONFIG_FILE);
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not save " + CONFIG_FILE, ex);
        }
    }

    // ==========================================
    // Config Getter Helpers
    // ==========================================

    private static void set(String path, Object val) {
        config.addDefault(path, val);
        config.set(path, val);
    }

    private static String getString(String path, String def) {
        config.addDefault(path, def);
        return config.getString(path, config.getString(path));
    }

    private static boolean getBoolean(String path, boolean def) {
        config.addDefault(path, def);
        return config.getBoolean(path, config.getBoolean(path));
    }

    private static double getDouble(String path, double def) {
        config.addDefault(path, def);
        return config.getDouble(path, config.getDouble(path));
    }

    private static int getInt(String path, int def) {
        config.addDefault(path, def);
        return config.getInt(path, config.getInt(path));
    }

    private static <T> List<T> getList(String path, List<T> def) {
        config.addDefault(path, def);
        return (List<T>) config.getList(path, config.getList(path));
    }

    // ==========================================
    // Config Loader Methods (called via reflection)
    // ==========================================

    private static void performanceSettings() {
        // Entity Optimization
        optimizeEntityActivation = getBoolean("performance.entity.optimize-activation", optimizeEntityActivation);
        entityActivationRangeAnimals = getInt("performance.entity.activation-range.animals", entityActivationRangeAnimals);
        entityActivationRangeMonsters = getInt("performance.entity.activation-range.monsters", entityActivationRangeMonsters);
        entityActivationRangeMisc = getInt("performance.entity.activation-range.misc", entityActivationRangeMisc);
        entityActivationRangeWater = getInt("performance.entity.activation-range.water", entityActivationRangeWater);
        entityActivationRangeVillagers = getInt("performance.entity.activation-range.villagers", entityActivationRangeVillagers);
        entityActivationRangeFlyingMonsters = getInt("performance.entity.activation-range.flying-monsters", entityActivationRangeFlyingMonsters);
        entityActivationTickInactiveVillagers = getBoolean("performance.entity.tick-inactive-villagers", entityActivationTickInactiveVillagers);
        entityActivationIgnoreSpectators = getBoolean("performance.entity.ignore-spectators", entityActivationIgnoreSpectators);

        // Chunk Optimization
        maxAutoSaveChunksPerTick = getInt("performance.chunks.max-auto-save-per-tick", maxAutoSaveChunksPerTick);
        optimizeChunkLoading = getBoolean("performance.chunks.optimize-loading", optimizeChunkLoading);
        preventMovingIntoUnloadedChunks = getBoolean("performance.chunks.prevent-moving-into-unloaded", preventMovingIntoUnloadedChunks);
        maxChunkSendsPerTick = getInt("performance.chunks.max-sends-per-tick", maxChunkSendsPerTick);

        // Tick Optimization
        optimizeRedstone = getBoolean("performance.tick.optimize-redstone", optimizeRedstone);
        optimizeHoppers = getBoolean("performance.tick.optimize-hoppers", optimizeHoppers);
        hopperTransferCooldown = getInt("performance.tick.hopper-transfer-cooldown", hopperTransferCooldown);
        hopperDisableOnFull = getBoolean("performance.tick.hopper-disable-on-full", hopperDisableOnFull);
        optimizeArmorStandTick = getBoolean("performance.tick.optimize-armor-stand", optimizeArmorStandTick);
        skipMapItemDataUpdatesIfNotTracking = getBoolean("performance.tick.skip-map-updates-if-not-tracking", skipMapItemDataUpdatesIfNotTracking);

        // TNT Optimization
        optimizeTNT = getBoolean("performance.tnt.optimize", optimizeTNT);
        tntMergeRadius = getDouble("performance.tnt.merge-radius", tntMergeRadius);

        // Mob Spawning
        perPlayerMobSpawning = getBoolean("performance.spawning.per-player-mob-spawning", perPlayerMobSpawning);
        mobSpawnerTickRate = getInt("performance.spawning.spawner-tick-rate", mobSpawnerTickRate);
        countAllMobsForSpawning = getBoolean("performance.spawning.count-all-mobs", countAllMobsForSpawning);

        // Item Merging
        itemMergeRadius = getDouble("performance.merging.item-radius", itemMergeRadius);
        experienceOrbMergeRadius = getDouble("performance.merging.experience-orb-radius", experienceOrbMergeRadius);

        // Memory
        flushRegionsOnSave = getBoolean("performance.memory.flush-regions-on-save", flushRegionsOnSave);
        reduceEntityAllocations = getBoolean("performance.memory.reduce-entity-allocations", reduceEntityAllocations);
        cacheNbtTagString = getBoolean("performance.memory.cache-nbt-tag-string", cacheNbtTagString);

        // Async
        asyncChunkGeneration = getBoolean("performance.async.chunk-generation", asyncChunkGeneration);
        asyncPathfinding = getBoolean("performance.async.pathfinding", asyncPathfinding);
        asyncPathfindingMaxThreads = getInt("performance.async.pathfinding-max-threads", asyncPathfindingMaxThreads);
    }

    private static void securitySettings() {
        // Packet Security
        enablePacketLimiter = getBoolean("security.packets.enable-limiter", enablePacketLimiter);
        maxPacketsPerSecond = getInt("security.packets.max-per-second", maxPacketsPerSecond);
        packetSpamBanThreshold = getInt("security.packets.spam-ban-threshold", packetSpamBanThreshold);
        packetSpamAction = getString("security.packets.spam-action", packetSpamAction);

        // Connection Security
        enableConnectionThrottle = getBoolean("security.connection.enable-throttle", enableConnectionThrottle);
        connectionThrottleLimit = getInt("security.connection.throttle-limit", connectionThrottleLimit);
        connectionThrottleTimeMs = getInt("security.connection.throttle-time-ms", connectionThrottleTimeMs);
        enableProxyProtocol = getBoolean("security.connection.enable-proxy-protocol", enableProxyProtocol);

        // Exploit Protection
        preventBookExploit = getBoolean("security.exploits.prevent-book", preventBookExploit);
        maxBookPageSize = getInt("security.exploits.max-book-page-size", maxBookPageSize);
        maxBookTotalSize = getInt("security.exploits.max-book-total-size", maxBookTotalSize);
        preventSignExploit = getBoolean("security.exploits.prevent-sign", preventSignExploit);
        maxSignLineLength = getInt("security.exploits.max-sign-line-length", maxSignLineLength);
        preventCreativeCrash = getBoolean("security.exploits.prevent-creative-crash", preventCreativeCrash);
        preventNbtOverflow = getBoolean("security.exploits.prevent-nbt-overflow", preventNbtOverflow);
        maxNbtSize = getInt("security.exploits.max-nbt-size", maxNbtSize);

        // Chat Security
        enableChatRateLimit = getBoolean("security.chat.enable-rate-limit", enableChatRateLimit);
        chatRateLimitMaxMessages = getInt("security.chat.rate-limit-max-messages", chatRateLimitMaxMessages);
        chatRateLimitTimeWindowMs = getInt("security.chat.rate-limit-time-window-ms", chatRateLimitTimeWindowMs);
        enableCommandRateLimit = getBoolean("security.chat.enable-command-rate-limit", enableCommandRateLimit);
        commandRateLimitMaxCommands = getInt("security.chat.command-rate-limit-max", commandRateLimitMaxCommands);
        commandRateLimitTimeWindowMs = getInt("security.chat.command-rate-limit-time-window-ms", commandRateLimitTimeWindowMs);

        // Movement Security
        enableMovementChecks = getBoolean("security.movement.enable-checks", enableMovementChecks);
        maxMovementSpeed = getDouble("security.movement.max-speed", maxMovementSpeed);
        preventInvalidMovement = getBoolean("security.movement.prevent-invalid", preventInvalidMovement);
        logSuspiciousMovement = getBoolean("security.movement.log-suspicious", logSuspiciousMovement);

        // Entity Security
        preventEntityInteractSpam = getBoolean("security.entity.prevent-interact-spam", preventEntityInteractSpam);
        entityInteractCooldownMs = getInt("security.entity.interact-cooldown-ms", entityInteractCooldownMs);
        preventInvalidEntityInteraction = getBoolean("security.entity.prevent-invalid-interaction", preventInvalidEntityInteraction);

        // World Security
        preventChunkBan = getBoolean("security.world.prevent-chunk-ban", preventChunkBan);
        preventFallingBlockCrash = getBoolean("security.world.prevent-falling-block-crash", preventFallingBlockCrash);
        maxFallingBlocksPerChunk = getInt("security.world.max-falling-blocks-per-chunk", maxFallingBlocksPerChunk);
        preventPistonCrash = getBoolean("security.world.prevent-piston-crash", preventPistonCrash);
        maxPistonPushLimit = getInt("security.world.max-piston-push-limit", maxPistonPushLimit);

        // Anti-Bot
        enableAntiBot = getBoolean("security.anti-bot.enabled", enableAntiBot);
        antiBotMaxJoinsPerSecond = getInt("security.anti-bot.max-joins-per-second", antiBotMaxJoinsPerSecond);
        antiBotCooldownSeconds = getInt("security.anti-bot.cooldown-seconds", antiBotCooldownSeconds);
        antiBotAction = getString("security.anti-bot.action", antiBotAction);
    }

    private static void loggingSettings() {
        enableSecurityLogging = getBoolean("logging.security-logging", enableSecurityLogging);
        logExploitAttempts = getBoolean("logging.log-exploit-attempts", logExploitAttempts);
        logPacketViolations = getBoolean("logging.log-packet-violations", logPacketViolations);
        logConnectionEvents = getBoolean("logging.log-connection-events", logConnectionEvents);
    }

    private static void brandingSettings() {
        serverBrand = getString("branding.server-brand", serverBrand);
        serverMotd = getString("branding.server-motd", serverMotd);
    }
}
