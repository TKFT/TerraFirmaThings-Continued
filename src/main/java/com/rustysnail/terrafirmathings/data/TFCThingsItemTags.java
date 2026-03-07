package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import net.dries007.tfc.common.TFCTags;

public final class TFCThingsItemTags extends ItemTagsProvider
{
    private static final TagKey<Item> C_TOOLS_RANGED_WEAPON = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/ranged_weapon"));
    private static final TagKey<Item> C_TOOLS_SPEAR = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear"));
    private static final TagKey<Item> C_TOOLS_SHARPENING = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/sharpening"));
    private static final TagKey<Item> C_TOOLS_SURVEYORS_HAMMER = TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/surveyors_hammer"));

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
            .add(TFCThingsItems.SLING.get())
            .add(TFCThingsItems.SLING_METAL.get());

        tag(TFCThingsTags.Items.SURVEYORS_HAMMERS)
            .add(TFCThingsItems.COPPER_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.BISMUTH_BRONZE_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.BLACK_BRONZE_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.BRONZE_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.WROUGHT_IRON_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.STEEL_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.BLACK_STEEL_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.BLUE_STEEL_PROSPECTORS_HAMMER.get())
            .add(TFCThingsItems.RED_STEEL_PROSPECTORS_HAMMER.get());

        tag(TFCThingsTags.Items.JAVELINS)
            .add(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get())
            .add(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.get())
            .add(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.get())
            .add(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.get())
            .add(TFCThingsItems.BRONZE_ROPE_JAVELIN.get())
            .add(TFCThingsItems.COPPER_ROPE_JAVELIN.get())
            .add(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.get())
            .add(TFCThingsItems.STEEL_ROPE_JAVELIN.get())
            .add(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.get())

            .add(TFCThingsItems.STEEL_HOOK_JAVELIN.get())
            .add(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.get())
            .add(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.get())
            .add(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.get());

        tag(TFCThingsTags.Items.SHARPENING_TOOLS)
            .add(TFCThingsItems.WHETSTONE.get())
            .add(TFCThingsItems.HONING_STEEL.get())
            .add(TFCThingsItems.DIAMOND_HONING_STEEL.get());

        tag(TFCThingsTags.Items.SHARPENING_TOOL_HEADS)
            .add(TFCThingsItems.HONING_STEEL_HEAD.get())
            .add(TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get());

        tag(TFCThingsTags.Items.GEM_DISPLAY_ELIGIBLE)
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.AMETHYST).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.DIAMOND).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.EMERALD).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.LAPIS_LAZULI).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.OPAL).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.PYRITE).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.RUBY).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.SAPPHIRE).get())
            .add(net.dries007.tfc.common.items.TFCItems.GEMS.get(net.dries007.tfc.common.blocks.rock.Ore.TOPAZ).get());

        tag(TFCThingsTags.Items.SHARPNESS_MINING_TOOLS)
            .addTag(net.minecraft.tags.ItemTags.PICKAXES)
            .addTag(net.minecraft.tags.ItemTags.AXES)
            .addTag(net.minecraft.tags.ItemTags.SHOVELS)
            .addTag(net.minecraft.tags.ItemTags.HOES)
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
            .addOptionalTag(TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife")))
            .addOptionalTag(TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear")));

        tag(TFCThingsTags.Items.SHARPENABLE)
            .addTag(net.minecraft.tags.ItemTags.AXES)
            .addTag(net.minecraft.tags.ItemTags.HOES)
            .addTag(net.minecraft.tags.ItemTags.PICKAXES)
            .addTag(net.minecraft.tags.ItemTags.SHOVELS)
            .addTag(net.minecraft.tags.ItemTags.SWORDS)
            .addOptionalTag(TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/knife")))
            .addOptionalTag(TagKey.create(net.minecraft.core.registries.Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "tools/spear")))
            .addTag(TFCThingsTags.Items.JAVELINS);

        tag(C_TOOLS_RANGED_WEAPON)
            .addTag(TFCThingsTags.Items.JAVELINS)
            .addTag(TFCThingsTags.Items.SLINGS);

        tag(C_TOOLS_SPEAR).addTag(TFCThingsTags.Items.JAVELINS);
        tag(C_TOOLS_SHARPENING).addTag(TFCThingsTags.Items.SHARPENING_TOOLS);
        tag(C_TOOLS_SURVEYORS_HAMMER).addTag(TFCThingsTags.Items.SURVEYORS_HAMMERS);
    }
}
