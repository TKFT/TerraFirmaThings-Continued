
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

![Snow Shoes](img/snowshoeuse.png)
![Snow Shoes Recipe Image](img/snowshoescraft.png)

- **What it does:** Reduces snow movement slowdown when worn in the boots slot. Allows player to walk on powder snow without sinking.
- **How to get it:** Craft basic snow shoes from cloth/leather/lumber; durable version upgrades through hiking boots.
- **Notes:** Durability is only consumed while actively negating slowdown.

### Hiking Boots

![Hiking Boots](img/hikingbootuse.png)
![Hiking Boots Recipe Image](img/hikingbootscraft.png)

- **What it does:** Reduces movement slowdown from dense plants and brush.
- **How to get it:** Forge metal bracing, then craft with leather boots and fiber.
- **Notes:** Durability is only consumed while actively negating slowdown.

### Rope Bridge

![Rope Bridge Item/Block Image](img/ropebridgecraft.png)
![Rope Bridge Recipe Image](img/ropebridgeuse.png)

- **What it does:** Throwable rope bridge that can bridge a gap between two blocks.
- **How to get it:** Craft rope bridge bundles from jute fiber and lumber.
- **Notes:** Stick right-click adjusts segment height for different looks; shift-right-click retrieves placed bridge sections. Bridge only connects blocks on the same x or z axis.

### Rope Ladder

![Rope Ladder Item/Block Image](img/ropeladdercraft.png)
![Rope Ladder Recipe Image](img/ropeladderuse.png)

- **What it does:** Wall-mounted ladder that auto-extends downward till it reaches a block.
- **How to get it:** Craft from jute fiber and lumber.
- **Notes:** Shift-right-click the top ladder allows picking up of ladder chains.

### Rope Javelin

![Rope Javelin Item/Block Image](img/ropejavelincraft.png)
![Rope Javelin Recipe Image](img/ropejavelinuse.png)

- **What it does:** Tethered throwing weapon with reel/retract behavior.
- **How to get it:** Crafted per metal tier (copper through red steel).
- **Notes:** Can tether entities and auto-retract if path/range is invalid.

### Bear Trap

![Bear Trap Item/Block Image](img/beartrapuse.png)
![Bear Trap Recipe Image](img/beartrapuse.png)

- **What it does:** Immobilizes and damages entities that trigger it.
- **How to get it:** Forge bear trap halves on an anvil, then weld halves into a trap.
- **Notes:** Can be buried with a shovel; shift-right-click closed trap to reset.

### Snare

![Snare Item/Block Image](img/snarecraft.png)
![Snare Recipe Image](img/snareuse.png)

- **What it does:** Passive trap for small creatures.
- **How to get it:** Crafted from rods, lumber, and jute fiber.
- **Notes:** Shift-right-click triggered snare to release/reset; breaking snare releases capture.

### Fishing Net

![Fishing Net Item/Block Image](img/fishingnetanchorcraft.png)
![Fishing Net Recipe Image](img/fishingnetuse.png)

- **What it does:** Passive fish-catching net stretched between anchors.
- **How to get it:** Craft net segments and fishing net anchors.
- **Notes:** Place anchors on a straight axis near water; shift-right-click anchor to recover deployed net.

### Crowns

![Crowns Item/Block Image](img/gemcrowncraft.png)
![Crowns Recipe Image](img/crownuse.png)

- **What it does:** Decorative helmet-slot crowns with gem variants.
- **How to get it:** Forge empty gold/platinum crowns, then set gems via crafting.
- **Notes:** Multiple gem variants are supported.

### Gem Display

![Gem Display Item/Block Image](img/gemdisplaycraft.png)
![Gem Display Recipe Image](img/gemdisplayuse.png)

- **What it does:** Decorative display stand for cut gems (all TFC rock types).
- **How to get it:** Craft variant by rock type.
- **Notes:** Right-click to insert/remove gems; supports comparator output.

### Hook Javelin

![Hook Javelin Item/Block Image](img/hookjavelincraft.png)
![Hook Javelin Recipe Image](img/hookjavelinuse.png)

- **What it does:** Grappling javelin for tethered traversal and recovery.
- **How to get it:** Forge hook heads (steel tiers), then craft assembled javelin.
- **Notes:** Throw to anchor, retract to reposition, sneak-retract to recover.

### Surveyor's Hammer

![Surveyors Hammer Item/Block Image](img/hammercraft.png)
![Surveyors Hammer Recipe Image](img/hammeruse.png)

- **What it does:** Cave stability checks and local rock-layer surveying.
- **How to get it:** Forge or cast hammer heads, then craft with a rod.
- **Notes:** Supports multiple metal tiers; includes mold knapping/heating/casting workflow.

### Sling and Ammunition

![Sling Item/Block Image](img/slingcraft.png)
![Sling Recipe Image](img/slinguse.png)

- **What it does:** Charge-and-release ranged weapon with multiple ammo types.
- **How to get it:** Knapp base sling; reinforce via metal bracing; craft/forge ammo variants.
- **Notes:** Ammo types include heavy, scatter, high-velocity, and sparking (fire) rounds.

### Sharpening and Grindstones

![Grindstone Base Recipe](img/grindstonebasecraft.png)
![Grindstone Image](img/grindstoneuse.png)

- **What it does:** Adds and consumes tool/weapon sharpness charges.
- **How to get it:** Use whetstones, honing steels, diamond honing steels, and grindstone systems.
- **Notes:** Grindstone block accepts wheel + tool; axle speed affects operation rate.