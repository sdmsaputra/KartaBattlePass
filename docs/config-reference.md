# Configuration Reference

This page provides a detailed reference for all configuration files used by KartaBattlePass.

## `config.yml`
This is the main configuration file for the plugin.

- **`storage`**: Configures the database connection.
- **`redis`**: Configures Redis for network features.
- **`economy`**: Sets the economy provider and prices.
- **`season`**: Defines the current season's start and end dates.
- ...

## `quests/*.yml`
Files in this directory define the quests available to players. Each file can contain multiple quests.

Example quest (`daily_break_logs`):
```yaml
quests:
  daily_break_logs:
    name: "Lumberjack"
    description:
      - "<gray>Chop down <green>64</green> Oak Logs."
    category: DAILY
    type: "BREAK_BLOCK"
    objectives:
      target: 64
      block: "OAK_LOG"
    points: 50
    repeatable: false
    rewards: []
```

## `rewards.yml`
This file defines the rewards for each tier of the battle pass.

---

*This document is a placeholder and will be updated as features are implemented.*
