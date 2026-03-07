package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.TFCTags;

public final class TFCThingsBlockTags extends BlockTagsProvider
{
    public TFCThingsBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper)
    {
        super(output, lookup, TerraFirmaThings.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(TFCThingsTags.Blocks.SNOW_SHOES_NEGATE_SLOW)
            .add(net.dries007.tfc.common.blocks.TFCBlocks.SNOW_PILE.get())
            .add(net.minecraft.world.level.block.Blocks.SNOW)
            .add(net.minecraft.world.level.block.Blocks.SNOW_BLOCK)
            .add(net.minecraft.world.level.block.Blocks.POWDER_SNOW);

        tag(TFCThingsTags.Blocks.HIKING_BOOTS_NEGATE_SLOW)
            .addOptionalTag(TFCTags.Blocks.NATURAL_REGROWING_PLANTS)
            .addOptionalTag(TFCTags.Blocks.SPREADING_BUSHES)
            .addOptionalTag(TFCTags.Blocks.THORNY_BUSHES);
    }
}
