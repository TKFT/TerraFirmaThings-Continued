package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.TFCItems;

public final class TFCThingsItemTags extends ItemTagsProvider
{
    public TFCThingsItemTags(
        PackOutput output,
        CompletableFuture<HolderLookup.Provider> lookup,
        CompletableFuture<TagLookup<net.minecraft.world.level.block.Block>> blockTags,
        ExistingFileHelper existingFileHelper
    )
    {
        super(output, lookup, blockTags, TerraFirmaThings.MOD_ID, existingFileHelper);
    }

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

        tag(Tags.Items.TOOLS_SPEAR).addTag(TFCThingsTags.Items.JAVELINS);
        tag(Tags.Items.TOOLS)
            .addTag(TFCThingsTags.Items.SURVEYORS_HAMMERS)
            .addTag(TFCThingsTags.Items.SHARPENING_TOOLS);


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
}
