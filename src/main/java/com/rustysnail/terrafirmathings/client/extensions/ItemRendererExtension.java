package com.rustysnail.terrafirmathings.client.extensions;

import java.util.function.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

public record ItemRendererExtension(Supplier<BlockEntityWithoutLevelRenderer> renderer) implements IClientItemExtensions
{
    public static ItemRendererExtension cached(com.google.common.base.Supplier<BlockEntityWithoutLevelRenderer> renderer)
    {
        return new ItemRendererExtension(Suppliers.memoize(renderer));
    }

    @Override
    public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer()
    {
        return renderer.get();
    }
}
