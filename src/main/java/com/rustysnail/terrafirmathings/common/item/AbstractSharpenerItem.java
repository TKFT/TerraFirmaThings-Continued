package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.util.SharpnessHelper;
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

public abstract class AbstractSharpenerItem extends Item
{
    protected AbstractSharpenerItem(Properties properties)
    {
        super(properties);
    }

    protected abstract GrindstoneItem.Tier getSharpenerTier();

    protected float getSoundPitch()
    {
        return 1.2f;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack sharpener = player.getItemInHand(hand);
        InteractionHand targetHand = getTargetHand(hand);
        ItemStack target = player.getItemInHand(targetHand);

        if (!SharpnessHelper.isSharpenable(target))
        {
            if (!level.isClientSide())
            {
                player.displayClientMessage(Component.translatable("tfcthings.sharpening_requires_other_hand"), true);
            }
            return InteractionResultHolder.fail(sharpener);
        }

        if (getSharpenerTier().getTicksPerOperation() <= 0)
        {
            if (!level.isClientSide())
            {
                trySharpen(level, player, sharpener, hand);
            }
            return InteractionResultHolder.sidedSuccess(sharpener, level.isClientSide());
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(sharpener);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return Math.max(1, getSharpenerTier().getTicksPerOperation());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack sharpener, Level level, LivingEntity entity)
    {
        if (!level.isClientSide() && entity instanceof Player player)
        {
            trySharpen(level, player, sharpener, player.getUsedItemHand());
        }
        return sharpener;
    }

    private void trySharpen(Level level, Player player, ItemStack sharpener, InteractionHand sharpeningHand)
    {
        InteractionHand targetHand = getTargetHand(sharpeningHand);
        ItemStack target = player.getItemInHand(targetHand);
        if (!SharpnessHelper.isSharpenable(target))
        {
            player.displayClientMessage(Component.translatable("tfcthings.sharpening_requires_other_hand"), true);
            return;
        }

        GrindstoneItem.Tier tier = getSharpenerTier();
        int added = SharpnessHelper.applySharpness(target, tier.getChargesPerOperation(), tier.getMaxToolCharges());
        if (added <= 0)
        {
            player.displayClientMessage(Component.translatable("tfcthings.maximum_sharpness"), true);
            return;
        }

        damageSharpenedTool(target, player, targetHand);
        sharpener.hurtAndBreak(1, player, LivingEntity.getSlotForHand(sharpeningHand));
        player.displayClientMessage(Component.translatable("tfcthings.sharpened", SharpnessHelper.getCharges(target)), true);
        level.playSound(null, player, SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.6f, getSoundPitch());
    }

    private static InteractionHand getTargetHand(InteractionHand sharpeningHand)
    {
        return sharpeningHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    private static void damageSharpenedTool(ItemStack target, Player player, InteractionHand targetHand)
    {
        if (!target.isEmpty() && target.isDamageableItem())
        {
            target.hurtAndBreak(1, player, LivingEntity.getSlotForHand(targetHand));
        }
    }
}
