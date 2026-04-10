package com.rustysnail.terrafirmathings.data;

import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.util.PhysicalDamage;
import net.dries007.tfc.util.data.ItemDamageResistance;

public final class TFCThingsItemDamageResistance extends TFCThingsDataManagerProvider<ItemDamageResistance>
{
    public TFCThingsItemDamageResistance(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(ItemDamageResistance.CODEC, "Item Damage Resistance", output, lookup, "tfc/item_damage_resistance");
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add("leather_like_footwear", new ItemDamageResistance(
            Ingredient.of(TFCThingsTags.Items.LEATHER_LIKE_FOOTWEAR),
            new PhysicalDamage(0f, 3.0f, 0f)));
    }
}
