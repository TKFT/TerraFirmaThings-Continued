package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.component.heat.HeatDefinition;

public final class TFCThingsItemHeat extends TFCThingsDataManagerProvider<HeatDefinition>
{
    private static final float CAP_INGOT   = 2.857143f;
    private static final float CAP_D_INGOT = 5.714286f;

    private static final float CAP_GOLD_D_INGOT = 3.333333f;

    public TFCThingsItemHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(HeatDefinition.CODEC, "Item Heat", output, lookup, "tfc/item_heat");
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        hammerHead("copper",        1080f);
        hammerHead("bronze",         950f);
        hammerHead("bismuth_bronze", 985f);
        hammerHead("black_bronze",  1070f);
        hammerHead("wrought_iron",  1535f);
        hammerHead("steel",         1540f);
        hammerHead("black_steel",   1485f);
        hammerHead("blue_steel",    1540f);
        hammerHead("red_steel",     1540f);

        add("honing_steel_head", new HeatDefinition(
            Ingredient.of(TFCThingsItems.HONING_STEEL_HEAD.get()),
            CAP_INGOT, 1540f * 0.6f, 1540f * 0.8f));

        add("diamond_honing_steel_head", new HeatDefinition(
            Ingredient.of(TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get()),
            CAP_INGOT, 1540f * 0.6f, 1540f * 0.8f));

        hookJavelinHead("steel",       1540f);
        hookJavelinHead("black_steel", 1485f);
        hookJavelinHead("blue_steel",  1540f);
        hookJavelinHead("red_steel",   1540f);

        ropeJavelin("copper",        1080f);
        ropeJavelin("bronze",         950f);
        ropeJavelin("bismuth_bronze", 985f);
        ropeJavelin("black_bronze",  1070f);
        ropeJavelin("wrought_iron",  1535f);
        ropeJavelin("steel",         1540f);
        ropeJavelin("black_steel",   1485f);
        ropeJavelin("blue_steel",    1540f);
        ropeJavelin("red_steel",     1540f);

        hookJavelin("steel",       1540f);
        hookJavelin("black_steel", 1485f);
        hookJavelin("blue_steel",  1540f);
        hookJavelin("red_steel",   1540f);

        surveyorsHammer("copper",        1080f);
        surveyorsHammer("bronze",         950f);
        surveyorsHammer("bismuth_bronze", 985f);
        surveyorsHammer("black_bronze",  1070f);
        surveyorsHammer("wrought_iron",  1535f);
        surveyorsHammer("steel",         1540f);
        surveyorsHammer("black_steel",   1485f);
        surveyorsHammer("blue_steel",    1540f);
        surveyorsHammer("red_steel",     1540f);

        add("honing_steel", new HeatDefinition(
            Ingredient.of(TFCThingsItems.HONING_STEEL.get()),
            CAP_INGOT, 1540f * 0.6f, 1540f * 0.8f));

        add("diamond_honing_steel", new HeatDefinition(
            Ingredient.of(TFCThingsItems.DIAMOND_HONING_STEEL.get()),
            CAP_INGOT, 1540f * 0.6f, 1540f * 0.8f));

        add("crown/gold_empty", new HeatDefinition(
            Ingredient.of(TFCThingsItems.GOLD_CROWN_EMPTY.get()),
            CAP_GOLD_D_INGOT, 1060f * 0.6f, 1060f * 0.8f));

        add("crowns/gold", new HeatDefinition(
            Ingredient.of(
                TFCThingsItems.GOLD_CROWN_AMETHYST.get(),
                TFCThingsItems.GOLD_CROWN_DIAMOND.get(),
                TFCThingsItems.GOLD_CROWN_EMERALD.get(),
                TFCThingsItems.GOLD_CROWN_OPAL.get(),
                TFCThingsItems.GOLD_CROWN_RUBY.get(),
                TFCThingsItems.GOLD_CROWN_SAPPHIRE.get(),
                TFCThingsItems.GOLD_CROWN_TOPAZ.get()),
            CAP_GOLD_D_INGOT, 1060f * 0.6f, 1060f * 0.8f));

        add("crown/platinum_empty", new HeatDefinition(
            Ingredient.of(TFCThingsItems.PLATINUM_CROWN_EMPTY.get()),
            CAP_GOLD_D_INGOT, 1060f * 0.6f, 1060f * 0.8f));

