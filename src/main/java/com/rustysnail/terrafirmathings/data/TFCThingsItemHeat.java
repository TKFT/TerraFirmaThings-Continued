package com.rustysnail.terrafirmathings.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;

import net.dries007.tfc.common.component.heat.HeatDefinition;

public final class TFCThingsItemHeat extends TFCThingsDataManagerProvider<HeatDefinition>
{
    private static final float CAP_INGOT = 2.857143f;
    private static final float CAP_D_INGOT = 5.714286f;

    private static final float CAP_GOLD_D_INGOT = 3.333333f;

    public TFCThingsItemHeat(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(HeatDefinition.CODEC, "Item Heat", output, lookup, "tfc/item_heat");
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        for (SurveyorsHammerFamily f : SurveyorsHammerFamily.ALL)
        {
            add("surveyors_hammer_head/" + f.name, new HeatDefinition(
                Ingredient.of(f.headItem.get()), CAP_INGOT, f.meltTemp * 0.6f, f.meltTemp * 0.8f));
            add("surveyors_hammer/" + f.name, new HeatDefinition(
                Ingredient.of(f.hammerItem.get()), CAP_INGOT, f.meltTemp * 0.6f, f.meltTemp * 0.8f));
        }

        add("honing_steel_head", new HeatDefinition(
            Ingredient.of(TFCThingsItems.HONING_STEEL_HEAD.get()),
            CAP_INGOT, 1540f * 0.6f, 1540f * 0.8f));

        add("diamond_honing_steel_head", new HeatDefinition(
            Ingredient.of(TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get()),
            CAP_INGOT, 1540f * 0.6f, 1540f * 0.8f));

        for (HookJavelinFamily f : HookJavelinFamily.ALL)
        {
            add("hook_javelin_head/" + f.name, new HeatDefinition(
                Ingredient.of(f.headItem.get()), CAP_D_INGOT, f.meltTemp * 0.6f, f.meltTemp * 0.8f));
            add("hook_javelin/" + f.name, new HeatDefinition(
                Ingredient.of(f.javelinItem.get()), CAP_D_INGOT, f.meltTemp * 0.6f, f.meltTemp * 0.8f));
        }

        for (RopeJavelinFamily f : RopeJavelinFamily.ALL)
        {
            add("rope_javelin/" + f.name, new HeatDefinition(
                Ingredient.of(f.javelinItem.get()), CAP_INGOT, f.meltTemp * 0.6f, f.meltTemp * 0.8f));
        }

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
            goldGemCrowns(), CAP_GOLD_D_INGOT, 1060f * 0.6f, 1060f * 0.8f));

        add("crown/platinum_empty", new HeatDefinition(
            Ingredient.of(TFCThingsItems.PLATINUM_CROWN_EMPTY.get()),
            CAP_GOLD_D_INGOT, 1730f * 0.6f, 1730f * 0.8f));

        add("crowns/platinum", new HeatDefinition(
            platinumGemCrowns(), CAP_GOLD_D_INGOT, 1730f * 0.6f, 1730f * 0.8f));
    }

    private static Ingredient goldGemCrowns()
    {
        List<ItemLike> items = new ArrayList<>();
        for (CrownGem g : CrownGem.ALL)
        {
            items.add(g.goldResult.get());
        }
        return Ingredient.of(items.toArray(new ItemLike[0]));
    }

    private static Ingredient platinumGemCrowns()
    {
        List<ItemLike> items = new ArrayList<>();
        for (CrownGem g : CrownGem.ALL)
        {
            items.add(g.platinumResult.get());
        }
        return Ingredient.of(items.toArray(new ItemLike[0]));
    }
}
