package com.rustysnail.terrafirmathings.common.item;

import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.entity.ThrownRopeJavelin;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;

public class RopeJavelinItem extends Item
{
    private static final float THROW_VELOCITY = 2.5F;
    private static final float THROW_INACCURACY = 1.0F;
    private static final int MIN_CHARGE_TICKS = 10;

    public static boolean isThrown(ItemStack stack)
    {
        return stack.getOrDefault(TFCThingsDataComponents.JAVELIN_THROWN.get(), false);
    }

    public static boolean isLinkedThrownStack(ItemStack stack, UUID javelinUuid)
    {
        UUID linked = stack.get(TFCThingsDataComponents.JAVELIN_ENTITY_UUID.get());
        return stack.getItem() instanceof RopeJavelinItem && isThrown(stack) && linked != null && linked.equals(javelinUuid);
    }

    public static void setThrownState(ItemStack stack, UUID javelinUuid)
    {
        HookJavelinItem.setThrownState(stack, javelinUuid);
    }

    public static void setCapturedEntity(ItemStack stack, @Nullable UUID capturedUuid)
    {
        if (capturedUuid == null)
        {
            stack.remove(TFCThingsDataComponents.CAPTURED_ENTITY_UUID.get());
        }
        else
        {
            stack.set(TFCThingsDataComponents.CAPTURED_ENTITY_UUID.get(), capturedUuid);
        }
    }

    public static void clearThrownState(ItemStack stack)
    {
        HookJavelinItem.clearThrownState(stack);
    }

    @Nullable
    public static ThrownRopeJavelin getLinkedJavelin(ItemStack stack, ServerLevel level)
    {
        UUID uuid = stack.get(TFCThingsDataComponents.JAVELIN_ENTITY_UUID.get());
        if (uuid == null)
        {
            return null;
        }

        Entity entity = level.getEntity(uuid);
        if (entity instanceof ThrownRopeJavelin javelin)
        {
            return javelin;
        }
        return null;
    }

    public RopeJavelinItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableRopeJavelin.get())
        {
            return InteractionResultHolder.fail(stack);
        }

        if (isThrown(stack))
        {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel)
            {
                ThrownRopeJavelin linked = getLinkedJavelin(stack, serverLevel);
                if (linked != null)
                {
                    if (linked.canPullCapturedTowardOwner())
                    {
                        linked.pullCapturedTowardOwner(TFCThingsConfig.ITEMS.ROPE_JAVELIN.pullStrength.get().floatValue());
                    }
                    linked.startAutoRetract();
                }
                else
                {
                    clearThrownState(stack);
                }
            }
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide());
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected)
    {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

        if (level.isClientSide() || !(level instanceof ServerLevel serverLevel))
        {
            return;
        }
        if (!isThrown(stack))
        {
            return;
        }

        ThrownRopeJavelin linked = getLinkedJavelin(stack, serverLevel);
        if (linked == null || !linked.isAlive())
        {
            clearThrownState(stack);
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.SPEAR;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft)
    {
        if (!(entity instanceof Player player)) return;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableRopeJavelin.get()) return;
        if (isThrown(stack)) return;

        int charge = this.getUseDuration(stack, entity) - timeLeft;
        if (charge < MIN_CHARGE_TICKS) return;

        if (!level.isClientSide())
        {
            ThrownRopeJavelin javelin = new ThrownRopeJavelin(level, player, stack);
            javelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, THROW_VELOCITY, THROW_INACCURACY);
            level.addFreshEntity(javelin);

            setThrownState(stack, javelin.getUUID());

            level.playSound(null, javelin, TFCSounds.JAVELIN_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

}
