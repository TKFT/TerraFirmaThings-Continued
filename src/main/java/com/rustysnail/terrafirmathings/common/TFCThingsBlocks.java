package com.rustysnail.terrafirmathings.common;

import java.util.LinkedHashMap;
import java.util.Map;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.block.BearTrapBlock;
import com.rustysnail.terrafirmathings.common.block.FishingNetAnchorBlock;
import com.rustysnail.terrafirmathings.common.block.FishingNetBlock;
import com.rustysnail.terrafirmathings.common.block.GemDisplayBlock;
import com.rustysnail.terrafirmathings.common.block.GrindstoneBlock;
import com.rustysnail.terrafirmathings.common.block.RopeBridgeBlock;
import com.rustysnail.terrafirmathings.common.block.RopeLadderBlock;
import com.rustysnail.terrafirmathings.common.block.SnareBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.blocks.rock.Rock;

public final class TFCThingsBlocks
{

    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(TerraFirmaThings.MOD_ID);

    public static final DeferredBlock<RopeBridgeBlock> ROPE_BRIDGE = BLOCKS.register("rope_bridge",
        () -> new RopeBridgeBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()));

    public static final DeferredBlock<RopeLadderBlock> ROPE_LADDER = BLOCKS.register("rope_ladder",
        () -> new RopeLadderBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.5F)
            .sound(SoundType.LADDER)
            .noOcclusion()));

    public static final DeferredBlock<BearTrapBlock> BEAR_TRAP = BLOCKS.register("bear_trap",
        () -> new BearTrapBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.METAL)
            .strength(10.0F, 10.0F)
            .sound(SoundType.METAL)
            .noOcclusion()
            .requiresCorrectToolForDrops()));

    public static final DeferredBlock<SnareBlock> SNARE = BLOCKS.register("snare",
        () -> new SnareBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.5F)
            .sound(SoundType.WOOD)
            .noOcclusion()));


    public static final DeferredBlock<FishingNetBlock> FISHING_NET = BLOCKS.register("fishing_net",
        () -> new FishingNetBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.3F)
            .sound(SoundType.WOOL)
            .noCollission()
            .noOcclusion()
            .dynamicShape()));

    public static final DeferredBlock<FishingNetAnchorBlock> FISHING_NET_ANCHOR = BLOCKS.register("fishing_net_anchor",
        () -> new FishingNetAnchorBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(0.8F)
            .sound(SoundType.WOOD)
            .noOcclusion()));

    public static final DeferredBlock<GrindstoneBlock> GRINDSTONE = BLOCKS.register("grindstone",
        () -> new GrindstoneBlock(BlockBehaviour.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(3.5F)
            .sound(SoundType.STONE)
            .noOcclusion()
            .requiresCorrectToolForDrops()));

    public static final Map<Rock, DeferredBlock<GemDisplayBlock>> GEM_DISPLAYS = new LinkedHashMap<>();

    static
    {
        for (Rock rock : Rock.values())
        {
            GEM_DISPLAYS.put(rock, BLOCKS.register("gem_display/" + rock.getSerializedName(),
                () -> new GemDisplayBlock(BlockBehaviour.Properties.of()
                    .mapColor(rock.color())
                    .strength(1.5F)
                    .sound(SoundType.STONE)
                    .noOcclusion()
                    .requiresCorrectToolForDrops())));
        }
    }

}
