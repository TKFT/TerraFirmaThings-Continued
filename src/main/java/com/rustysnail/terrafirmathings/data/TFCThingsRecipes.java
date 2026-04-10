package com.rustysnail.terrafirmathings.data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import javax.annotation.Nullable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipePattern;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.component.forge.ForgeRule;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.AdvancedShapedRecipe;
import net.dries007.tfc.common.recipes.AdvancedShapelessRecipe;
import net.dries007.tfc.common.recipes.AnvilRecipe;
import net.dries007.tfc.common.recipes.CastingRecipe;
import net.dries007.tfc.common.recipes.HeatingRecipe;
import net.dries007.tfc.common.recipes.KnappingRecipe;
import net.dries007.tfc.common.recipes.WeldingRecipe;
import net.dries007.tfc.common.recipes.outputs.DamageCraftingRemainderModifier;
import net.dries007.tfc.common.recipes.outputs.ItemStackProvider;
import net.dries007.tfc.util.Metal;
import net.dries007.tfc.util.data.KnappingPattern;
import net.dries007.tfc.util.data.KnappingType;

public class TFCThingsRecipes extends RecipeProvider
{
    private static final TagKey<Item> WOODEN_RODS =
        ItemTags.create(ResourceLocation.parse("c:rods/wooden"));

    private static final TagKey<Item> HAMMER =
        ItemTags.create(ResourceLocation.parse("c:tools/hammer"));

    private static final TagKey<Item> CHISEL =
        ItemTags.create(ResourceLocation.parse("c:tools/chisel"));

    private static final TagKey<Item> GOLD_DOUBLE_SHEETS =
        ItemTags.create(ResourceLocation.parse("c:double_sheets/gold"));

    private static final TagKey<Item> PLATINUM_DOUBLE_SHEETS =
        ItemTags.create(ResourceLocation.parse("c:double_sheets/platinum"));

    private static final ResourceLocation KNAPPING_LEATHER = ResourceLocation.parse("tfc:leather");
    private static final ResourceLocation KNAPPING_CLAY = ResourceLocation.parse("tfc:clay");
    private static final ResourceLocation KNAPPING_ROCK = ResourceLocation.parse("tfc:rock");

    private static final List<ForgeRule> HAMMER_HEAD_RULES =
        List.of(ForgeRule.PUNCH_LAST, ForgeRule.DRAW_NOT_LAST, ForgeRule.SHRINK_NOT_LAST);

    private static final List<ForgeRule> HOOK_HEAD_RULES =
        List.of(ForgeRule.DRAW_LAST, ForgeRule.UPSET_NOT_LAST, ForgeRule.BEND_NOT_LAST);

    private static final List<ForgeRule> CROWN_RULES =
        List.of(ForgeRule.SHRINK_THIRD_LAST, ForgeRule.UPSET_SECOND_LAST, ForgeRule.HIT_LAST);

    private static final List<ForgeRule> BEAR_TRAP_RULES =
        List.of(ForgeRule.SHRINK_THIRD_LAST, ForgeRule.DRAW_SECOND_LAST, ForgeRule.HIT_LAST);

    private static final List<ForgeRule> HONING_HEAD_RULES =
        List.of(ForgeRule.HIT_LAST, ForgeRule.DRAW_NOT_LAST, ForgeRule.BEND_NOT_LAST);

    private static final List<ForgeRule> METAL_BRACING_RULES =
        List.of(ForgeRule.BEND_LAST, ForgeRule.HIT_NOT_LAST, ForgeRule.DRAW_NOT_LAST);

    private static final List<ForgeRule> SLING_AMMO_RULES =
        List.of(ForgeRule.HIT_LAST, ForgeRule.HIT_NOT_LAST, ForgeRule.BEND_NOT_LAST);

