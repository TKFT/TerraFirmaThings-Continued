package com.rustysnail.terrafirmathings.compat.jade;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

public enum FishingNetAnchorProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, FishingNetAnchorProvider.Data>
{
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "fishing_net_anchor");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        Data data = decodeFromData(accessor).orElse(null);
        if (data == null) return;

        IThemeHelper t = IThemeHelper.get();

        if (data.linked())
        {
            tooltip.add(t.success(Component.translatable("jade.tfcthings.fishing_net_anchor.linked")));
            tooltip.add(Component.translatable("jade.tfcthings.fishing_net_anchor.other_anchor",
                t.info(data.otherX()), t.info(data.otherY()), t.info(data.otherZ())));
        }
        else
        {
            tooltip.add(t.warning(Component.translatable("jade.tfcthings.fishing_net_anchor.unlinked")));
        }
    }

    @Override
    @Nullable
    public Data streamData(BlockAccessor accessor)
    {
        FishingNetAnchorBlockEntity be = (FishingNetAnchorBlockEntity) accessor.getBlockEntity();

        boolean linked = be.isLinked();
        int otherX = 0, otherY = 0, otherZ = 0;

        if (linked)
        {
            BlockPos other = be.getOtherAnchor();
            if (other != null)
            {
                otherX = other.getX();
                otherY = other.getY();
                otherZ = other.getZ();
            }
        }

        return new Data(linked, otherX, otherY, otherZ);
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

    public record Data(boolean linked, int otherX, int otherY, int otherZ)
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL.cast(), Data::linked,
            ByteBufCodecs.VAR_INT.cast(), Data::otherX,
            ByteBufCodecs.VAR_INT.cast(), Data::otherY,
            ByteBufCodecs.VAR_INT.cast(), Data::otherZ,
            Data::new
        );
    }
}
