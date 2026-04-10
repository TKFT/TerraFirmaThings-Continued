package com.rustysnail.terrafirmathings.common.block;

import java.util.Locale;
import com.mojang.serialization.MapCodec;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.blockentity.GrainPileBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GrainPileBlock extends BaseEntityBlock
{
    public enum GrainVariant implements StringRepresentable
    {
        BARLEY, CORN, OAT, RICE, RYE, WHEAT, UNKNOWN;

        @Override
        public String getSerializedName()
        {
            return name().toLowerCase(Locale.ROOT);
        }

        public static GrainVariant fromItem(ItemStack stack)
        {
            if (stack.isEmpty()) return UNKNOWN;
            String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
            if (path.contains("barley")) return BARLEY;
            if (path.contains("corn") || path.contains("maize")) return CORN;
            if (path.contains("oat")) return OAT;
            if (path.contains("rice")) return RICE;
            if (path.contains("rye")) return RYE;
            if (path.contains("wheat")) return WHEAT;
            return UNKNOWN;
        }
    }

    public static final MapCodec<GrainPileBlock> CODEC = simpleCodec(GrainPileBlock::new);

    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);

    private static final VoxelShape[] SHAPES = new VoxelShape[9];

    static
    {
        for (int i = 1; i <= 8; i++)
        {
            double height = i * 2.0;
            SHAPES[i] = Block.box(0, 0, 0, 16, height, 16);
        }
    }

    @Override
    public MapCodec<GrainPileBlock> codec()
    {
        return CODEC;
    }

    public GrainPileBlock(Properties properties)
    {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(LAYERS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(LAYERS);
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new GrainPileBlockEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state)
    {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> blockEntityType)
    {
        if (level.isClientSide()) return null;
        return createTickerHelper(blockEntityType, TFCThingsBlockEntities.GRAIN_PILE.get(),
            GrainPileBlockEntity::serverTick);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(LAYERS)];
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(LAYERS)];
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        BlockPos below = pos.below();
        BlockState belowState = level.getBlockState(below);
        return belowState.isFaceSturdy(level, below, Direction.UP)
            || belowState.getBlock() instanceof GrainPileBlock;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                     LevelAccessor level, BlockPos pos, BlockPos neighborPos)
    {
        if (direction == Direction.DOWN && !canSurvive(state, level, pos))
            return Blocks.AIR.defaultBlockState();
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston)
    {
        if (!state.is(newState.getBlock()) && !level.isClientSide())
        {
            if (level.getBlockEntity(pos) instanceof GrainPileBlockEntity grainPile
                && grainPile.getCount() > 0)
            {
                ItemStack toDrop = grainPile.extractGrain(grainPile.getCount());
                if (!toDrop.isEmpty())
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, toDrop);
            }
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                              Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGrainPiles.get())
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (hand != InteractionHand.MAIN_HAND)
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (stack.isEmpty() || !stack.is(TFCThingsTags.Items.GRAIN_PILE_ITEMS))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        if (level.isClientSide()) return ItemInteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof GrainPileBlockEntity be))
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

        if (!be.isEmpty() && !ItemStack.isSameItem(be.getGrainType(), stack))
        {
            player.displayClientMessage(Component.translatable("tfcthings.grain_pile.wrong_type"), true);
            return ItemInteractionResult.FAIL;
        }

        if (be.canInsertGrain(stack))
        {
            ItemStack single = stack.copyWithCount(1);
            int inserted = be.insertGrain(single);
            if (inserted > 0)
            {
                stack.shrink(inserted);
                be.syncBlockState();
                level.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
                return ItemInteractionResult.SUCCESS;
            }
        }

        return ItemInteractionResult.CONSUME;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGrainPiles.get()) return InteractionResult.PASS;
        if (level.isClientSide()) return InteractionResult.SUCCESS;
        if (!(level.getBlockEntity(pos) instanceof GrainPileBlockEntity be)) return InteractionResult.PASS;

        ItemStack extracted = be.extractGrain(16);
        if (!extracted.isEmpty())
        {
            be.syncBlockState();
            if (!player.addItem(extracted))
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, extracted);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.2f);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    public static int formPile(Level level, BlockPos pos, ItemStack stack, GrainPileBlock block)
    {
        if (stack.isEmpty() || !stack.is(TFCThingsTags.Items.GRAIN_PILE_ITEMS)) return 0;
        if (!level.getBlockState(pos).isAir()) return 0;

        BlockState newState = block.defaultBlockState().setValue(LAYERS, 1);
        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof GrainPileBlockEntity be)
        {
            ItemStack copy = stack.copy();
            be.insertGrain(copy);
            int inserted = stack.getCount() - copy.getCount();
            if (inserted > 0)
            {
                be.syncBlockState();
                return inserted;
            }
        }

        level.removeBlock(pos, false);
        return 0;
    }
}
