package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;

public class TFCThingsHeatingRecipes extends RecipeProvider
{
    public TFCThingsHeatingRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        // Rope Javelins (100 mB, use_durability: true)
        ropeJavelin(output, "copper",         TFCThingsItems.COPPER_ROPE_JAVELIN.get(),         Metal.COPPER,        1080f);
        ropeJavelin(output, "bronze",          TFCThingsItems.BRONZE_ROPE_JAVELIN.get(),         Metal.BRONZE,         950f);
        ropeJavelin(output, "bismuth_bronze",  TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get(), Metal.BISMUTH_BRONZE,  985f);
        ropeJavelin(output, "black_bronze",    TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.get(),   Metal.BLACK_BRONZE,  1070f);
        ropeJavelin(output, "wrought_iron",    TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.get(),   Metal.CAST_IRON,     1535f);
        ropeJavelin(output, "steel",           TFCThingsItems.STEEL_ROPE_JAVELIN.get(),          Metal.STEEL,         1540f);
        ropeJavelin(output, "black_steel",     TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.get(),    Metal.BLACK_STEEL,   1485f);
        ropeJavelin(output, "blue_steel",      TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.get(),     Metal.BLUE_STEEL,    1540f);
        ropeJavelin(output, "red_steel",       TFCThingsItems.RED_STEEL_ROPE_JAVELIN.get(),      Metal.RED_STEEL,     1540f);

        // Hook Javelins (200 mB, use_durability: true)
        hookJavelin(output, "steel",       TFCThingsItems.STEEL_HOOK_JAVELIN.get(),       Metal.STEEL,       1540f);
        hookJavelin(output, "black_steel", TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.get(), Metal.BLACK_STEEL, 1485f);
        hookJavelin(output, "blue_steel",  TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.get(),  Metal.BLUE_STEEL,  1540f);
        hookJavelin(output, "red_steel",   TFCThingsItems.RED_STEEL_HOOK_JAVELIN.get(),   Metal.RED_STEEL,   1540f);

        // Hook Javelin Heads (200 mB, use_durability: false)
        hookJavelinHead(output, "steel",       TFCThingsItems.STEEL_HOOK_JAVELIN_HEAD.get(),       Metal.STEEL,       1540f);
        hookJavelinHead(output, "black_steel", TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN_HEAD.get(), Metal.BLACK_STEEL, 1485f);
        hookJavelinHead(output, "blue_steel",  TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN_HEAD.get(),  Metal.BLUE_STEEL,  1540f);
        hookJavelinHead(output, "red_steel",   TFCThingsItems.RED_STEEL_HOOK_JAVELIN_HEAD.get(),   Metal.RED_STEEL,   1540f);

        // Surveyor's Hammer Heads (100 mB, use_durability: false)
        surveyorsHammerHead(output, "copper",         TFCThingsItems.COPPER_SURVEYORS_HAMMER_HEAD.get(),         Metal.COPPER,        1080f);
        surveyorsHammerHead(output, "bronze",          TFCThingsItems.BRONZE_SURVEYORS_HAMMER_HEAD.get(),       Metal.BRONZE,         950f);
        surveyorsHammerHead(output, "bismuth_bronze",  TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER_HEAD.get(), Metal.BISMUTH_BRONZE,  985f);
        surveyorsHammerHead(output, "black_bronze",    TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER_HEAD.get(), Metal.BLACK_BRONZE,  1070f);
        surveyorsHammerHead(output, "wrought_iron",    TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER_HEAD.get(), Metal.CAST_IRON,     1535f);
        surveyorsHammerHead(output, "steel",           TFCThingsItems.STEEL_SURVEYORS_HAMMER_HEAD.get(),        Metal.STEEL,         1540f);
        surveyorsHammerHead(output, "black_steel",     TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER_HEAD.get(),  Metal.BLACK_STEEL,   1485f);
        surveyorsHammerHead(output, "blue_steel",      TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER_HEAD.get(),   Metal.BLUE_STEEL,    1540f);
        surveyorsHammerHead(output, "red_steel",       TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER_HEAD.get(),    Metal.RED_STEEL,     1540f);

