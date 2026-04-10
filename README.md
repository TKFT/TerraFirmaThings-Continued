
# TerraFirma Things

TerraFirma Things (`tfcthings`) is a NeoForge mod for Minecraft 1.21.1 that extends TerraFirmaCraft with traversal gear, traps, ranged tools, surveying equipment, sharpening systems, and decorative blocks.

## Mod Info

- **Mod ID:** `tfcthings`
- **Minecraft:** `1.21.1`
- **NeoForge:** `21.1.218`
- **TFC Version:** `4.1.0+`

## Dependencies and Compatibility

- **Required:** TerraFirmaCraft
- **Modloader:** NeoForge
- **Optional:** JEI, Jade, Curios API, TFC Cold Sweat, Amb

## Configuration

Common config is under `config/tfcthings-common.toml` and includes:

- Master toggles for each system allowing the user to disable unwanted features
- Rope bridge and rope ladder max lengths
- Rope and hook javelin tether/retract behavior
- Trap/snare behavior and catch/breakout tuning
- Whetstone/honing/grindstone charge and speed settings
- Sling tuning (charge, multipliers, ammo behavior)
- Other misc settings

## Item and Block Guide

### Snow Shoes

![Snow Shoes](img/snowshoeuse.png)
![Snow Shoes Recipe Image](img/snowshoescraft.png)

- Snow shoes negate slowdown from snow and allow the player to walk over powdered snow. If Cold Sweat is installed, the snow shoes provide a warmth bonus.
- Two versions, one crafted with leather boots and a more durable version crafted with hiking boots.

### Hiking Boots

![Hiking Boots](img/hikingbootuse.png)
![Hiking Boots Recipe Image](img/hikingbootscraft.png)

- Hiking boots negate slowdown from tall grasses and plants. If Cold Sweat is installed, they provide a small warmth bonus.

### Rope Bridge

![Rope Bridge Item/Block Image](img/ropebridgecraft.png)
![Rope Bridge Recipe Image](img/ropebridgeuse.png)

- Throwable rope bridge that will bridge a gap.
- To use, throw a rope bridge with across the gap you want to bridge and have enough bridge bundles in your inventory to cover the space.
- Bridge can cover up to 100 blocks by default.

### Rope Ladder

![Rope Ladder Item/Block Image](img/ropeladdercraft.png)
![Rope Ladder Recipe Image](img/ropeladderuse.png)

- A rope ladder that will automatically extend downwards until it reaches a solid block or the player urns out of segments.
- Shift right-click the top of the ladder to pick it back up.

### Rope Javelin

![Rope Javelin Item/Block Image](img/ropejavelincraft.png)
![Rope Javelin Recipe Image](img/ropejavelinuse.png)

- The rope javelin allows the player to retrieve javelins after throwing them. If an entity is hit, pulling the javelin back will also pull the hit entity towards the player.
- Max rope length is configurable.
- Craftable with any javelin from copper through the colored steels.

### Hook Javelin

![Hook Javelin Item/Block Image](img/hookjavelincraft.png)
![Hook Javelin Recipe Image](img/hookjavelinuse.png)

- Hook javelin allows the player to throw and anchor the javelin in any solid block. Once a javelin is anchored, the player can swing from the rope or climb up it.
- Right-click to climb the rope and shift-right-click retrieve the javelin.

### Snare

![Snare Item/Block Image](img/snarecraft.png)
![Snare Recipe Image](img/snareuse.png)

- The snare has two modes, the first mode will catch any small entity that walks into it, while the second mode works when the trap is baited and will spawn and capture a mob local to the biome.
- Snare can be baited with any grain or seeds.

### Bear Trap

![Bear Trap Item/Block Image](img/beartrapuse.png)
![Bear Trap Recipe Image](img/beartrapuse.png)

- The bear trap is a trap that will capture any entity that walks into it, dealing with damage and giving them temporary debuffs.
- Predators have a small chance to break free of the trap.
- The trap can be buried in dirt, sand, or gravel by right-clicking on it with a shovel.

### Fishing Net

![Fishing Net Item/Block Image](img/fishingnetanchorcraft.png)
![Fishing Net Recipe Image](img/fishingnetuse.png)

- The fishing net allows the player to place a net across a body of water and catch any fish that swims into its path.
- It works by placing to fishing net anchors on opposite sides of the water and then clicking on the anchors with nets.
- Shift click the anchor again to haul in the net and gather the fish. 

