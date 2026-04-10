package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.common.component.size.ItemSizeDefinition;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;

public final class TFCThingsItemSizes extends TFCThingsDataManagerProvider<ItemSizeDefinition>
{
    public TFCThingsItemSizes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ItemSizeDefinition.CODEC, "Item Sizes", output, lookup, "tfc/item_size");
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("javelins", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.JAVELINS), Size.LARGE, Weight.HEAVY));

        add("slings", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.SLINGS), Size.LARGE, Weight.HEAVY));

        add("sling_ammo", new ItemSizeDefinition(
            Ingredient.of(
                TFCThingsItems.SLING_AMMO_HEAVY.get(),
                TFCThingsItems.SLING_AMMO_SPREAD.get(),
                TFCThingsItems.SLING_AMMO_LIGHT.get(),
                TFCThingsItems.SLING_AMMO_FIRE.get()),
            Size.SMALL, Weight.LIGHT));

        add("sharpening_tools", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.SHARPENING_TOOLS), Size.SMALL, Weight.LIGHT));

        add("grindstone_items", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.GRINDSTONE_WHEELS), Size.LARGE, Weight.HEAVY));

        add("surveyors_hammers", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.SURVEYORS_HAMMERS), Size.LARGE, Weight.HEAVY));

        add("crowns", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.CROWNS), Size.LARGE, Weight.HEAVY));

        add("hiking_boots", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.HIKING_BOOTS.get()), Size.NORMAL, Weight.MEDIUM));

        add("crampons", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.CRAMPONS.get()), Size.NORMAL, Weight.HEAVY));

        add("snow_shoes", new ItemSizeDefinition(
            Ingredient.of(
                TFCThingsItems.SNOW_SHOES.get(),
                TFCThingsItems.DURABLE_SNOW_SHOES.get()),
            Size.LARGE, Weight.HEAVY));

        add("metal_bracing", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.METAL_BRACING.get()), Size.SMALL, Weight.LIGHT));

        add("rope_bridge_bundle", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()), Size.NORMAL, Weight.MEDIUM));

        add("rope_bridge", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.ROPE_BRIDGE_SEGMENT.get()), Size.NORMAL, Weight.MEDIUM));

        add("bear_trap_half", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.BEAR_TRAP_HALF.get()), Size.NORMAL, Weight.HEAVY));

        add("bear_trap", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.BEAR_TRAP.get()), Size.LARGE, Weight.HEAVY));

        add("fishing_net", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.FISHING_NET_ITEM.get()), Size.SMALL, Weight.LIGHT));

        add("fishing_net_anchor", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.FISHING_NET_ANCHOR.get()), Size.LARGE, Weight.HEAVY));

        add("rope_bridge", new ItemSizeDefinition(
            Ingredient.of(TFCThingsBlocks.ROPE_BRIDGE.get()), Size.NORMAL, Weight.MEDIUM));

        add("rope_ladder", new ItemSizeDefinition(
            Ingredient.of(TFCThingsBlocks.ROPE_LADDER.get()), Size.NORMAL, Weight.MEDIUM));

        add("gem_display", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.GEM_DISPLAY_ITEMS), Size.LARGE, Weight.HEAVY));
    }
}
