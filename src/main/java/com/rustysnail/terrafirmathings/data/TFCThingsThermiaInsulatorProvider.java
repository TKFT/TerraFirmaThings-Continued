package com.rustysnail.terrafirmathings.data;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.rustysnail.terrafirmathings.compat.WearableCompat;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class TFCThingsThermiaInsulatorProvider implements DataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final PackOutput output;

    public TFCThingsThermiaInsulatorProvider(PackOutput output)
    {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        JsonObject values = new JsonObject();

        JsonObject snowshoes = new JsonObject();
        snowshoes.addProperty("insulation_modifier", WearableCompat.SNOWSHOES_THERMIA);
        values.add("#tfcthings:snowshoes", snowshoes);

        JsonObject hikingBoots = new JsonObject();
        hikingBoots.addProperty("insulation_modifier", WearableCompat.HIKING_BOOTS_THERMIA);
        values.add("#tfcthings:hiking_boots", hikingBoots);

        JsonObject crowns = new JsonObject();
        crowns.addProperty("insulation_modifier", WearableCompat.CROWN_THERMIA);
        values.add("#tfcthings:crowns", crowns);

        JsonObject root = new JsonObject();
        root.add("values", values);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
            .resolve("thermia/data_maps/item/item_insulation.json");

        return DataProvider.saveStable(cache, GSON.toJsonTree(root), path);
    }

    @Override
    public String getName()
    {
        return "TFCThings Thermia Insulators";
    }
}
