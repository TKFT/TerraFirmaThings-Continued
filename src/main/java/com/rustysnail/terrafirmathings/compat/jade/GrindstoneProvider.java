package com.rustysnail.terrafirmathings.compat.jade;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.blockentity.GrindstoneBlockEntity;
import com.rustysnail.terrafirmathings.common.item.GrindstoneItem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum GrindstoneProvider implements IBlockComponentProvider
{
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "grindstone");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        if (!(accessor.getBlockEntity() instanceof GrindstoneBlockEntity be)) return;

        ItemStack grindstone = be.getGrindstone();
        ItemStack tool = be.getTool();

        if (grindstone.isEmpty() && tool.isEmpty()) return;

        IThemeHelper t = IThemeHelper.get();

        if (!tool.isEmpty())
        {
            int sharpness = tool.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
            int maxSharpness = 0;
            if (!grindstone.isEmpty() && grindstone.getItem() instanceof GrindstoneItem grindstoneItem)
            {
                maxSharpness = grindstoneItem.getTier().getMaxToolCharges();
            }
            tooltip.add(Component.translatable("jade.tfcthings.grindstone.tool",
                tool.getHoverName(),
                t.info(Component.translatable("jade.tfcthings.grindstone.sharpness", sharpness, maxSharpness))));
        }

        if (!grindstone.isEmpty())
        {
            tooltip.add(Component.translatable("jade.tfcthings.grindstone.wheel", grindstone.getHoverName()));
        }
    }

    @Override
    public ResourceLocation getUid()
    {
        return UID;
    }
}
