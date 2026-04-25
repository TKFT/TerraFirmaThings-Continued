package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.BiomeTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public final class TFCThingsBiomeTags extends BiomeTagsProvider
{
    public TFCThingsBiomeTags(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookup,
        ExistingFileHelper existingFileHelper
    )
    {
        super(output, lookup, TerraFirmaThings.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
    }
}
