package com.rustysnail.terrafirmathings.compat.jade;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.block.SnareBlock;
import com.rustysnail.terrafirmathings.common.blockentity.SnareBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum SnareProvider implements IBlockComponentProvider
{
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "snare");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (!(accessor.getBlockEntity() instanceof SnareBlockEntity be)) return;

        IThemeHelper t = IThemeHelper.get();
        boolean triggered = accessor.getBlockState().getValue(SnareBlock.TRIGGERED);

        // Status line
        if (triggered)
        {
            tooltip.add(t.danger(Component.translatable("jade.tfcthings.snare.triggered")));
        }
        else if (be.hasBait())
        {
            tooltip.add(t.success(Component.translatable("jade.tfcthings.snare.waiting")));
        }
        else
        {
            tooltip.add(t.info(Component.translatable("jade.tfcthings.snare.empty")));
        }

        // Bait line
        ItemStack bait = be.getBait();
        if (!bait.isEmpty())
        {
            tooltip.add(Component.translatable("jade.tfcthings.snare.bait", bait.getHoverName()));
        }
        else if (!triggered)
        {
            tooltip.add(t.warning(Component.translatable("jade.tfcthings.snare.no_bait")));
        }
    }

    @Override
    public ResourceLocation getUid()
    {
        return UID;
    }
}
