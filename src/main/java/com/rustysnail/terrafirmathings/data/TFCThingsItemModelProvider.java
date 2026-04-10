package com.rustysnail.terrafirmathings.data;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import net.dries007.tfc.common.blocks.rock.Rock;

final class TFCThingsItemModelProvider implements DataProvider
{
    private final PackOutput output;

    TFCThingsItemModelProvider(PackOutput output)
    {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        crownModels(cache, futures);
        surveyorsHammerModels(cache, futures);
        surveyorsHammerHeadModels(cache, futures);
        hookJavelinHeadModels(cache, futures);
        ropeJavelinModels(cache, futures);
        hookJavelinModels(cache, futures);
        gemDisplayItemModels(cache, futures);
        slingAmmoModels(cache, futures);
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName()
    {
        return "TFCThings Item Models";
    }

    // Crowns

    private void crownModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        layer0Model(cache, futures, "crown/gold_empty", "minecraft:item/generated", "item/crown/gold_empty");
        layer0Model(cache, futures, "crown/platinum_empty", "minecraft:item/generated", "item/crown/platinum_empty");
        for (CrownGem gem : CrownGem.ALL)
        {
            layer0Model(cache, futures, "crown/gold_" + gem.name, "minecraft:item/generated", "item/crown/gold_" + gem.name);
            layer0Model(cache, futures, "crown/platinum_" + gem.name, "minecraft:item/generated", "item/crown/platinum_" + gem.name);
        }
    }

    // Surveyor's hammers

