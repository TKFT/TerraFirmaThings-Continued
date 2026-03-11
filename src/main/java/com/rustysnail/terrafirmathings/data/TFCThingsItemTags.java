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

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        tag(TFCThingsTags.Items.SLINGS)
            .add(TFCThingsItems.SLING.value())
            .add(TFCThingsItems.SLING_METAL.value());

        tag(TFCThingsTags.Items.SURVEYORS_HAMMERS)
            .add(TFCThingsItems.COPPER_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BRONZE_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.STEEL_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER.value());

        tag(TFCThingsTags.Items.JAVELINS)
            .add(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.value())
            .add(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.value())
            .add(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.value())
            .add(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.value())
            .add(TFCThingsItems.BRONZE_ROPE_JAVELIN.value())
            .add(TFCThingsItems.COPPER_ROPE_JAVELIN.value())
            .add(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.value())
            .add(TFCThingsItems.STEEL_ROPE_JAVELIN.value())
            .add(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.value())

            .add(TFCThingsItems.STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.value());

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
            .add(TFCItems.GEMS.get(Ore.TOPAZ).get());

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
            .addTag(net.minecraft.tags.ItemTags.SWORDS)
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife")))
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear")));

        tag(TFCThingsTags.Items.SHARPENABLE)
            .addTag(net.minecraft.tags.ItemTags.AXES)
            .addTag(net.minecraft.tags.ItemTags.HOES)
            .addTag(net.minecraft.tags.ItemTags.PICKAXES)
            .addTag(net.minecraft.tags.ItemTags.SHOVELS)
            .addTag(net.minecraft.tags.ItemTags.SWORDS)
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife")))
            .addOptionalTag(TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear")))
            .addTag(TFCThingsTags.Items.JAVELINS);

        tag(TFCThingsTags.Items.GRINDSTONE_WHEELS)
            .add(TFCThingsItems.GRINDSTONE_QUARTZ.value())
            .add(TFCThingsItems.GRINDSTONE_STEEL.value())
            .add(TFCThingsItems.GRINDSTONE_DIAMOND.value());

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

        tag(TFCTags.Items.FIRED_MOLDS).add(TFCThingsItems.SURVEYORS_HAMMER_HEAD_MOLD.value());
        tag(TFCTags.Items.UNFIRED_MOLDS).add(TFCThingsItems.UNFIRED_SURVEYORS_HAMMER_HEAD_MOLD.value());

        tag(TFCTags.Items.TOOLS_BISMUTH_BRONZE)
            .add(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.value())
            .add(TFCThingsItems.BISMUTH_BRONZE_SURVEYORS_HAMMER.value());
        tag(TFCTags.Items.TOOLS_BLACK_BRONZE)
            .add(TFCThingsItems.BLACK_BRONZE_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.value());
        tag(TFCTags.Items.TOOLS_BRONZE)
            .add(TFCThingsItems.BRONZE_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BRONZE_ROPE_JAVELIN.value());
        tag(TFCTags.Items.TOOLS_BLACK_STEEL)
            .add(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.value())
            .add(TFCThingsItems.BLACK_STEEL_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.HONING_STEEL.value())
            .add(TFCThingsItems.DIAMOND_HONING_STEEL.value());
        tag(TFCTags.Items.TOOLS_COPPER)
            .add(TFCThingsItems.COPPER_ROPE_JAVELIN.value())
            .add(TFCThingsItems.COPPER_SURVEYORS_HAMMER.value());
        tag(TFCTags.Items.TOOLS_BLUE_STEEL)
            .add(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.BLUE_STEEL_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.value());
        tag(TFCTags.Items.TOOLS_RED_STEEL)
            .add(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.value())
            .add(TFCThingsItems.RED_STEEL_SURVEYORS_HAMMER.value());
        tag(TFCTags.Items.TOOLS_STEEL)
            .add(TFCThingsItems.STEEL_HOOK_JAVELIN.value())
            .add(TFCThingsItems.STEEL_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.STEEL_ROPE_JAVELIN.value());
        tag(TFCTags.Items.TOOLS_WROUGHT_IRON)
            .add(TFCThingsItems.WROUGHT_IRON_SURVEYORS_HAMMER.value())
            .add(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.value());
    }
}
