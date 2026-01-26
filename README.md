# RexWelcome

A powerful welcome message plugin for Minecraft servers with randomized titles, first-join tracking, and PlaceholderAPI integration.

## Features

- **Randomized Welcome Titles** - Multiple title variations for both first-time and returning players
- **First-Join Detection** - Different messages and actions for new vs returning players
- **Join Number Tracking** - Track who was the 1st, 2nd, 3rd player to ever join
- **First-Join Commands** - Execute custom commands when players join for the first time
- **Broadcast System** - Announce first-time joins to all online players
- **PlaceholderAPI Integration** - Custom placeholders for use in other plugins
- **Advanced Color Support** - Legacy codes, hex colors, and MiniMessage format
- **Join Sounds** - Customizable sounds when players join
- **Chat Clearing** - Optional chat clear before welcome messages
- **Hot Reload** - Reload configuration without restarting

## Requirements

- **Java 21** (Required)
- **Minecraft 1.20.4+** (Paper recommended, Spigot compatible)
- **PlaceholderAPI** (Optional, for placeholder support)

## Installation

1. Download the latest release from [GitHub Releases](https://github.com/rexsystems/RexWelcome/releases)
2. Place the JAR in your server's `plugins/` folder
3. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
4. Restart your server
5. Edit `plugins/RexWelcome/config.yml` to customize
6. Run `/rw reload` to apply changes

## Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/rexwelcome` or `/rw` | Main plugin command | - |
| `/rw reload` | Reload configuration | `rexwelcome.reload` |
| `/rw info` | Show plugin information | - |
| `/rw help` | Show help message | - |

## Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `rexwelcome.reload` | Reload plugin configuration | op |
| `rexwelcome.info` | View plugin information | op |
| `rexwelcome.help` | View help message | op |

## Placeholders

### PlaceholderAPI Placeholders

Use these in **any plugin** that supports PlaceholderAPI:

| Placeholder | Description | Example |
|-------------|-------------|---------|
| `%rexwelcome_playernumber%` | Player's join number | `42` |
| `%rexwelcome_totalplayers%` | Total unique players | `150` |
| `%rexwelcome_player_<N>%` | Name of Nth player | `Steve` |

**Example Usage:**
```yaml
# In DeluxeChat, EssentialsX, or any PAPI-compatible plugin
format: "&7[#%rexwelcome_playernumber%] &f{USERNAME}: {MESSAGE}"
message: "&aYou are player #%rexwelcome_playernumber%!"
```

### Internal Placeholders

Use these **inside RexWelcome's config.yml**:

| Placeholder | Description |
|-------------|-------------|
| `%player%` | Player's name |
| `%displayname%` | Player's display name |
| `%prefix%` | Plugin prefix |
| `%online%` | Online player count |
| `%max_players%` | Maximum players |
| `%total_players%` | Total unique players |
| `%joincount%` | Alias for %total_players% |

## Configuration

### Color Format Support

RexWelcome supports multiple color formats:

```yaml
# Legacy color codes
message: "&aGreen &bAqua &cRed &eYellow"

# Hex colors
message: "&#ff5733Orange text"
message: "#ff5733This also works"

# MiniMessage format
message: "<color:#ff5733>Orange</color>"
message: "<gradient:#ff0000:#00ff00>Gradient text</gradient>"
message: "<bold><#FFD700>Bold gold text</bold>"
```

### Example: Randomized Titles

```yaml
returning-player:
  titles:
    - title: "&#FFD700WELCOME BACK!"
      subtitle: "&fWe missed you, &e%player%&f!"
    - title: "&#FF6B6BYOU'RE BACK!"
      subtitle: "&7Good to see you again!"
    - title: "&#00FF7FBACK AGAIN?"
      subtitle: "&fOf course you are!"
```

### Example: First-Join Commands

```yaml
first-join:
  enabled: true
  commands:
    - "give %player% diamond 5"
    - "eco give %player% 1000"
    - "advancement grant %player% only story/mine_diamond"
```

### Example: Welcome Messages

```yaml
first-join:
  messages:
    - "&7"
    - "%prefix%&#00BFFF✨ Welcome &f%player%&b!"
    - "&7• &fYou are player #&#f75634%total_players%"
    - "&7• &fVisit: &#f75634https://rexsystems.cc"
    - "&7"
```

## Player Data Storage

Player data is stored in `plugins/RexWelcome/playerdata.yml`:

```yaml
players:
  550e8400-e29b-41d4-a716-446655440000:
    join-number: 1
    name: "Steve"
    first-join: 1673472000000
```

This tracks:
- **join-number** - Order in which players joined (1st, 2nd, 3rd, etc.)
- **name** - Player's username
- **first-join** - Timestamp of first join

## Support

- **Website**: [rexsystems.cc](https://rexsystems.cc)
- **Documentation**: [rexsystems.github.io](https://rexsystems.github.io)
- **Issues**: [GitHub Issues](https://github.com/rexsystems/RexWelcome/issues)

## License

This project is licensed under the MIT License.

---

**Developed by RexSystems** • Version 1.0.0
