package com.rustysnail.terrafirmathings;

import com.mojang.logging.LogUtils;
import com.rustysnail.terrafirmathings.client.TFCThingsClient;
import com.rustysnail.terrafirmathings.common.TFCThingsArmorMaterials;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.TFCThingsDataComponents;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.condition.TFCThingsRecipeConditions;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;

import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.capabilities.ItemCapabilities;

@Mod(TerraFirmaThings.MOD_ID)
public class TerraFirmaThings
{

    public static final String MOD_ID = "tfcthings";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    @SuppressWarnings("unused")
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TFCTHINGS_TAB =
        CREATIVE_MODE_TABS.register("tfcthings_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.tfcthings"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> TFCThingsItems.SNOW_SHOES.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                TFCThingsConfig.Items.MasterList m = TFCThingsConfig.ITEMS.MASTER_LIST;

                if (m.enableSnowShoes.get())
                {
                    output.accept(TFCThingsItems.SNOW_SHOES.get());
                    output.accept(TFCThingsItems.DURABLE_SNOW_SHOES.get());
                }

                if (m.enableHikingBoots.get())
                    output.accept(TFCThingsItems.HIKING_BOOTS.get());

                if (m.enableCrampons.get())
                    output.accept(TFCThingsItems.CRAMPONS.get());

                if (m.enableRopeBridge.get())
                {
                    output.accept(TFCThingsItems.ROPE_BRIDGE_BUNDLE.get());
                }

                if (m.enableRopeLadder.get())
                    output.accept(TFCThingsItems.ROPE_LADDER.get());

                if (m.enableRopeJavelin.get())
                {
                    output.accept(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.BRONZE_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.COPPER_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.STEEL_ROPE_JAVELIN.get());
                    output.accept(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.get());
                }

                if (m.enableHookJavelins.get())
                {
                    output.accept(TFCThingsItems.STEEL_HOOK_JAVELIN.get());
                    output.accept(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.get());
                    output.accept(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.get());
                    output.accept(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.get());
                    output.accept(TFCThingsItems.STEEL_HOOK_JAVELIN_HEAD.get());
                    output.accept(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN_HEAD.get());
                    output.accept(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN_HEAD.get());
                    output.accept(TFCThingsItems.RED_STEEL_HOOK_JAVELIN_HEAD.get());
                }

                if (m.enableBearTrap.get())
                {
                    output.accept(TFCThingsItems.BEAR_TRAP.get());
                    output.accept(TFCThingsItems.BEAR_TRAP_HALF.get());
                }

                if (m.enableSnare.get())
                    output.accept(TFCThingsItems.SNARE.get());

                if (m.enableFishingNet.get())
                {
                    output.accept(TFCThingsItems.FISHING_NET_ITEM.get());
                    output.accept(TFCThingsItems.FISHING_NET_ANCHOR.get());
                }

                if (m.enableProspectorsHammers.get())
                {
                    output.accept(TFCThingsItems.COPPER_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.BISMUTH_BRONZE_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.BLACK_BRONZE_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.BRONZE_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.WROUGHT_IRON_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.STEEL_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.BLACK_STEEL_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.BLUE_STEEL_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.RED_STEEL_PROSPECTORS_HAMMER.get());
                    output.accept(TFCThingsItems.COPPER_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.BISMUTH_BRONZE_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.BLACK_BRONZE_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.BRONZE_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.WROUGHT_IRON_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.STEEL_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.BLACK_STEEL_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.BLUE_STEEL_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.RED_STEEL_PROSPECTORS_HAMMER_HEAD.get());
                    output.accept(TFCThingsItems.UNFIRED_PROSPECTORS_HAMMER_HEAD_MOLD.get());
                    output.accept(TFCThingsItems.PROSPECTORS_HAMMER_HEAD_MOLD.get());
                }

                output.accept(TFCThingsItems.GOLD_CROWN_EMPTY.get());
                output.accept(TFCThingsItems.GOLD_CROWN_AMETHYST.get());
                output.accept(TFCThingsItems.GOLD_CROWN_DIAMOND.get());
                output.accept(TFCThingsItems.GOLD_CROWN_EMERALD.get());
                output.accept(TFCThingsItems.GOLD_CROWN_OPAL.get());
                output.accept(TFCThingsItems.GOLD_CROWN_RUBY.get());
                output.accept(TFCThingsItems.GOLD_CROWN_SAPPHIRE.get());
                output.accept(TFCThingsItems.GOLD_CROWN_TOPAZ.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_EMPTY.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_AMETHYST.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_DIAMOND.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_EMERALD.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_OPAL.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_RUBY.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_SAPPHIRE.get());
                output.accept(TFCThingsItems.PLATINUM_CROWN_TOPAZ.get());

                if (m.enableWhetstones.get())
                {
                    output.accept(TFCThingsItems.GRINDSTONE.get());
                    output.accept(TFCThingsItems.GRINDSTONE_QUARTZ.get());
                    output.accept(TFCThingsItems.GRINDSTONE_STEEL.get());
                    output.accept(TFCThingsItems.GRINDSTONE_DIAMOND.get());
                    output.accept(TFCThingsItems.WHETSTONE.get());
                    output.accept(TFCThingsItems.HONING_STEEL.get());
                    output.accept(TFCThingsItems.DIAMOND_HONING_STEEL.get());
                    output.accept(TFCThingsItems.HONING_STEEL_HEAD.get());
                    output.accept(TFCThingsItems.DIAMOND_HONING_STEEL_HEAD.get());
                }

                if (m.enableHikingBoots.get() || m.enableSlings.get())
                    output.accept(TFCThingsItems.METAL_BRACING.get());

                if (m.enableSlings.get())
                {
                    output.accept(TFCThingsItems.SLING.get());
                    output.accept(TFCThingsItems.SLING_METAL.get());
                    output.accept(TFCThingsItems.SLING_AMMO.get());
                    output.accept(TFCThingsItems.SLING_AMMO_SPREAD.get());
                    output.accept(TFCThingsItems.SLING_AMMO_LIGHT.get());
                    output.accept(TFCThingsItems.SLING_AMMO_FIRE.get());
                }

                if (m.enableGemDisplays.get())
                {
                    for (Rock rock : Rock.values())
                    {
                        output.accept(TFCThingsItems.GEM_DISPLAY_ITEMS.get(rock).get());
                    }
                }
            }).build());

    public TerraFirmaThings(IEventBus modEventBus, ModContainer modContainer)
    {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        TFCThingsArmorMaterials.ARMOR_MATERIALS.register(modEventBus);
        TFCThingsDataComponents.DATA_COMPONENTS.register(modEventBus);
        TFCThingsBlocks.BLOCKS.register(modEventBus);
        TFCThingsBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        TFCThingsItems.ITEMS.register(modEventBus);
        TFCThingsEntities.ENTITY_TYPES.register(modEventBus);
        TFCThingsRecipeConditions.CONDITION_CODECS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            TFCThingsClient.init(modEventBus);
        }

        modEventBus.addListener(this::addCreative);

        modContainer.registerConfig(ModConfig.Type.COMMON, TFCThingsConfig.SPEC);
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("TerraFirma Things server starting");
    }

    private void commonSetup(FMLCommonSetupEvent event)
    {
        LOGGER.info("TerraFirma Things common setup");
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event)
    {
        event.registerItem(ItemCapabilities.MOLD, ItemCapabilities::forMold, TFCThingsItems.PROSPECTORS_HAMMER_HEAD_MOLD.get());
        event.registerItem(ItemCapabilities.HEAT, ItemCapabilities::forMold, TFCThingsItems.PROSPECTORS_HAMMER_HEAD_MOLD.get());
        event.registerItem(ItemCapabilities.FLUID, ItemCapabilities::forMold, TFCThingsItems.PROSPECTORS_HAMMER_HEAD_MOLD.get());
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.COMBAT)
        {
            if (TFCThingsConfig.ITEMS.MASTER_LIST.enableSnowShoes.get())
                event.accept(TFCThingsItems.SNOW_SHOES.get());
        }
    }
}
