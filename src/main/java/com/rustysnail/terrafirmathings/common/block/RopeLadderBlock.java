package com.rustysnail.terrafirmathings.common.block;

import java.util.ArrayList;
import java.util.List;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RopeLadderBlock extends Block
{

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    protected static final VoxelShape EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
    protected static final VoxelShape WEST_AABB = Block.box(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
    protected static final VoxelShape SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
    protected static final VoxelShape NORTH_AABB = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);

    public RopeLadderBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState belowState = level.getBlockState(pos.below());
        if (belowState.getBlock() instanceof RopeLadderBlock)
        {
            return this.defaultBlockState().setValue(FACING, belowState.getValue(FACING));
        }

        Direction clickedFace = context.getClickedFace();
        if (clickedFace.getAxis().isHorizontal())
        {
            BlockPos attachPos = pos.relative(clickedFace.getOpposite());
            if (canAttachTo(level, attachPos, clickedFace))
            {
                return this.defaultBlockState().setValue(FACING, clickedFace);
            }
        }

        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            if (canAttachTo(level, pos.relative(dir.getOpposite()), dir))
            {
                return this.defaultBlockState().setValue(FACING, dir);
            }
        }

        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (level.isClientSide() || !(placer instanceof Player player))
        {
            return;
        }
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableRopeLadder.get())
        {
            return;
        }

        int maxLength = TFCThingsConfig.ITEMS.ROPE_LADDER.maxLength.get();
        Direction facing = state.getValue(FACING);
        BlockPos nextPos = pos.below();
        int placed = 0;

        while (placed < maxLength && nextPos.getY() >= level.getMinBuildHeight())
        {
            BlockState nextState = level.getBlockState(nextPos);

            if (!nextState.canBeReplaced() || !nextState.getFluidState().isEmpty())
            {
                break;
            }

            ItemStack ladderStack = findLadderInInventory(player);
            if (ladderStack.isEmpty())
            {
                break;
            }

            level.setBlock(nextPos, this.defaultBlockState().setValue(FACING, facing), 3);

            if (!player.getAbilities().instabuild)
            {
                ladderStack.shrink(1);
            }

            placed++;
            nextPos = nextPos.below();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING);
    }

    @Override
    public boolean isLadder(BlockState state, LevelReader level, BlockPos pos, LivingEntity entity)
    {
        return TFCThingsConfig.ITEMS.MASTER_LIST.enableRopeLadder.get();
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction dir, BlockState neighbor, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (!canSurvive(state, level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, dir, neighbor, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (level.isClientSide() || movedByPiston || newState.getBlock() instanceof RopeLadderBlock)
        {
            return;
        }

        BlockPos below = pos.below();
        while (level.getBlockState(below).getBlock() instanceof RopeLadderBlock)
        {
            popResource(level, below, new ItemStack(TFCThingsItems.ROPE_LADDER.get()));
            level.removeBlock(below, false);
            below = below.below();
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (player.isShiftKeyDown())
        {
            if (level.getBlockState(pos.above()).getBlock() instanceof RopeLadderBlock)
            {
                return InteractionResult.FAIL;
            }

            if (!level.isClientSide())
            {
                BlockPos next = pos;
                List<BlockPos> chain = new ArrayList<>();

                while (level.getBlockState(next).getBlock() instanceof RopeLadderBlock)
                {
                    chain.add(next);
                    next = next.below();
                }

                for (int i = chain.size() - 1; i >= 0; i--)
                {
                    giveLadderToPlayer(player);
                    level.removeBlock(chain.get(i), false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        if (level.getBlockState(pos.above()).getBlock() instanceof RopeLadderBlock)
        {
            return true;
        }
        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing.getOpposite());
        BlockState supportState = level.getBlockState(supportPos);
        return supportState.isFaceSturdy(level, supportPos, facing);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return switch (state.getValue(FACING))
        {
            case SOUTH -> SOUTH_AABB;
            case WEST -> WEST_AABB;
            case EAST -> EAST_AABB;
            default -> NORTH_AABB;
        };
    }

    private boolean canAttachTo(LevelReader level, BlockPos pos, Direction direction)
    {
        BlockState state = level.getBlockState(pos);
        return state.isFaceSturdy(level, pos, direction);
    }

    private ItemStack findLadderInInventory(Player player)
    {
        for (int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(TFCThingsItems.ROPE_LADDER.get()))
            {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }

    private void giveLadderToPlayer(Player player)
    {
        ItemStack ladderStack = new ItemStack(TFCThingsItems.ROPE_LADDER.get(), 1);
        if (!player.getInventory().add(ladderStack))
        {
            player.drop(ladderStack, false);
        }
    }

}
