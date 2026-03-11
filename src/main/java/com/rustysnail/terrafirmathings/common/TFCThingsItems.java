package com.rustysnail.terrafirmathings.common;

import java.util.LinkedHashMap;
import java.util.Map;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.item.BearTrapHalfItem;
import com.rustysnail.terrafirmathings.common.item.CramponsItem;
import com.rustysnail.terrafirmathings.common.item.CrownItem;
import com.rustysnail.terrafirmathings.common.item.FishingNetItem;
import com.rustysnail.terrafirmathings.common.item.GrindstoneItem;
import com.rustysnail.terrafirmathings.common.item.HikingBootsItem;
import com.rustysnail.terrafirmathings.common.item.HoningSteelItem;
import com.rustysnail.terrafirmathings.common.item.HookJavelinItem;
import com.rustysnail.terrafirmathings.common.item.MetalBracingItem;
import com.rustysnail.terrafirmathings.common.item.RopeBridgeBundleItem;
import com.rustysnail.terrafirmathings.common.item.RopeJavelinItem;
import com.rustysnail.terrafirmathings.common.item.SlingAmmoItem;
import com.rustysnail.terrafirmathings.common.item.SlingItem;
import com.rustysnail.terrafirmathings.common.item.SnowShoesItem;
import com.rustysnail.terrafirmathings.common.item.SurveyorsHammerItem;
import com.rustysnail.terrafirmathings.common.item.WhetstoneItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.TFCTiers;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.MoldItem;
import net.dries007.tfc.config.TFCConfig;

public final class TFCThingsItems
{

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(TerraFirmaThings.MOD_ID);

    // Snow Shoes
    public static final DeferredItem<SnowShoesItem> SNOW_SHOES = ITEMS.register("snow_shoes",
        () -> new SnowShoesItem(TFCThingsArmorMaterials.SNOW_SHOES, ArmorItem.Type.BOOTS, new Item.Properties().durability(182)));

    public static final DeferredItem<SnowShoesItem> DURABLE_SNOW_SHOES = ITEMS.register("durable_snow_shoes",
        () -> new SnowShoesItem(TFCThingsArmorMaterials.DURABLE_SNOW_SHOES, ArmorItem.Type.BOOTS, new Item.Properties().durability(364)));

    // Hiking Boots
    public static final DeferredItem<HikingBootsItem> HIKING_BOOTS = ITEMS.register("hiking_boots",
        () -> new HikingBootsItem(TFCThingsArmorMaterials.HIKING_BOOTS, ArmorItem.Type.BOOTS, new Item.Properties().durability(364)));

    // Crampons
    public static final DeferredItem<CramponsItem> CRAMPONS = ITEMS.register("crampons",
        () -> new CramponsItem(TFCThingsArmorMaterials.CRAMPONS, ArmorItem.Type.BOOTS, new Item.Properties().durability(364)));

    // Rope Bridge Bundle
    public static final DeferredItem<RopeBridgeBundleItem> ROPE_BRIDGE_BUNDLE = ITEMS.register("rope_bridge_bundle",
        () -> new RopeBridgeBundleItem(new Item.Properties().stacksTo(64)));

    // Rope Ladder Block Item
    public static final DeferredItem<BlockItem> ROPE_LADDER = ITEMS.register("rope_ladder",
        () -> new BlockItem(TFCThingsBlocks.ROPE_LADDER.get(), new Item.Properties().stacksTo(32)));

