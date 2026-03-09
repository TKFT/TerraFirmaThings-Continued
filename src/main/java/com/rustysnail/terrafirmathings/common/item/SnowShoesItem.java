package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SnowShoesItem extends ArmorItem
{

    public static int getDistanceTraveled(ItemStack stack)
    {
        Integer distance = stack.get(TFCThingsDataComponents.DISTANCE_TRAVELED.get());
        return distance != null ? distance : 0;
    }

    public static void setDistanceTraveled(ItemStack stack, int distance)
    {
        stack.set(TFCThingsDataComponents.DISTANCE_TRAVELED.get(), distance);
    }

    public static void addDistance(ItemStack stack, int distanceCm, Runnable damageCallback)
    {
        int current = getDistanceTraveled(stack);
        int total = current + distanceCm;

        int damageThreshold = TFCThingsConfig.ITEMS.SNOW_SHOES.damageDistance.get();
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

    public SnowShoesItem(Holder<ArmorMaterial> armorMaterial, ArmorItem.Type type, Item.Properties properties)
    {
        super(armorMaterial, type, properties);
    }

    @Override
    public boolean canWalkOnPowderedSnow(ItemStack stack, LivingEntity wearer)
    {
        return TFCThingsConfig.ITEMS.MASTER_LIST.enableSnowShoes.get();
    }

}
