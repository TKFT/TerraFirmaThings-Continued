package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.rustysnail.terrafirmathings.common.block.GrainPileBlock;
import com.rustysnail.terrafirmathings.common.blockentity.GrainPileBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;

public class GrainPileBlockEntityRenderer implements BlockEntityRenderer<GrainPileBlockEntity>
{

    public GrainPileBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredCtx)
    {
    }

    @Override
    public void render(GrainPileBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (be.getLevel() == null)
        {
            return;
        }

        int layer = be.getBlockState().getValue(GrainPileBlock.LAYERS);
        ItemStack grain = be.getGrainType();

        ModelResourceLocation modelId = resolveModel(grain, layer);

        Minecraft mc = Minecraft.getInstance();
        ModelBlockRenderer modelRenderer = mc.getBlockRenderer().getModelRenderer();
        BakedModel model = mc.getModelManager().getModel(modelId);

        RenderType renderType = RenderType.cutoutMipped();
        modelRenderer.renderModel(
            poseStack.last(),
            buffer.getBuffer(renderType),
            null,
            model,
            1.0f, 1.0f, 1.0f,
            packedLight,
            packedOverlay,
            ModelData.EMPTY,
            renderType
        );
    }

    /**
     * Resolves the grain pile model for a given grain item and fill layer.
     * Lookup order:
     *   1. {item_namespace}:block/grain_pile/{item_path_last_segment}_{layer}
     *      — for addon/resource-pack authors providing custom grain pile models
     *   2. tfcthings:block/grain_pile/{known_grain_name}_{layer}
     *      — for known TFC/vanilla grains using existing built-in assets (barley, corn, oat, rice, rye, wheat)
     *   3. tfcthings:block/grain_pile/grain_pile_{layer}
     *      — generic hay-texture fallback for any unrecognised grain
     * Addon authors: place model JSON at
     *   assets/{modid}/models/block/grain_pile/{item_path_last_segment}_{1..8}.json
     * Also register each model as a standalone model via ModelEvent.RegisterAdditional
     * in your mod's client setup
     */
    private static ModelResourceLocation resolveModel(ItemStack grain, int layer)
    {
        ModelManager mm = Minecraft.getInstance().getModelManager();

        if (!grain.isEmpty())
        {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(grain.getItem());
            String path = itemId.getPath();
            String lastSegment = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;

            ModelResourceLocation tier1 = ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath(
                    itemId.getNamespace(), "block/grain_pile/" + lastSegment + "_" + layer));
            if (mm.getModel(tier1) != mm.getMissingModel())
            {
                return tier1;
            }

            GrainPileBlock.GrainVariant variant = GrainPileBlock.GrainVariant.fromItem(grain);
            if (variant != GrainPileBlock.GrainVariant.UNKNOWN)
            {
                ModelResourceLocation tier2 = ModelResourceLocation.standalone(
                    ResourceLocation.fromNamespaceAndPath(
                        "tfcthings", "block/grain_pile/" + variant.getSerializedName() + "_" + layer));
                if (mm.getModel(tier2) != mm.getMissingModel())
                {
                    return tier2;
                }
            }
        }

        return ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(
                "tfcthings", "block/grain_pile/grain_pile_" + layer));
    }
}
