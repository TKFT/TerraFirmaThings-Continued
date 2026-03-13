package com.rustysnail.terrafirmathings.common;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class TFCThingsTags
{

    public static class Blocks
    {
        public static final TagKey<Block> SNOW_SHOES_NEGATE_SLOW = tag("snow_shoes_negate_slow");

        public static final TagKey<Block> HIKING_BOOTS_NEGATE_SLOW = tag("hiking_boots_negate_slow");

        public static final TagKey<Block> CRAMPONS_NEGATE_SLIP = tag("crampons_negate_slip");

        public static final TagKey<Block> GEM_DISPLAY = tag("gem_displays");

        private static TagKey<Block> tag(String name)
        {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, name));
        }
    }

    public static class Entities
    {
        public static final TagKey<EntityType<?>> SNARE_CATCHABLE = tag("snare_catchable");

        public static final TagKey<EntityType<?>> FISHING_NET_CATCHABLE = tag("fishing_net_catchable");

        public static final TagKey<EntityType<?>> BEAR_TRAP_BREAKOUT = tag("bear_trap_breakout");

        private static TagKey<EntityType<?>> tag(String name)
        {
            return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, name));
        }
    }

    public static class Items
    {
        public static final TagKey<Item> GEM_DISPLAY_ELIGIBLE = tag("gem_display_eligible");

        public static final TagKey<Item> JAVELINS = tag("javelins");
        public static final TagKey<Item> SLINGS = tag("slings");
        public static final TagKey<Item> SLING_AMMO = tag("sling_ammo");
        public static final TagKey<Item> SHARPENING_TOOLS = tag("sharpening_tools");
        public static final TagKey<Item> SURVEYORS_HAMMERS = tag("surveyors_hammers");
        public static final TagKey<Item> SHARPENING_TOOL_HEADS = tag("sharpening_tool_heads");

        public static final TagKey<Item> SHARPENABLE = tag("sharpenable");
        public static final TagKey<Item> SHARPNESS_MINING_TOOLS = tag("sharpness_mining_tools");
        public static final TagKey<Item> SHARPNESS_WEAPONS = tag("sharpness_weapons");

        public static final TagKey<Item> GRINDSTONE_WHEELS = tag("grindstone_wheels");

        public static final TagKey<Item> SNARE_BAIT = tag("snare_bait");

        private static TagKey<Item> tag(String name)
        {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, name));
        }
    }

    public static class Fluids
    {
        public static final TagKey<Fluid> FISHING_NET_PLACEABLE = tag();

        private static TagKey<Fluid> tag()
        {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "fishing_net_placeable"));
        }
    }
}
