package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.TFCTags;

public final class TFCThingsFluidTags extends FluidTagsProvider
{
    public TFCThingsFluidTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper)
    {
        super(output, lookup, TerraFirmaThings.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE)
            .addOptionalTag(TFCTags.Fluids.ANY_FRESH_WATER)
            .addOptional(ResourceLocation.fromNamespaceAndPath("tfc", "salt_water"))
            .addOptional(ResourceLocation.fromNamespaceAndPath("tfc", "flowing_salt_water"));
    }
}
