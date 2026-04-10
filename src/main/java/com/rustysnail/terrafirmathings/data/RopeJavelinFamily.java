package com.rustysnail.terrafirmathings.data;

import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Metal;

final class RopeJavelinFamily
{
    static final RopeJavelinFamily[] ALL =
        {
            new RopeJavelinFamily("copper", Metal.COPPER, Metal.COPPER, 1080f, TFCThingsItems.COPPER_ROPE_JAVELIN, TFCTags.Items.TOOLS_COPPER),
            new RopeJavelinFamily("bronze", Metal.BRONZE, Metal.BRONZE, 950f, TFCThingsItems.BRONZE_ROPE_JAVELIN, TFCTags.Items.TOOLS_BRONZE),
            new RopeJavelinFamily("bismuth_bronze", Metal.BISMUTH_BRONZE, Metal.BISMUTH_BRONZE, 985f, TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN, TFCTags.Items.TOOLS_BISMUTH_BRONZE),
            new RopeJavelinFamily("black_bronze", Metal.BLACK_BRONZE, Metal.BLACK_BRONZE, 1070f, TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN, TFCTags.Items.TOOLS_BLACK_BRONZE),
            new RopeJavelinFamily("wrought_iron", Metal.WROUGHT_IRON, Metal.CAST_IRON, 1535f, TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN, TFCTags.Items.TOOLS_WROUGHT_IRON),
            new RopeJavelinFamily("steel", Metal.STEEL, Metal.STEEL, 1540f, TFCThingsItems.STEEL_ROPE_JAVELIN, TFCTags.Items.TOOLS_STEEL),
            new RopeJavelinFamily("black_steel", Metal.BLACK_STEEL, Metal.BLACK_STEEL, 1485f, TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN, TFCTags.Items.TOOLS_BLACK_STEEL),
            new RopeJavelinFamily("blue_steel", Metal.BLUE_STEEL, Metal.BLUE_STEEL, 1540f, TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN, TFCTags.Items.TOOLS_BLUE_STEEL),
            new RopeJavelinFamily("red_steel", Metal.RED_STEEL, Metal.RED_STEEL, 1540f, TFCThingsItems.RED_STEEL_ROPE_JAVELIN, TFCTags.Items.TOOLS_RED_STEEL),
        };

    final String name;

    final Metal tfcMetal;

    final Metal fluidMetal;

    final float meltTemp;

    final DeferredItem<? extends Item> javelinItem;

    final TagKey<Item> tfcToolsTag;

    private RopeJavelinFamily(String name, Metal tfcMetal, Metal fluidMetal, float meltTemp,
                              DeferredItem<? extends Item> javelinItem, TagKey<Item> tfcToolsTag)
    {
        this.name = name;
        this.tfcMetal = tfcMetal;
        this.fluidMetal = fluidMetal;
        this.meltTemp = meltTemp;
        this.javelinItem = javelinItem;
        this.tfcToolsTag = tfcToolsTag;
    }
}
