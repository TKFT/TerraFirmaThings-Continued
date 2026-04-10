package com.rustysnail.terrafirmathings.data;

import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Metal;

final class SurveyorsHammerFamily
{
    static final SurveyorsHammerFamily[] ALL =
        {
            new SurveyorsHammerFamily("copper", "c:ingots/copper", 1, true, Metal.COPPER, 1080f,
                TFCThingsItems.COPPER_SURVEYORS_HAMMER_HEAD, TFCThingsItems.COPPER_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_COPPER),
            new SurveyorsHammerFamily("bronze", "c:ingots/bronze", 2, true, Metal.BRONZE, 950f,
                TFCThingsItems.BRONZE_SURVEYORS_HAMMER_HEAD, TFCThingsItems.BRONZE_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_BRONZE),
            new SurveyorsHammerFamily("bismuth_bronze", "c:ingots/bismuth_bronze", 2, true, Metal.BISMUTH_BRONZE, 985f,
                TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER_HEAD, TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_BISMUTH_BRONZE),
            new SurveyorsHammerFamily("black_bronze", "c:ingots/black_bronze", 2, true, Metal.BLACK_BRONZE, 1070f,
                TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER_HEAD, TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_BLACK_BRONZE),
            new SurveyorsHammerFamily("wrought_iron", "c:ingots/wrought_iron", 3, false, Metal.CAST_IRON, 1535f,
                TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER_HEAD, TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_WROUGHT_IRON),
            new SurveyorsHammerFamily("steel", "c:ingots/steel", 4, false, Metal.STEEL, 1540f,
                TFCThingsItems.STEEL_SURVEYORS_HAMMER_HEAD, TFCThingsItems.STEEL_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_STEEL),
            new SurveyorsHammerFamily("black_steel", "c:ingots/black_steel", 5, false, Metal.BLACK_STEEL, 1485f,
                TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER_HEAD, TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_BLACK_STEEL),
            new SurveyorsHammerFamily("blue_steel", "c:ingots/blue_steel", 6, false, Metal.BLUE_STEEL, 1540f,
                TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER_HEAD, TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_BLUE_STEEL),
            new SurveyorsHammerFamily("red_steel", "c:ingots/red_steel", 6, false, Metal.RED_STEEL, 1540f,
                TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER_HEAD, TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER, TFCTags.Items.TOOLS_RED_STEEL),
        };

    final String name;

    final String ingotTag;

    final int anvilTier;

    final boolean castable;

    final Metal fluidMetal;

    final float meltTemp;

    final DeferredItem<? extends Item> headItem;

    final DeferredItem<? extends Item> hammerItem;

    final TagKey<Item> tfcToolsTag;

    private SurveyorsHammerFamily(String name, String ingotTag, int anvilTier, boolean castable,
                                  Metal fluidMetal, float meltTemp,
                                  DeferredItem<? extends Item> headItem,
                                  DeferredItem<? extends Item> hammerItem,
                                  TagKey<Item> tfcToolsTag)
    {
        this.name = name;
        this.ingotTag = ingotTag;
        this.anvilTier = anvilTier;
        this.castable = castable;
        this.fluidMetal = fluidMetal;
        this.meltTemp = meltTemp;
        this.headItem = headItem;
        this.hammerItem = hammerItem;
        this.tfcToolsTag = tfcToolsTag;
    }
}
