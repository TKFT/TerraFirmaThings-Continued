package com.rustysnail.terrafirmathings.data;

import java.util.Set;

import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class TFCThingsBlockLootTables extends BlockLootSubProvider
{
    public TFCThingsBlockLootTables(HolderLookup.Provider registries)
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate()
    {
        dropSelf(TFCThingsBlocks.ROPE_LADDER.get());
        dropSelf(TFCThingsBlocks.BEAR_TRAP.get());
        dropSelf(TFCThingsBlocks.SNARE.get());
        dropSelf(TFCThingsBlocks.FISHING_NET_ANCHOR.get());
        dropSelf(TFCThingsBlocks.GRINDSTONE.get());

        add(TFCThingsBlocks.FISHING_NET.get(),
            createSingleItemTable(TFCThingsItems.FISHING_NET_ITEM.get()));

        add(TFCThingsBlocks.ROPE_BRIDGE.get(),
            LootTable.lootTable()
                .withPool(LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0F))
                    .add(LootItem.lootTableItem(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get()))));

        for (var entry : TFCThingsBlocks.GEM_DISPLAYS.entrySet())
        {
            dropSelf(entry.getValue().get());
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return TFCThingsBlocks.BLOCKS.getEntries().stream()
            .<Block>map(DeferredHolder::value)
            .toList();
    }
}
