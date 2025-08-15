# KartaBattlePass

KartaBattlePass is a flexible and feature-rich Battle Pass plugin for Spigot-based Minecraft servers. It allows server owners to create seasonal or ongoing Battle Passes with customizable levels, experience sources, and a powerful reward system.

## Features

- **Leveling System:** Players earn XP and level up their Battle Pass.
- **Sequential Quest System:** Players complete quests in ordered categories. This provides a structured progression system.
- **Flexible Reward System:**
  - Define complex rewards for each level in a dedicated `rewards.yml`.
  - Multiple reward types: Items, Commands, Money (via Vault), and Permissions (via Vault).
  - Support for separate `free` and `premium` reward tracks.
- **Interactive GUIs:**
  - `/bp` opens a main menu to navigate to other GUIs.
  - `/bp rewards`: A user-friendly GUI for players to view and claim their earned rewards.
  - `/bp quests`: A GUI to view quest categories and the quests within them, showing current progress.
  - `/bp top`: A leaderboard to see who is the highest level.
- **Auto-Grant or Manual Claim:** Choose whether rewards are granted automatically on level-up or if players must claim them manually.
- **PlaceholderAPI Support:** Integrated placeholders to display Battle Pass information.
- **Admin Commands:** Powerful commands for managing player progress and reloading the plugin.
- **Offline Player Support:** Player data is saved and loaded reliably.

## Dependencies

- **[Spigot/Paper/Folia](https://papermc.io/downloads)** (or other forks of Bukkit) 1.21+
- **[PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)** (Required)
- **[Vault](https://www.spigotmc.org/resources/vault.34315/)** (Optional, but required for `money` and `permission` rewards)

## Installation

1. Download the latest version of KartaBattlePass from the releases page.
2. Place the `KartaBattlePass-latest.jar` file into your server's `plugins` directory.
3. Install the required dependencies (PlaceholderAPI, Vault).
4. Start or restart your server. The default configuration files (`config.yml`, `rewards.yml`, `quests.yml`, `messages.yml`) will be generated in the `/plugins/KartaBattlePass/` directory.

## Configuration

### `config.yml`

This file contains the main settings for the Battle Pass system.

#### Level Progression
You can control how much experience is required to level up using the `battlepass.exp-per-level-base` setting. The formula used is `EXP Required = current_level * exp-per-level-base`.
For example, if `exp-per-level-base` is `100`:
- To get from Level 1 to Level 2, you need `1 * 100 = 100` EXP.
- To get from Level 10 to Level 11, you need `10 * 100 = 1000` EXP.

See the comments in the generated file for other details.

### `rewards.yml`

This file defines all the rewards players can earn from leveling up. The structure is explained in the generated file.

### `quests.yml`

This file defines the quest categories and the individual quests. Players must complete quests in the order they are listed within a category.

```yaml
# Define your categories here.
# The 'quests' list under each category defines the sequence.
quest-categories:
  mining:
    display-name: "&b⛏ Mining Quests"
    display-item: "DIAMOND_PICKAXE"
    quests:
      - "mine_stone"
      - "mine_coal"
      - "mine_diamonds"
  combat:
    display-name: "&c⚔ Combat Quests"
    display-item: "DIAMOND_SWORD"
    quests:
      - "kill_zombies"
      - "kill_skeletons"

# Define the individual quests here.
# The IDs must match the ones used in the categories above.
quests:
  mine_stone:
    type: "block-break"
    target: "STONE"
    amount: 64
    exp: 50
    display-name: "&7Mine 64 Stone"
    rewards:
      - "eco give %player% 100"
  kill_zombies:
    type: "kill-mob"
    target: "ZOMBIE"
    amount: 10
    exp: 25
    display-name: "&2Kill 10 Zombies"
    rewards:
      - "minecraft:give %player% minecraft:iron_sword 1"
# ... and so on for all other quests
```

## Commands

- `/battlepass`, `/bp` - Main command alias.
- `/bp rewards` - Opens the reward claiming GUI.
- `/bp quests` - Opens the quest category GUI.
- `/bp top` - Opens the leaderboard GUI.
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
- `kbattlepass.claim` - Allows claiming rewards.
- `kbattlepass.progress.others` - Allows viewing other players' progress.
- `kartabattlepass.premium` - Grants access to the premium reward track.

## Placeholders

- `%kartabattlepass_level%` - The player's current Battle Pass level.
- `%kartabattlepass_exp%` - The player's current XP.
- `%kartabattlepass_exp_required%` - The XP required for the next level.
- `%kartabattlepass_unclaimed_rewards%` - The number of rewards the player has not yet claimed.
- `%kartabattlepass_next_reward_level%` - The next level that has a reward.