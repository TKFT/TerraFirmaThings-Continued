package com.rustysnail.terrafirmathings.compat.jade;

import com.rustysnail.terrafirmathings.common.block.FishingNetAnchorBlock;
import com.rustysnail.terrafirmathings.common.block.GrindstoneBlock;
import com.rustysnail.terrafirmathings.common.block.SnareBlock;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class TFCThingsJadePlugin implements IWailaPlugin
{
    @Override
    public void register(IWailaCommonRegistration registration)
    {
        registration.registerBlockDataProvider(FishingNetAnchorProvider.INSTANCE, FishingNetAnchorBlockEntity.class);
        registration.registerItemStorage(FishingNetInventoryProvider.INSTANCE, FishingNetAnchorBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration)
    {
        registration.registerBlockComponent(GrindstoneProvider.INSTANCE, GrindstoneBlock.class);
        registration.registerBlockComponent(FishingNetAnchorProvider.INSTANCE, FishingNetAnchorBlock.class);
        registration.registerBlockComponent(SnareProvider.INSTANCE, SnareBlock.class);
        registration.registerItemStorageClient(FishingNetInventoryProvider.INSTANCE);
    }
}
