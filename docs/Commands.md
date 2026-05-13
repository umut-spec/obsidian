# Commands

All Obsidian commands are accessed through `/obsidian`.

---

## `/obsidian version`
**Permission:** `obsidian.command`  
Shows server version, Minecraft version, and base (Paper).

## `/obsidian status`
**Permission:** `obsidian.command`  
Displays the enabled/disabled state of all performance and security modules.

## `/obsidian reload`
**Permission:** `obsidian.command.reload`  
Reloads `obsidian.yml` without restarting the server.

> **Note:** Some settings (like async pathfinding threads) require a full restart.

## `/obsidian bans`
**Permission:** `obsidian.command`  
Lists all currently active IP bans with remaining time.

## `/obsidian unban <ip>`
**Permission:** `obsidian.command.unban`  
Removes the IP ban for a specific address.

## `/obsidian unban all`
**Permission:** `obsidian.command.unban`  
Clears all active IP bans.

---

## Permissions

| Permission | Default | Description |
|-----------|---------|-------------|
| `obsidian.command` | OP | Access to /obsidian base command |
| `obsidian.command.reload` | OP | Reload configuration |
| `obsidian.command.unban` | OP | Manage IP bans |
