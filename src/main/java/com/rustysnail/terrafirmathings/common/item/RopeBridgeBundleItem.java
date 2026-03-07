package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.entity.ThrownRopeBridge;
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

public class RopeBridgeBundleItem extends Item
{

    public static float getPowerForTime(int charge)
    {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F)
        {
            f = 1.0F;
        }
        return f;
    }

    public RopeBridgeBundleItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableRopeBridge.get())
        {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000; // Same as bow
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft)
    {
        if (!(entity instanceof Player player))
        {
            return;
        }

        int charge = this.getUseDuration(stack, entity) - timeLeft;
        if (charge < 5)
        {
            return;
        }

        float power = getPowerForTime(charge);

        if (!level.isClientSide())
        {
            ThrownRopeBridge bridge = new ThrownRopeBridge(level, player, stack.copy());
            bridge.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, power * 1.5F, 0.0F);
            level.addFreshEntity(bridge);
        }

        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
            SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS,
            0.5F, 0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F));
    }

}
