package com.rustysnail.terrafirmathings.compat.curios;

import com.rustysnail.terrafirmathings.compat.curios.client.TFCThingsCuriosClientCompat;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;

public final class TFCThingsCuriosCompat
{
    public static void register(IEventBus modBus)
    {
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            modBus.addListener(TFCThingsCuriosClientCompat::registerRenderers);
        }
    }
}
