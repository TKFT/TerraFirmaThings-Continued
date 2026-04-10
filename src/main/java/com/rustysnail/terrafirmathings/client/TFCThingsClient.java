package com.rustysnail.terrafirmathings.client;

import java.util.function.Function;
import java.util.function.Supplier;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.client.extensions.ItemRendererExtension;
import com.rustysnail.terrafirmathings.client.model.HookJavelinModel;
import com.rustysnail.terrafirmathings.client.renderer.GemDisplayBlockEntityRenderer;
import com.rustysnail.terrafirmathings.client.renderer.GrainPileBlockEntityRenderer;
import com.rustysnail.terrafirmathings.client.renderer.GrindstoneBlockEntityRenderer;
import com.rustysnail.terrafirmathings.client.renderer.HookJavelinItemRenderer;
import com.rustysnail.terrafirmathings.client.renderer.RopeBridgeBlockEntityRenderer;
import com.rustysnail.terrafirmathings.client.renderer.RopeJavelinItemRenderer;
import com.rustysnail.terrafirmathings.client.renderer.SlingStoneRenderer;
import com.rustysnail.terrafirmathings.client.renderer.ThrownHookJavelinRenderer;
import com.rustysnail.terrafirmathings.client.renderer.ThrownRopeJavelinRenderer;
import com.rustysnail.terrafirmathings.common.TFCThingsBlockEntities;
import com.rustysnail.terrafirmathings.common.block.GrainPileBlock;
import com.rustysnail.terrafirmathings.common.TFCThingsEntities;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.item.HookJavelinItem;
import com.rustysnail.terrafirmathings.common.item.RopeJavelinItem;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;

public class TFCThingsClient
{

    public static void init(IEventBus modBus)
    {
        modBus.addListener(TFCThingsClient::registerEntityRenderers);
        modBus.addListener(TFCThingsClient::registerLayerDefinitions);
        modBus.addListener(TFCThingsClient::clientSetup);
        modBus.addListener(TFCThingsClient::registerExtensions);
        modBus.addListener(TFCThingsClient::registerAdditionalModels);
    }

    @SuppressWarnings("deprecation")
    private static void clientSetup(final FMLClientSetupEvent event)
    {
        event.enqueueWork(() -> {
            ResourceLocation spinningId = ResourceLocation.fromNamespaceAndPath("tfcthings", "spinning");
            ItemProperties.register(TFCThingsItems.SLING.get(), spinningId, TFCThingsClient::slingSpinningPredicate);
            ItemProperties.register(TFCThingsItems.SLING_METAL.get(), spinningId, TFCThingsClient::slingSpinningPredicate);

            ResourceLocation thrownId = ResourceLocation.fromNamespaceAndPath("tfcthings", "thrown");
            registerRopeJavelinThrownPredicate(thrownId);
            registerHookJavelinThrownPredicate(thrownId);

            ResourceLocation throwingId = ResourceLocation.fromNamespaceAndPath("tfcthings", "throwing");
            registerRopeJavelinThrowingPredicate(throwingId);
            registerHookJavelinThrowingPredicate(throwingId);
        });
    }

    @SuppressWarnings("deprecation")
    private static void registerRopeJavelinThrownPredicate(ResourceLocation thrownId)
    {
        ItemProperties.register(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.BRONZE_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.COPPER_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.STEEL_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.get(), thrownId, TFCThingsClient::ropeJavelinThrownPredicate);
    }

    @SuppressWarnings("deprecation")
    private static void registerHookJavelinThrownPredicate(ResourceLocation thrownId)
    {
        ItemProperties.register(TFCThingsItems.STEEL_HOOK_JAVELIN.get(), thrownId, TFCThingsClient::hookJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.get(), thrownId, TFCThingsClient::hookJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.get(), thrownId, TFCThingsClient::hookJavelinThrownPredicate);
        ItemProperties.register(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.get(), thrownId, TFCThingsClient::hookJavelinThrownPredicate);
    }

    @SuppressWarnings("deprecation")
    private static void registerRopeJavelinThrowingPredicate(ResourceLocation throwingId)
    {
        ItemProperties.register(TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.BRONZE_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.COPPER_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.RED_STEEL_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.STEEL_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN.get(), throwingId, TFCThingsClient::ropeJavelinThrowingPredicate);
    }

    @SuppressWarnings("deprecation")
    private static void registerHookJavelinThrowingPredicate(ResourceLocation throwingId)
    {
        ItemProperties.register(TFCThingsItems.STEEL_HOOK_JAVELIN.get(), throwingId, TFCThingsClient::hookJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN.get(), throwingId, TFCThingsClient::hookJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN.get(), throwingId, TFCThingsClient::hookJavelinThrowingPredicate);
        ItemProperties.register(TFCThingsItems.RED_STEEL_HOOK_JAVELIN.get(), throwingId, TFCThingsClient::hookJavelinThrowingPredicate);
    }

    private static float slingSpinningPredicate(ItemStack stack,
                                                ClientLevel ignoredLevel,
                                                LivingEntity entity,
                                                int ignoredSeed)
    {
        if (entity == null || !entity.isUsingItem()) return 0.0F;
        net.minecraft.world.item.ItemStack active = entity.getUseItem();
        if (active.isEmpty() || !net.minecraft.world.item.ItemStack.isSameItemSameComponents(active, stack)) return 0.0F;
        int chargeTime = stack.getUseDuration(entity) - entity.getUseItemRemainingTicks();
        if (chargeTime <= 0) return 0.0F;
        int maxPower = TFCThingsConfig.ITEMS.SLING.maxPower.get();
        int chargeSpeed = TFCThingsConfig.ITEMS.SLING.chargeSpeed.get();
        float powerRatio = Math.min((float) chargeTime / chargeSpeed, maxPower) / (float) maxPower;
        return (float) Math.floor(((chargeTime * powerRatio) % 8.0F) + 1.0F);
    }

