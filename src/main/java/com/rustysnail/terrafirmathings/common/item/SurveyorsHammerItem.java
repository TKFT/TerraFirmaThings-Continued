package com.rustysnail.terrafirmathings.common.item;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.LevelTier;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.ToolItem;
import net.dries007.tfc.common.recipes.CollapseRecipe;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.Support;
import net.dries007.tfc.world.chunkdata.ChunkData;
import net.dries007.tfc.world.chunkdata.RockData;
import net.dries007.tfc.world.settings.RockSettings;

public class SurveyorsHammerItem extends ToolItem
{
    private static final int COOLDOWN = 10;
    private static final int SAFETY_CHECK_RADIUS = 4;
    private static final int SAFETY_CHECK_HEIGHT = 2;

    private static Map<Block, Rock> BLOCK_TO_ROCK = null;

    private static Component rockDisplayName(Rock rock)
    {
        return Component.translatable("tfcthings.rock." + rock.getSerializedName());
    }

    private static Component formatRockList(List<Rock> layers)
    {
        MutableComponent result = Component.empty();
        for (int i = 0; i < layers.size(); i++)
        {
            if (i > 0)
            {
                result.append(Component.translatable("tfcthings.tooltip.prohammer_rock_separator"));
            }
            result.append(rockDisplayName(layers.get(i)));
        }
        return result;
    }

    private static synchronized void buildBlockToRockMap()
    {
        if (BLOCK_TO_ROCK != null) return;
        Map<Block, Rock> map = new IdentityHashMap<>();
        TFCBlocks.ROCK_BLOCKS.forEach((rock, blockTypes) ->
            blockTypes.forEach((type, id) ->
                map.put(id.get(), rock)));
        BLOCK_TO_ROCK = map;
    }

    private final int tierLevel;

    public SurveyorsHammerItem(LevelTier tier, Properties properties)
    {
        super(tier, TFCTags.Blocks.MINEABLE_WITH_PROPICK,
            properties
                .durability(tier.getUses() / 4)
                .attributes(ToolItem.productAttributes(tier, 0.5f, -2.8f)));
        this.tierLevel = tier.level();
    }

    @Override
    public InteractionResult useOn(UseOnContext context)
    {
        final Level level = context.getLevel();
        final Player player = context.getPlayer();
        final BlockPos pos = context.getClickedPos();
        final BlockState state = level.getBlockState(pos);

        if (player == null) return InteractionResult.PASS;
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableProspectorsHammers.get()) return InteractionResult.PASS;

        final SoundType sound = state.getSoundType(level, pos, player);
        level.playSound(player, pos, sound.getHitSound(), SoundSource.PLAYERS, sound.getVolume(), sound.getPitch());

        if (player instanceof ServerPlayer)
        {
            if (player.isShiftKeyDown())
            {
                scanRockLayers(level, pos, player);
                Helpers.damageItem(context.getItemInHand(), 20, player, context.getHand());
            }
            else
            {
                checkCaveInSafety(level, pos, player);
                Helpers.damageItem(context.getItemInHand(), player, context.getHand());
            }
        }

        player.getCooldowns().addCooldown(this, COOLDOWN);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }


    private void checkCaveInSafety(Level level, BlockPos pos, Player player)
    {
        boolean canCollapse = CollapseRecipe.canCollapse(level.getBlockState(pos));
        boolean hasRubbleAbove = level.getBlockState(pos.above()).getBlock() instanceof FallingBlock;

        Component msg;
        if (!canCollapse)
        {
            msg = Component.translatable("tfcthings.tooltip.prohammer_na");
        }
        else
        {
            BlockPos from = pos.offset(-SAFETY_CHECK_RADIUS, -SAFETY_CHECK_HEIGHT, -SAFETY_CHECK_RADIUS);
            BlockPos to = pos.offset(SAFETY_CHECK_RADIUS, SAFETY_CHECK_HEIGHT, SAFETY_CHECK_RADIUS);
            Set<BlockPos> unsupported = Support.findUnsupportedPositions(level, from, to);

            int unsafeCount = 0;
            for (BlockPos unsupportedPos : unsupported)
            {
                if (CollapseRecipe.canStartCollapse(level, unsupportedPos))
                    unsafeCount++;
            }
            if (tierLevel > 3)
            {
                if (unsafeCount == 0)
                    msg = Component.translatable("tfcthings.tooltip.prohammer_safe");
                else if (unsafeCount <= 3)
                    msg = Component.translatable("tfcthings.tooltip.prohammer_minor");
                else if (unsafeCount <= 8)
                    msg = Component.translatable("tfcthings.tooltip.prohammer_unsafe");
                else
                    msg = Component.translatable("tfcthings.tooltip.prohammer_critical");
            }
            else {
                if (unsafeCount > 0){
                    msg = Component.translatable("tfcthings.tooltip.prohammer_unsafe_low_tier");
                }
                else
                {
                    msg = Component.translatable("tfcthings.tooltip.prohammer_safe");
                }
            }
        }

        if (hasRubbleAbove)
            msg = Component.empty().append(msg).append(Component.translatable("tfcthings.tooltip.prohammer_rubble_above"));

        player.displayClientMessage(msg, true);
    }

    private void scanRockLayers(Level level, BlockPos pos, Player player)
    {
        if (BLOCK_TO_ROCK == null) buildBlockToRockMap();

        int maxLayers = 1 + tierLevel;
        int maxDepth = maxLayers * 60;

        ChunkData chunkData = ChunkData.get(level, pos);
        List<Rock> layers = new ArrayList<>();

        if (chunkData != ChunkData.EMPTY)
        {
            RockData rockData = chunkData.getRockData();
            Rock lastRock = null;

            for (int depth = 0; depth <= maxDepth; depth += 10)
            {
                int y = pos.getY() - depth;
                if (y < level.getMinBuildHeight()) break;
                if (layers.size() >= maxLayers) break;

                RockSettings settings;
                try
                {
                    settings = rockData.getRock(pos.getX(), y, pos.getZ());
                }
                catch (Exception | AssertionError e)
                {
                    break;
                }

                Rock rock = BLOCK_TO_ROCK.get(settings.raw());
                if (rock != null && !rock.equals(lastRock))
                {
                    layers.add(rock);
                    lastRock = rock;
                }
            }
        }

        if (layers.isEmpty())
        {
            player.displayClientMessage(
                Component.translatable("tfcthings.tooltip.prohammer_no_rocks"), true);
            return;
        }

        player.displayClientMessage(
            Component.translatable("tfcthings.tooltip.prohammer_rock_layers", formatRockList(layers)), true);
    }
}
