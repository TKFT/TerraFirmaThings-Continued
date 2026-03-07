package com.rustysnail.terrafirmathings.common.block;

import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import com.rustysnail.terrafirmathings.common.util.FishingNetUtil;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;

public class FishingNetAnchorBlock extends Block implements EntityBlock, IFluidLoggable
{

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final FluidProperty FLUID = TFCBlockStateProperties.ALL_WATER;

    private static final VoxelShape POST = Block.box(6, 0, 6, 10, 16, 10);

    private static void clearLinkPair(Level level, FishingNetAnchorBlockEntity anchor)
    {
        BlockPos otherPos = anchor.getOtherAnchor();
        anchor.clearLink();
        if (otherPos != null)
        {
            BlockEntity other = level.getBlockEntity(otherPos);
            if (other instanceof FishingNetAnchorBlockEntity otherAnchor)
            {
                otherAnchor.clearLink();
            }
        }
    }

    private static void dropNetItems(Level level, BlockPos pos, int count)
    {
        int remaining = count;
        while (remaining > 0)
        {
            int stackSize = Math.min(64, remaining);
            popResource(level, pos, new ItemStack(TFCThingsItems.FISHING_NET_ITEM.get(), stackSize));
            remaining -= stackSize;
        }
    }

    private static void giveNetItemsToPlayer(Player player, int count)
    {
        int remaining = count;
        while (remaining > 0)
        {
            int stackSize = Math.min(64, remaining);
            ItemStack netStack = new ItemStack(TFCThingsItems.FISHING_NET_ITEM.get(), stackSize);
            if (!player.getInventory().add(netStack))
            {
                player.drop(netStack, false);
            }
            remaining -= stackSize;
        }
    }


    public FishingNetAnchorBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(AXIS, Direction.Axis.X)
            .setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx)
    {
        BlockPos pos = ctx.getClickedPos();
        Level level = ctx.getLevel();
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);

        if (level.getFluidState(pos).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE)) return null;

        if (!belowState.isFaceSturdy(level, below, Direction.UP)) return null;

        boolean nearWater =
            level.getFluidState(pos).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.north()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.south()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.east()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.west()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.below()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.north().below()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.south().below()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.east().below()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE) ||
                level.getFluidState(pos.west().below()).is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE);

        if (!nearWater) return null;

        return defaultBlockState()
            .setValue(AXIS, ctx.getHorizontalDirection().getAxis())
            .setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(AXIS, getFluidProperty());
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FishingNetAnchorBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
    {
        if (level.isClientSide() || blockEntityType != TFCThingsBlockEntities.FISHING_NET_ANCHOR.get())
        {
            return null;
        }
        return (lvl, pos, st, be) -> FishingNetAnchorBlockEntity.serverTick(lvl, pos, st, (FishingNetAnchorBlockEntity) be);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FishingNetAnchorBlockEntity anchor)
            {
                anchor.dropAllItems(level, pos);
                clearLinkPair(level, anchor);
            }

            Direction.Axis axis = state.getValue(AXIS);
            int removed = FishingNetUtil.removeConnectedNets(level, pos, axis);
            if (removed > 0)
            {
                dropNetItems(level, pos, removed);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit)
    {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (player.isShiftKeyDown())
        {
            Direction.Axis axis = state.getValue(AXIS);
            int removed = FishingNetUtil.removeConnectedNets(level, pos, axis);
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof FishingNetAnchorBlockEntity anchor)
            {
                clearLinkPair(level, anchor);
            }
            if (removed > 0)
            {
                giveNetItemsToPlayer(player, removed);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.0f);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.CONSUME;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof FishingNetAnchorBlockEntity net)
        {
            player.openMenu(net);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return Shapes.empty();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return POST;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return POST;
    }
}
