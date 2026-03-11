package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
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

public class WhetstoneItem extends Item
{
    public static int getCharges(ItemStack stack)
    {
        if (!isSharpenable(stack)) return 0;
        return stack.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
    }

    public static void consumeCharge(ItemStack stack)
    {
        int current = getCharges(stack);
        if (current > 0)
        {
            stack.set(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), current - 1);
        }
    }

    public static int applySharpness(ItemStack target, int chargesToAdd, int max)
    {
        int current = target.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
        if (current >= max) return 0;
        int newTotal = Math.min(current + chargesToAdd, max);
        target.set(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), newTotal);
        return newTotal - current;
    }

    public static boolean isSharpenable(ItemStack stack)
    {
        return !stack.isEmpty() && stack.is(TFCThingsTags.Items.SHARPENABLE);
    }

    public static InteractionHand getTargetHand(InteractionHand sharpeningHand)
    {
        return sharpeningHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }

    public static void damageSharpenedTool(ItemStack target, Player player, InteractionHand targetHand)
    {
        if (!target.isEmpty() && target.isDamageableItem())
        {
            target.hurtAndBreak(1, player, LivingEntity.getSlotForHand(targetHand));
        }
    }

    public WhetstoneItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return InteractionResultHolder.fail(player.getItemInHand(hand));
        }

        ItemStack whetstone = player.getItemInHand(hand);
        InteractionHand targetHand = getTargetHand(hand);
        ItemStack target = player.getItemInHand(targetHand);

        if (!isSharpenable(target))
        {
            if (!level.isClientSide())
            {
                player.displayClientMessage(Component.translatable("tfcthings.sharpening_requires_other_hand"), true);
            }
            return InteractionResultHolder.fail(whetstone);
        }

        if (getRequiredUseTicks() <= 0)
        {
            if (!level.isClientSide())
            {
                trySharpen(level, player, whetstone, hand);
            }
            return InteractionResultHolder.sidedSuccess(whetstone, level.isClientSide());
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(whetstone);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return Math.max(1, getRequiredUseTicks());
    }

    @Override
    public ItemStack finishUsingItem(ItemStack whetstone, Level level, LivingEntity entity)
    {
        if (!level.isClientSide() && entity instanceof Player player)
        {
            trySharpen(level, player, whetstone, player.getUsedItemHand());
        }
        return whetstone;
    }


    protected int getRequiredUseTicks()
    {
        return TFCThingsConfig.ITEMS.WHETSTONE.ticksPerWhetstoneUse.get();
    }

    protected void trySharpen(Level level, Player player, ItemStack sharpener, InteractionHand sharpeningHand)
    {
        InteractionHand targetHand = getTargetHand(sharpeningHand);
        ItemStack target = player.getItemInHand(targetHand);
        if (!isSharpenable(target))
        {
            player.displayClientMessage(Component.translatable("tfcthings.sharpening_requires_other_hand"), true);
            return;
        }

        int added = applySharpness(target, TFCThingsConfig.ITEMS.WHETSTONE.chargesPerWhetstone.get(),
            TFCThingsConfig.ITEMS.WHETSTONE.maxChargesWhetstone.get());
        if (added <= 0)
        {
            player.displayClientMessage(Component.translatable("tfcthings.maximum_sharpness"), true);
            return;
        }

        damageSharpenedTool(target, player, targetHand);
        sharpener.hurtAndBreak(1, player, LivingEntity.getSlotForHand(sharpeningHand));
        int total = target.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
        player.displayClientMessage(Component.translatable("tfcthings.sharpened", total), true);
        level.playSound(null, player, SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS, 0.6f, 1.2f);
    }
}