    private static float ropeJavelinThrownPredicate(ItemStack stack,
                                                    ClientLevel ignoredLevel,
                                                    LivingEntity entity,
                                                    int ignoredSeed)
    {
        if (entity == null) return 0.0F;
        boolean main = entity.getMainHandItem() == stack;
        boolean off = entity.getOffhandItem() == stack;
        if (entity.getMainHandItem().getItem() instanceof RopeJavelinItem) off = false;
        return (main || off) && RopeJavelinItem.isThrown(stack) ? 1.0F : 0.0F;
    }

    private static float hookJavelinThrownPredicate(ItemStack stack,
                                                    ClientLevel ignoredLevel,
                                                    LivingEntity entity,
                                                    int ignoredSeed)
    {
        if (entity == null) return 0.0F;
        boolean main = entity.getMainHandItem() == stack;
        boolean off = entity.getOffhandItem() == stack;
        if (entity.getMainHandItem().getItem() instanceof HookJavelinItem) off = false;
        return (main || off) && HookJavelinItem.isThrown(stack) ? 1.0F : 0.0F;
    }

    private static float ropeJavelinThrowingPredicate(ItemStack stack,
                                                      ClientLevel igoredLevel,
                                                      LivingEntity entity,
                                                      int ignoredSeed)
    {
        if (entity == null || RopeJavelinItem.isThrown(stack)) return 0.0F;
        return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
    }

    private static float hookJavelinThrowingPredicate(ItemStack stack,
                                                      ClientLevel ignoredLevel,
                                                      LivingEntity entity,
                                                      int ignoreSeed)
    {
        if (entity == null || HookJavelinItem.isThrown(stack)) return 0.0F;
        return entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F;
    }

    private static void registerExtensions(RegisterClientExtensionsEvent event)
    {
        registerCustomItemRenderer(event, TFCThingsItems.BISMUTH_BRONZE_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.BLACK_BRONZE_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.BLACK_STEEL_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.BLUE_STEEL_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.BRONZE_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.COPPER_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.RED_STEEL_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.STEEL_ROPE_JAVELIN, RopeJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.WROUGHT_IRON_ROPE_JAVELIN, RopeJavelinItemRenderer::new);

        registerCustomItemRenderer(event, TFCThingsItems.STEEL_HOOK_JAVELIN, HookJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.BLACK_STEEL_HOOK_JAVELIN, HookJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.BLUE_STEEL_HOOK_JAVELIN, HookJavelinItemRenderer::new);
        registerCustomItemRenderer(event, TFCThingsItems.RED_STEEL_HOOK_JAVELIN, HookJavelinItemRenderer::new);
    }

    @SuppressWarnings("unchecked")
    private static <T> void registerCustomItemRenderer(RegisterClientExtensionsEvent event, @Nullable Supplier<? extends ItemLike> itemSupplier, Function<T, BlockEntityWithoutLevelRenderer> rendererFactory)
    {
        if (itemSupplier == null)
        {
            return;
        }
        Item item = itemSupplier.get().asItem();
        event.registerItem(ItemRendererExtension.cached(() -> rendererFactory.apply((T) item)), item);
    }

    private static void registerAdditionalModels(ModelEvent.RegisterAdditional event)
    {
        for (String gem : new String[] {"agate", "amethyst", "beryl", "diamond", "emerald", "garnet", "jade", "jasper", "lapis_lazuli", "nether_quartz", "opal", "prismarine_crystals", "pyrite", "ruby", "sapphire", "topaz", "tourmaline"})
        {
            event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath("tfcthings", "block/gem_display/gems/" + gem)));
        }
        for (String type : new String[] {"heavy", "scatter", "light", "fire", "stone"})
        {
            event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath("tfcthings", "entity/sling_stone/" + type)));
        }
        for (int layer = 1; layer <= 8; layer++)
        {
            // Base fallback models (also re-registered as standalone for BER lookup)
            event.register(ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath("tfcthings", "block/grain_pile/grain_pile_" + layer)));
            // Known TFC grain models
            for (GrainPileBlock.GrainVariant grain : GrainPileBlock.GrainVariant.values())
            {
                if (grain == GrainPileBlock.GrainVariant.UNKNOWN) continue;
                event.register(ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath("tfcthings",
                        "block/grain_pile/" + grain.getSerializedName() + "_" + layer)));
            }
        }
    }

    private static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event)
    {
        event.registerLayerDefinition(HookJavelinModel.LAYER_LOCATION, HookJavelinModel::createBodyLayer);
    }

    private static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event)
    {
        event.registerEntityRenderer(TFCThingsEntities.THROWN_ROPE_BRIDGE.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(TFCThingsEntities.THROWN_ROPE_JAVELIN.get(), ThrownRopeJavelinRenderer::new);
        event.registerEntityRenderer(TFCThingsEntities.THROWN_HOOK_JAVELIN.get(), ThrownHookJavelinRenderer::new);
        event.registerEntityRenderer(TFCThingsEntities.SLING_STONE.get(), SlingStoneRenderer::new);
        event.registerBlockEntityRenderer(TFCThingsBlockEntities.ROPE_BRIDGE.get(), RopeBridgeBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCThingsBlockEntities.GEM_DISPLAY.get(), GemDisplayBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCThingsBlockEntities.GRINDSTONE_BASE.get(), GrindstoneBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(TFCThingsBlockEntities.GRAIN_PILE.get(), GrainPileBlockEntityRenderer::new);
    }
}