    // Rope Javelin
    public static final DeferredItem<RopeJavelinItem> BISMUTH_BRONZE_ROPE_JAVELIN = ITEMS.register("bismuth_bronze_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.BISMUTH_BRONZE, new Item.Properties().stacksTo(1).durability(TFCTiers.BISMUTH_BRONZE.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BISMUTH_BRONZE, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> BLACK_BRONZE_ROPE_JAVELIN = ITEMS.register("black_bronze_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.BLACK_BRONZE, new Item.Properties().stacksTo(1).durability(TFCTiers.BLACK_BRONZE.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BLACK_BRONZE, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> BLACK_STEEL_ROPE_JAVELIN = ITEMS.register("black_steel_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.BLACK_STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.BLACK_STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BLACK_STEEL, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> BLUE_STEEL_ROPE_JAVELIN = ITEMS.register("blue_steel_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.BLUE_STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.BLUE_STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BLUE_STEEL, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> BRONZE_ROPE_JAVELIN = ITEMS.register("bronze_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.BRONZE, new Item.Properties().stacksTo(1).durability(TFCTiers.BRONZE.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BRONZE, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> COPPER_ROPE_JAVELIN = ITEMS.register("copper_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.COPPER, new Item.Properties().stacksTo(1).durability(TFCTiers.COPPER.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.COPPER, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> RED_STEEL_ROPE_JAVELIN = ITEMS.register("red_steel_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.RED_STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.RED_STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.RED_STEEL, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> STEEL_ROPE_JAVELIN = ITEMS.register("steel_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.STEEL, 3, -2.4F))));

    public static final DeferredItem<RopeJavelinItem> WROUGHT_IRON_ROPE_JAVELIN = ITEMS.register("wrought_iron_rope_javelin",
        () -> new RopeJavelinItem(TFCTiers.WROUGHT_IRON, new Item.Properties().stacksTo(1).durability(TFCTiers.WROUGHT_IRON.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.WROUGHT_IRON, 3, -2.4F))));

    // Hook Javelin
    public static final DeferredItem<HookJavelinItem> STEEL_HOOK_JAVELIN = ITEMS.register("hook_javelin/steel",
        () -> new HookJavelinItem(TFCTiers.STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.STEEL, 3, -2.4F))));

    public static final DeferredItem<HookJavelinItem> BLACK_STEEL_HOOK_JAVELIN = ITEMS.register("hook_javelin/black_steel",
        () -> new HookJavelinItem(TFCTiers.BLACK_STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.BLACK_STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BLACK_STEEL, 3, -2.4F))));

    public static final DeferredItem<HookJavelinItem> BLUE_STEEL_HOOK_JAVELIN = ITEMS.register("hook_javelin/blue_steel",
        () -> new HookJavelinItem(TFCTiers.BLUE_STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.BLUE_STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.BLUE_STEEL, 3, -2.4F))));

    public static final DeferredItem<HookJavelinItem> RED_STEEL_HOOK_JAVELIN = ITEMS.register("hook_javelin/red_steel",
        () -> new HookJavelinItem(TFCTiers.RED_STEEL, new Item.Properties().stacksTo(1).durability(TFCTiers.RED_STEEL.getUses())
            .attributes(SwordItem.createAttributes(TFCTiers.RED_STEEL, 3, -2.4F))));

    // Hook Javelin Heads
    public static final DeferredItem<Item> STEEL_HOOK_JAVELIN_HEAD = ITEMS.register("hook_javelin_head/steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLACK_STEEL_HOOK_JAVELIN_HEAD = ITEMS.register("hook_javelin_head/black_steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLUE_STEEL_HOOK_JAVELIN_HEAD = ITEMS.register("hook_javelin_head/blue_steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RED_STEEL_HOOK_JAVELIN_HEAD = ITEMS.register("hook_javelin_head/red_steel", () -> new Item(new Item.Properties()));

    // Bear Trap Block Item
    public static final DeferredItem<BlockItem> BEAR_TRAP = ITEMS.register("bear_trap",
        () -> new BlockItem(TFCThingsBlocks.BEAR_TRAP.get(), new Item.Properties().stacksTo(1)));

    // Bear Trap Half (crafting component)
    public static final DeferredItem<BearTrapHalfItem> BEAR_TRAP_HALF = ITEMS.register("bear_trap_half",
        () -> new BearTrapHalfItem(new Item.Properties().stacksTo(1)));

    // Snare Block Item
    public static final DeferredItem<BlockItem> SNARE = ITEMS.register("snare",
        () -> new BlockItem(TFCThingsBlocks.SNARE.get(), new Item.Properties().stacksTo(16)));


    // Fishing Net (places a multi-block river net)
    public static final DeferredItem<FishingNetItem> FISHING_NET_ITEM = ITEMS.register("fishing_net",
        () -> new FishingNetItem(new Item.Properties().stacksTo(32)));

    public static final DeferredItem<BlockItem> FISHING_NET_ANCHOR = ITEMS.register("fishing_net_anchor",
        () -> new BlockItem(TFCThingsBlocks.FISHING_NET_ANCHOR.get(), new Item.Properties().stacksTo(16)));

    // Surveyor's Hammer
    public static final DeferredItem<SurveyorsHammerItem> COPPER_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/copper",
        () -> new SurveyorsHammerItem(TFCTiers.COPPER, new Item.Properties().durability(TFCTiers.COPPER.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> BISMUTH_BRONZE_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/bismuth_bronze",
        () -> new SurveyorsHammerItem(TFCTiers.BISMUTH_BRONZE, new Item.Properties().durability(TFCTiers.BISMUTH_BRONZE.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> BLACK_BRONZE_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/black_bronze",
        () -> new SurveyorsHammerItem(TFCTiers.BLACK_BRONZE, new Item.Properties().durability(TFCTiers.BLACK_BRONZE.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> BRONZE_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/bronze",
        () -> new SurveyorsHammerItem(TFCTiers.BRONZE, new Item.Properties().durability(TFCTiers.BRONZE.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> WROUGHT_IRON_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/wrought_iron",
        () -> new SurveyorsHammerItem(TFCTiers.WROUGHT_IRON, new Item.Properties().durability(TFCTiers.WROUGHT_IRON.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> STEEL_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/steel",
        () -> new SurveyorsHammerItem(TFCTiers.STEEL, new Item.Properties().durability(TFCTiers.STEEL.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> BLACK_STEEL_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/black_steel",
        () -> new SurveyorsHammerItem(TFCTiers.BLACK_STEEL, new Item.Properties().durability(TFCTiers.BLACK_STEEL.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> BLUE_STEEL_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/blue_steel",
        () -> new SurveyorsHammerItem(TFCTiers.BLUE_STEEL, new Item.Properties().durability(TFCTiers.BLUE_STEEL.getUses())));
    public static final DeferredItem<SurveyorsHammerItem> RED_STEEL_SURVEYORS_HAMMER = ITEMS.register("surveyors_hammer/red_steel",
        () -> new SurveyorsHammerItem(TFCTiers.RED_STEEL, new Item.Properties().durability(TFCTiers.RED_STEEL.getUses())));

    // Surveyor's Hammer Heads
    public static final DeferredItem<Item> COPPER_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/copper", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BISMUTH_BRONZE_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/bismuth_bronze", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLACK_BRONZE_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/black_bronze", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BRONZE_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/bronze", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> WROUGHT_IRON_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/wrought_iron", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> STEEL_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLACK_STEEL_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/black_steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> BLUE_STEEL_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/blue_steel", () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> RED_STEEL_SURVEYORS_HAMMER_HEAD = ITEMS.register("surveyors_hammer_head/red_steel", () -> new Item(new Item.Properties()));

    // Surveyor's Hammer Molds
    public static final DeferredItem<Item> UNFIRED_SURVEYORS_HAMMER_HEAD_MOLD = ITEMS.register("unfired_surveyors_hammer_head_mold",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<MoldItem> SURVEYORS_HAMMER_HEAD_MOLD = ITEMS.register("surveyors_hammer_head_mold",
        () -> new MoldItem(TFCConfig.SERVER.moldHammerHeadCapacity, TFCTags.Fluids.USABLE_IN_TOOL_HEAD_MOLD, new Item.Properties()));

    public static final DeferredItem<CrownItem> GOLD_CROWN_EMPTY = ITEMS.register("crown/gold_empty",
        () -> new CrownItem(TFCThingsArmorMaterials.GOLD_CROWNS.get("empty"), ArmorItem.Type.HELMET,
            new Item.Properties().stacksTo(1), null));

    /* public static final DeferredItem<CrownItem> GOLD_CROWN_AGATE = goldCrown("agate", "Agate"); */
    public static final DeferredItem<CrownItem> GOLD_CROWN_AMETHYST = goldCrown("amethyst", "Amethyst");
    /* public static final DeferredItem<CrownItem> GOLD_CROWN_BERYL = goldCrown("beryl", "Beryl"); */
    public static final DeferredItem<CrownItem> GOLD_CROWN_DIAMOND = goldCrown("diamond", "Diamond");
    public static final DeferredItem<CrownItem> GOLD_CROWN_EMERALD = goldCrown("emerald", "Emerald");
    /**
     * public static final DeferredItem<CrownItem> GOLD_CROWN_GARNET = goldCrown("garnet", "Garnet");
     * public static final DeferredItem<CrownItem> GOLD_CROWN_JADE = goldCrown("jade", "Jade");
     * public static final DeferredItem<CrownItem> GOLD_CROWN_JASPER = goldCrown("jasper", "Jasper");
     */
    public static final DeferredItem<CrownItem> GOLD_CROWN_OPAL = goldCrown("opal", "Opal");
    public static final DeferredItem<CrownItem> GOLD_CROWN_RUBY = goldCrown("ruby", "Ruby");
    public static final DeferredItem<CrownItem> GOLD_CROWN_SAPPHIRE = goldCrown("sapphire", "Sapphire");
    public static final DeferredItem<CrownItem> GOLD_CROWN_TOPAZ = goldCrown("topaz", "Topaz");

    //Crowns (Platinum)
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_EMPTY = ITEMS.register("crown/platinum_empty",
        () -> new CrownItem(TFCThingsArmorMaterials.PLATINUM_CROWNS.get("empty"), ArmorItem.Type.HELMET,
            new Item.Properties().stacksTo(1), null));

    /* public static final DeferredItem<CrownItem> PLATINUM_CROWN_AGATE = platinumCrown("agate", "Agate"); */
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_AMETHYST = platinumCrown("amethyst", "Amethyst");
    /**
     * public static final DeferredItem<CrownItem> GOLD_CROWN_TOURMALINE = goldCrown("tourmaline", "Tourmaline");
     * public static final DeferredItem<CrownItem> PLATINUM_CROWN_BERYL = platinumCrown("beryl", "Beryl");
     */
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_DIAMOND = platinumCrown("diamond", "Diamond");
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_EMERALD = platinumCrown("emerald", "Emerald");
    /**
     * public static final DeferredItem<CrownItem> PLATINUM_CROWN_GARNET = platinumCrown("garnet", "Garnet");
     * public static final DeferredItem<CrownItem> PLATINUM_CROWN_JADE = platinumCrown("jade", "Jade");
     * public static final DeferredItem<CrownItem> PLATINUM_CROWN_JASPER = platinumCrown("jasper", "Jasper");
     */
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_OPAL = platinumCrown("opal", "Opal");
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_RUBY = platinumCrown("ruby", "Ruby");
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_SAPPHIRE = platinumCrown("sapphire", "Sapphire");
    public static final DeferredItem<CrownItem> PLATINUM_CROWN_TOPAZ = platinumCrown("topaz", "Topaz");

    //Metal Bracing
    public static final DeferredItem<MetalBracingItem> METAL_BRACING = ITEMS.register("metal_bracing",
        () -> new MetalBracingItem(new Item.Properties().stacksTo(64)));
    //Slings
    public static final DeferredItem<SlingItem> SLING = ITEMS.register("sling",
        () -> new SlingItem(0, new Item.Properties().stacksTo(1).durability(64)));
    //public static final DeferredItem<CrownItem> PLATINUM_CROWN_TOURMALINE = platinumCrown("tourmaline", "Tourmaline");
    public static final DeferredItem<SlingItem> SLING_METAL = ITEMS.register("sling_metal",
        () -> new SlingItem(1, new Item.Properties().stacksTo(1).durability(256)));
    //Sling Ammo
    public static final DeferredItem<SlingAmmoItem> SLING_AMMO = ITEMS.register("sling_ammo",
        () -> new SlingAmmoItem(SlingAmmoItem.AmmoType.HEAVY, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<SlingAmmoItem> SLING_AMMO_SPREAD = ITEMS.register("sling_ammo_spread",
        () -> new SlingAmmoItem(SlingAmmoItem.AmmoType.SCATTER, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<SlingAmmoItem> SLING_AMMO_LIGHT = ITEMS.register("sling_ammo_light",
        () -> new SlingAmmoItem(SlingAmmoItem.AmmoType.LIGHT, new Item.Properties().stacksTo(64)));
    public static final DeferredItem<SlingAmmoItem> SLING_AMMO_FIRE = ITEMS.register("sling_ammo_fire",
        () -> new SlingAmmoItem(SlingAmmoItem.AmmoType.FIRE, new Item.Properties().stacksTo(64)));
    //Grindstones
    public static final DeferredItem<BlockItem> GRINDSTONE = ITEMS.register("grindstone",
        () -> new BlockItem(TFCThingsBlocks.GRINDSTONE.get(), new Item.Properties()));
    public static final DeferredItem<GrindstoneItem> GRINDSTONE_QUARTZ = ITEMS.register("grindstone_quartz",
        () -> new GrindstoneItem(GrindstoneItem.Tier.QUARTZ, new Item.Properties().stacksTo(1).durability(250)));
    public static final DeferredItem<GrindstoneItem> GRINDSTONE_STEEL = ITEMS.register("grindstone_steel",
        () -> new GrindstoneItem(GrindstoneItem.Tier.STEEL, new Item.Properties().stacksTo(1).durability(4200)));
    public static final DeferredItem<GrindstoneItem> GRINDSTONE_DIAMOND = ITEMS.register("grindstone_diamond",
        () -> new GrindstoneItem(GrindstoneItem.Tier.DIAMOND, new Item.Properties().stacksTo(1).durability(4200)));
    //Whetstones & Honing Steels
    public static final DeferredItem<WhetstoneItem> WHETSTONE = ITEMS.register("whetstone",
        () -> new WhetstoneItem(new Item.Properties().stacksTo(64).durability(250)));
    public static final DeferredItem<HoningSteelItem> HONING_STEEL = ITEMS.register("honing_steel",
        () -> new HoningSteelItem(false, new Item.Properties().stacksTo(1).durability(3300)));
    public static final DeferredItem<HoningSteelItem> DIAMOND_HONING_STEEL = ITEMS.register("diamond_honing_steel",
        () -> new HoningSteelItem(true, new Item.Properties().stacksTo(1).durability(3300)));
    public static final DeferredItem<Item> HONING_STEEL_HEAD = ITEMS.register("honing_steel_head",
        () -> new Item(new Item.Properties()));
    public static final DeferredItem<Item> DIAMOND_HONING_STEEL_HEAD = ITEMS.register("diamond_honing_steel_head",
        () -> new Item(new Item.Properties()));
    //Gem Display Block Items (one per rock type)
    public static final Map<Rock, DeferredItem<BlockItem>> GEM_DISPLAY_ITEMS = new LinkedHashMap<>();

    static
    {
        for (Rock rock : Rock.values())
        {
            GEM_DISPLAY_ITEMS.put(rock, ITEMS.register("gem_display/" + rock.getSerializedName(),
                () -> new BlockItem(TFCThingsBlocks.GEM_DISPLAYS.get(rock).get(), new Item.Properties())));
        }
    }

    // Crowns (Gold)
    private static DeferredItem<CrownItem> goldCrown(String gem, String gemDisplayName)
    {
        return ITEMS.register("crown/gold_" + gem,
            () -> new CrownItem(TFCThingsArmorMaterials.GOLD_CROWNS.get(gem), ArmorItem.Type.HELMET,
                new Item.Properties().stacksTo(1), gemDisplayName));
    }

    private static DeferredItem<CrownItem> platinumCrown(String gem, String gemDisplayName)
    {
        return ITEMS.register("crown/platinum_" + gem,
            () -> new CrownItem(TFCThingsArmorMaterials.PLATINUM_CROWNS.get(gem), ArmorItem.Type.HELMET,
                new Item.Properties().stacksTo(1), gemDisplayName));
    }

}
