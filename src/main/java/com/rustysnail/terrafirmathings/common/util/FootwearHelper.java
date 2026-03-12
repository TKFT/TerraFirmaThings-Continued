package com.rustysnail.terrafirmathings.common.util;

import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import net.minecraft.world.item.ItemStack;

public final class FootwearHelper
{
    private FootwearHelper() {}

    public static int getDistanceTraveled(ItemStack stack)
    {
        Integer distance = stack.get(TFCThingsDataComponents.DISTANCE_TRAVELED.get());
        return distance != null ? distance : 0;
    }

    public static void setDistanceTraveled(ItemStack stack, int distance)
    {
        stack.set(TFCThingsDataComponents.DISTANCE_TRAVELED.get(), distance);
    }

    public static void addDistance(ItemStack stack, int distanceCm, int damageThreshold, Runnable damageCallback)
    {
        int total = getDistanceTraveled(stack) + distanceCm;

        if (damageThreshold > 0 && total >= damageThreshold)
        {
            int damageTimes = total / damageThreshold;
            total = total % damageThreshold;

            for (int i = 0; i < damageTimes; i++)
            {
                damageCallback.run();
            }
        }

        setDistanceTraveled(stack, total);
    }
}
