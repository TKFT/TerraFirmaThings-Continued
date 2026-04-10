package com.rustysnail.terrafirmathings.compat.jade;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.blockentity.GemDisplayBlockEntity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum GemDisplayProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, GemDisplayProvider.Data>
{
    INSTANCE;

    private static final ResourceLocation UID =
        ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "gem_display");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        Data data = decodeFromData(accessor).orElse(null);
        if (data == null) return;

        IThemeHelper t = IThemeHelper.get();

        if (data.gem0().isEmpty() && data.gem1().isEmpty() && data.gem2().isEmpty())
        {
            tooltip.add(t.info(Component.translatable("jade.tfcthings.gem_display.empty")));
            return;
        }

        ItemStack[] slots = {data.gem0(), data.gem1(), data.gem2()};
        for (int i = 0; i < slots.length; i++)
        {
            ItemStack gem = slots[i];
            if (!gem.isEmpty())
            {
                tooltip.add(Component.translatable("jade.tfcthings.gem_display.slot",
                    i + 1, gem.getHoverName()));
            }
        }
    }

    @Override
    public Data streamData(BlockAccessor accessor)
    {
        if (!(accessor.getBlockEntity() instanceof GemDisplayBlockEntity be))
            return new Data(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY);
        return new Data(
            be.getGem(0).copy(),
            be.getGem(1).copy(),
            be.getGem(2).copy()
        );
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec()
    {
        return Data.STREAM_CODEC;
    }

    @Override
    public ResourceLocation getUid()
    {
        return UID;
    }

    public record Data(ItemStack gem0, ItemStack gem1, ItemStack gem2)
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC =
            StreamCodec.composite(
                ItemStack.OPTIONAL_STREAM_CODEC, Data::gem0,
                ItemStack.OPTIONAL_STREAM_CODEC, Data::gem1,
                ItemStack.OPTIONAL_STREAM_CODEC, Data::gem2,
                Data::new
            );
    }
}
