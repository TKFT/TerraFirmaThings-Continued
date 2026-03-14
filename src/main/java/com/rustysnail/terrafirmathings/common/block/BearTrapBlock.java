package com.rustysnail.terrafirmathings.common.block;

import com.mojang.serialization.MapCodec;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.blockentity.BearTrapBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class BearTrapBlock extends BaseEntityBlock
{
    public static final MapCodec<BearTrapBlock> CODEC = simpleCodec(BearTrapBlock::new);

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty CLOSED = BooleanProperty.create("closed");
    public static final BooleanProperty BURIED = BooleanProperty.create("buried");

    protected static final VoxelShape TRAP_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    @Override
    public MapCodec<BearTrapBlock> codec()
    {
        return CODEC;
    }

    public BearTrapBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(FACING, Direction.NORTH)
            .setValue(CLOSED, false)
            .setValue(BURIED, false));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool)
    {
        if (blockEntity instanceof BearTrapBlockEntity)
        {
            if (state.getValue(CLOSED))
            {
                double breakChance = TFCThingsConfig.ITEMS.BEAR_TRAP.breakChance.get();
                if (Math.random() < breakChance)
                {
                    level.playSound(null, pos, SoundEvents.ITEM_BREAK, SoundSource.BLOCKS, 1.0f, 0.8f);
                    return;
                }
            }
        }
        super.playerDestroy(level, player, pos, state, blockEntity, tool);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(FACING, CLOSED, BURIED);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BearTrapBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType)
    {
        return level.isClientSide() ? null : createTickerHelper(blockEntityType, TFCThingsBlockEntities.BEAR_TRAP.get(),
            (lvl, pos, st, be) -> be.serverTick());
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos))
        {
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
    {
        ItemStack heldItem = player.getMainHandItem();

        if (heldItem.getItem() instanceof ShovelItem)
        {
            boolean newBuried = !state.getValue(BURIED);
            if (newBuried)
            {
                BlockState belowState = level.getBlockState(pos.below());
                if (!belowState.is(BlockTags.MINEABLE_WITH_SHOVEL))
                {
                    return InteractionResult.PASS;
                }
            }
            if (!level.isClientSide())
            {
                level.setBlock(pos, state.setValue(BURIED, newBuried), 3);
                level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                heldItem.hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        if (player.isShiftKeyDown() && state.getValue(CLOSED))
        {
            if (!level.isClientSide())
            {
                BlockEntity be = level.getBlockEntity(pos);
                if (be instanceof BearTrapBlockEntity trapEntity)
                {
                    trapEntity.reset();
                    level.setBlock(pos, state.setValue(CLOSED, false), 3);
                    level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.isFaceSturdy(level, below, Direction.UP);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return TRAP_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return Shapes.empty();
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        if (level.isClientSide() || !(entity instanceof LivingEntity livingEntity))
        {
            return;
        }

        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof BearTrapBlockEntity trapEntity))
        {
            return;
        }

        if (!state.getValue(CLOSED))
        {
            triggerTrap(level, pos, state, livingEntity, trapEntity);
        }
    }

    private void triggerTrap(Level level, BlockPos pos, BlockState state, LivingEntity entity, BearTrapBlockEntity trapEntity)
    {
        int debuffDuration = TFCThingsConfig.ITEMS.BEAR_TRAP.debuffDuration.get();
        if (debuffDuration > 0)
        {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, debuffDuration, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, debuffDuration, 2));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, debuffDuration, 1));
        }

        double fixedDamage = TFCThingsConfig.ITEMS.BEAR_TRAP.fixedDamage.get();
        double healthCut = TFCThingsConfig.ITEMS.BEAR_TRAP.healthCut.get();

        if (fixedDamage > 0)
        {
            entity.hurt(level.damageSources().generic(), (float) fixedDamage);
        }
        else if (healthCut > 0)
        {
            entity.hurt(level.damageSources().generic(), entity.getHealth() / (float) healthCut);
        }

        trapEntity.setCapturedEntity(entity);
        entity.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        level.setBlock(pos, state.setValue(CLOSED, true), 3);
        level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 2.0f, 0.4f);
    }
}
