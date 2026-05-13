# Security Features

Obsidian provides multi-layered security protection at the network, packet, and gameplay levels.

---

## 📡 Packet Rate Limiter
**Hook:** `Connection.channelRead0()`

Monitors packets per second per player. Escalation chain:
1. **Warning** — logged to console
2. **Kick** — player disconnected with message
3. **IP Ban** — automatic temp-ban after `spam-ban-threshold` violations

**Config:**
```yaml
security.packets.enable-limiter: true
security.packets.max-per-second: 500
security.packets.spam-ban-threshold: 3
```

---

## 🤖 Anti-Bot System
**Hook:** `ServerHandshakePacketListenerImpl`

Detects bot attacks at the handshake level (before the player even loads):
- **Global rate limit** — max joins per second across all IPs
- **Per-IP throttle** — prevents rapid reconnection from same IP
- **IP ban check** — blocks banned IPs at handshake

**Config:**
```yaml
security.anti-bot.enabled: true
security.anti-bot.max-joins-per-second: 3
security.anti-bot.cooldown-seconds: 10
```

---

## 🔒 IP Ban Persistence
**File:** `obsidian-bans.json`

All IP bans are persisted to disk. Bans survive server restarts.

```json
{
  "ip_bans": {
    "192.168.1.100": 1718300000000
  }
}
```

**Commands:**
- `/obsidian bans` — list active bans
- `/obsidian unban <ip>` — unban specific IP
- `/obsidian unban all` — clear all bans

---

## 💥 Falling Block Protection
**Hook:** `FallingBlockEntity.fall()`

Counts falling block entities per chunk. If the count exceeds `max-falling-blocks-per-chunk`, new falling blocks are silently blocked from spawning.

**Prevents:** Sand/gravel crash machines that spawn thousands of falling blocks.

**Config:**
```yaml
security.world.prevent-falling-block-crash: true
security.world.max-falling-blocks-per-chunk: 100
```

---

## 🔧 Piston Push Limit
**Hook:** `PistonBaseBlock.moveBlocks()`

Validates the number of blocks a piston attempts to push. If it exceeds the configured limit, the push is cancelled.

**Prevents:** Piston-based lag machines and dupe exploits.

**Config:**
```yaml
security.world.prevent-piston-crash: true
security.world.max-piston-push-limit: 12
```

---

## 📖 Book/Sign/NBT Protection
**Hooks:** `ServerGamePacketListenerImpl`

- **Books** — validates page count, page size, and total book size
- **Signs** — validates line length
- **Creative Items** — validates NBT component count and size
- **NBT Overflow** — caps total NBT data size at 2MB

**Config:**
```yaml
security.exploits.prevent-book: true
security.exploits.prevent-sign: true
security.exploits.prevent-creative-crash: true
security.exploits.prevent-nbt-overflow: true
```

---

## 💬 Chat/Command Rate Limiter
**Hook:** `ServerGamePacketListenerImpl` (chat + command handlers)

Per-player sliding window rate limiter for both chat messages and commands. Violations trigger warning → kick → ban escalation.

**Config:**
```yaml
security.chat.enable-rate-limit: true
security.chat.rate-limit-max-messages: 5
security.chat.rate-limit-time-window-ms: 5000
security.chat.enable-command-rate-limit: true
security.chat.command-rate-limit-max: 10
```
