# Obsidian - Progress Tracker

## Genel Durum: 🟢 Beta (v1.0.0)

### 🐛 Bilinen Buglar
- ~~**TEMP-BAN bypass:** ChatGuard temp-ban veriyor ama oyuncu geri girebiliyor (login kontrolü yok)~~ ✅ FİXLENDİ
- ~~**Ban persistence:** IP banları sunucu restart sonrası kayboluyor~~ ✅ FİXLENDİ (obsidian-bans.json)

---

## ✅ Tamamlanan — Temel Altyapı
- [x] Paper 26.1.2 klonlama ve rebranding
- [x] Gradle yapılandırması (settings, build, properties)
- [x] `ObsidianConfig.java` — Merkezi konfigürasyon sistemi (obsidian.yml)
- [x] `ObsidianCommand.java` — /obsidian version|status|reload (HOOK AKTİF ✅)
- [x] DedicatedServer.java hook — Config init + startup banner (HOOK AKTİF ✅)
- [x] CraftServer.java hook — Komut kaydı + reload + ban load (HOOK AKTİF ✅)
- [x] README.md — Proje dökümantasyonu
- [x] build-obsidian.bat — Windows build script
- [x] Test sunucu kurulumu (Test-Server-MC)
- [x] İlk başarılı build + çalışan sunucu
- [x] Obsidian brand string (F3 → "Obsidian")

## ✅ Tamamlanan — Güvenlik Hook'ları (Tam NMS Entegrasyonu)
- [x] ConnectionGuard → ServerHandshakePacketListenerImpl (anti-bot + IP throttle + ban check)
- [x] ChatGuard → Chat handler (rate limit + violation escalation)
- [x] ChatGuard → Command handler (rate limit + violation escalation)
- [x] ChatGuard → IP ban persistence (obsidian-bans.json read/write)
- [x] PacketLimiter → Connection.channelRead0 (per-player packet rate limit + auto-ban)
- [x] ExploitProtection → Book handler (page/total size validation)
- [x] ExploitProtection → Sign handler (line length validation)
- [x] ExploitProtection → Creative mode handler (NBT component size validation)
- [x] ExploitProtection → FallingBlockEntity (per-chunk falling block limit)
- [x] ExploitProtection → PistonBaseBlock (push block count limit)

## ✅ Tamamlanan — Performans Hook'ları (Derin NMS)
- [x] EntityOptimizer → ArmorStand tick skip (ServerLevel.tickNonPassenger)
- [x] HopperOptimizer → pushItemsTick cooldown extension (HopperBlockEntity)
- [x] TNTOptimizer → TNT entity merging (PrimedTnt.tick)
- [x] RedstoneOptimizer → Neighbor update per-tick limiter (RedStoneWireBlock.neighborChanged)
- [x] RedstoneOptimizer → Tick counter reset (MinecraftServer.tickServer)
- [x] PathfindingOptimizer → Distance-based recompute throttle (PathNavigation.recomputePath)

## ❌ Yapılacak — Diğer
- [ ] GitHub Actions CI/CD
- [ ] Logo / server-icon.png
- [ ] Wiki/Docs

---

## Dosya Yapısı — Performance
```
performance/
├── ChunkOptimizer.java      — Chunk loading/saving/sending limitleri
├── EntityOptimizer.java      — Armor stand tick skip + activation range
├── HopperOptimizer.java      — Transfer cooldown + destination-full check
├── PathfindingOptimizer.java — Distance-based recompute throttle
├── RedstoneOptimizer.java    — Neighbor update per-tick limiter
└── TNTOptimizer.java         — TNT entity merging
```

## Dosya Yapısı — Security
```
security/
├── ChatGuard.java            — Chat/Command rate limit + IP ban persistence
├── ConnectionGuard.java      — Anti-bot + IP throttle + handshake ban check
├── ExploitProtection.java    — Book/Sign/NBT/FallingBlock/Piston validation
└── PacketLimiter.java        — Per-player packet rate limit + escalation
```

## NMS Hook Haritası
| Hook Noktası | Dosya | Açıklama |
|---|---|---|
| `ServerHandshakePacketListenerImpl` | Handshake | Anti-bot + IP ban enforcement |
| `Connection.channelRead0` | Packet IO | Packet rate limit |
| `ServerGamePacketListenerImpl` | Game handler | Chat/Command/Creative/Book/Sign hooks |
| `MinecraftServer.tickServer` | Server tick | Redstone counter reset |
| `ServerLevel.tickNonPassenger` | Entity tick | Armor stand optimization |
| `PrimedTnt.tick` | TNT tick | TNT merging |
| `HopperBlockEntity.pushItemsTick` | Hopper tick | Transfer cooldown |
| `PathNavigation.recomputePath` | AI navigation | Pathfinding throttle |
| `RedStoneWireBlock.neighborChanged` | Redstone | Update limiter |
| `FallingBlockEntity.fall` | Entity spawn | Per-chunk limit |
| `PistonBaseBlock.moveBlocks` | Piston | Push block limit |

---

## Build Notları
```bash
# Patch uygula (ilk sefer veya kaynak değişince)
.\gradlew.bat applyPatches

# Build et
.\gradlew.bat createPaperclipJar

# JAR konumu
paper-server\build\libs\paper-paperclip-26.1.2.local-SNAPSHOT.jar
```

## Önemli Notlar
- `JAVA_HOME` = `C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot`
- Git global user: `ObsidianMC <obsidian@obsidianmc.dev>`
- İlk build uzun sürer (decompile + 927 patch), sonraki build'ler ~1dk
- Test sunucu: `c:\Users\umut\Desktop\thenewerea\Test-Server-MC\`
- Offline mode aktif (test için)
