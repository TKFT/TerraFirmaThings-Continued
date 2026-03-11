package com.rustysnail.terrafirmathings.data;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = TerraFirmaThings.MOD_ID)
public final class TFCThingsDataEntryPoint
{
    private TFCThingsDataEntryPoint() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event)
    {
        final PackOutput output = event.getGenerator().getPackOutput();
        final CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

        final var blockTags = event.getGenerator().addProvider(
            event.includeServer(),
            new TFCThingsBlockTags(output, lookup, event.getExistingFileHelper())
        );

        event.getGenerator().addProvider(
            event.includeServer(),
            new TFCThingsItemTags(output, lookup, blockTags.contentsGetter(), event.getExistingFileHelper())
        );

        event.getGenerator().addProvider(
            event.includeServer(),
            new TFCThingsEntityTypeTags(output, lookup, event.getExistingFileHelper())
        );

        event.getGenerator().addProvider(
            event.includeServer(),
            new TFCThingsFluidTags(output, lookup, event.getExistingFileHelper())
        );

        event.getGenerator().addProvider(
            event.includeServer(),
            new LootTableProvider(
                output,
                Set.of(),
                List.of(new LootTableProvider.SubProviderEntry(TFCThingsBlockLootTables::new, LootContextParamSets.BLOCK)),
                lookup
            )
        );

        event.getGenerator().addProvider(event.includeServer(), new TFCThingsItemSizes(output, lookup));
        event.getGenerator().addProvider(event.includeServer(), new TFCThingsItemHeat(output, lookup));
        event.getGenerator().addProvider(event.includeServer(), new TFCThingsHeatingRecipes(output, lookup));
    }
}