        add("crowns/platinum", new HeatDefinition(
            Ingredient.of(
                TFCThingsItems.PLATINUM_CROWN_AMETHYST.get(),
                TFCThingsItems.PLATINUM_CROWN_DIAMOND.get(),
                TFCThingsItems.PLATINUM_CROWN_EMERALD.get(),
                TFCThingsItems.PLATINUM_CROWN_OPAL.get(),
                TFCThingsItems.PLATINUM_CROWN_RUBY.get(),
                TFCThingsItems.PLATINUM_CROWN_SAPPHIRE.get(),
                TFCThingsItems.PLATINUM_CROWN_TOPAZ.get()),
            CAP_GOLD_D_INGOT, 1060f * 0.6f, 1060f * 0.8f));
    }

    private void hammerHead(String metal, float melt)
    {
        Ingredient ing = switch (metal)
        {
            case "copper"        -> Ingredient.of(TFCThingsItems.COPPER_SURVEYORS_HAMMER_HEAD.get());
            case "bronze"        -> Ingredient.of(TFCThingsItems.BRONZE_SURVEYORS_HAMMER_HEAD.get());
            case "bismuth_bronze"-> Ingredient.of(TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER_HEAD.get());
            case "black_bronze"  -> Ingredient.of(TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER_HEAD.get());
            case "wrought_iron"  -> Ingredient.of(TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER_HEAD.get());
            case "steel"         -> Ingredient.of(TFCThingsItems.STEEL_SURVEYORS_HAMMER_HEAD.get());
            case "black_steel"   -> Ingredient.of(TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER_HEAD.get());
            case "blue_steel"    -> Ingredient.of(TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER_HEAD.get());
            case "red_steel"     -> Ingredient.of(TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER_HEAD.get());
            default -> throw new IllegalArgumentException("Unknown metal: " + metal);
        };
        add("surveyors_hammer_head/" + metal, new HeatDefinition(ing, CAP_INGOT, melt * 0.6f, melt * 0.8f));
    }

    private void hookJavelinHead(String metal, float melt)
    {
        Ingredient ing = switch (metal)
        {
            case "steel"       -> Ingredient.of(TFCThingsItems.STEEL_HOOK_JAVELIN_HEAD.get());
            case "black_steel" -> Ingredient.of(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN_HEAD.get());
            case "blue_steel"  -> Ingredient.of(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN_HEAD.get());
            case "red_steel"   -> Ingredient.of(TFCThingsItems.RED_STEEL_HOOK_JAVELIN_HEAD.get());
            default -> throw new IllegalArgumentException("Unknown metal: " + metal);
        };
        add("hook_javelin_head/" + metal, new HeatDefinition(ing, CAP_D_INGOT, melt * 0.6f, melt * 0.8f));
    }

    private void ropeJavelin(String metal, float melt)
    {
        Ingredient ing = switch (metal)
        {
            case "copper"        -> Ingredient.of(TFCThingsItems.COPPER_ROPE_JAVELIN.get());
            case "bronze"        -> Ingredient.of(TFCThingsItems.BRONZE_ROPE_JAVELIN.get());
            case "bismuth_bronze"-> Ingredient.of(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get());
            case "black_bronze"  -> Ingredient.of(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.get());
            case "wrought_iron"  -> Ingredient.of(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.get());
            case "steel"         -> Ingredient.of(TFCThingsItems.STEEL_ROPE_JAVELIN.get());
            case "black_steel"   -> Ingredient.of(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.get());
            case "blue_steel"    -> Ingredient.of(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.get());
            case "red_steel"     -> Ingredient.of(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.get());
            default -> throw new IllegalArgumentException("Unknown metal: " + metal);
        };
        add("rope_javelin/" + metal, new HeatDefinition(ing, CAP_INGOT, melt * 0.6f, melt * 0.8f));
    }

    private void surveyorsHammer(String metal, float melt)
    {
        Ingredient ing = switch (metal)
        {
            case "copper"        -> Ingredient.of(TFCThingsItems.COPPER_SURVEYORS_HAMMER.get());
            case "bronze"        -> Ingredient.of(TFCThingsItems.BRONZE_SURVEYORS_HAMMER.get());
            case "bismuth_bronze"-> Ingredient.of(TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER.get());
            case "black_bronze"  -> Ingredient.of(TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER.get());
            case "wrought_iron"  -> Ingredient.of(TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER.get());
            case "steel"         -> Ingredient.of(TFCThingsItems.STEEL_SURVEYORS_HAMMER.get());
            case "black_steel"   -> Ingredient.of(TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER.get());
            case "blue_steel"    -> Ingredient.of(TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER.get());
            case "red_steel"     -> Ingredient.of(TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER.get());
            default -> throw new IllegalArgumentException("Unknown metal: " + metal);
        };
        add("surveyors_hammer/" + metal, new HeatDefinition(ing, CAP_INGOT, melt * 0.6f, melt * 0.8f));
    }

    private void hookJavelin(String metal, float melt)
    {
        Ingredient ing = switch (metal)
        {
            case "steel"       -> Ingredient.of(TFCThingsItems.STEEL_HOOK_JAVELIN.get());
            case "black_steel" -> Ingredient.of(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.get());
            case "blue_steel"  -> Ingredient.of(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.get());
            case "red_steel"   -> Ingredient.of(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.get());
            default -> throw new IllegalArgumentException("Unknown metal: " + metal);
        };
        add("hook_javelin/" + metal, new HeatDefinition(ing, CAP_D_INGOT, melt * 0.6f, melt * 0.8f));
    }
}
