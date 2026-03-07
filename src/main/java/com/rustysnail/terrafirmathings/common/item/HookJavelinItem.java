package com.rustysnail.terrafirmathings.common.item;

import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.entity.ThrownHookJavelin;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.phys.AABB;

import net.dries007.tfc.client.TFCSounds;

public class HookJavelinItem extends Item
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

    public HookJavelinItem(Properties properties)
    {
        super(properties);
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
                    if (!player.onGround())
                    {
                        javelin.shortenAndPullOwner(TFCThingsConfig.ITEMS.HOOK_JAVELIN.retractAmount.get().floatValue());
                        if (player.isShiftKeyDown())
                        {
                            dismountHookedPlayer(player);
                            javelin.startAutoRetract();
                        }
                    }
                    else
                    {
                        javelin.startAutoRetract();
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

            level.playSound(null, javelin, TFCSounds.JAVELIN_THROWN.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    private void dismountHookedPlayer(Player player)
    {
        Level level = player.level();
        Direction facing = player.getDirection();
        Direction perpendicular = facing.getClockWise();

        int[][] offsets = {
            {0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0},
            {0, 2}, {0, -2}, {-1, 2}, {-1, -2}, {1, 2}, {1, -2}, {2, 2}, {2, -2}, {-2, 2}, {-2, -2},
            {2, 0}, {2, 1}, {2, -1}, {-2, 0}, {-2, 1}, {-2, -1}
        };

        double fallbackX = player.getX();
        double fallbackY = player.getY();
        double fallbackZ = player.getZ();

        double centerX = Math.floor(player.getX()) + 0.5;
        double centerZ = Math.floor(player.getZ()) + 0.5;
        double halfW = player.getBbWidth() / 2.0;
        AABB baseBox = new AABB(
            centerX - halfW, player.getBoundingBox().minY, centerZ - halfW,
            centerX + halfW, Math.floor(player.getBoundingBox().minY) + player.getBbHeight(), centerZ + halfW
        );

        for (int[] offset : offsets)
        {
            double dx = facing.getStepX() * offset[0] + perpendicular.getStepX() * offset[1];
            double dz = facing.getStepZ() * offset[0] + perpendicular.getStepZ() * offset[1];
            double checkX = centerX + dx;
            double checkZ = centerZ + dz;
            AABB offsetBox = baseBox.move(dx, 0, dz);

            if (level.noCollision(player, offsetBox))
            {
                BlockPos pos = BlockPos.containing(checkX, player.getY(), checkZ);
                if (level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP))
                {
                    player.moveTo(checkX, player.getY() + 1.0, checkZ);
                    return;
                }
                BlockPos posBelow = BlockPos.containing(checkX, player.getY() - 1.0, checkZ);
                if (level.getBlockState(posBelow).isFaceSturdy(level, posBelow, Direction.UP))
                {
                    fallbackX = checkX;
                    fallbackY = player.getY() + 1.0;
                    fallbackZ = checkZ;
                }
            }
            else if (level.noCollision(player, offsetBox.move(0, 2, 0)))
            {
                BlockPos posAbove = BlockPos.containing(checkX, player.getY() + 1.0, checkZ);
                if (level.getBlockState(posAbove).isFaceSturdy(level, posAbove, Direction.UP))
                {
                    player.moveTo(checkX, player.getY() + 2.0, checkZ);
                    return;
                }
                BlockPos posAt = BlockPos.containing(checkX, player.getY(), checkZ);
                if (level.getBlockState(posAt).isFaceSturdy(level, posAt, Direction.UP))
                {
                    player.moveTo(checkX, player.getY() + 1.2, checkZ);
                    return;
                }
            }
        }

        player.moveTo(fallbackX, fallbackY, fallbackZ);
    }
}
