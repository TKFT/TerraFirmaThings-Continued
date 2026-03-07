package com.rustysnail.terrafirmathings.data;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.mojang.serialization.Codec;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

public abstract class TFCThingsDataManagerProvider<T> implements DataProvider
{
    private final Codec<T> codec;
    private final String providerName;
    private final PackOutput.PathProvider pathProvider;
    private final CompletableFuture<HolderLookup.Provider> lookup;
    private final Map<ResourceLocation, T> elements = new LinkedHashMap<>();

    protected TFCThingsDataManagerProvider(
        Codec<T> codec,
        String providerName,
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookup,
        String folder
    )
    {
        this.codec = codec;
        this.providerName = providerName;
        this.lookup = lookup;
        this.pathProvider = output.createPathProvider(PackOutput.Target.DATA_PACK, folder);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        return lookup.thenCompose(provider -> {
            addData(provider);
            return CompletableFuture.allOf(
                elements.entrySet().stream()
                    .map(e -> DataProvider.saveStable(cache, provider, codec, e.getValue(), pathProvider.json(e.getKey())))
                    .toArray(CompletableFuture[]::new)
            );
        });
    }

    protected abstract void addData(HolderLookup.Provider provider);

    protected void add(String name, T value)
    {
        elements.put(ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, name), value);
    }

    @Override
    public String getName()
    {
        return "TFCThings " + providerName;
    }
}
