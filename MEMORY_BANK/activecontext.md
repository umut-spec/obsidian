# Obsidian — Active Context

## Son Güncelleme: 2026-05-13 (Tüm hook'lar tamamlandı)

## Mevcut Durum
- **Build**: ✅ BAŞARILI (`BUILD SUCCESSFUL`)
- **JAR**: `paper-server/build/libs/paper-paperclip-26.1.2.local-SNAPSHOT.jar`
- **Test Sunucu**: Deploy edildi → `Test-Server-MC/server.jar`
- **Proje Durumu**: 🟢 Beta — Tüm güvenlik ve performans hook'ları aktif

## Tamamlanan İşler (Bu Oturum)

### Güvenlik Hook'ları
1. ✅ **PacketLimiter** → `Connection.channelRead0` — Per-player packet rate limit + IP ban
2. ✅ **Creative NBT** → `ServerGamePacketListenerImpl.handleSetCreativeModeSlot` — Component size check
3. ✅ **FallingBlock Limit** → `FallingBlockEntity.fall()` — Per-chunk falling block cap
4. ✅ **Piston Limit** → `PistonBaseBlock.moveBlocks()` — Push block count validation
5. ✅ **Ban Persistence** → `ChatGuard.saveBans/loadBans` — obsidian-bans.json disk persistence

### Performans Hook'ları
6. ✅ **EntityOptimizer** → `ServerLevel.tickNonPassenger` — Stationary armor stand skip
7. ✅ **HopperOptimizer** → `HopperBlockEntity.pushItemsTick` — Destination-full cooldown
8. ✅ **TNTOptimizer** → `PrimedTnt.tick` — Nearby TNT merging (radius-based)
9. ✅ **RedstoneOptimizer** → `RedStoneWireBlock.neighborChanged` — 10k update/tick cap
10. ✅ **RedstoneOptimizer** → `MinecraftServer.tickServer` — Per-tick counter reset
11. ✅ **PathfindingOptimizer** → `PathNavigation.recomputePath` — Distance-based throttle

## Kalan İşler
- [ ] GitHub Actions CI/CD
- [ ] Logo / server-icon.png
- [ ] Wiki/Docs

## Ortam Bilgisi
- **JDK**: Eclipse Adoptium JDK 25.0.3.9 (hotspot)
- **OS**: Windows
- **Gradle**: Paper custom (createPaperclipJar)
- **IDE hataları**: NMS decompiler artifact'leri — build ile doğrulama yapılmalı
