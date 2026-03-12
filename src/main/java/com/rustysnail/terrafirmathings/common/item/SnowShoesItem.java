package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SnowShoesItem extends ArmorItem
{

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
