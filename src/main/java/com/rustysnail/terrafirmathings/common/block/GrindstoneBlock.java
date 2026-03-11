package com.rustysnail.terrafirmathings.common.block;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.blockentity.GrindstoneBlockEntity;
import com.rustysnail.terrafirmathings.common.item.GrindstoneItem;
import com.rustysnail.terrafirmathings.common.item.WhetstoneItem;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;

public class GrindstoneBlock extends Block implements EntityBlock
{
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty HAS_TOOL = BooleanProperty.create("has_tool");
    public static final BooleanProperty HAS_GRINDSTONE = BooleanProperty.create("has_grindstone");

    public GrindstoneBlock(Properties properties)
    {
        super(properties);
        registerDefaultState(stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(HAS_GRINDSTONE, false)
            .setValue(HAS_TOOL, false));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, HAS_GRINDSTONE, HAS_TOOL);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new GrindstoneBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (!level.isClientSide() && type == TFCThingsBlockEntities.GRINDSTONE.get())
        {
            return (lvl, pos, st, be) -> GrindstoneBlockEntity.serverTick(lvl, pos, st, (GrindstoneBlockEntity) be);
        }
        return null;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()))
        {
            GrindstoneBlockEntity be = (GrindstoneBlockEntity) level.getBlockEntity(pos);
            if (be != null)
            {
                ItemStack tool = be.getTool();
                ItemStack grindstone = be.getGrindstone();
                if (!tool.isEmpty())
                {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, tool);
                }
                if (!grindstone.isEmpty())
                {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, grindstone);
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return InteractionResult.PASS;
        }
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }

        GrindstoneBlockEntity be = (GrindstoneBlockEntity) level.getBlockEntity(pos);
        if (be == null)
        {
            return InteractionResult.PASS;
        }

        if (be.hasTool())
        {
            ItemStack tool = be.getTool();
            if (!player.addItem(tool))
            {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, tool);
            }
            be.setTool(ItemStack.EMPTY);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.2f);
            return InteractionResult.SUCCESS;
        }

        if (be.hasGrindstone())
        {
            ItemStack grindstone = be.getGrindstone();
            if (!player.addItem(grindstone))
            {
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, grindstone);
            }
            be.setGrindstone(ItemStack.EMPTY);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.2f);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide())
        {
            return ItemInteractionResult.SUCCESS;
        }

        GrindstoneBlockEntity be = (GrindstoneBlockEntity) level.getBlockEntity(pos);
        if (be == null)
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        if (!be.hasGrindstone() && stack.getItem() instanceof GrindstoneItem)
        {
            ItemStack toInsert = stack.copyWithCount(1);
            stack.shrink(1);
            be.setGrindstone(toInsert);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 0.8f);
            return ItemInteractionResult.SUCCESS;
        }

        if (!be.hasTool() && be.hasGrindstone() && WhetstoneItem.isSharpenable(stack))
        {
            ItemStack toInsert = stack.copyWithCount(1);
            stack.shrink(1);
            be.setTool(toInsert);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 0.8f);
            return ItemInteractionResult.SUCCESS;
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }
}
