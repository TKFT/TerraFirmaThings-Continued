package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.block.FishingNetAnchorBlock;
import com.rustysnail.terrafirmathings.common.util.FishingNetPlacement;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;


public class FishingNetItem extends Item
{

    private static final String NET_START_POS = "tfcthings_net_start_pos";
    private static final String NET_START_TIME = "tfcthings_net_start_time";
    private static final int NET_SELECT_TIMEOUT_TICKS = 20 * 180;

    private static void showPlacementError(Player player, FishingNetPlacement.Status status)
    {
        Component message = switch (status)
        {
            case NOT_SAME_Y -> Component.translatable("tfcthings.tooltip.fishing_net.anchors_same_y");
            case NOT_ALIGNED -> Component.translatable("tfcthings.tooltip.fishing_net.anchors_aligned");
            case TOO_FAR -> Component.translatable("tfcthings.tooltip.fishing_net.anchors_too_far");
            case NO_WATER -> Component.translatable("tfcthings.tooltip.fishing_net.no_water");
            case BLOCKED -> Component.translatable("tfcthings.tooltip.fishing_net.path_blocked");
            default -> Component.translatable("tfcthings.tooltip.fishing_net.unable");
        };
        player.displayClientMessage(message, true);
    }

    private static int countInInventory(Player player, Item item)
    {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack s = player.getInventory().getItem(i);
            if (s.is(item)) count += s.getCount();
        }
        return count;
    }

    private static void removeFromInventory(Player player, Item item, int amount)
    {
        int remaining = amount;
        for (int i = 0; i < player.getInventory().getContainerSize() && remaining > 0; i++)
        {
            ItemStack s = player.getInventory().getItem(i);
            if (!s.is(item)) continue;

            int take = Math.min(remaining, s.getCount());
            s.shrink(take);
            remaining -= take;
        }
    }

    private static BlockPos resolveAnchorPos(UseOnContext ctx)
    {
        Level level = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        if (level.getBlockState(pos).getBlock() instanceof FishingNetAnchorBlock) return pos;

        BlockPos alt = pos.relative(ctx.getClickedFace());
        if (level.getBlockState(alt).getBlock() instanceof FishingNetAnchorBlock) return alt;

        return pos;
    }

    public FishingNetItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx)
    {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();
        if (player == null) return InteractionResult.PASS;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableFishingNet.get()) return InteractionResult.PASS;

        if (!player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockPos end = resolveAnchorPos(ctx);
        BlockState state = level.getBlockState(end);
        if (!(state.getBlock() instanceof FishingNetAnchorBlock)) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.SUCCESS;

        CompoundTag playerTag = player.getPersistentData();
        long now = level.getGameTime();

        if (playerTag.contains(NET_START_POS) && playerTag.contains(NET_START_TIME))
        {
            long startTime = playerTag.getLong(NET_START_TIME);
            if (now - startTime > NET_SELECT_TIMEOUT_TICKS)
            {
                playerTag.remove(NET_START_POS);
                playerTag.remove(NET_START_TIME);
                player.displayClientMessage(Component.translatable("tfcthings.tooltip.fishing_net.selection_timed_out"), true);
            }
        }

        if (!playerTag.contains(NET_START_POS))
        {
            playerTag.putLong(NET_START_POS, end.asLong());
            playerTag.putLong(NET_START_TIME, now);
            player.displayClientMessage(Component.translatable("tfcthings.tooltip.fishing_net.first_anchor_selected"), true);
            return InteractionResult.CONSUME;
        }

        BlockPos start = BlockPos.of(playerTag.getLong(NET_START_POS));

        if (start.equals(end))
        {
            playerTag.remove(NET_START_POS);
            playerTag.remove(NET_START_TIME);
            player.displayClientMessage(Component.translatable("tfcthings.tooltip.fishing_net.selection_cleared"), true);
            return InteractionResult.CONSUME;
        }

        playerTag.remove(NET_START_POS);
        playerTag.remove(NET_START_TIME);

        FishingNetPlacement.Result preview = FishingNetPlacement.preview((ServerLevel) level, start, end);
        if (preview.hasError())
        {
            showPlacementError(player, preview.status());
            return InteractionResult.CONSUME;
        }

        int needed = preview.requiredSegments();
        if (!player.getAbilities().instabuild)
        {
            int available = countInInventory(player, this);
            if (available < needed)
            {
                player.displayClientMessage(Component.translatable("tfcthings.tooltip.fishing_net.need_nets", needed, available), true);
                return InteractionResult.CONSUME;
            }
        }

        FishingNetPlacement.Result placed = FishingNetPlacement.place((ServerLevel) level, start, end);
        if (placed.hasError())
        {
            showPlacementError(player, placed.status());
            return InteractionResult.CONSUME;
        }

        if (!player.getAbilities().instabuild)
        {
            removeFromInventory(player, this, needed);
        }

        player.displayClientMessage(Component.translatable("tfcthings.tooltip.fishing_net.success"), true);
        return InteractionResult.CONSUME;
    }


}
