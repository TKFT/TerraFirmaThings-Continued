package com.rustysnail.terrafirmathings.common.block;

import java.util.ArrayList;
import java.util.List;
import com.mojang.serialization.MapCodec;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.blockentity.RopeBridgeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;

public class RopeBridgeBlock extends BaseEntityBlock implements IItemSize
{

    public static final IntegerProperty OFFSET = IntegerProperty.create("offset", 0, 7);
    public static final BooleanProperty AXIS = BooleanProperty.create("axis");
    public static final MapCodec<RopeBridgeBlock> CODEC = simpleCodec(RopeBridgeBlock::new);

    public RopeBridgeBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(OFFSET, 0)
            .setValue(AXIS, false));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new RopeBridgeBlockEntity(pos, state);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        super.stepOn(level, pos, state, entity);
        if (level.isClientSide()) return;
        if (!(entity instanceof Player player)) return;

        if (level.getBlockEntity(pos) instanceof RopeBridgeBlockEntity be)
        {
            double spd = player.getDeltaMovement().horizontalDistance();
            float strength = (float) Math.min(1.0, 0.25 + spd * 1.5);
            be.recordStepImpulse(level.getGameTime(), strength);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(OFFSET, AXIS);
    }

    @Override
    public Size getSize(ItemStack stack)
    {
        return Size.SMALL;
    }

    @Override
    public Weight getWeight(ItemStack stack)
    {
        return Weight.LIGHT;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec()
    {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.INVISIBLE;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        super.onRemove(state, level, pos, newState, movedByPiston);

        if (level.isClientSide() || movedByPiston || newState.getBlock() instanceof RopeBridgeBlock)
        {
            return;
        }

        boolean axis = state.getValue(AXIS);

        Direction dir1 = axis ? Direction.NORTH : Direction.EAST;
        Direction dir2 = axis ? Direction.SOUTH : Direction.WEST;

        collapseBridgeInDirection(level, pos, dir1, axis);
        collapseBridgeInDirection(level, pos, dir2, axis);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        if (player.isShiftKeyDown())
        {
            if (!level.isClientSide())
            {
                int dir = getPickupDirection(level, pos, state);
                if (dir == 0)
                {
                    return InteractionResult.FAIL;
                }

                ItemStack bridgeStack = new ItemStack(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get(), 0);
                BlockPos next = pos;

                while (level.getBlockState(next).getBlock() instanceof RopeBridgeBlock)
                {
                    bridgeStack.grow(1);
                    BlockState nextState = level.getBlockState(next);
                    int offset = nextState.getValue(OFFSET);

                    BlockPos moved = moveInDirection(next, dir);
                    if (level.getBlockState(moved).getBlock() instanceof RopeBridgeBlock)
                    {
                        level.removeBlock(next, true);
                        next = moved;
                    }
                    else if (offset == 0)
                    {
                        level.removeBlock(next, true);
                        next = moved.below();
                    }
                    else if (offset == 7)
                    {
                        level.removeBlock(next, true);
                        next = moved.above();
                    }
                    else
                    {
                        level.removeBlock(next, true);
                        break;
                    }
                }

                if (!player.getInventory().add(bridgeStack))
                {
                    player.drop(bridgeStack, false);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }
        return InteractionResult.PASS;
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
    {
        if (stack.is(Items.STICK))
        {
            if (!level.isClientSide())
            {
                int newOffset = (state.getValue(OFFSET) + 1) % 8;
                level.setBlock(pos, state.setValue(OFFSET, newOffset), 2);
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide());
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        int offset = state.getValue(OFFSET);
        double minY = offset * 0.125;
        double maxY = minY + (3 * 0.0625);
        return Block.box(0, minY * 16, 0, 16, maxY * 16, 16);
    }

    private BlockPos moveInDirection(BlockPos pos, int dir)
    {
        return switch (dir)
        {
            case 1 -> pos.north();
            case 2 -> pos.south();
            case 3 -> pos.east();
            default -> pos.west();
        };
    }

    private int getPickupDirection(Level level, BlockPos pos, BlockState state)
    {
        List<Integer> dirs = new ArrayList<>();
        BlockPos side1;
        BlockPos side2;

        if (state.getValue(AXIS))
        {
            side1 = pos.north();
            side2 = pos.south();
        }
        else
        {
            side1 = pos.east();
            side2 = pos.west();
        }

        if (level.getBlockState(side1).getBlock() instanceof RopeBridgeBlock)
        {
            dirs.add(1);
        }
        if (level.getBlockState(side2).getBlock() instanceof RopeBridgeBlock)
        {
            dirs.add(2);
        }

        int offset = state.getValue(OFFSET);
        if (offset == 0)
        {
            if (level.getBlockState(side1.below()).getBlock() instanceof RopeBridgeBlock)
            {
                dirs.add(1);
            }
            if (level.getBlockState(side2.below()).getBlock() instanceof RopeBridgeBlock)
            {
                dirs.add(2);
            }
        }
        if (offset == 7)
        {
            if (level.getBlockState(side1.above()).getBlock() instanceof RopeBridgeBlock)
            {
                dirs.add(1);
            }
            if (level.getBlockState(side2.above()).getBlock() instanceof RopeBridgeBlock)
            {
                dirs.add(2);
            }
        }

        if (dirs.size() == 1)
        {
            return dirs.getFirst() + (state.getValue(AXIS) ? 0 : 2);
        }
        return 0;
    }

    private void collapseBridgeInDirection(Level level, BlockPos startPos, Direction direction, boolean axis)
    {
        BlockPos currentPos = startPos.relative(direction);

        while (true)
        {
            BlockState currentState = level.getBlockState(currentPos);

            if (currentState.getBlock() instanceof RopeBridgeBlock && currentState.getValue(AXIS) == axis)
            {
                popResource(level, currentPos, new ItemStack(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()));
                level.removeBlock(currentPos, false);
                currentPos = currentPos.relative(direction);
                continue;
            }

            BlockState aboveState = level.getBlockState(currentPos.above());
            if (aboveState.getBlock() instanceof RopeBridgeBlock && aboveState.getValue(AXIS) == axis)
            {
                popResource(level, currentPos.above(), new ItemStack(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()));
                level.removeBlock(currentPos.above(), false);
                currentPos = currentPos.relative(direction).above();
                continue;
            }

            BlockState belowState = level.getBlockState(currentPos.below());
            if (belowState.getBlock() instanceof RopeBridgeBlock && belowState.getValue(AXIS) == axis)
            {
                popResource(level, currentPos.below(), new ItemStack(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()));
                level.removeBlock(currentPos.below(), false);
                currentPos = currentPos.relative(direction).below();
                continue;
            }

            break;
        }
    }
}
