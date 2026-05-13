# Obsidian - Tech Stack & Context

## Teknolojiler
| Teknoloji | Versiyon | Kullanım |
|-----------|----------|----------|
| Java | 25 (Temurin 25.0.3+9) | Ana dil |
| Gradle | 9.4.1 | Build sistemi |
| paperweight-core | 2.0.0-SNAPSHOT | Paper build plugin |
| Minecraft | 26.1.2 | Hedef MC sürümü |
| Paper | 26.1.2 | Base fork |
| Git | Latest | Versiyon kontrol |

## Build Pipeline
```
Minecraft Server JAR (Mojang)
    ↓ extractFromBundler
Obfuscated JAR
    ↓ macheRemapJar (remapping)
    ↓ macheDecompileJar (Vineflower decompiler)
Decompiled Source
    ↓ setupMacheSources + 117 mache patches
    ↓ applySourcePatches + 927 Paper patches
    ↓ applyFeaturePatches
Paper Source + Obsidian Source
    ↓ compileJava
    ↓ createBundlerJar
    ↓ createPaperclipJar
obsidian-26.1.2.jar (Final Output)
```

## Key Gradle Tasks
| Task | Açıklama |
|------|----------|
| `applyPatches` | MC decompile + patch uygula |
| `createPaperclipJar` | Final server JAR oluştur |
| `runDevServer` | Dev sunucu başlat |
| `rebuildPatches` | Değişiklikleri patch'e dönüştür |
| `obsidianInfo` | Obsidian bilgisi göster |

## Gradle Properties
```properties
group=dev.obsidianmc.obsidian
mcVersion=26.1.2
obsidianVersion=1.0.0
obsidianBrand=Obsidian
obsidianDescription=Performance and Security focused Paper fork
```

## JVM Flags (Aikar's)
```
-Xms2G -Xmx2G
-XX:+UseG1GC
-XX:+ParallelRefProcEnabled
-XX:MaxGCPauseMillis=200
-XX:G1NewSizePercent=30
-XX:G1MaxNewSizePercent=40
-XX:G1HeapRegionSize=8M
-XX:G1ReservePercent=20
-XX:InitiatingHeapOccupancyPercent=15
-XX:SurvivorRatio=32
-XX:MaxTenuringThreshold=1
```
