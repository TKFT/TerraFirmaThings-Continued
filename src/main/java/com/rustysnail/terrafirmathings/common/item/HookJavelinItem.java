package com.rustysnail.terrafirmathings.common.item;

import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.entity.ThrownHookJavelin;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.items.JavelinItem;

public class HookJavelinItem extends JavelinItem
{
    private static final float THROW_VELOCITY = 2.5F;
    private static final float THROW_INACCURACY = 0.0F;
    private static final int MIN_CHARGE_TICKS = 10;

    public static boolean isThrown(ItemStack stack)
    {
        return stack.getOrDefault(TFCThingsDataComponents.JAVELIN_THROWN.get(), false);
    }

    public static boolean isLinkedThrownStack(ItemStack stack, UUID javelinUuid)
    {
        UUID linked = stack.get(TFCThingsDataComponents.JAVELIN_ENTITY_UUID.get());
        return stack.getItem() instanceof HookJavelinItem && isThrown(stack) && linked != null && linked.equals(javelinUuid);
    }

    public static void setThrownState(ItemStack stack, UUID javelinUuid)
    {
        stack.set(TFCThingsDataComponents.JAVELIN_THROWN.get(), true);
        stack.set(TFCThingsDataComponents.JAVELIN_ENTITY_UUID.get(), javelinUuid);
        stack.remove(TFCThingsDataComponents.CAPTURED_ENTITY_UUID.get());
    }

    public static void clearThrownState(ItemStack stack)
    {
        stack.set(TFCThingsDataComponents.JAVELIN_THROWN.get(), false);
        stack.remove(TFCThingsDataComponents.JAVELIN_ENTITY_UUID.get());
        stack.remove(TFCThingsDataComponents.CAPTURED_ENTITY_UUID.get());
    }

    @Nullable
    public static ThrownHookJavelin getLinkedJavelin(ItemStack stack, ServerLevel level)
    {
        UUID uuid = stack.get(TFCThingsDataComponents.JAVELIN_ENTITY_UUID.get());
        if (uuid == null)
        {
            return null;
        }
        Entity entity = level.getEntity(uuid);
        if (entity instanceof ThrownHookJavelin javelin)
        {
            return javelin;
        }
        return null;
    }

    public HookJavelinItem(Tier tier, Properties properties)
    {
        super(tier, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableHookJavelins.get())
        {
            return InteractionResultHolder.fail(stack);
        }

        if (isThrown(stack))
        {
            if (!level.isClientSide() && level instanceof ServerLevel serverLevel)
            {
                ThrownHookJavelin javelin = getLinkedJavelin(stack, serverLevel);
                if (javelin != null)
                {
                    if (javelin.isAnchored())
                    {
                        if (player.isShiftKeyDown())
                        {
                            detachAndRetract(javelin);
                        }
                        else
                        {
                            reelIn(javelin);
                        }
                    }
                    else
                    {
                        retrieveHook(javelin);
                    }
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

    private static void reelIn(ThrownHookJavelin javelin)
    {
        float amount = TFCThingsConfig.ITEMS.HOOK_JAVELIN.retractAmount.get().floatValue();
        javelin.retractRope(amount);
    }

    private static void detachAndRetract(ThrownHookJavelin javelin)
    {
        javelin.startAutoRetract();
    }

    private static void retrieveHook(ThrownHookJavelin javelin)
    {
        javelin.startAutoRetract();
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

        ThrownHookJavelin linked = getLinkedJavelin(stack, serverLevel);
        if (linked == null || !linked.isAlive())
        {
            clearThrownState(stack);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft)
    {
        if (!(entity instanceof Player player)) return;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableHookJavelins.get()) return;
        if (isThrown(stack)) return;

        int charge = this.getUseDuration(stack, entity) - timeLeft;
        if (charge < MIN_CHARGE_TICKS) return;

        if (!level.isClientSide())
        {
            ThrownHookJavelin javelin = new ThrownHookJavelin(level, player, stack);
            javelin.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, THROW_VELOCITY, THROW_INACCURACY);
            level.addFreshEntity(javelin);

            setThrownState(stack, javelin.getUUID());
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));

            level.playSound(null, javelin, TFCSounds.JAVELIN_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }
}
