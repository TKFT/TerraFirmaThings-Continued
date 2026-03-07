
# TerraFirma Things

TerraFirma Things (`tfcthings`) is a NeoForge mod for Minecraft 1.21.1 that extends TerraFirmaCraft with traversal gear, traps, ranged tools, surveying equipment, sharpening systems, and decorative blocks.

## Mod Info

- **Mod ID:** `tfcthings`
- **Minecraft:** `1.21.1`
- **NeoForge:** `21.1.218`
- **Java:** `21`
- **Version:** `1.0.0`
- **License:** `GPL-3.0`

## Dependencies and Compatibility

- **Required:** TerraFirmaCraft
- **Modloader:** NeoForge
- **Optional:** JEI, Jade

## Installation

1. Install Minecraft `1.21.1`.
2. Install NeoForge `21.1.218` or higher.
3. Install TerraFirmaCraft for 1.21.1.
4. Place `TerraFirmaThings` in your `mods` folder.
5. Launch the game.

## Configuration

Common config is under `config/tfcthings-common.toml` and includes:

- Master toggles for each system (snow shoes, traps, rope tools, slings, displays, etc.)
- Rope bridge and rope ladder max lengths
- Rope and hook javelin tether/retract behavior
- Trap/snare behavior and catch/breakout tuning
- Whetstone/honing/grindstone charge and speed settings
- Sling tuning (charge, multipliers, ammo behavior)

## Item and Block Guide

### Snow Shoes

![Snow Shoes Item](img/snowshoesuse.png)
![Snow Shoes Recipe Image](img/snowshoecraft.png)

- **What it does:** Reduces snow movement slowdown when worn in the boots slot. Allows player to walk on powder snow without sinking.
- **How to get it:** Craft basic snow shoes from cloth/leather/lumber; durable version upgrades through hiking boots.
- **Notes:** Durability is consumed while actively negating slowdown.

### Hiking Boots

![Hiking Boots Item/Block Image](docs/images/hiking_boots_item_block.png)
![Hiking Boots Recipe Image](docs/images/hiking_boots_recipe.png)

- **What it does:** Reduces movement slowdown from dense plants and brush.
- **How to get it:** Forge metal bracing, then craft with leather boots and fiber.
- **Notes:** Durability is consumed while slowdown reduction is active.

### Rope Bridge

![Rope Bridge Item/Block Image](docs/images/rope_bridge_item_block.png)
![Rope Bridge Recipe Image](docs/images/rope_bridge_recipe.png)

- **What it does:** Throwable bundle that places rope bridge segments across valid straight gaps.
- **How to get it:** Craft rope bridge bundles from jute fiber and lumber.
- **Notes:** Stick right-click adjusts segment height; shift-right-click retrieves placed bridge sections.

### Rope Ladder

![Rope Ladder Item/Block Image](docs/images/rope_ladder_item_block.png)
![Rope Ladder Recipe Image](docs/images/rope_ladder_recipe.png)

- **What it does:** Wall-mounted ladder that auto-extends downward while consuming ladders from inventory.
- **How to get it:** Craft from jute fiber and lumber.
- **Notes:** Shift-right-click allows pickup of ladder chains.

### Rope Javelin

![Rope Javelin Item/Block Image](docs/images/rope_javelin_item_block.png)
![Rope Javelin Recipe Image](docs/images/rope_javelin_recipe.png)

- **What it does:** Tethered throwing weapon with reel/retract behavior.
- **How to get it:** Crafted per metal tier (copper through red steel).
- **Notes:** Can tether entities and auto-retract if path/range is invalid.

### Bear Trap

![Bear Trap Item/Block Image](docs/images/bear_trap_item_block.png)
![Bear Trap Recipe Image](docs/images/bear_trap_recipe.png)

- **What it does:** Immobilizes and damages entities that trigger it.
- **How to get it:** Forge bear trap halves on an anvil, then weld halves into a trap.
- **Notes:** Can be buried with a shovel; shift-right-click closed trap to reset.

### Snare

![Snare Item/Block Image](docs/images/snare_item_block.png)
![Snare Recipe Image](docs/images/snare_recipe.png)

- **What it does:** Passive trap for small creatures.
- **How to get it:** Crafted from rods, lumber, and jute fiber.
- **Notes:** Shift-right-click triggered snare to release/reset; breaking snare releases capture.

### Fishing Net

![Fishing Net Item/Block Image](docs/images/fishing_net_item_block.png)
![Fishing Net Recipe Image](docs/images/fishing_net_recipe.png)

- **What it does:** Passive fish-catching net stretched between anchors.
- **How to get it:** Craft net segments and fishing net anchors.
- **Notes:** Place anchors on a straight axis near water; shift-right-click anchor to recover deployed net.

### Crowns

![Crowns Item/Block Image](docs/images/crowns_item_block.png)
![Crowns Recipe Image](docs/images/crowns_recipe.png)

- **What it does:** Decorative helmet-slot crowns with gem variants.
- **How to get it:** Forge empty gold/platinum crowns, then set gems via crafting.
- **Notes:** Multiple gem variants are supported.

### Gem Display

![Gem Display Item/Block Image](docs/images/gem_display_item_block.png)
![Gem Display Recipe Image](docs/images/gem_display_recipe.png)

- **What it does:** Decorative display stand for cut gems (all TFC rock types).
- **How to get it:** Craft variant by rock type.
- **Notes:** Right-click to insert/remove gems; supports comparator output.

### Hook Javelin

![Hook Javelin Item/Block Image](docs/images/hook_javelin_item_block.png)
![Hook Javelin Recipe Image](docs/images/hook_javelin_recipe.png)

- **What it does:** Grappling javelin for tethered traversal and recovery.
- **How to get it:** Forge hook heads (steel tiers), then craft assembled javelin.
- **Notes:** Throw to anchor, retract to reposition, sneak-retract to recover.

### Surveyor's Hammer

![Surveyors Hammer Item/Block Image](docs/images/surveyors_hammer_item_block.png)
![Surveyors Hammer Recipe Image](docs/images/surveyors_hammer_recipe.png)

- **What it does:** Cave stability checks and local rock-layer surveying.
- **How to get it:** Forge or cast hammer heads, then craft with a rod.
- **Notes:** Supports multiple metal tiers; includes mold knapping/heating/casting workflow.

### Sling and Ammunition

![Sling Item/Block Image](docs/images/sling_item_block.png)
![Sling Recipe Image](docs/images/sling_recipe.png)

- **What it does:** Charge-and-release ranged weapon with multiple ammo types.
- **How to get it:** Knapp base sling; reinforce via metal bracing; craft/forge ammo variants.
- **Notes:** Ammo types include heavy, scatter, high-velocity, and sparking (fire) rounds.

### Sharpening and Grindstones

![Sharpening Item/Block Image](docs/images/sharpening_grindstone_item_block.png)
![Sharpening Recipe Image](docs/images/sharpening_grindstone_recipe.png)

- **What it does:** Adds and consumes tool/weapon sharpness charges.
- **How to get it:** Use whetstones, honing steels, diamond honing steels, and grindstone systems.
- **Notes:** Grindstone block accepts wheel + tool; axle speed affects operation rate.

### Materials and Components

![Materials Components Item/Block Image](docs/images/materials_components_item_block.png)
![Materials Components Recipe Image](docs/images/materials_components_recipe.png)

- **What it covers:** Shared components used across recipes and systems.
- **Examples:** Rope bridge bundles, metal bracing, bear trap halves, hook javelin heads.
