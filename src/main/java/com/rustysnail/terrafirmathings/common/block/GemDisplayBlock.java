package com.rustysnail.terrafirmathings.common.block;

import com.mojang.serialization.MapCodec;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.blockentity.GemDisplayBlockEntity;
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
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.FluidProperty;
import net.dries007.tfc.common.fluids.IFluidLoggable;
import net.dries007.tfc.common.fluids.TFCFluids;

public class GemDisplayBlock extends BaseEntityBlock implements IFluidLoggable
{
    public static final MapCodec<GemDisplayBlock> CODEC = simpleCodec(GemDisplayBlock::new);

    @Override
    public MapCodec<GemDisplayBlock> codec()
    {
        return CODEC;
    }

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final FluidProperty FLUID = TFCBlockStateProperties.ALL_WATER;

    public GemDisplayBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(getFluidProperty(), getFluidProperty().keyFor(Fluids.EMPTY)));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        FluidState fluidState = context.getLevel().getFluidState(context.getClickedPos());
        Fluid fluidForLogging = fluidState.getType();

        if (fluidForLogging == TFCFluids.RIVER_WATER.get())
        {
            fluidForLogging = Fluids.WATER;
        }

        return this.defaultBlockState()
            .setValue(FACING, context.getHorizontalDirection())
            .setValue(getFluidProperty(), getFluidProperty().keyForOrEmpty(fluidForLogging));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, getFluidProperty());
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new GemDisplayBlockEntity(pos, state);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()))
        {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof GemDisplayBlockEntity gemDisplay)
            {
                for (int i = 0; i < GemDisplayBlockEntity.MAX_GEMS; i++)
                {
                    ItemStack gem = gemDisplay.getGem(i);
                    if (!gem.isEmpty())
                    {
                        Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, gem);
                    }
                }
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    public FluidProperty getFluidProperty()
    {
        return FLUID;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGemDisplays.get())
        {
            return InteractionResult.PASS;
        }
        if (level.isClientSide())
        {
            return InteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GemDisplayBlockEntity gemDisplay)
        {
            ItemStack removed = gemDisplay.removeLastGem();
            if (!removed.isEmpty())
            {
                if (!player.getInventory().add(removed))
                {
                    player.drop(removed, false);
                }
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.5f, 1.0f);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState,
                                     LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        FluidHelpers.tickFluid(level, currentPos, state);
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        return IFluidLoggable.super.getFluidState(state);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGemDisplays.get())
        {
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        if (level.isClientSide())
        {
            return ItemInteractionResult.SUCCESS;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GemDisplayBlockEntity gemDisplay)
        {
            if (gemDisplay.isGemEligible(stack) && !gemDisplay.isFull())
            {
                if (gemDisplay.addGem(stack))
                {
                    if (!player.isCreative())
                    {
                        stack.shrink(1);
                    }
                    level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0f, 1.0f);
                    return ItemInteractionResult.SUCCESS;
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof GemDisplayBlockEntity gemDisplay)
        {
            int count = gemDisplay.getGemCount();
            return (count * 15) / GemDisplayBlockEntity.MAX_GEMS;
        }
        return 0;
    }
}
