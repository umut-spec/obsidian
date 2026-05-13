# Configuration

All Obsidian settings live in `obsidian.yml`, created on first server start.

---

## Performance Settings

### Entity Optimization
```yaml
performance:
  entity:
    optimize-activation: true       # Enable entity activation range optimization
    activation-range:
      animals: 32                    # Blocks — range for animal activation
      monsters: 32                   # Blocks — range for hostile mob activation
      misc: 16                       # Blocks — range for misc entities
      water: 16                      # Blocks — range for water entities
      villagers: 32                  # Blocks — range for villager activation
      flying-monsters: 48            # Blocks — range for phantoms, ghasts etc.
    tick-inactive-villagers: true     # Keep ticking villagers even when inactive
    ignore-spectators: true          # Don't count spectators for activation
```

### Chunk Optimization
```yaml
performance:
  chunks:
    max-auto-save-per-tick: 24       # Max chunks saved per tick during autosave
    optimize-loading: true           # Enable optimized chunk loading
    prevent-moving-into-unloaded: true  # Block player movement into unloaded chunks
    max-sends-per-tick: 81           # Max chunk packets sent per tick per player
```

### Tick Optimization
```yaml
performance:
  tick:
    optimize-redstone: true          # Enable redstone neighbor update limiter (10k/tick cap)
    optimize-hoppers: true           # Enable hopper transfer cooldown optimization
    hopper-transfer-cooldown: 8      # Ticks between hopper transfers
    hopper-disable-on-full: false    # Disable hoppers pointing at full containers
    optimize-armor-stand: true       # Skip ticking stationary armor stands
    skip-map-updates-if-not-tracking: true  # Skip map item updates when no player tracking
```

### TNT Optimization
```yaml
performance:
  tnt:
    optimize: true                   # Enable TNT entity merging
    merge-radius: 0.5               # Blocks — merge TNTs within this radius
```

### Async Operations
```yaml
performance:
  async:
    chunk-generation: true           # Async chunk generation
    pathfinding: true                # Distance-based pathfinding throttle
    pathfinding-max-threads: 2       # Max threads for async pathfinding
```

### Item Merging
```yaml
performance:
  merging:
    item-radius: 2.5                 # Blocks — merge dropped items within radius
    experience-orb-radius: 3.0       # Blocks — merge XP orbs within radius
```

### Memory Optimization
```yaml
performance:
  memory:
    flush-regions-on-save: true      # Free region memory after saving
    reduce-entity-allocations: true  # Reduce object allocations in entity ticking
    cache-nbt-tag-string: true       # Cache NBT tag string representations
```

---

## Security Settings

### Packet Limiter
```yaml
security:
  packets:
    enable-limiter: true             # Enable per-player packet rate limiting
    max-per-second: 500              # Max packets per second before action
    spam-ban-threshold: 3            # Violations before temp IP ban
    spam-action: "KICK"              # Action: KICK, BAN, or WARN
```

### Anti-Bot
```yaml
security:
  anti-bot:
    enabled: true                    # Enable anti-bot system
    max-joins-per-second: 3          # Max joins per second globally
    cooldown-seconds: 10             # Cooldown after throttle
    action: "THROTTLE"               # Action: THROTTLE, KICK, or BAN
```

### Exploit Protection
```yaml
security:
  exploits:
    prevent-book: true               # Validate book page/total size
    max-book-page-size: 2560         # Max bytes per book page
    max-book-total-size: 102400      # Max total book size in bytes
    prevent-sign: true               # Validate sign line length
    max-sign-line-length: 384        # Max characters per sign line
    prevent-creative-crash: true     # Block oversized creative items
    prevent-nbt-overflow: true       # Validate NBT data size
    max-nbt-size: 2097152            # Max NBT size (2MB)
```

### World Protection
```yaml
security:
  world:
    prevent-chunk-ban: true          # Prevent chunk-based crash exploits
    prevent-falling-block-crash: true # Enable per-chunk falling block limit
    max-falling-blocks-per-chunk: 100 # Max falling blocks per chunk
    prevent-piston-crash: true        # Enable piston push limit
    max-piston-push-limit: 12         # Max blocks a piston can push
```

### Chat/Command Rate Limit
```yaml
security:
  chat:
    enable-rate-limit: true          # Rate limit chat messages
    rate-limit-max-messages: 5       # Max messages per window
    rate-limit-time-window-ms: 5000  # Time window in ms
    enable-command-rate-limit: true   # Rate limit commands
    command-rate-limit-max: 10        # Max commands per window
    command-rate-limit-time-window-ms: 5000
```

---

## Branding
```yaml
branding:
  server-brand: "Obsidian"           # Shown in F3 debug screen
  server-motd: "An Obsidian Server"  # Default MOTD
```

---

## Logging
```yaml
logging:
  security-logging: true             # Enable security event logging
  log-exploit-attempts: true         # Log exploit attempts
  log-packet-violations: false       # Log packet rate violations (verbose)
  log-connection-events: true        # Log connection/disconnect events
```
