package com.rustysnail.terrafirmathings.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.block.GrainPileBlock;
import javax.annotation.Nullable;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.common.blocks.rock.Rock;

final class TFCThingsBlockModelAndStateProvider implements DataProvider
{
    private final PackOutput output;

    TFCThingsBlockModelAndStateProvider(PackOutput output)
    {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        gemDisplays(cache, futures);
        grainPile(cache, futures);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName()
    {
        return "TFCThings Block Models and Blockstates";
    }

    private void gemDisplays(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (Rock rock : TFCThingsBlocks.GEM_DISPLAYS.keySet())
        {
            String rockName = rock.getSerializedName();
            futures.add(saveJson(cache, "models/block/gem_display/" + rockName + ".json",
                gemDisplayBlockModel(rockName)));
            futures.add(saveJson(cache, "blockstates/gem_display/" + rockName + ".json",
                gemDisplayBlockState(rockName)));
        }
    }

    private static JsonObject gemDisplayBlockModel(String rock)
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("all", "tfc:block/rock/smooth/" + rock);

        JsonObject json = new JsonObject();
        json.addProperty("parent", TerraFirmaThings.MOD_ID + ":block/gem_display/gem_display");
        json.add("textures", textures);
        return json;
    }

    private static JsonObject gemDisplayBlockState(String rock)
    {
        String modelId = TerraFirmaThings.MOD_ID + ":block/gem_display/" + rock;

        JsonObject north = modelVariant(modelId, 0);
        JsonObject east = modelVariant(modelId, 90);
        JsonObject south = modelVariant(modelId, 180);
        JsonObject west = modelVariant(modelId, 270);

        JsonObject variants = new JsonObject();
        variants.add("facing=east", east);
        variants.add("facing=north", north);
        variants.add("facing=south", south);
        variants.add("facing=west", west);

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    //Grain pile

    private void grainPile(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (int layer = 1; layer <= 8; layer++)
        {
            futures.add(saveJson(cache,
                "models/block/grain_pile/grain_pile_" + layer + ".json",
                grainPileBaseModel(layer)));
        }

        for (GrainPileBlock.GrainVariant grain : GrainPileBlock.GrainVariant.values())
        {
            if (grain == GrainPileBlock.GrainVariant.UNKNOWN) continue;
            String grainName = grain.getSerializedName();
            String grainTex = TerraFirmaThings.MOD_ID + ":block/grain_pile/" + grainName;
            for (int layer = 1; layer <= 8; layer++)
            {
                futures.add(saveJson(cache,
                    "models/block/grain_pile/" + grainName + "_" + layer + ".json",
                    grainPileDerivativeModel(layer, grainTex)));
            }
        }
        futures.add(saveJson(cache, "blockstates/grain_pile.json", grainPileBlockState()));
    }

    private static JsonObject grainPileBaseModel(int layer)
    {
        int height = layer * 2;
        int sideV = 16 - height;
        boolean full = (layer == 8);

        JsonObject textures = new JsonObject();
        textures.addProperty("top", "minecraft:block/hay_block_top");
        textures.addProperty("side", "minecraft:block/hay_block_side");
        textures.addProperty("particle", "minecraft:block/hay_block_side");

        JsonObject faces = new JsonObject();
        faces.add("down", makeFace(0, 0, 16, 16, "#top", "down"));
        faces.add("up", makeFace(0, 0, 16, 16, "#top", full ? "up" : null));
        faces.add("north", makeFace(0, sideV, 16, 16, "#side", full ? "north" : null));
        faces.add("south", makeFace(0, sideV, 16, 16, "#side", full ? "south" : null));
        faces.add("west", makeFace(0, sideV, 16, 16, "#side", full ? "west" : null));
        faces.add("east", makeFace(0, sideV, 16, 16, "#side", full ? "east" : null));

        JsonObject element = new JsonObject();
        element.add("from", xyz(0, 0, 0));
        element.add("to", xyz(16, height, 16));
        element.add("faces", faces);

        JsonArray elements = new JsonArray();
        elements.add(element);

        JsonObject json = new JsonObject();
        json.addProperty("parent", "block/block");
        json.add("textures", textures);
        json.add("elements", elements);
        return json;
    }

    /**
     * Grain-specific model that inherits geometry from the appropriate base model.
     */
    private static JsonObject grainPileDerivativeModel(int layer, String grainTexture)
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("top", grainTexture);
        textures.addProperty("side", grainTexture);
        textures.addProperty("particle", grainTexture);

        JsonObject json = new JsonObject();
        json.addProperty("parent", TerraFirmaThings.MOD_ID + ":block/grain_pile/grain_pile_" + layer);
        json.add("textures", textures);
        return json;
    }

    private static JsonObject grainPileBlockState()
    {
        JsonObject variants = new JsonObject();
        for (int layer = 1; layer <= 8; layer++)
        {
            JsonObject entry = new JsonObject();
            entry.addProperty("model", TerraFirmaThings.MOD_ID + ":block/grain_pile/grain_pile_" + layer);
            variants.add("layers=" + layer, entry);
        }
        JsonObject json = new JsonObject();
        json.add("variants", variants);
        return json;
    }

    // JSON helpers

    private static JsonObject modelVariant(String modelId, int yRotation)
    {
        JsonObject obj = new JsonObject();
        obj.addProperty("model", modelId);
        if (yRotation != 0) obj.addProperty("y", yRotation);
        return obj;
    }

    private static JsonObject makeFace(int u1, int v1, int u2, int v2,
                                       String texture, @Nullable String cullface)
    {
        JsonArray uv = new JsonArray();
        uv.add(u1);
        uv.add(v1);
        uv.add(u2);
        uv.add(v2);

        JsonObject face = new JsonObject();
        face.add("uv", uv);
        face.addProperty("texture", texture);
        if (cullface != null) face.addProperty("cullface", cullface);
        return face;
    }

    private static JsonArray xyz(int x, int y, int z)
    {
        JsonArray arr = new JsonArray();
        arr.add(x);
        arr.add(y);
        arr.add(z);
        return arr;
    }

    private CompletableFuture<?> saveJson(CachedOutput cache, String assetRelPath, JsonObject json)
    {
        Path path = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(TerraFirmaThings.MOD_ID + "/" + assetRelPath);
        return DataProvider.saveStable(cache, json, path);
    }
}
