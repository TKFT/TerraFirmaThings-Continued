package com.rustysnail.terrafirmathings.compat.jei;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;


@JeiPlugin
public class TFCThingsJEIPlugin implements IModPlugin
{

    private static final ResourceLocation PLUGIN_ID = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid()
    {
        return PLUGIN_ID;
    }
}
