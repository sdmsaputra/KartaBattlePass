# KartaBattlePass

KartaBattlePass is a flexible and feature-rich Battle Pass plugin for Spigot-based Minecraft servers. It allows server owners to create seasonal or ongoing Battle Passes with customizable levels, experience sources, and a powerful reward system.

## Features

- **Leveling System:** Players earn XP and level up their Battle Pass by performing in-game actions.
- **Configurable XP Sources:** Customize the amount of XP gained from various activities like mob kills, mining, fishing, and crafting.
- **Flexible Reward System:**
  - Define complex rewards for each level in a dedicated `rewards.yml`.
  - Multiple reward types: Items (with custom names, lore, enchantments), Commands, Money (via Vault), and Permissions (via Vault).
  - Support for separate `free` and `premium` reward tracks.
- **Reward GUI:** A user-friendly GUI (`/bp rewards`) for players to view and claim their earned rewards.
- **Auto-Grant or Manual Claim:** Choose whether rewards are granted automatically on level-up or if players must claim them manually.
- **PlaceholderAPI Support:** Integrated placeholders to display Battle Pass information on scoreboards, chat, or other plugins.
- **Admin Commands:** Powerful commands for managing player progress (add/set XP, set level) and reloading the plugin.
- **Offline Player Support:** Player data is saved and loaded reliably, ensuring progress is never lost.

## Dependencies

- **[Spigot/Paper/Folia](https://papermc.io/downloads)** (or other forks of Bukkit) 1.21+
- **[PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)** (Required)
- **[Vault](https://www.spigotmc.org/resources/vault.34315/)** (Optional, but required for `money` and `permission` rewards)
- A Vault-compatible economy plugin (e.g., EssentialsX) for money rewards.
- A Vault-compatible permissions plugin (e.g., LuckPerms) for permission rewards.

## Installation

1. Download the latest version of KartaBattlePass from the releases page.
2. Place the `KartaBattlePass-1.0.0.jar` file into your server's `plugins` directory.
3. Install the required dependencies (PlaceholderAPI, Vault).
4. Start or restart your server. The default configuration files (`config.yml`, `rewards.yml`, `messages.yml`) will be generated in the `/plugins/KartaBattlePass/` directory.

## Configuration

### `config.yml`

This file contains the main settings for the Battle Pass system.

```yaml
# General BattlePass settings
battlepass:
  max-level: 100
  # Formula: base-exp * level
  exp-per-level-base: 100

# Reward settings
rewards:
  auto-grant: false # Set to true to automatically claim rewards on level up.

# XP Sources
xp-sources:
  mob-kills:
    ZOMBIE: 5
    SKELETON: 5
  mining:
    DIAMOND_ORE: 10
  fishing: 5
```

### `rewards.yml`

This file defines all the rewards players can earn.

```yaml
# Each level is a top-level key. Under each level, you define a list of rewards.
#
# Reward Types:
#
# 1. Item
#    - type: item
#    - material: <Material_Name>
#    - amount: <number> (optional)
#    - name: "<display_name>" (optional, supports color codes)
#    - lore: [ "Line 1", "Line 2" ] (optional)
#    - enchantments: [ "sharpness:5", "unbreaking:3" ] (optional)
#    - track: <free|premium> (optional, defaults to free)
#
# 2. Command
#    - type: command
#    - command: "<command_line>" (use %player% for player name)
#    - executor: <console|player> (optional, defaults to console)
#    - track: <free|premium> (optional)
#
# 3. Money (Requires Vault)
#    - type: money
#    - amount: <number>
#    - track: <free|premium> (optional)
#
# 4. Permission (Requires Vault & a permission plugin)
#    - type: permission
#    - permission: "<permission.node>"
#    - duration: <time> (e.g., "7d", "1h". If omitted, permission is permanent. NOTE: Temporary permissions require a compatible permissions plugin.)
#    - track: <free|premium> (optional)

rewards:
  1:
    - type: item
      material: IRON_SWORD
      name: "&bRecruit's Blade"
      track: free
    - type: item
      material: DIAMOND_SWORD
      name: "&b&lPremium Blade"
      enchantments: [ "sharpness:1" ]
      track: premium
  10:
    - type: money
      amount: 500
      track: premium
    - type: permission
      permission: "essentials.fly"
      duration: "1d"
      track: premium
```

## Commands

- `/battlepass` or `/bp` - Main command alias.
- `/bp rewards` - Opens the reward claiming GUI.
- `/bp progress [player]` - Checks your or another player's progress.
- `/bp help` - Shows the help message.

### Admin Commands
- `/bp reload` - Reloads the plugin configuration.
- `/bp setxp <player> <amount>` - Sets a player's XP.
- `/bp addxp <player> <amount>` - Adds XP to a player.
- `/bp setlevel <player> <level>` - Sets a player's level.

## Permissions

- `kbattlepass.admin` - Grants access to all admin commands.
- `kbattlepass.open` - Allows opening the Battle Pass GUI.
- `kbattlepass.claim` - Allows claiming rewards (if not on auto-grant).
- `kbattlepass.progress.others` - Allows viewing other players' progress.
- `kartabattlepass.premium` - Grants access to the premium reward track.

## Placeholders

The following placeholders are available through PlaceholderAPI:

- `%kartabattlepass_level%` - The player's current Battle Pass level.
- `%kartabattlepass_exp%` - The player's current XP.
- `%kartabattlepass_exp_required%` - The XP required for the next level.
- `%kartabattlepass_unclaimed_rewards%` - The number of rewards the player has not yet claimed.
- `%kartabattlepass_next_reward_level%` - The next level that has a reward.