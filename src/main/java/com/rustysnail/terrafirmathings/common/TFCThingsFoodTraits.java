package com.rustysnail.terrafirmathings.common;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.Holder;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;

public class TFCThingsFoodTraits
{
    public static final DeferredRegister<FoodTrait> TRAITS =
        DeferredRegister.create(FoodTraits.KEY, TerraFirmaThings.MOD_ID);

    public static final DeferredHolder<FoodTrait, FoodTrait> GRAIN_PILE =
        TRAITS.register("grain_pile", () -> new FoodTrait(
            TFCThingsConfig.ITEMS.GRAIN_PILE.decayModifier,
            "tfcthings.food_trait.grain_pile"
        ));

    public static final DeferredHolder<FoodTrait, FoodTrait> GRAIN_PILE_SHELTERED =
        TRAITS.register("grain_pile_sheltered", () -> new FoodTrait(
            TFCThingsConfig.ITEMS.GRAIN_PILE.shelterDecayModifier,
            "tfcthings.food_trait.grain_pile_sheltered"
        ));

    public static Holder<FoodTrait> activeTraitFor(boolean sheltered)
    {
        return sheltered ? GRAIN_PILE_SHELTERED : GRAIN_PILE;
    }
}
