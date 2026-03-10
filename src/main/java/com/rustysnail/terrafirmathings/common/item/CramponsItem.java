package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class CramponsItem extends ArmorItem
{

    public static void addDistance(ItemStack stack, int distanceCm, Runnable damageCallback)
    {
        int current = SnowShoesItem.getDistanceTraveled(stack);
        int total = current + distanceCm;

        int damageThreshold = TFCThingsConfig.ITEMS.CRAMPONS.damageDistance.get();
        if (damageThreshold > 0 && total >= damageThreshold)
        {
            int damageTimes = total / damageThreshold;
            total = total % damageThreshold;

            for (int i = 0; i < damageTimes; i++)
            {
                damageCallback.run();
            }
        }

        SnowShoesItem.setDistanceTraveled(stack, total);
    }

    public CramponsItem(Holder<ArmorMaterial> armorMaterial, Type type, Properties properties)
    {
        super(armorMaterial, type, properties);
    }

}
