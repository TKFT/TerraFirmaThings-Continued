package com.rustysnail.terrafirmathings.data;

import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Metal;

final class HookJavelinFamily
{
    static final HookJavelinFamily[] ALL =
        {
            new HookJavelinFamily("steel", "c:double_ingots/steel", 4, Metal.STEEL, 1540f, TFCThingsItems.STEEL_HOOK_JAVELIN_HEAD, TFCThingsItems.STEEL_HOOK_JAVELIN, TFCTags.Items.TOOLS_STEEL),
            new HookJavelinFamily("black_steel", "c:double_ingots/black_steel", 5, Metal.BLACK_STEEL, 1485f, TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN_HEAD, TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN, TFCTags.Items.TOOLS_BLACK_STEEL),
            new HookJavelinFamily("blue_steel", "c:double_ingots/blue_steel", 5, Metal.BLUE_STEEL, 1540f, TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN_HEAD, TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN, TFCTags.Items.TOOLS_BLUE_STEEL),
            new HookJavelinFamily("red_steel", "c:double_ingots/red_steel", 5, Metal.RED_STEEL, 1540f, TFCThingsItems.RED_STEEL_HOOK_JAVELIN_HEAD, TFCThingsItems.RED_STEEL_HOOK_JAVELIN, TFCTags.Items.TOOLS_RED_STEEL),
        };

    final String name;

    final String doubleIngotTag;

    final int anvilTier;

    final Metal fluidMetal;

    final float meltTemp;

    final DeferredItem<? extends Item> headItem;

    final DeferredItem<? extends Item> javelinItem;

    final TagKey<Item> tfcToolsTag;

    private HookJavelinFamily(String name, String doubleIngotTag, int anvilTier,
                              Metal fluidMetal, float meltTemp,
                              DeferredItem<? extends Item> headItem,
                              DeferredItem<? extends Item> javelinItem,
                              TagKey<Item> tfcToolsTag)
    {
        this.name = name;
        this.doubleIngotTag = doubleIngotTag;
        this.anvilTier = anvilTier;
        this.fluidMetal = fluidMetal;
        this.meltTemp = meltTemp;
        this.headItem = headItem;
        this.javelinItem = javelinItem;
        this.tfcToolsTag = tfcToolsTag;
    }
}
