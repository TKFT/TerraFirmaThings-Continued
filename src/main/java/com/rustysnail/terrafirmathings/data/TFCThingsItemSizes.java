package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;

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
            Ingredient.of(TFCThingsTags.Items.JAVELINS), Size.NORMAL, Weight.MEDIUM));

        add("slings", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.SLINGS), Size.NORMAL, Weight.MEDIUM));

        add("sling_ammo", new ItemSizeDefinition(
            Ingredient.of(
                TFCThingsItems.SLING_AMMO.get(),
                TFCThingsItems.SLING_AMMO_SPREAD.get(),
                TFCThingsItems.SLING_AMMO_LIGHT.get(),
                TFCThingsItems.SLING_AMMO_FIRE.get()),
            Size.SMALL, Weight.LIGHT));

        add("sharpening_tools", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.SHARPENING_TOOLS), Size.SMALL, Weight.LIGHT));

        add("grindstone_items", new ItemSizeDefinition(
            Ingredient.of(
                TFCThingsItems.GRINDSTONE_QUARTZ.get(),
                TFCThingsItems.GRINDSTONE_STEEL.get(),
                TFCThingsItems.GRINDSTONE_DIAMOND.get()),
            Size.LARGE, Weight.HEAVY));

        add("surveyors_hammers", new ItemSizeDefinition(
            Ingredient.of(TFCThingsTags.Items.SURVEYORS_HAMMERS), Size.NORMAL, Weight.MEDIUM));

        add("crowns", new ItemSizeDefinition(
            Ingredient.of(
                TFCThingsItems.GOLD_CROWN_EMPTY.get(),
                TFCThingsItems.GOLD_CROWN_AMETHYST.get(),
                TFCThingsItems.GOLD_CROWN_DIAMOND.get(),
                TFCThingsItems.GOLD_CROWN_EMERALD.get(),
                TFCThingsItems.GOLD_CROWN_OPAL.get(),
                TFCThingsItems.GOLD_CROWN_RUBY.get(),
                TFCThingsItems.GOLD_CROWN_SAPPHIRE.get(),
                TFCThingsItems.GOLD_CROWN_TOPAZ.get(),
                TFCThingsItems.PLATINUM_CROWN_EMPTY.get(),
                TFCThingsItems.PLATINUM_CROWN_AMETHYST.get(),
                TFCThingsItems.PLATINUM_CROWN_DIAMOND.get(),
                TFCThingsItems.PLATINUM_CROWN_EMERALD.get(),
                TFCThingsItems.PLATINUM_CROWN_OPAL.get(),
                TFCThingsItems.PLATINUM_CROWN_RUBY.get(),
                TFCThingsItems.PLATINUM_CROWN_SAPPHIRE.get(),
                TFCThingsItems.PLATINUM_CROWN_TOPAZ.get()),
            Size.LARGE, Weight.HEAVY));

        add("hiking_boots", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.HIKING_BOOTS.get()), Size.NORMAL, Weight.MEDIUM));

        add("snow_shoes", new ItemSizeDefinition(
            Ingredient.of(
                TFCThingsItems.SNOW_SHOES.get(),
                TFCThingsItems.DURABLE_SNOW_SHOES.get()),
            Size.LARGE, Weight.HEAVY));

        add("metal_bracing", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.METAL_BRACING.get()), Size.SMALL, Weight.LIGHT));

        add("rope_bridge_bundle", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()), Size.SMALL, Weight.LIGHT));

        add("bear_trap_half", new ItemSizeDefinition(
            Ingredient.of(TFCThingsItems.BEAR_TRAP_HALF.get()), Size.NORMAL, Weight.HEAVY));
    }
}
