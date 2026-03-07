package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class HoningSteelItem extends Item
{
    private static final int MAX_USE_DURATION = 72000;

    private final boolean diamond;

    public HoningSteelItem(boolean diamond, Properties properties)
    {
        super(properties);
        this.diamond = diamond;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack honingSteel = player.getItemInHand(hand);
        InteractionHand targetHand = WhetstoneItem.getTargetHand(hand);
        ItemStack target = player.getItemInHand(targetHand);

        if (!WhetstoneItem.isSharpenable(target))
        {
            if (!level.isClientSide())
            {
                player.displayClientMessage(Component.translatable("tfcthings.sharpening_requires_other_hand"), true);
            }
            return InteractionResultHolder.fail(honingSteel);
        }

        if (getRequiredUseTicks() <= 0)
        {
            if (!level.isClientSide())
            {
                trySharpen(level, player, honingSteel, hand);
            }
            return InteractionResultHolder.sidedSuccess(honingSteel, level.isClientSide());
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(honingSteel);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return MAX_USE_DURATION;
    }

    @Override
    public void releaseUsing(ItemStack honingSteel, Level level, LivingEntity entity, int timeLeft)
    {
        if (level.isClientSide() || !(entity instanceof Player player))
        {
            return;
        }

        int required = getRequiredUseTicks();
        if (required <= 0)
        {
            return;
        }

        int usedTicks = getUseDuration(honingSteel, entity) - timeLeft;
        if (usedTicks < required)
        {
            return;
        }

        InteractionHand sharpeningHand = player.getUsedItemHand();
        trySharpen(level, player, honingSteel, sharpeningHand);
    }


    private int getRequiredUseTicks()
    {
        return diamond
            ? TFCThingsConfig.ITEMS.WHETSTONE.ticksPerDiamondHoningSteelUse.get()
            : TFCThingsConfig.ITEMS.WHETSTONE.ticksPerHoningSteelUse.get();
    }

    private void trySharpen(Level level, Player player, ItemStack honingSteel, InteractionHand sharpeningHand)
    {
        InteractionHand targetHand = WhetstoneItem.getTargetHand(sharpeningHand);
        ItemStack target = player.getItemInHand(targetHand);
        if (!WhetstoneItem.isSharpenable(target))
        {
            player.displayClientMessage(Component.translatable("tfcthings.sharpening_requires_other_hand"), true);
            return;
        }

        int charges = diamond
            ? TFCThingsConfig.ITEMS.WHETSTONE.chargesPerDiamondHoningSteel.get()
            : TFCThingsConfig.ITEMS.WHETSTONE.chargesPerHoningSteel.get();

        int max = diamond
            ? TFCThingsConfig.ITEMS.WHETSTONE.maxChargesDiamondHoningSteel.get()
            : TFCThingsConfig.ITEMS.WHETSTONE.maxChargesHoningSteel.get();
        int added = WhetstoneItem.applySharpness(target, charges, max);
        if (added <= 0)
        {
            player.displayClientMessage(Component.translatable("tfcthings.maximum_sharpness"), true);
            return;
        }

        WhetstoneItem.damageSharpenedTool(target, player, targetHand);
        honingSteel.hurtAndBreak(1, player, LivingEntity.getSlotForHand(sharpeningHand));
        int total = target.getOrDefault(
            com.rustysnail.terrafirmathings.common.TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
        player.displayClientMessage(Component.translatable("tfcthings.sharpened", total), true);
        level.playSound(null, player, SoundEvents.GRINDSTONE_USE,
            SoundSource.PLAYERS, 0.6f, diamond ? 1.5f : 1.2f);
    }
}
