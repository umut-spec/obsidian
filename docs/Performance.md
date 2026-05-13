# Performance Optimizations

Obsidian hooks directly into NMS (Net Minecraft Server) code to deliver performance gains that config-only solutions can't achieve.

---

## 🧨 TNT Merging
**Hook:** `PrimedTnt.tick()` → `TNTOptimizer.tryMergeNearby()`

When multiple TNT entities are within `merge-radius` blocks of each other, they merge into a single entity with increased explosion power (capped at 40.0F).

**Impact:** 50 TNTs → 1-2 entity calculations instead of 50.

**Config:**
```yaml
performance.tnt.optimize: true
performance.tnt.merge-radius: 0.5
```

---

## ⚡ Redstone Update Limiter
**Hook:** `RedStoneWireBlock.neighborChanged()` → `RedstoneOptimizer.allowNeighborUpdate()`  
**Reset:** `MinecraftServer.tickServer()` → `RedstoneOptimizer.resetTickCounter()`

Caps redstone neighbor updates at 10,000 per server tick. Prevents redstone clock lag machines from killing TPS. Counter resets every tick.

**Impact:** Redstone lag machines become harmless.

**Config:**
```yaml
performance.tick.optimize-redstone: true
```

---

## 🧭 Pathfinding Throttle
**Hook:** `PathNavigation.recomputePath()` → `PathfindingOptimizer.getRecomputeInterval()`

Reduces pathfinding recomputation frequency based on entity distance to the nearest player:

| Distance | Recompute Interval |
|----------|-------------------|
| < 16 blocks | Every tick (no throttle) |
| 16-32 blocks | Every 10 ticks (0.5s) |
| 32-48 blocks | Every 20 ticks (1s) |
| > 48 blocks | Every 40 ticks (2s) |

**Impact:** 60-80% less pathfinding CPU usage for distant mobs.

**Config:**
```yaml
performance.async.pathfinding: true
```

---

## 🗿 Armor Stand Tick Skip
**Hook:** `ServerLevel.tickNonPassenger()` → `EntityOptimizer.shouldSkipArmorStandTick()`

Stationary armor stands (no passengers, no velocity, no recent position changes) are completely skipped during entity ticking.

**Impact:** Servers with many decorative armor stands see significant TPS gains.

**Config:**
```yaml
performance.tick.optimize-armor-stand: true
```

---

## 📦 Hopper Cooldown Extension
**Hook:** `HopperBlockEntity.pushItemsTick()` → `HopperOptimizer.shouldThrottle()`

When a hopper tries to push items into a full container, Obsidian extends its cooldown to avoid wasting tick cycles checking every tick.

**Impact:** Reduces hopper-related lag in farms and sorting systems.

**Config:**
```yaml
performance.tick.optimize-hoppers: true
performance.tick.hopper-transfer-cooldown: 8
```

---

## 📊 Performance Monitoring

Use `/obsidian status` in-game to see which optimizations are active. Use spark (`/spark profiler`) to measure TPS impact.
