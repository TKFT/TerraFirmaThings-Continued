package com.rustysnail.terrafirmathings.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.condition.TFCThingsConfigCondition;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Metal;

public final class TFCThingsItemTags extends ItemTagsProvider
{

    private final CompletableFuture<HolderLookup.Provider> lookupFuture;
    private final Map<ResourceLocation, List<ICondition>> conditionalTags = new LinkedHashMap<>();

    public TFCThingsItemTags(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookup,
        CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags,
        ExistingFileHelper existingFileHelper
    )
    {
        super(output, lookup, blockTags, TerraFirmaThings.MOD_ID, existingFileHelper);
        this.lookupFuture = lookup;

        makeConditional(Tags.Items.TOOLS_IGNITER, new TFCThingsConfigCondition("enableTagFixes"));
        makeConditional(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/lapis")), new TFCThingsConfigCondition("enableTagFixes"));
        for(String gem : gemNames) {
            makeConditional(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/" + gem)), new TFCThingsConfigCondition("enableTagFixes"));
        }
        for(String gem : gemNames.subList(3, gemNames.size())) {
            makeConditional(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "gems/" + gem)), new TFCThingsConfigCondition("enableTagFixes"));
        }

    }

    private static final List<String> gemNames = List.of("amethyst", "diamond", "emerald", "opal", "pyrite", "ruby", "sapphire", "topaz");
    private static final List<String> TOOL_METALS = List.of("bismuth_bronze", "black_bronze", "bronze", "copper", "wrought_iron", "steel", "black_steel", "blue_steel", "red_steel");
    private static final List<String> ALL_METALS = List.of("bismuth_bronze", "black_bronze", "bronze", "copper", "wrought_iron", "steel", "black_steel", "blue_steel", "red_steel", "bismuth", "brass", "gold", "nickle", "rose_gold", "silver", "tin", "zinc", "steerling_silver", "cast_iron");
    private static final List<String> ORES = List.of("native_copper", "native_gold", "hematite", "native_silver", "cassiterite", "bismuthinite", "garnierite", "malachite", "magnetite", "limonite", "sphalerite", "tetrahedrite");
    private static final List<String> ORE_TYPES = List.of("small", "poor", "normal", "rich");
    private static final Map<String, String> ORE_TO_METAL = Map.ofEntries(
        Map.entry("native_copper", "copper"),
        Map.entry("native_gold", "gold"),
        Map.entry("hematite", "iron"),
        Map.entry("native_silver", "silver"),
        Map.entry("cassiterite", "tin"),
        Map.entry("bismuthinite", "bismuth"),
        Map.entry("garnierite", "nickle"),
        Map.entry("malachite", "copper"),
        Map.entry("magnetite", "iron"),
        Map.entry("limonite", "iron"),
        Map.entry("sphalerite", "zinc"),
        Map.entry("tetrahedrite", "copper")
    );

