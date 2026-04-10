package com.rustysnail.terrafirmathings.compat.curios.client;

import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public final class TFCThingsCuriosClientCompat
{

    public static void registerRenderers(FMLClientSetupEvent event)
    {
        event.enqueueWork(() ->
            TFCThingsItems.ALL_CROWNS.forEach(di ->
                CuriosRendererRegistry.register(di.get(), CrownCurioRenderer::new)));
    }
}