### Crowns

![Crowns Item/Block Image](img/gemcrowncraft.png)
![Crowns Recipe Image](img/crownuse.png)

- Crowns are decorative items that can be worn by the player. They can be crafted from gold or platinum and any gem.
- If Curios API is installed, they can be placed on the head slot.
- Platinum crowns are craftable but require a secondary mod such as TFC Metallum to add platinum into the game.
- Extra crowns exist in creative mode for depreciated TFC gems but are craftable if those gems are added back into the game by another mod.

### Gem Display

![Gem Display Item/Block Image]()
![Gem Display Recipe Image](img/gemdisplayuse.png)

- The gem display allows the player to display their hard-earned gems.
- Each display can hold three gems, and each gem type has a unique model when it is placed on the display.
- Supports any gem registered in under c:gems
- Handcrafted gem models exist for Agate, Amethyst, Beryl, Diamond, Emerald, Garnet, Jasper, Jade, Lapis, Prismarine Crystals, Pyrite, Quartz, Ruby, Sapphire, Topaz, and Tourmaline.
- Supports adding custom gem models and textures for any gem type.

### Surveyor's Hammer

![Surveyors Hammer Item/Block Image](img/hammercraft.png)
![Surveyors Hammer Recipe Image](img/hammeruse.png)

- The surveyor's hammer has two modes, right-clicking a block will tell the player if it is safe to mine or if it can trigger a collapse. The secondary mode is activated by shift right-clicking and will tell the player what rock layers are found in the area.
- The hammer can be crafted from any metal with higher tier metals giving more information.

### Sling and Ammunition

![Sling Item](img/slingcraft.png)
![Sling Recipe Image](img/slinguse.png)

- The sling is an early game ranged weapon.
- Any loose rock can be used as ammunition, higher tier ammo's can be crafted with iron or steel in an anvil.
- Ammo types include heavy, light, scatter, and fire.

### Grain Pile

- The grain pile is a storage block that can hold up to 128 grain items of a single type.
- Grain stored in the pile has a buff to the decay rate, making it last longer, if the grain is sheltered from the Sun, the decay buff is higher.
- Stacked grain piles of the same type will feed into each other, grain can also be added to the piles by right-clicking on them or dropping grain items ontop of them.
- New grain types can be added by modifying the grain_pile_items tag.
- Supports custom grain textures for any grain type. Will fall back to haybale texture if no custom texture is found.

### Sharpening and Grindstones

![Grindstone Base Recipe](img/grindstonebasecraft.png)
![Grindstone Image](img/grindstoneuse.png)

- Sharpening is a mechanic that allows the player to apply a sharpness buff to any tool or weapon at the cost of some durability. 
- Sharpened tools will mine fast while sharpened weapons will deal more damage.
- There are three different tiers to sharpening that are accessed by higher tier sharpening tools.
- Whetstones and quartz grindstones are the lowest tier, only allowing 64 charges and a small buff.
- Honing steels and steel grindstones allow 128 charges and a medium buff.
- Diamond honing steels and diamond grindstone allow 256 charges and the highest buff.
- Buffs and charges are user-configurable, but by default they function as Effencicy and Shaprness 1, 3, and 5 respectively.



# Adding Custom Gems and Grains to TerraFirmaThings

This is the quick guide for adding support for custom gems on the gem display and custom grains in grain piles.

The short version is simple:

- **gems** work by item-driven model lookup
- **grains** work by item-driven model lookup plus layer-based grain pile models
- if a custom model is not found, TerraFirmaThings falls back safely instead of crashing

# Part 1 — Custom Gems for the Gem Display

## How it works

When a gem is placed in the gem display, TerraFirmaThings looks at the **actual item stack** in the display and tries to find a matching gem-display model.

The lookup works in this order:

1. **Custom model from the gem item’s own namespace**
2. **Built-in TerraFirmaThings fallback for known gems**
3. **Normal item rendering fallback**

So if your gem has its own gem-display model, that model will be used.  
If it does not, the display will fall back to the normal item model instead of breaking.

## The model path it looks for

For a displayed gem item, TerraFirmaThings takes the item id and uses the **last part of the item path**.
{item_namespace}:block/gem_display/gems/{item_path_last_segment}

# Part 2 – Custom Grains for the Grain Pile