    private void surveyorsHammerModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (SurveyorsHammerFamily f : SurveyorsHammerFamily.ALL)
        {
            layer0Model(cache, futures,
                "surveyors_hammer/" + f.name,
                "minecraft:item/handheld",
                "item/surveyors_hammer/" + f.name);
        }
    }

    // Surveyor's hammer heads

    private void surveyorsHammerHeadModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (SurveyorsHammerFamily f : SurveyorsHammerFamily.ALL)
        {
            layer0Model(cache, futures,
                "surveyors_hammer_head/" + f.name,
                "minecraft:item/generated",
                "item/surveyors_hammer_head/" + f.name);
        }
    }

    // Hook javelin heads

    private void hookJavelinHeadModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (HookJavelinFamily f : HookJavelinFamily.ALL)
        {
            layer0Model(cache, futures,
                "hook_javelin_head/" + f.name,
                "minecraft:item/generated",
                "item/hook_javelin_head/" + f.name);
        }
    }

    // Rope javelins

    private void ropeJavelinModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (RopeJavelinFamily f : RopeJavelinFamily.ALL)
        {
            javelinModelSet(cache, futures, "rope_javelin", f.name);
        }
    }

    // Hook javelins

    private void hookJavelinModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (HookJavelinFamily f : HookJavelinFamily.ALL)
        {
            javelinModelSet(cache, futures, "hook_javelin", f.name);
        }
    }

    private void javelinModelSet(CachedOutput cache, List<CompletableFuture<?>> futures,
                                 String family, String metal)
    {
        String prefix = family + "/" + metal;
        String tex = TerraFirmaThings.MOD_ID + ":item/" + prefix;

        JsonObject gui = new JsonObject();
        gui.addProperty("parent", "item/generated");
        JsonObject guiTex = new JsonObject();
        guiTex.addProperty("layer0", tex);
        gui.add("textures", guiTex);
        futures.add(saveJson(cache, "models/item/" + prefix + "/gui.json", gui));

        JsonObject inHand = new JsonObject();
        inHand.addProperty("parent", "item/trident_in_hand");
        JsonObject inHandTex = new JsonObject();
        inHandTex.addProperty("particle", tex);
        inHand.add("textures", inHandTex);
        futures.add(saveJson(cache, "models/item/" + prefix + "/in_hand.json", inHand));

        JsonObject throwBase = new JsonObject();
        throwBase.addProperty("parent", "item/trident_throwing");
        JsonObject throwBaseTex = new JsonObject();
        throwBaseTex.addProperty("particle", tex);
        throwBase.add("textures", throwBaseTex);
        futures.add(saveJson(cache, "models/item/" + prefix + "/throwing_base.json", throwBase));

        JsonObject throwing = separateTransformsPerspectives(
            TerraFirmaThings.MOD_ID + ":item/" + prefix + "/throwing_base",
            TerraFirmaThings.MOD_ID + ":item/" + prefix + "/gui");
        futures.add(saveJson(cache, "models/item/" + prefix + "/throwing.json", throwing));

        final var main = getJsonObject(tex, prefix);

        futures.add(saveJson(cache, "models/item/" + prefix + ".json", main));
    }

    private static JsonObject getJsonObject(String tex, String prefix)
    {
        JsonObject main = new JsonObject();
        main.addProperty("loader", "neoforge:separate_transforms");
        JsonObject mainTex = new JsonObject();
        mainTex.addProperty("particle", tex);
        main.add("textures", mainTex);
        main.addProperty("gui_light", "front");

        final var overrides = getJsonElements(prefix);
        main.add("overrides", overrides);

        JsonObject base = new JsonObject();
        base.addProperty("parent", TerraFirmaThings.MOD_ID + ":item/" + prefix + "/in_hand");
        main.add("base", base);

        JsonObject perspectives = new JsonObject();
        for (String view : new String[] {"none", "fixed", "ground", "gui"})
        {
            JsonObject v = new JsonObject();
            v.addProperty("parent", TerraFirmaThings.MOD_ID + ":item/" + prefix + "/gui");
            perspectives.add(view, v);
        }
        main.add("perspectives", perspectives);
        return main;
    }

    private static JsonArray getJsonElements(String prefix)
    {
        JsonArray overrides = new JsonArray();
        JsonObject ovThrow = new JsonObject();
        JsonObject ovThrowPred = new JsonObject();
        ovThrowPred.addProperty("tfcthings:throwing", 1);
        ovThrow.add("predicate", ovThrowPred);
        ovThrow.addProperty("model", TerraFirmaThings.MOD_ID + ":item/" + prefix + "/throwing");
        overrides.add(ovThrow);
        JsonObject ovThrown = new JsonObject();
        JsonObject ovThrownPred = new JsonObject();
        ovThrownPred.addProperty("tfcthings:thrown", 1);
        ovThrown.add("predicate", ovThrownPred);
        ovThrown.addProperty("model", TerraFirmaThings.MOD_ID + ":item/rope");
        overrides.add(ovThrown);
        return overrides;
    }

    // Sling ammo

    private void slingAmmoModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (String variant : new String[] {"heavy", "light", "fire", "spread"})
        {
            layer0Model(cache, futures,
                "sling_ammo/" + variant,
                "minecraft:item/generated",
                "item/sling_ammo/" + variant);
        }
    }

    // Gem display block items

    private void gemDisplayItemModels(CachedOutput cache, List<CompletableFuture<?>> futures)
    {
        for (Rock rock : Rock.values())
        {
            String rockName = rock.getSerializedName();
            JsonObject json = new JsonObject();
            json.addProperty("parent", TerraFirmaThings.MOD_ID + ":block/gem_display/" + rockName);
            futures.add(saveJson(cache, "models/item/gem_display/" + rockName + ".json", json));
        }
    }

    private static JsonObject separateTransformsPerspectives(String baseParent, String guiParent)
    {
        JsonObject json = new JsonObject();
        json.addProperty("loader", "neoforge:separate_transforms");
        json.addProperty("gui_light", "front");

        JsonObject base = new JsonObject();
        base.addProperty("parent", baseParent);
        json.add("base", base);

        JsonObject perspectives = new JsonObject();
        for (String view : new String[] {"none", "fixed", "ground", "gui"})
        {
            JsonObject v = new JsonObject();
            v.addProperty("parent", guiParent);
            perspectives.add(view, v);
        }
        json.add("perspectives", perspectives);

        return json;
    }

    private void layer0Model(CachedOutput cache, List<CompletableFuture<?>> futures,
                             String modelPath, String parent, String textureRelPath)
    {
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", TerraFirmaThings.MOD_ID + ":" + textureRelPath);

        JsonObject json = new JsonObject();
        json.addProperty("parent", parent);
        json.add("textures", textures);

        futures.add(saveJson(cache, "models/item/" + modelPath + ".json", json));
    }

    private CompletableFuture<?> saveJson(CachedOutput cache, String assetRelPath, JsonObject json)
    {
        Path path = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
            .resolve(TerraFirmaThings.MOD_ID + "/" + assetRelPath);
        return DataProvider.saveStable(cache, json, path);
    }
}
