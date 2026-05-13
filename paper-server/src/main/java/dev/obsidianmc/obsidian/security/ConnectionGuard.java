package dev.obsidianmc.obsidian.security;

import dev.obsidianmc.obsidian.ObsidianConfig;
import java.net.InetAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * Anti-Bot & Connection Throttle System
 * Prevents bot attacks by limiting connection rate per IP and globally.
 */
public class ConnectionGuard {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-ConnectionGuard");
    private static final Map<String, ConnectionData> IP_DATA = new ConcurrentHashMap<>();
    private static final AtomicInteger globalJoinsThisSecond = new AtomicInteger(0);
    private static final AtomicLong globalWindowStart = new AtomicLong(System.currentTimeMillis());

    public static class ConnectionData {
        final AtomicInteger connectionCount = new AtomicInteger(0);
        final AtomicLong firstConnection = new AtomicLong(System.currentTimeMillis());
        final AtomicLong lastConnection = new AtomicLong(0);

        public void recordConnection() {
            long now = System.currentTimeMillis();
            if (now - firstConnection.get() > ObsidianConfig.connectionThrottleTimeMs) {
                connectionCount.set(0);
                firstConnection.set(now);
            }
            connectionCount.incrementAndGet();
            lastConnection.set(now);
        }

        public boolean isThrottled() {
            return connectionCount.get() > ObsidianConfig.connectionThrottleLimit;
        }
    }

    /**
     * Check if a connection from an IP should be allowed
     */
    public static ConnectionResult checkConnection(InetAddress address) {
        if (!ObsidianConfig.enableConnectionThrottle && !ObsidianConfig.enableAntiBot) {
            return ConnectionResult.ALLOW;
        }

        String ip = address.getHostAddress();

        // Per-IP throttle
        if (ObsidianConfig.enableConnectionThrottle) {
            ConnectionData data = IP_DATA.computeIfAbsent(ip, k -> new ConnectionData());
            data.recordConnection();
            if (data.isThrottled()) {
                if (ObsidianConfig.logConnectionEvents) {
                    LOGGER.warning("[Obsidian Security] Connection throttled for IP: " + ip
                            + " (" + data.connectionCount.get() + " connections in window)");
                }
                return ConnectionResult.THROTTLE;
            }
        }

        // Global anti-bot
        if (ObsidianConfig.enableAntiBot) {
            long now = System.currentTimeMillis();
            if (now - globalWindowStart.get() > 1000) {
                globalJoinsThisSecond.set(0);
                globalWindowStart.set(now);
            }
            int joins = globalJoinsThisSecond.incrementAndGet();
            if (joins > ObsidianConfig.antiBotMaxJoinsPerSecond) {
                if (ObsidianConfig.logConnectionEvents) {
                    LOGGER.warning("[Obsidian Security] Anti-bot triggered: " + joins
                            + " joins/sec from IP " + ip);
                }
                return ConnectionResult.valueOf(ObsidianConfig.antiBotAction.toUpperCase());
            }
        }

        return ConnectionResult.ALLOW;
    }

    public static void removeIp(String ip) {
        IP_DATA.remove(ip);
    }

    public static void cleanup() {
        long now = System.currentTimeMillis();
        IP_DATA.entrySet().removeIf(entry ->
                now - entry.getValue().lastConnection.get() > 60000);
    }

    public enum ConnectionResult {
        ALLOW, THROTTLE, KICK, BAN
    }
}