    private static final TagKey<Item> GRAINS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/grain"));
    private static final TagKey<Item> SEEDS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "foods/seeds"));
    private static final TagKey<Item> FLAWLESS_GEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "flawless_gems"));
    private static final TagKey<Item> EXQUISITE_GEMS = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "exquisite_gems"));

    private static final TagKey<Item> CURIOS_CROWN = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("curios", "crown"));

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        //TFCThings Tags
        tag(TFCThingsTags.Items.SLINGS)
            .add(TFCThingsItems.SLING.value())
            .add(TFCThingsItems.SLING_METAL.value());

        tag(TFCThingsTags.Items.RECOVERABLE_SLING_AMMO)
            .add(TFCThingsItems.SLING_AMMO_HEAVY.value())
            .add(TFCThingsItems.SLING_AMMO_LIGHT.value())
            .addOptionalTag(TFCTags.Items.STONES_LOOSE);

        tag(TFCThingsTags.Items.NONRECOVERABLE_SLING_AMMO)
            .add(TFCThingsItems.SLING_AMMO_SPREAD.value())
            .add(TFCThingsItems.SLING_AMMO_FIRE.value());

        tag(TFCThingsTags.Items.SLING_AMMO)
            .addTag(TFCThingsTags.Items.RECOVERABLE_SLING_AMMO)
            .addTag(TFCThingsTags.Items.NONRECOVERABLE_SLING_AMMO);

        {
            var t = tag(TFCThingsTags.Items.SURVEYORS_HAMMERS);
            for (SurveyorsHammerFamily f : SurveyorsHammerFamily.ALL)
            {
                t.add(f.hammerItem.value());
            }
        }

        tag(TFCThingsTags.Items.JAVELINS)
            .addTag(TFCThingsTags.Items.ROPE_JAVELINS)
            .addTag(TFCThingsTags.Items.HOOK_JAVELINS);

        tag(TFCThingsTags.Items.SHARPENING_TOOLS)
            .add(TFCThingsItems.WHETSTONE.value())
            .add(TFCThingsItems.HONING_STEEL.value())
            .add(TFCThingsItems.DIAMOND_HONING_STEEL.value());

        tag(TFCThingsTags.Items.SHARPENING_TOOL_HEADS)
            .add(TFCThingsItems.HONING_STEEL_HEAD.value())
            .add(TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get());

        tag(TFCThingsTags.Items.GEM_DISPLAY_ELIGIBLE)
            .add(TFCItems.GEMS.get(Ore.AMETHYST).get())
            .add(TFCItems.GEMS.get(Ore.DIAMOND).get())
            .add(TFCItems.GEMS.get(Ore.EMERALD).get())
            .add(TFCItems.GEMS.get(Ore.LAPIS_LAZULI).get())
            .add(TFCItems.GEMS.get(Ore.OPAL).get())
            .add(TFCItems.GEMS.get(Ore.PYRITE).get())
            .add(TFCItems.GEMS.get(Ore.RUBY).get())
            .add(TFCItems.GEMS.get(Ore.SAPPHIRE).get())
            .add(TFCItems.GEMS.get(Ore.TOPAZ).get())
            .addOptionalTag(Tags.Items.GEMS)
            .addOptionalTag(FLAWLESS_GEMS)
            .addOptionalTag(EXQUISITE_GEMS);

        {
            var t = tag(TFCThingsTags.Items.GEM_DISPLAY_ITEMS);
            TFCThingsItems.GEM_DISPLAY_ITEMS.values().forEach(di -> t.add(di.value()));
        }

        tag(TFCThingsTags.Items.SHARPNESS_MINING_TOOLS)
            .addTag(ItemTags.PICKAXES)
            .addTag(ItemTags.AXES)
            .addTag(ItemTags.SHOVELS)
            .addTag(ItemTags.HOES)
            .addOptionalTag(TFCTags.Items.TOOLS_STONE)
            .addOptionalTag(TFCTags.Items.TOOLS_COPPER)
            .addOptionalTag(TFCTags.Items.TOOLS_BISMUTH_BRONZE)
            .addOptionalTag(TFCTags.Items.TOOLS_BLACK_BRONZE)
            .addOptionalTag(TFCTags.Items.TOOLS_BRONZE)
            .addOptionalTag(TFCTags.Items.TOOLS_WROUGHT_IRON)
            .addOptionalTag(TFCTags.Items.TOOLS_STEEL)
            .addOptionalTag(TFCTags.Items.TOOLS_BLACK_STEEL)
            .addOptionalTag(TFCTags.Items.TOOLS_BLUE_STEEL)
            .addOptionalTag(TFCTags.Items.TOOLS_RED_STEEL);

        tag(TFCThingsTags.Items.SHARPNESS_WEAPONS)
            .addTag(ItemTags.SWORDS)
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife")))
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear")));

        tag(TFCThingsTags.Items.SHARPENABLE)
            .addTag(ItemTags.AXES)
            .addTag(ItemTags.HOES)
            .addTag(ItemTags.PICKAXES)
            .addTag(ItemTags.SHOVELS)
            .addTag(ItemTags.SWORDS)
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife")))
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear")))
            .addTag(TFCThingsTags.Items.JAVELINS);

        tag(TFCThingsTags.Items.GRINDSTONE_WHEELS)
            .add(TFCThingsItems.GRINDSTONE_WHEEL_QUARTZ.value())
            .add(TFCThingsItems.GRINDSTONE_WHEEL_STEEL.value())
            .add(TFCThingsItems.GRINDSTONE_WHEEL_DIAMOND.value());

        tag(TFCThingsTags.Items.SNARE_BAIT)
            .addOptionalTag(GRAINS)
            .addOptionalTag(SEEDS);

        tag(TFCThingsTags.Items.GRAIN_PILE_ITEMS)
            .addOptionalTag(GRAINS);

        tag(TFCThingsTags.Items.WHETSTONE_ROCKS)
            .addOptional(ResourceLocation.parse("tfc:rock/loose/chert"))
            .addOptional(ResourceLocation.parse("tfc:rock/loose/slate"))
            .addOptional(ResourceLocation.parse("tfc:rock/loose/phyllite"))
            .addOptional(ResourceLocation.parse("tfc:rock/loose/quartzite"))
            .addOptional(ResourceLocation.parse("tfc:rock/mossy_loose/chert"))
            .addOptional(ResourceLocation.parse("tfc:rock/mossy_loose/slate"))
            .addOptional(ResourceLocation.parse("tfc:rock/mossy_loose/phyllite"))
            .addOptional(ResourceLocation.parse("tfc:rock/mossy_loose/quartzite"));

        {
            var t = tag(TFCThingsTags.Items.CROWNS_GOLD);
            t.add(TFCThingsItems.GOLD_CROWN_EMPTY.value());
            for (CrownGem g : CrownGem.ALL)
            {
                t.add(g.goldResult.value());
            }
        }

        {
            var t = tag(TFCThingsTags.Items.CROWNS_PLATINUM);
            t.add(TFCThingsItems.PLATINUM_CROWN_EMPTY.value());
            for (CrownGem g : CrownGem.ALL)
            {
                t.add(g.platinumResult.value());
            }
        }

        tag(TFCThingsTags.Items.CROWNS)
            .addTag(TFCThingsTags.Items.CROWNS_GOLD)
            .addTag(TFCThingsTags.Items.CROWNS_PLATINUM);

        tag(TFCThingsTags.Items.SNOWSHOES)
            .add(TFCThingsItems.SNOW_SHOES.value())
            .add(TFCThingsItems.DURABLE_SNOW_SHOES.value());

        tag(TFCThingsTags.Items.HIKING_BOOTS)
            .add(TFCThingsItems.HIKING_BOOTS.value());

        tag(TFCThingsTags.Items.CRAMPONS)
            .add(TFCThingsItems.CRAMPONS.value());

        {
            var t = tag(TFCThingsTags.Items.ROPE_JAVELINS);
            for (RopeJavelinFamily f : RopeJavelinFamily.ALL)
            {
                t.add(f.javelinItem.value());
            }
        }

        {
            var t = tag(TFCThingsTags.Items.HOOK_JAVELINS);
            for (HookJavelinFamily f : HookJavelinFamily.ALL)
            {
                t.add(f.javelinItem.value());
            }
        }

        tag(TFCThingsTags.Items.WARM_FOOTWEAR)
            .addTag(TFCThingsTags.Items.SNOWSHOES)
            .addTag(TFCThingsTags.Items.HIKING_BOOTS);

        tag(TFCThingsTags.Items.LEATHER_LIKE_FOOTWEAR)
            .addTag(TFCThingsTags.Items.SNOWSHOES)
            .addTag(TFCThingsTags.Items.HIKING_BOOTS);

        tag(net.minecraft.tags.ItemTags.FOOT_ARMOR)
            .add(TFCThingsItems.HIKING_BOOTS.value())
            .add(TFCThingsItems.SNOW_SHOES.value())
            .add(TFCThingsItems.DURABLE_SNOW_SHOES.value())
            .add(TFCThingsItems.CRAMPONS.value());

        //C Tags
        tag(ItemTags.FOOT_ARMOR)
            .add(TFCThingsItems.HIKING_BOOTS.value())
            .add(TFCThingsItems.SNOW_SHOES.value())
            .add(TFCThingsItems.DURABLE_SNOW_SHOES.value())
            .add(TFCThingsItems.CRAMPONS.value());

        tag(Tags.Items.RANGED_WEAPON_TOOLS)
            .addTag(TFCThingsTags.Items.JAVELINS)
            .addTag(TFCThingsTags.Items.SLINGS);

        tag(Tags.Items.TOOLS_SPEAR)
            .addTag(TFCThingsTags.Items.JAVELINS);

        tag(Tags.Items.TOOLS)
            .addTag(TFCThingsTags.Items.SURVEYORS_HAMMERS)
            .addTag(TFCThingsTags.Items.SHARPENING_TOOLS);


        //Extra Common Tag fixes
        tag(Tags.Items.TOOLS_IGNITER)
            .add(TFCItems.FLINT_AND_PYRITE.get());

        for (String gem : gemNames)
        {
            tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/" + gem)))
                .add(TagEntry.element((ResourceLocation.parse("tfc:powder/" + gem))));
        }
        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/lapis")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/lapis_lazuli"))));

        for (String gem : gemNames.subList(3, gemNames.size()))
        {
            tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "gems/" + gem)))
                .add(TagEntry.element((ResourceLocation.parse("tfc:gem/" + gem))));
        }

        tag(Tags.Items.FOODS_BERRY)
            .addOptional(ResourceLocation.parse("tfc:food/blackberry"))
            .addOptional(ResourceLocation.parse("tfc:food/blueberry"))
            .addOptional(ResourceLocation.parse("tfc:food/bunchkberry"))
            .addOptional(ResourceLocation.parse("tfc:food/cloudberry"))
            .addOptional(ResourceLocation.parse("tfc:food/cranberry"))
            .addOptional(ResourceLocation.parse("tfc:food/elderberry"))
            .addOptional(ResourceLocation.parse("tfc:food/gooseberry"))
            .addOptional(ResourceLocation.parse("tfc:food/raspberry"))
            .addOptional(ResourceLocation.parse("tfc:food/snowberry"))
            .addOptional(ResourceLocation.parse("tfc:food/strawberry"))
            .addOptional(ResourceLocation.parse("tfc:food/wintergreen_berry"));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/saltpeter")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/saltpeter"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/charcoal")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/charcoal"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/coke")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/coke"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/kaolinite")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/kaolinite"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/graphite")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/graphite"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/sylvite")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/sylvite"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/salt")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/salt"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/flux")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/flux"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/wood_ash")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/wood_ash"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/soda_ash")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/soda_ash"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "dusts/lime")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:powder/lime"))));

        for (String metal : ALL_METALS)
        {
            tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "plates/" + metal)))
                .add(TagEntry.element((ResourceLocation.parse("tfc:metal/sheet/" + metal))));
            tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "double_plates/" + metal)))
                .add(TagEntry.element((ResourceLocation.parse("tfc:metal/double_sheet/" + metal))));
        }

        tag(Tags.Items.BUCKETS_EMPTY)
            .add(TFCItems.RED_STEEL_BUCKET.asItem())
            .add(TFCItems.BLUE_STEEL_BUCKET.asItem())
            .add(TFCItems.WOODEN_BUCKET.asItem());

        for (String oreType : ORE_TYPES)
        {
            for (String ore : ORES)
            {
                tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/" + oreType + "/" + ORE_TO_METAL.get(ore))))
                    .add(TagEntry.element((ResourceLocation.parse("tfc:ore/" + oreType + "_" + ore))));

                tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/" + ORE_TO_METAL.get(ore) + "/" + oreType)))
                    .add(TagEntry.element((ResourceLocation.parse("tfc:ore/" + oreType + "_" + ore))));

                tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/" + ORE_TO_METAL.get(ore))))
                    .add(TagEntry.element((ResourceLocation.parse("tfc:ore/" + oreType + "_" + ore))));
            }
        }

        for (String gem : gemNames) {
             tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/" + gem)))
                .add(TagEntry.element((ResourceLocation.parse("tfc:ore/" + gem))));
        }

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/redstone")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/cinnabar"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/redstone")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/cryolite"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/flux")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/borax"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/graphite")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/graphite"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/saltpeter")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/salpeter"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/sulpur")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/sulpur"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/sylvite")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/sylvite"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "raw_materials/salt")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:ore/halite"))));

        tag(Tags.Items.FERTILIZERS)
            .addOptional(ResourceLocation.parse("tfc:compost"))
            .addOptional(ResourceLocation.parse("tfc:food/shellfish"))
            .addOptional(ResourceLocation.parse("tfc:powder/saltpeter"))
            .addOptional(ResourceLocation.parse("tfc:groundcover/guano"))
            .addOptional(ResourceLocation.parse("tfc:powder/wood_ash"))
            .addOptional(ResourceLocation.parse("tfc:powder/sylvite"));

        //TODO: Bricks, Mud Bricks and other Brick type items.

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/sandpaper")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:sandpaper"))));

        tag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spindle")))
            .add(TagEntry.element((ResourceLocation.parse("tfc:spindle"))));

        //TFC TAGS
        tag(TFCTags.Items.TOOL_RACK_TOOLS)
            .addTag(TFCThingsTags.Items.JAVELINS)
            .addTag(TFCThingsTags.Items.SLINGS)
            .addTag(TFCThingsTags.Items.SHARPENING_TOOLS)
            .addTag(TFCThingsTags.Items.SURVEYORS_HAMMERS);

        tag(TFCTags.Items.DEALS_CRUSHING_DAMAGE)
            .addTag(TFCThingsTags.Items.SURVEYORS_HAMMERS);

        tag(TFCTags.Items.FIRED_MOLDS).add(TFCThingsItems.SURVEYORS_HAMMER_HEAD_MOLD.value());
        tag(TFCTags.Items.UNFIRED_MOLDS).add(TFCThingsItems.UNFIRED_SURVEYORS_HAMMER_HEAD_MOLD.value());
        tag(TFCTags.Items.USABLE_IN_MOLD_TABLE).add(TFCThingsItems.SURVEYORS_HAMMER_HEAD_MOLD.value());

        for (RopeJavelinFamily f : RopeJavelinFamily.ALL)
        {
            tag(f.tfcToolsTag).add(f.javelinItem.value());
        }
        for (HookJavelinFamily f : HookJavelinFamily.ALL)
        {
            tag(f.tfcToolsTag).add(f.javelinItem.value());
        }
        for (SurveyorsHammerFamily f : SurveyorsHammerFamily.ALL)
        {
            tag(f.tfcToolsTag).add(f.hammerItem.value());
        }
        tag(TFCTags.Items.TOOLS_BLACK_STEEL)
            .add(TFCThingsItems.HONING_STEEL.value())
            .add(TFCThingsItems.DIAMOND_HONING_STEEL.value());

        tag(CURIOS_CROWN)
            .addTag(TFCThingsTags.Items.CROWNS);
    }

    private void makeConditional(TagKey<Item> tag, ICondition... conditions)
    {
        conditionalTags.put(tag.location(), List.of(conditions));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        return super.run(cache)
            .thenCombine(lookupFuture, (ignored, registries) -> registries)
            .thenCompose(registries -> patchConditionalTags(cache, registries));
    }

    private CompletableFuture<?> patchConditionalTags(CachedOutput cache, HolderLookup.Provider registries)
    {
        if (conditionalTags.isEmpty())
        {
            return CompletableFuture.completedFuture(null);
        }

        final List<CompletableFuture<?>> futures = new ArrayList<>();

        try
        {
            for (var entry : conditionalTags.entrySet())
            {
                final ResourceLocation tagId = entry.getKey();
                final List<ICondition> conditions = entry.getValue();
                final Path path = getPath(tagId);

                if (!Files.exists(path))
                {
                    continue;
                }

                final JsonObject json;
                try (var reader = Files.newBufferedReader(path))
                {
                    json = JsonParser.parseReader(reader).getAsJsonObject();
                }

                ICondition.writeConditions(registries, json, conditions);
                futures.add(DataProvider.saveStable(cache, json, path));
            }
        }
        catch (IOException e)
        {
            return CompletableFuture.failedFuture(e);
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}
