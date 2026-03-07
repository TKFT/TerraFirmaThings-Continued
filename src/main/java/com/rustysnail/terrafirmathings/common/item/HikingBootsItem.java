package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HikingBootsItem extends ArmorItem
{

    public static int getTicksTraveled(ItemStack stack)
    {
        return SnowShoesItem.getDistanceTraveled(stack);
    }

    public static void setTicksTraveled(ItemStack stack, int ticks)
    {
        stack.set(TFCThingsDataComponents.DISTANCE_TRAVELED.get(), ticks);
    }

    public static void addTicks(ItemStack stack, int ticks, int damageThreshold, Runnable damageCallback)
    {
        if (damageThreshold <= 0)
        {
            return;
        }

        int current = getTicksTraveled(stack);
        int total = current + ticks;

        if (total >= damageThreshold)
        {
            int damageTimes = total / damageThreshold;
            total = total % damageThreshold;

            for (int i = 0; i < damageTimes; i++)
            {
                damageCallback.run();
            }
        }

        setTicksTraveled(stack, total);
    }

    public HikingBootsItem(Holder<ArmorMaterial> armorMaterial, ArmorItem.Type type, Item.Properties properties)
    {
        super(armorMaterial, type, properties);
    }

}