        // Surveyor's Hammers — finished tools (100 mB, use_durability: true)
        surveyorsHammer(output, "copper",         TFCThingsItems.COPPER_SURVEYORS_HAMMER.get(),         Metal.COPPER,        1080f);
        surveyorsHammer(output, "bronze",          TFCThingsItems.BRONZE_SURVEYORS_HAMMER.get(),         Metal.BRONZE,         950f);
        surveyorsHammer(output, "bismuth_bronze",  TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER.get(), Metal.BISMUTH_BRONZE,  985f);
        surveyorsHammer(output, "black_bronze",    TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER.get(),   Metal.BLACK_BRONZE,  1070f);
        surveyorsHammer(output, "wrought_iron",    TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER.get(),   Metal.CAST_IRON,     1535f);
        surveyorsHammer(output, "steel",           TFCThingsItems.STEEL_SURVEYORS_HAMMER.get(),          Metal.STEEL,         1540f);
        surveyorsHammer(output, "black_steel",     TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER.get(),    Metal.BLACK_STEEL,   1485f);
        surveyorsHammer(output, "blue_steel",      TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER.get(),     Metal.BLUE_STEEL,    1540f);
        surveyorsHammer(output, "red_steel",       TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER.get(),      Metal.RED_STEEL,     1540f);

        // Honing Steel Head (100 mB, steel temp, use_durability: false)
        add(output, "honing_steel_head",
            Ingredient.of(TFCThingsItems.HONING_STEEL_HEAD.get()),
            metalFluid(Metal.STEEL), 100, 1540f, false);

        // Diamond Honing Steel Head (100 mB, steel temp, use_durability: false)
        add(output, "diamond_honing_steel_head",
            Ingredient.of(TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get()),
            metalFluid(Metal.STEEL), 100, 1540f, false);

        // Honing Steel — finished (100 mB, steel temp, use_durability: true)
        add(output, "honing_steel",
            Ingredient.of(TFCThingsItems.HONING_STEEL.get()),
            metalFluid(Metal.STEEL), 100, 1540f, true);

        // Diamond Honing Steel — finished (100 mB, steel temp, use_durability: true)
        add(output, "diamond_honing_steel",
            Ingredient.of(TFCThingsItems.DIAMOND_HONING_STEEL.get()),
            metalFluid(Metal.STEEL), 100, 1540f, true);
    }

    private void ropeJavelin(RecipeOutput output, String metal, Item item, Metal fluid, float temp)
    {
        add(output, "rope_javelin/" + metal, Ingredient.of(item), metalFluid(fluid), 100, temp, true);
    }

    private void hookJavelin(RecipeOutput output, String metal, Item item, Metal fluid, float temp)
    {
        add(output, "hook_javelin/" + metal, Ingredient.of(item), metalFluid(fluid), 200, temp, true);
    }

    private void hookJavelinHead(RecipeOutput output, String metal, Item item, Metal fluid, float temp)
    {
        add(output, "hook_javelin_head/" + metal, Ingredient.of(item), metalFluid(fluid), 200, temp, false);
    }

    private void surveyorsHammerHead(RecipeOutput output, String metal, Item item, Metal fluid, float temp)
    {
        add(output, "surveyors_hammer_head/" + metal, Ingredient.of(item), metalFluid(fluid), 100, temp, false);
    }

    private void surveyorsHammer(RecipeOutput output, String metal, Item item, Metal fluid, float temp)
    {
        add(output, "surveyors_hammer/" + metal, Ingredient.of(item), metalFluid(fluid), 100, temp, true);
    }

    private void add(RecipeOutput output, String name, Ingredient ingredient, Fluid fluid, int amount, float temp, boolean useDurability)
    {
        output.accept(
            ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "heating/" + name),
            new HeatingRecipe(ingredient, ItemStackProvider.empty(), new FluidStack(fluid, amount), temp, useDurability),
            null
        );
    }

    private Fluid metalFluid(Metal metal)
    {
        return TFCFluids.METALS.get(metal).getSource();
    }

}