    public TFCThingsRecipes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(output, lookup);
    }

    @Override
    protected void buildRecipes(RecipeOutput output)
    {
        heatingRecipes(output);
        anvilRecipes(output);
        weldingRecipes(output);
        castingRecipes(output);
        craftingRecipes(output);
        knappingRecipes(output);
        crownRecipes(output);
        gemDisplayRecipes(output);
    }

    private void heatingRecipes(RecipeOutput output)
    {
        for (RopeJavelinFamily m : RopeJavelinFamily.ALL)
            heating(output, "rope_javelin/" + m.name, m.javelinItem.get(), m.fluidMetal, 100, m.meltTemp, true);

        for (HookJavelinFamily m : HookJavelinFamily.ALL)
            heating(output, "hook_javelin_head/" + m.name, m.headItem.get(), m.fluidMetal, 200, m.meltTemp, false);

        for (HookJavelinFamily m : HookJavelinFamily.ALL)
            heating(output, "hook_javelin/" + m.name, m.javelinItem.get(), m.fluidMetal, 200, m.meltTemp, true);

        for (SurveyorsHammerFamily m : SurveyorsHammerFamily.ALL)
            heating(output, "surveyors_hammer_head/" + m.name, m.headItem.get(), m.fluidMetal, 100, m.meltTemp, false);

        for (SurveyorsHammerFamily m : SurveyorsHammerFamily.ALL)
            heating(output, "surveyors_hammer/" + m.name, m.hammerItem.get(), m.fluidMetal, 100, m.meltTemp, true);

        heating(output, "honing_steel_head", TFCThingsItems.HONING_STEEL_HEAD.get(), Metal.STEEL, 100, 1540f, false);
        heating(output, "diamond_honing_steel_head", TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get(), Metal.STEEL, 100, 1540f, false);

        heating(output, "honing_steel", TFCThingsItems.HONING_STEEL.get(), Metal.STEEL, 100, 1540f, true);
        heating(output, "diamond_honing_steel", TFCThingsItems.DIAMOND_HONING_STEEL.get(), Metal.STEEL, 100, 1540f, true);
    }

    private void anvilRecipes(RecipeOutput output)
    {
        for (HookJavelinFamily m : HookJavelinFamily.ALL)
        {
            anvil(output,
                "hook_javelin_head/" + m.name,
                Ingredient.of(ItemTags.create(ResourceLocation.parse(m.doubleIngotTag))),
                m.anvilTier,
                HOOK_HEAD_RULES,
                m.headItem.get(),
                RecipeConditions.HOOK_JAVELINS);
        }

        for (SurveyorsHammerFamily m : SurveyorsHammerFamily.ALL)
        {
            anvil(output,
                "surveyors_hammer_head/" + m.name,
                Ingredient.of(ItemTags.create(ResourceLocation.parse(m.ingotTag))),
                m.anvilTier,
                HAMMER_HEAD_RULES,
                m.headItem.get(),
                RecipeConditions.PROSPECTORS_HAMMERS);
        }

        // Bear trap half
        anvil(output, "bear_trap_half",
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:sheets/steel"))),
            4, BEAR_TRAP_RULES,
            TFCThingsItems.BEAR_TRAP_HALF.get(),
            RecipeConditions.BEAR_TRAP);

        // Honing steel head
        anvil(output, "honing_steel_head",
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:ingots/steel"))),
            3, HONING_HEAD_RULES,
            TFCThingsItems.HONING_STEEL_HEAD.get(),
            RecipeConditions.WHETSTONES);

        // Sling ammo
        ICondition slingsEnabled = RecipeConditions.flag(RecipeConditions.SLINGS);
        anvil(output, "sling_ammo/steel",
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:ingots/steel"))),
            4, SLING_AMMO_RULES,
            ItemStackProvider.of(TFCThingsItems.SLING_AMMO_HEAVY.get(), 16),
            slingsEnabled);
        anvil(output, "sling_ammo/wrought_iron",
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:ingots/wrought_iron"))),
            3, SLING_AMMO_RULES,
            ItemStackProvider.of(TFCThingsItems.SLING_AMMO_HEAVY.get(), 8),
            slingsEnabled);

        // Metal bracing
        ICondition bracingEnabled =
            RecipeConditions.anyFlag(RecipeConditions.HIKING_BOOTS, RecipeConditions.SLINGS);
        anvil(output, "metal_bracing/steel",
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:ingots/steel"))),
            4, METAL_BRACING_RULES,
            ItemStackProvider.of(TFCThingsItems.METAL_BRACING.get(), 2),
            bracingEnabled);
        anvil(output, "metal_bracing/wrought_iron",
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:ingots/wrought_iron"))),
            3, METAL_BRACING_RULES,
            ItemStackProvider.of(TFCThingsItems.METAL_BRACING.get(), 1),
            bracingEnabled);
    }

    private void castingRecipes(RecipeOutput output)
    {
        Ingredient mold = Ingredient.of(TFCThingsItems.SURVEYORS_HAMMER_HEAD_MOLD.get());
        for (SurveyorsHammerFamily m : SurveyorsHammerFamily.ALL)
        {
            if (!m.castable) continue;
            casting(output,
                "surveyors_hammer_head/" + m.name,
                mold,
                SizedFluidIngredient.of(metalFluid(m.fluidMetal), 100),
                m.headItem.get(),
                RecipeConditions.PROSPECTORS_HAMMERS);
        }
    }

    private void craftingRecipes(RecipeOutput output)
    {
        ropeJavelinCrafting(output);
        hookJavelinCrafting(output);
        surveyorsHammerCrafting(output);
        grindstoneBaseCrafting(output);
    }

    private void ropeJavelinCrafting(RecipeOutput output)
    {
        Item juteFiber = TFCItems.JUTE_FIBER.get();
        RecipeOutput conditioned = output.withConditions(
            RecipeConditions.flag(RecipeConditions.ROPE_JAVELIN));
        for (RopeJavelinFamily m : RopeJavelinFamily.ALL)
        {
            Item tfcJavelin = TFCItems.METAL_ITEMS.get(m.tfcMetal).get(Metal.ItemType.JAVELIN).get();
            ShapedRecipeBuilder
                .shaped(RecipeCategory.COMBAT, m.javelinItem.get())
                .define('R', Ingredient.of(juteFiber))
                .define('J', Ingredient.of(tfcJavelin))
                .pattern("RRR")
                .pattern("RJR")
                .pattern("RRR")
                .unlockedBy("has_javelin", has(tfcJavelin))
                .save(conditioned, crafting("rope_javelin/" + m.name));
        }
    }

    private void hookJavelinCrafting(RecipeOutput output)
    {
        Item juteFiber = TFCItems.JUTE_FIBER.get();
        RecipeOutput conditioned = output.withConditions(
            RecipeConditions.flag(RecipeConditions.HOOK_JAVELINS));
        for (HookJavelinFamily m : HookJavelinFamily.ALL)
        {
            ShapedRecipeBuilder
                .shaped(RecipeCategory.COMBAT, m.javelinItem.get())
                .define('R', Ingredient.of(juteFiber))
                .define('H', Ingredient.of(m.headItem.get()))
                .define('S', Ingredient.of(WOODEN_RODS))
                .pattern("RRR")
                .pattern("RHR")
                .pattern("RSR")
                .unlockedBy("has_head", has(m.headItem.get()))
                .save(conditioned, crafting("hook_javelin/" + m.name));
        }
    }

    private void surveyorsHammerCrafting(RecipeOutput output)
    {
        RecipeOutput conditioned = output.withConditions(
            RecipeConditions.flag(RecipeConditions.PROSPECTORS_HAMMERS));
        for (SurveyorsHammerFamily m : SurveyorsHammerFamily.ALL)
        {
            ShapedRecipeBuilder
                .shaped(RecipeCategory.TOOLS, m.hammerItem.get())
                .define('H', Ingredient.of(m.headItem.get()))
                .define('S', Ingredient.of(WOODEN_RODS))
                .pattern("H")
                .pattern("S")
                .unlockedBy("has_head", has(m.headItem.get()))
                .save(conditioned, crafting("surveyors_hammer/" + m.name));
        }
    }

    private void grindstoneBaseCrafting(RecipeOutput output)
    {
        TagKey<Item> lumber = ItemTags.create(ResourceLocation.parse("tfc:lumber"));
        RecipeOutput conditioned = output.withConditions(
            RecipeConditions.flag(RecipeConditions.WHETSTONES));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, TFCThingsItems.GRINDSTONE_BASE.get())
            .define('R', Ingredient.of(ItemTags.create(ResourceLocation.parse("c:rods/steel"))))
            .define('L', Ingredient.of(lumber))
            .pattern("LRL")
            .pattern("L L")
            .unlockedBy("has_steel_rod", has(ItemTags.create(ResourceLocation.parse("c:rods/steel"))))
            .save(conditioned, crafting("grindstone_base/steel"));

        ShapedRecipeBuilder
            .shaped(RecipeCategory.TOOLS, TFCThingsItems.GRINDSTONE_BASE.get())
            .define('R', Ingredient.of(ItemTags.create(ResourceLocation.parse("c:rods/wrought_iron"))))
            .define('L', Ingredient.of(lumber))
            .pattern("RRR")
            .pattern("L L")
            .unlockedBy("has_wrought_iron_rod",
                has(ItemTags.create(ResourceLocation.parse("c:rods/wrought_iron"))))
            .save(conditioned, crafting("grindstone_base/wrought_iron"));
    }

    private void knappingRecipes(RecipeOutput output)
    {
        knapping(output, "sling",
            KNAPPING_LEATHER, null,
            TFCThingsItems.SLING.get(), RecipeConditions.SLINGS,
            "  ###", "    #", " ####", "##   ", "#    ");

        knapping(output, "surveyors_hammer_head_mold",
            KNAPPING_CLAY, null,
            TFCThingsItems.UNFIRED_SURVEYORS_HAMMER_HEAD_MOLD.get(), RecipeConditions.PROSPECTORS_HAMMERS,
            "#####", " ### ", "     ", " # # ", "#####");

        knapping(output, "whetstone",
            KNAPPING_ROCK, Ingredient.of(TFCThingsTags.Items.WHETSTONE_ROCKS),
            TFCThingsItems.WHETSTONE.get(), RecipeConditions.WHETSTONES,
            "     ", "#####", "#####", "#####", "     ");
    }

    private void knapping(RecipeOutput output, String name,
                          ResourceLocation knappingType, @Nullable Ingredient ingredient,
                          ItemLike result, String flag, String... pattern)
    {
        output.accept(
            id("knapping/" + name),
            new KnappingRecipe(
                KnappingType.MANAGER.getReference(knappingType),
                KnappingPattern.from(false, pattern),
                Optional.ofNullable(ingredient),
                new ItemStack(result)
            ),
            null,
            RecipeConditions.flag(flag)
        );
    }

    private void crownRecipes(RecipeOutput output)
    {
        anvil(output, "crown/gold_empty",
            Ingredient.of(GOLD_DOUBLE_SHEETS), 2, CROWN_RULES,
            TFCThingsItems.GOLD_CROWN_EMPTY.get(), RecipeConditions.CROWNS);

        anvil(output, "crown/platinum_empty",
            Ingredient.of(PLATINUM_DOUBLE_SHEETS), 2, CROWN_RULES,
            TFCThingsItems.PLATINUM_CROWN_EMPTY.get(), RecipeConditions.CROWNS);

        RecipeOutput conditioned = output.withConditions(RecipeConditions.flag(RecipeConditions.CROWNS));
        for (CrownGem gem : CrownGem.ALL)
        {
            crownGemRecipe(conditioned, "crown/gold_" + gem.name,
                TFCThingsItems.GOLD_CROWN_EMPTY.get(), gem.gemIngredient, gem.goldResult.get());
            crownGemRecipe(conditioned, "crown/platinum_" + gem.name,
                TFCThingsItems.PLATINUM_CROWN_EMPTY.get(), gem.gemIngredient, gem.platinumResult.get());
        }
    }

    private void crownGemRecipe(RecipeOutput output, String name,
                                Item emptyCrown, Ingredient gemIngredient, Item result)
    {
        NonNullList<Ingredient> ingredients = NonNullList.of(
            Ingredient.EMPTY,
            Ingredient.of(emptyCrown),
            Ingredient.of(HAMMER),
            gemIngredient
        );
        output.accept(
            crafting(name),
            new AdvancedShapelessRecipe(
                ingredients,
                ItemStackProvider.of(result),
                Optional.of(ItemStackProvider.of(DamageCraftingRemainderModifier.INSTANCE)),
                Optional.of(Ingredient.of(HAMMER))
            ),
            null
        );
    }

    private void gemDisplayRecipes(RecipeOutput output)
    {
        ItemStackProvider remainder =
            ItemStackProvider.of(DamageCraftingRemainderModifier.INSTANCE);
        RecipeOutput conditioned = output.withConditions(
            RecipeConditions.flag(RecipeConditions.GEM_DISPLAYS));

        for (Rock rock : Rock.values())
        {
            String rockName = rock.getSerializedName();
            Ingredient smoothRock = Ingredient.of(TFCBlocks.ROCK_BLOCKS.get(rock).get(Rock.BlockType.SMOOTH));

            Map<Character, Ingredient> key = new LinkedHashMap<>();
            key.put('R', smoothRock);
            key.put('C', Ingredient.of(CHISEL));

            ShapedRecipePattern pattern = ShapedRecipePattern.of(key, "RCR", "RRR", "R R");

            conditioned.accept(
                crafting("gem_display/" + rockName),
                new AdvancedShapedRecipe(
                    pattern,
                    true,
                    ItemStackProvider.of(TFCThingsItems.GEM_DISPLAY_ITEMS.get(rock).get()),
                    Optional.of(remainder),
                    0, 1
                ),
                null
            );
        }
    }

    private void heating(RecipeOutput output, String name, ItemLike item,
                         Metal metal, int amount, float temp, boolean useDurability)
    {
        output.accept(
            id("heating/" + name),
            new HeatingRecipe(
                Ingredient.of(item),
                ItemStackProvider.empty(),
                new FluidStack(metalFluid(metal), amount),
                temp,
                useDurability),
            null
        );
    }

    private Fluid metalFluid(Metal metal)
    {
        return TFCFluids.METALS.get(metal).getSource();
    }

    private void anvil(RecipeOutput output, String name, Ingredient ingredient, int tier,
                       List<ForgeRule> rules, ItemLike result, String flag)
    {
        anvil(output, name, ingredient, tier, rules, ItemStackProvider.of(result),
            RecipeConditions.flag(flag));
    }

    private void anvil(RecipeOutput output, String name, Ingredient ingredient, int tier,
                       List<ForgeRule> rules, ItemStackProvider result, ICondition condition)
    {
        output.accept(
            id("anvil/" + name),
            new AnvilRecipe(ingredient, tier, rules, false, result),
            null,
            condition
        );
    }

    private void casting(RecipeOutput output, String name, Ingredient mold,
                         SizedFluidIngredient fluid, ItemLike result, @SuppressWarnings("SameParameterValue") String flag)
    {
        output.accept(
            id("casting/" + name),
            new CastingRecipe(mold, fluid, ItemStackProvider.of(result), 1.0f),
            null,
            RecipeConditions.flag(flag)
        );
    }

    private void weldingRecipes(RecipeOutput output)
    {
        // Two bear trap halves → complete bear trap
        welding(output, "bear_trap",
            Ingredient.of(TFCThingsItems.BEAR_TRAP_HALF.get()),
            Ingredient.of(TFCThingsItems.BEAR_TRAP_HALF.get()),
            4, TFCThingsItems.BEAR_TRAP.get(),
            RecipeConditions.BEAR_TRAP);

        // Honing steel head + diamond powder → diamond honing steel head
        welding(output, "diamond_honing_steel_head",
            Ingredient.of(TFCThingsItems.HONING_STEEL_HEAD.get()),
            Ingredient.of(TFCItems.ORE_POWDERS.get(Ore.DIAMOND)),
            4, TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get(),
            RecipeConditions.WHETSTONES);

        // Quartz grindstone + black steel double sheets → steel grindstone
        welding(output, "grindstone_wheel/steel",
            Ingredient.of(TFCThingsItems.GRINDSTONE_WHEEL_QUARTZ.get()),
            Ingredient.of(ItemTags.create(ResourceLocation.parse("c:double_sheets/black_steel"))),
            4, TFCThingsItems.GRINDSTONE_WHEEL_STEEL.get(),
            RecipeConditions.WHETSTONES);
    }

    private void welding(RecipeOutput output, String name, Ingredient first, Ingredient second,
                         int tier, ItemLike result, String flag)
    {
        output.accept(
            id("welding/" + name),
            new WeldingRecipe(first, second, tier, ItemStackProvider.of(result), WeldingRecipe.Behavior.IGNORE),
            null,
            RecipeConditions.flag(flag)
        );
    }

    private static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, path);
    }

    private static ResourceLocation crafting(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "crafting/" + path);
    }
}
