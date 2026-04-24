package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;

public final class TFCThingsBlockTags extends BlockTagsProvider
{
    public TFCThingsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper)
    {
        super(output, lookup, TerraFirmaThings.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        TFCThingsBlocks.GEM_DISPLAYS.values().forEach(block -> {
            tag(TFCThingsTags.Blocks.GEM_DISPLAY).add(block.get());
            tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get());
        });

        tag(TFCThingsTags.Blocks.SNOW_SHOES_NEGATE_SLOW)
            .addTag(BlockTags.SNOW)
            .add(TFCBlocks.SNOW_PILE.get())
            .add(Blocks.SNOW)
            .add(Blocks.SNOW_BLOCK)
            .add(Blocks.POWDER_SNOW);

        tag(TFCThingsTags.Blocks.HIKING_BOOTS_NEGATE_SLOW)
            .addOptionalTag(TFCTags.Blocks.NATURAL_REGROWING_PLANTS)
            .addOptionalTag(TFCTags.Blocks.SPREADING_BUSHES)
            .addOptionalTag(TFCTags.Blocks.THORNY_BUSHES);

        tag(TFCThingsTags.Blocks.CRAMPONS_NEGATE_SLIP)
            .addTag(BlockTags.ICE);

        tag(BlockTags.MINEABLE_WITH_AXE)
            .add(TFCThingsBlocks.SNARE.get())
            .add(TFCThingsBlocks.FISHING_NET_ANCHOR.get())
            .add(TFCThingsBlocks.ROPE_LADDER.get())
            .add(TFCThingsBlocks.ROPE_BRIDGE.get());

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(TFCThingsBlocks.GRINDSTONE_BASE.get())
            .add(TFCThingsBlocks.BEAR_TRAP.get());

        tag(TFCThingsTags.Blocks.UNHOOKABLE)
            .addTag(BlockTags.LEAVES)
            .addTag(BlockTags.CROPS)
            .add(Blocks.VINE)
            .add(Blocks.BAMBOO)
            .add(Blocks.COBWEB)
            .add(TFCThingsBlocks.ROPE_LADDER.get())
            .add(TFCThingsBlocks.ROPE_BRIDGE.get());
    }
}
