package com.rustysnail.terrafirmathings.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rustysnail.terrafirmathings.compat.WearableCompat;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

public class TFCThingsColdSweatInsulatorProvider implements DataProvider
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final PackOutput output;

    public TFCThingsColdSweatInsulatorProvider(PackOutput output)
    {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(saveInsulator(cache, "snowshoes",
            "#tfcthings:snowshoes",
            WearableCompat.SNOWSHOES_COLD_SWEAT_COLD,
            WearableCompat.SNOWSHOES_COLD_SWEAT_HEAT));

        futures.add(saveInsulator(cache, "hiking_boots",
            "#tfcthings:hiking_boots",
            WearableCompat.HIKING_BOOTS_COLD_SWEAT_COLD,
            WearableCompat.HIKING_BOOTS_COLD_SWEAT_HEAT));

        futures.add(saveInsulator(cache, "crowns",
            "#tfcthings:crowns",
            WearableCompat.CROWN_COLD_SWEAT_COLD,
            WearableCompat.CROWN_COLD_SWEAT_HEAT));

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    private CompletableFuture<?> saveInsulator(
        CachedOutput cache,
        String name,
        String itemTag,
        double cold,
        double heat)
    {
        JsonObject root = new JsonObject();

        JsonArray requiredMods = new JsonArray();
        requiredMods.add("cold_sweat");
        root.add("required_mods", requiredMods);

        root.addProperty("type", "armor");

        JsonObject item = new JsonObject();
        JsonArray items = new JsonArray();
        items.add(itemTag);
        item.add("items", items);
        root.add("item", item);

        JsonObject insulation = new JsonObject();
        insulation.addProperty("cold", cold);
        insulation.addProperty("heat", heat);
        root.add("insulation", insulation);

        Path path = output.getOutputFolder(PackOutput.Target.DATA_PACK)
            .resolve("tfcthings/cold_sweat/item/insulator/" + name + ".json");

        return DataProvider.saveStable(cache, GSON.toJsonTree(root), path);
    }

    @Override
    public String getName()
    {
        return "TFCThings Cold Sweat Insulators";
    }
}
