# Obsidian - Architecture

## Dizin Yapısı
```
Obsidian/
├── paper-api/                          # Paper API (dokunulmadı)
├── paper-server/
│   ├── src/
│   │   ├── main/java/
│   │   │   ├── dev/obsidianmc/obsidian/          # ⬛ OBSIDIAN KODLARI
│   │   │   │   ├── ObsidianConfig.java           # Ana konfigürasyon
│   │   │   │   ├── command/
│   │   │   │   │   └── ObsidianCommand.java      # /obsidian komutu
│   │   │   │   ├── performance/
│   │   │   │   │   ├── EntityOptimizer.java       # Entity activation range
│   │   │   │   │   ├── HopperOptimizer.java       # Hopper cooldown
│   │   │   │   │   ├── TNTOptimizer.java          # TNT merging
│   │   │   │   │   └── ChunkOptimizer.java        # Chunk loading limits
│   │   │   │   └── security/
│   │   │   │       ├── PacketLimiter.java          # Paket spam koruması
│   │   │   │       ├── ExploitProtection.java      # Book/Sign/NBT koruma
│   │   │   │       ├── ConnectionGuard.java        # Anti-bot sistemi
│   │   │   │       └── ChatGuard.java              # Chat/Command limiter
│   │   │   └── org/bukkit/craftbukkit/            # CraftBukkit (hook noktası)
│   │   └── minecraft/java/net/minecraft/          # NMS (hook noktası)
│   └── build.gradle.kts                           # Server build (rebranded)
├── build.gradle.kts                               # Root build
├── settings.gradle.kts                            # Proje yapısı (rebranded)
├── gradle.properties                              # Version & brand props
├── build-obsidian.bat                             # Windows build script
└── MEMORY_BANK/                                   # Bu dosyalar
```

## Hook Noktaları (NMS & CraftBukkit)

### Aktif Hook'lar ✅
| Dosya | Satır | Hook |
|-------|-------|------|
| `DedicatedServer.java` | ~285 | `ObsidianConfig.init()` — Config yükleme |
| `DedicatedServer.java` | ~309 | `/obsidian` komutu kaydı (startup) |
| `CraftServer.java` | ~1024 | `/obsidian` komutu kaydı (reload) + config reload |
| `ServerHandshakePacketListenerImpl.java` | ~73 | `ConnectionGuard.checkConnection()` |
| `ServerGamePacketListenerImpl.java` | handleChat | `ChatGuard.checkChat()` — WARN/KICK/BAN |
| `ServerGamePacketListenerImpl.java` | handleChatCommand | `ChatGuard.checkCommand()` — WARN/KICK/BAN |
| `ServerGamePacketListenerImpl.java` | handleEditBook | `ExploitProtection` — Book boyut kontrolü |
| `ServerGamePacketListenerImpl.java` | handleSignUpdate | `ExploitProtection` — Sign uzunluk kontrolü |
| `MinecraftServer.java` | getServerModName | `"Obsidian"` — F3 brand string |

### Bekleyen Hook'lar ❌
| Dosya | Hook | Açıklama |
|-------|------|----------|
| `ServerCommonPacketListenerImpl.java` | `PacketLimiter.checkPacket()` | Her gelen pakette kontrol |
| `ServerGamePacketListenerImpl.java` | `ChatGuard.allowChat()` | Chat mesajlarında kontrol |
| `ServerGamePacketListenerImpl.java` | `ChatGuard.allowCommand()` | Komutlarda kontrol |
| `ServerGamePacketListenerImpl.java` | `ExploitProtection.validateBook()` | Kitap düzenleme |
| `ServerGamePacketListenerImpl.java` | `ExploitProtection.validateSign()` | Tabela düzenleme |

## Konfigürasyon Sistemi
- **Dosya:** `obsidian.yml` (sunucu root'unda)
- **Yükleme:** `ObsidianConfig.init(File)` — Reflection ile static field'ları doldurur
- **Reload:** `/obsidian reload` veya sunucu `/reload` komutu
- **60+ ayar** — performance.*, security.*, branding.*
