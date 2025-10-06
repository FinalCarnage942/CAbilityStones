# CAbilityStones

**CAbilityStones** is a powerful, flexible Minecraft plugin that allows players to enhance their gameplay by collecting, using, and combining special ability stones. Each stone grants unique powers or effects, giving players new ways to interact with the world and customize their playstyle. Designed for performance and ease-of-use, this plugin is perfect for servers looking to add a layer of strategy and fun.

## Features

- **Collectable Ability Stones**: Players can find or earn stones that grant various abilities, from buffs to special actions.  
- **Custom Stone Effects**: Each stone can be configured with unique effects, durations, and cooldowns.    
- **Command Support**: Manage stones with commands.  
- **Fully Configurable**: Adjust stone types, effects, rarity, and more through a simple `config.yml`.  
- **Performance Focused**: All stone effects are optimized to minimize server lag, even on large multiplayer worlds.  
- **Clean & Maintainable**: Follows clean code principles and provides robust error handling and logging.

## Installation

1. **Prerequisites**:

   - Minecraft server running **Spigot/Paper 1.21** or compatible.  

2. **Steps**:

   - Build it from source.  
   - Place the `CAbilityStones.jar` file in your server's `plugins` folder.  
   - Restart your server or use `/restart` to load the plugin.  
   - The plugin will automatically generate a `config.yml` and any required data files in `plugins/CAbilityStones`.
---

## Configuration

All plugin settings are managed through `config.yml` located in `plugins/CAbilityStones`. Example configuration:

```yaml
stones:
    fire:
    material: RED_DYE
    name: '&c&lFire Stone'
    ability_name: 'Fire Burst'
    lore:
      - '&7Ability: &eFire Burst'
      - '&7Shoot a fireball that explodes'
      - '&7on impact'
      - ''
      - '&eCooldown: 10s'
    cooldown: 10

    air:
    material: WHITE_DYE
    name: '&f&lAir Stone'
    ability_name: 'Dash Forward'
    lore:
      - '&7Ability: &eDash Forward'
      - '&7Quickly dash forward 5-7 blocks'
      - '&7with Speed II and particles'
      - ''
      - '&eCooldown: 8s'
    cooldown: 8
