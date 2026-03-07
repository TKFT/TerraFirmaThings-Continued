package com.rustysnail.terrafirmathings.common;

import java.util.function.Supplier;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.blockentity.BearTrapBlockEntity;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetBlockEntity;
import com.rustysnail.terrafirmathings.common.blockentity.GemDisplayBlockEntity;
import com.rustysnail.terrafirmathings.common.blockentity.GrindstoneBlockEntity;
import com.rustysnail.terrafirmathings.common.blockentity.RopeBridgeBlockEntity;
import com.rustysnail.terrafirmathings.common.blockentity.SnareBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TFCThingsBlockEntities
{

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, TerraFirmaThings.MOD_ID);

    @SuppressWarnings("DataFlowIssue")
    private static <T extends BlockEntity> BlockEntityType<T> buildWithoutDataFixer(BlockEntityType.Builder<T> builder)
    {
        return builder.build(null);
    }

    public static final Supplier<BlockEntityType<BearTrapBlockEntity>> BEAR_TRAP =
        BLOCK_ENTITIES.register("bear_trap", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(BearTrapBlockEntity::new, TFCThingsBlocks.BEAR_TRAP.get())));

    public static final Supplier<BlockEntityType<SnareBlockEntity>> SNARE =
        BLOCK_ENTITIES.register("snare", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(SnareBlockEntity::new, TFCThingsBlocks.SNARE.get())));


    public static final Supplier<BlockEntityType<FishingNetAnchorBlockEntity>> FISHING_NET_ANCHOR =
        BLOCK_ENTITIES.register("fishing_net_anchor", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(FishingNetAnchorBlockEntity::new, TFCThingsBlocks.FISHING_NET_ANCHOR.get())));

    public static final Supplier<BlockEntityType<RopeBridgeBlockEntity>> ROPE_BRIDGE =
        BLOCK_ENTITIES.register("rope_bridge", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(RopeBridgeBlockEntity::new, TFCThingsBlocks.ROPE_BRIDGE.get())));

    public static final Supplier<BlockEntityType<FishingNetBlockEntity>> FISHING_NET =
        BLOCK_ENTITIES.register("fishing_net", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(FishingNetBlockEntity::new, TFCThingsBlocks.FISHING_NET.get())));

    public static final Supplier<BlockEntityType<GrindstoneBlockEntity>> GRINDSTONE =
        BLOCK_ENTITIES.register("grindstone", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(GrindstoneBlockEntity::new,
                TFCThingsBlocks.GRINDSTONE.get())));

    public static final Supplier<BlockEntityType<GemDisplayBlockEntity>> GEM_DISPLAY =
        BLOCK_ENTITIES.register("gem_display", () ->
            buildWithoutDataFixer(BlockEntityType.Builder.of(GemDisplayBlockEntity::new,
                TFCThingsBlocks.GEM_DISPLAYS.values().stream()
                    .map(DeferredBlock::get)
                    .toArray(Block[]::new))));


}
