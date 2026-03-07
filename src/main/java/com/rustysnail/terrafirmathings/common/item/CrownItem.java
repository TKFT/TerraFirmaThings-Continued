package com.rustysnail.terrafirmathings.common.item;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class CrownItem extends ArmorItem
{

    @Nullable
    private final String gemName;

    public CrownItem(Holder<ArmorMaterial> material, Type type, Properties properties, @Nullable String gemName)
    {
        super(material, type, properties);
        this.gemName = gemName;
    }

    @Override
    public boolean isDamageable(ItemStack stack)
    {
        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
    {
        if (gemName == null)
        {
            tooltipComponents.add(Component.translatable("tfcthings.tooltip.crown.gem.empty"));
        }
        else
        {
            tooltipComponents.add(Component.translatable("tfcthings.tooltip.crown.gem", gemName));
        }
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

}
