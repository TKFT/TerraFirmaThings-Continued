package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.entities.TFCEntities;

public final class TFCThingsEntityTypeTags extends EntityTypeTagsProvider
{
    public TFCThingsEntityTypeTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, ExistingFileHelper existingFileHelper)
    {
        super(output, lookup, TerraFirmaThings.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(TFCThingsTags.Entities.SNARE_CATCHABLE)
            .add(EntityType.RABBIT)
            .add(EntityType.CHICKEN)
            .add(TFCEntities.RABBIT.get())
            .add(TFCEntities.QUAIL.get())
            .add(TFCEntities.DUCK.get())
            .add(TFCEntities.TURKEY.get())
            .add(TFCEntities.CHICKEN.get())
            .add(TFCEntities.GROUSE.get())
            .add(TFCEntities.PHEASANT.get())
            .add(TFCEntities.RAT.get())
            .add(TFCEntities.LEMMING.get())
            .add(TFCEntities.JERBOA.get())
            .add(TFCEntities.PEAFOWL.get());

        tag(TFCThingsTags.Entities.FISHING_NET_CATCHABLE)
            .addOptionalTag(net.dries007.tfc.common.TFCTags.Entities.SMALL_FISH);

        tag(TFCThingsTags.Entities.BEAR_TRAP_BREAKOUT)
            .addOptionalTag(net.dries007.tfc.common.TFCTags.Entities.LAND_PREDATORS);
    }
}
