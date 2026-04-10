package com.rustysnail.terrafirmathings.compat.jade;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.blockentity.GrainPileBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;

import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.util.calendar.Calendars;

public enum GrainPileProvider implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, GrainPileProvider.Data>
{
    INSTANCE;

    private static final ResourceLocation UID =
        ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "grain_pile");

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config)
    {
        Data data = decodeFromData(accessor).orElse(null);
        if (data == null) return;

        IThemeHelper t = IThemeHelper.get();

        if (data.grainType().isEmpty() || data.count() == 0)
        {
            tooltip.add(t.info(Component.translatable("jade.tfcthings.grain_pile.empty")));
            return;
        }

        tooltip.add(Component.translatable("tfcthings.grain_pile.count",
            data.grainType().getHoverName(),
            data.count(),
            data.maxCapacity()));

        IFood food = FoodCapability.get(data.grainType());
        if (food == null) return;

        long creationDate = food.getCreationDate();

        if (creationDate == IFood.ROTTEN_FLAG)
        {
            tooltip.add(t.danger(Component.translatable("tfc.tooltip.food_rotten")));
        }
        else if (creationDate == IFood.NEVER_DECAY_FLAG
            || food.getDecayDateModifier() == Float.POSITIVE_INFINITY)
        {
            tooltip.add(t.success(Component.translatable("tfc.tooltip.food_infinite_expiry")));
        }
        else if (creationDate != IFood.TRANSIENT_NEVER_DECAY_FLAG
            && creationDate != IFood.INVISIBLE_NEVER_DECAY_FLAG)
        {
            long rottenDate = food.getRottenDate();
            long ticksLeft = rottenDate - Calendars.CLIENT.getTicks();

            if (ticksLeft <= 0)
            {
                tooltip.add(t.danger(Component.translatable("tfc.tooltip.food_rotten")));
            }
            else
            {
                tooltip.add(Component.translatable("jade.tfcthings.grain_pile.expires",
                        Calendars.CLIENT.getExactTimeAndDate(rottenDate))
                    .withStyle(ChatFormatting.DARK_GREEN));
                tooltip.add(Component.translatable("jade.tfcthings.grain_pile.expires_in",
                        Calendars.CLIENT.getTimeDelta(ticksLeft))
                    .withStyle(ChatFormatting.DARK_GREEN));
            }
        }
    }

    @Override
    public Data streamData(BlockAccessor accessor)
    {
        GrainPileBlockEntity be = (GrainPileBlockEntity) accessor.getBlockEntity();
        return new Data(be.getGrainType(), be.getCount(), be.getMaxCapacity());
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

    public record Data(ItemStack grainType, int count, int maxCapacity)
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC =
            StreamCodec.composite(
                ItemStack.OPTIONAL_STREAM_CODEC, Data::grainType,
                ByteBufCodecs.VAR_INT.cast(), Data::count,
                ByteBufCodecs.VAR_INT.cast(), Data::maxCapacity,
                Data::new
            );
    }
}
