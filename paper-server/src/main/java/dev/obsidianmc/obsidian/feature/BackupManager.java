package dev.obsidianmc.obsidian.feature;

import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Obsidian Backup System
 *
 * Automated world backup with ZIP compression.
 */
public class BackupManager {
    private static final Logger LOGGER = Logger.getLogger("Obsidian-Backup");
    private static long lastBackupTime = 0;

    /**
     * Check if backup is due and perform it
     */
    public static void checkAndBackup(int intervalMinutes) {
        if (intervalMinutes <= 0) return;

        long now = System.currentTimeMillis();
        if (lastBackupTime == 0) {
            lastBackupTime = now;
            return;
        }

        if (now - lastBackupTime >= intervalMinutes * 60_000L) {
            lastBackupTime = now;
            performBackupAsync();
        }
    }

    /**
     * Perform backup on async thread
     */
    public static void performBackupAsync() {
        new Thread(() -> {
            try {
                LOGGER.info("[Obsidian] Starting world backup...");
                // Worlds auto-save periodically, we just zip the current state

                File backupDir = new File("obsidian-backups");
                if (!backupDir.exists()) backupDir.mkdirs();

                String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm").format(new Date());

                for (World world : Bukkit.getWorlds()) {
                    File worldDir = world.getWorldFolder();
                    File backupFile = new File(backupDir, world.getName() + "_" + timestamp + ".zip");

                    zipDirectory(worldDir, backupFile);
                    LOGGER.info("[Obsidian] Backed up " + world.getName() + " → " + backupFile.getName()
                            + " (" + (backupFile.length() / 1024 / 1024) + " MB)");
                }

                // Cleanup old backups (keep last 5)
                cleanupOldBackups(backupDir, 5);

                LOGGER.info("[Obsidian] Backup complete!");
            } catch (Exception e) {
                LOGGER.warning("[Obsidian] Backup failed: " + e.getMessage());
            }
        }, "Obsidian-Backup").start();
    }

    /**
     * ZIP a directory
     */
    private static void zipDirectory(File sourceDir, File zipFile) throws Exception {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            zos.setLevel(5); // Balanced compression
            zipRecursive(sourceDir, sourceDir, zos);
        }
    }

    private static void zipRecursive(File rootDir, File currentDir, ZipOutputStream zos) throws Exception {
        File[] files = currentDir.listFiles();
        if (files == null) return;

        byte[] buffer = new byte[8192];
        for (File file : files) {
            // Skip session.lock and temp files
            if (file.getName().equals("session.lock") || file.getName().endsWith(".tmp")) continue;

            String relativePath = rootDir.toPath().relativize(file.toPath()).toString();
            if (file.isDirectory()) {
                zos.putNextEntry(new ZipEntry(relativePath + "/"));
                zos.closeEntry();
                zipRecursive(rootDir, file, zos);
            } else {
                zos.putNextEntry(new ZipEntry(relativePath));
                try (FileInputStream fis = new FileInputStream(file)) {
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                }
                zos.closeEntry();
            }
        }
    }

    /**
     * Keep only the N most recent backups per world
     */
    private static void cleanupOldBackups(File backupDir, int keepCount) {
        File[] backups = backupDir.listFiles((dir, name) -> name.endsWith(".zip"));
        if (backups == null || backups.length <= keepCount) return;

        java.util.Arrays.sort(backups, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));
        for (int i = keepCount; i < backups.length; i++) {
            if (backups[i].delete()) {
                LOGGER.info("[Obsidian] Deleted old backup: " + backups[i].getName());
            }
        }
    }
}
