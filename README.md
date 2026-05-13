<div align="center">

<img src="logo.png" alt="Obsidian" width="128" height="128">

# ⬛ Obsidian

### Performance & Security Focused Paper Fork

[![MC Version](https://img.shields.io/badge/Minecraft-26.1.2-purple?style=for-the-badge)](https://minecraft.net)
[![Java](https://img.shields.io/badge/Java-25-orange?style=for-the-badge)](https://adoptium.net)
[![License](https://img.shields.io/badge/License-GPL--3.0-blue?style=for-the-badge)](LICENSE.md)
[![Build](https://img.shields.io/github/actions/workflow/status/umut-spec/obsidian/build.yml?style=for-the-badge&label=Build)](https://github.com/umut-spec/obsidian/actions)
[![Website](https://img.shields.io/badge/Website-obsidian.umuterden.com-blueviolet?style=for-the-badge)](https://obsidian.umuterden.com)

**Obsidian** is a high-performance, security-hardened Minecraft server fork based on [Paper](https://github.com/PaperMC/Paper). It combines aggressive performance optimizations with comprehensive exploit protection to deliver the most robust server experience possible.

[Website](https://obsidian.umuterden.com) · [Downloads](https://github.com/umut-spec/obsidian/releases) · [Issues](https://github.com/umut-spec/obsidian/issues)

</div>

---

## ⚡ Performance Features

| Feature | Description | NMS Hook |
|---------|-------------|----------|
| **TNT Merging** | Nearby TNT entities merge into a single explosion | `PrimedTnt.tick()` |
| **Redstone Limiter** | Caps neighbor updates to 10k/tick — kills lag machines | `RedStoneWireBlock.neighborChanged()` |
| **Pathfinding Throttle** | Distance-based recompute interval for mob AI | `PathNavigation.recomputePath()` |
| **Armor Stand Skip** | Stationary armor stands skip tick processing | `ServerLevel.tickNonPassenger()` |
| **Hopper Cooldown** | Extended cooldown when target container is full | `HopperBlockEntity.pushItemsTick()` |
| **Chunk Optimization** | Configurable send/save limits per tick | `ChunkOptimizer` |
| **Entity Activation** | Per-type activation ranges reduce entity ticking | `ObsidianConfig` |
| **Async Chunk Gen** | Chunks generate asynchronously | Paper integration |
| **Memory Optimization** | Reduced allocations, NBT caching, region flushing | `ObsidianConfig` |
| **Item/XP Merging** | Configurable merge radii reduce entity count | `ObsidianConfig` |

## 🛡️ Security Features

| Feature | Description | NMS Hook |
|---------|-------------|----------|
| **Packet Rate Limiter** | Per-player packet flood detection + auto IP-ban | `Connection.channelRead0()` |
| **Anti-Bot System** | Global + per-IP join throttle at handshake level | `ServerHandshakePacketListenerImpl` |
| **IP Ban Persistence** | Temp-bans survive restarts via `obsidian-bans.json` | `ChatGuard` |
| **Falling Block Limit** | Per-chunk cap prevents crash machines | `FallingBlockEntity.fall()` |
| **Piston Push Limit** | Configurable max blocks per piston push | `PistonBaseBlock.moveBlocks()` |
| **Creative NBT Check** | Blocks oversized items in creative mode | `ServerGamePacketListenerImpl` |
| **Book/Sign Protection** | Validates page size and sign line length | `ExploitProtection` |
| **Chat/Command Limiter** | Rate-limits messages and commands per player | `ChatGuard` |
| **Movement Validation** | Detects invalid/suspicious player movement | `ObsidianConfig` |
| **Chunk Ban Protection** | Prevents chunk-based client crash exploits | `ObsidianConfig` |

## 🔧 Building

### Requirements
- **Java 25** ([Adoptium Temurin](https://adoptium.net) recommended)
- **Git**

### Build Steps

```bash
git clone https://github.com/umut-spec/obsidian.git
cd obsidian

# Windows
.\gradlew.bat applyPatches
.\gradlew.bat createPaperclipJar

# Linux/Mac
./gradlew applyPatches
./gradlew createPaperclipJar
```

The compiled jar will be in `paper-server/build/libs/`.

### Running

```bash
java -Xms4G -Xmx4G -XX:+UseG1GC -jar obsidian-26.1.2.jar --nogui
```

## ⚙️ Configuration

Obsidian creates `obsidian.yml` on first run:

```yaml
# Performance
performance:
  entity:
    optimize-activation: true
    activation-range:
      animals: 32
      monsters: 32
  tick:
    optimize-hoppers: true
    optimize-redstone: true
    optimize-armor-stand: true
  tnt:
    optimize: true
    merge-radius: 0.5
  async:
    chunk-generation: true
    pathfinding: true
    pathfinding-max-threads: 2

# Security
security:
  packets:
    enable-limiter: true
    max-per-second: 500
    spam-ban-threshold: 3
  anti-bot:
    enabled: true
    max-joins-per-second: 3
  exploits:
    prevent-book: true
    prevent-nbt-overflow: true
    prevent-creative-crash: true
  world:
    max-falling-blocks-per-chunk: 100
    max-piston-push-limit: 12

# Branding
branding:
  server-brand: Obsidian
```

## 🎮 Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/obsidian version` | `obsidian.command` | Show version info |
| `/obsidian reload` | `obsidian.command.reload` | Reload configuration |
| `/obsidian status` | `obsidian.command` | Show all module statuses |
| `/obsidian bans` | `obsidian.command` | List active IP bans |
| `/obsidian unban <ip\|all>` | `obsidian.command.unban` | Remove IP ban(s) |

## 🏗️ Architecture

```
paper-server/src/main/java/dev/obsidianmc/obsidian/
├── ObsidianConfig.java          # Central config (obsidian.yml)
├── command/
│   └── ObsidianCommand.java     # /obsidian command
├── performance/
│   ├── ChunkOptimizer.java      # Chunk loading/saving limits
│   ├── EntityOptimizer.java     # Armor stand tick skip
│   ├── HopperOptimizer.java     # Transfer cooldown
│   ├── PathfindingOptimizer.java # Distance-based throttle
│   ├── RedstoneOptimizer.java   # Neighbor update limiter
│   └── TNTOptimizer.java        # TNT entity merging
└── security/
    ├── ChatGuard.java           # Chat/Command rate limit + IP bans
    ├── ConnectionGuard.java     # Anti-bot + connection throttle
    ├── ExploitProtection.java   # Book/Sign/NBT/FallingBlock/Piston
    └── PacketLimiter.java       # Per-player packet rate limit
```

## 📋 Credits

- [PaperMC](https://papermc.io) — The foundation this fork is built on
- [SpigotMC](https://spigotmc.org) — The original Bukkit continuation
- [Mojang](https://mojang.com) — The creators of Minecraft
- [umut-spec](https://github.com/umut-spec) — Obsidian developer

## 🔗 Links

- **Website:** [obsidian.umuterden.com](https://obsidian.umuterden.com)
- **GitHub:** [github.com/umut-spec/obsidian](https://github.com/umut-spec/obsidian)
- **Downloads:** [Releases](https://github.com/umut-spec/obsidian/releases)

## 📄 License

Obsidian inherits its license from Paper. See [LICENSE.md](LICENSE.md) for details.
