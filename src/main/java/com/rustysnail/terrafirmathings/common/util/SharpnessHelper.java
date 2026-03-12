package com.rustysnail.terrafirmathings.common.util;

import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.world.item.ItemStack;

public final class SharpnessHelper
{
    private SharpnessHelper() {}

    public static int getCharges(ItemStack stack)
    {
        if (!isSharpenable(stack)) return 0;
        return stack.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
    }

    public static void consumeCharge(ItemStack stack)
    {
        int current = getCharges(stack);
        if (current > 0)
        {
            stack.set(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), current - 1);
        }
    }

    public static int applySharpness(ItemStack target, int chargesToAdd, int max)
    {
        int current = target.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
        if (current >= max) return 0;
        int newTotal = Math.min(current + chargesToAdd, max);
        target.set(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), newTotal);
        return newTotal - current;
    }

    public static boolean isSharpenable(ItemStack stack)
    {
        return !stack.isEmpty() && stack.is(TFCThingsTags.Items.SHARPENABLE);
    }
}
