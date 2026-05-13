# Installation

## Requirements
- **Java 25** or higher ([Adoptium Temurin](https://adoptium.net) recommended)
- **Minimum 2GB RAM** (4GB+ recommended for production)
- **64-bit operating system**

## Quick Start

### Option 1: Download Release
1. Go to [Releases](https://github.com/umutspec/obsidian/releases)
2. Download the latest `paper-paperclip-*.jar`
3. Rename it to `server.jar`
4. Run:
```bash
java -Xms4G -Xmx4G -XX:+UseG1GC -jar server.jar --nogui
```

### Option 2: Build from Source
```bash
git clone https://github.com/umutspec/obsidian.git
cd obsidian

# Apply patches (first time only)
./gradlew applyPatches

# Build the server JAR
./gradlew createPaperclipJar
```
The JAR will be at `paper-server/build/libs/paper-paperclip-*.jar`.

## First Run
On first run, Obsidian will:
1. Generate `obsidian.yml` with default settings
2. Print the Obsidian startup banner in console
3. Load all performance and security modules

## Recommended JVM Flags
```bash
java -Xms4G -Xmx4G \
  -XX:+UseG1GC \
  -XX:+ParallelRefProcEnabled \
  -XX:MaxGCPauseMillis=200 \
  -XX:+UnlockExperimentalVMOptions \
  -XX:+DisableExplicitGC \
  -XX:G1NewSizePercent=30 \
  -XX:G1MaxNewSizePercent=40 \
  -XX:G1HeapRegionSize=8M \
  -XX:G1ReservePercent=20 \
  -XX:G1HeapWastePercent=5 \
  -XX:G1MixedGCCountTarget=4 \
  -XX:InitiatingHeapOccupancyPercent=15 \
  -XX:G1MixedGCLiveThresholdPercent=90 \
  -XX:G1RSetUpdatingPauseTimePercent=5 \
  -XX:SurvivorRatio=32 \
  -XX:+PerfDisableSharedMem \
  -XX:MaxTenuringThreshold=1 \
  -jar server.jar --nogui
```

## Migrating from Paper
Obsidian is a drop-in replacement for Paper:
1. Stop your Paper server
2. Replace the Paper JAR with Obsidian's JAR
3. Start the server — all existing configs and worlds are compatible
4. Customize `obsidian.yml` as needed
