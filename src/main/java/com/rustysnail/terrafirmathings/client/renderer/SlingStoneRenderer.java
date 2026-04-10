package com.rustysnail.terrafirmathings.client.renderer;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.common.entity.SlingStoneEntity;
import com.rustysnail.terrafirmathings.common.item.SlingAmmoItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.data.ModelData;

public class SlingStoneRenderer extends EntityRenderer<SlingStoneEntity>
{

    private static final Map<SlingAmmoItem.AmmoType, ModelResourceLocation> MODEL_IDS;

    static
    {
        MODEL_IDS = new EnumMap<>(SlingAmmoItem.AmmoType.class);
        for (SlingAmmoItem.AmmoType type : SlingAmmoItem.AmmoType.values())
        {
            String name = type.name().toLowerCase(Locale.ROOT);
            MODEL_IDS.put(type, ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath("tfcthings", "entity/sling_stone/" + name)));
        }
    }

    public SlingStoneRenderer(EntityRendererProvider.Context context)
    {
        super(context);
    }

    @Override
    public void render(SlingStoneEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight)
    {
        SlingAmmoItem.AmmoType ammoType = entity.getAmmoType();
        ModelResourceLocation modelId = MODEL_IDS.get(ammoType);

        Minecraft minecraft = Minecraft.getInstance();
        BakedModel bakedModel = minecraft.getModelManager().getModel(modelId);

        if (bakedModel != minecraft.getModelManager().getMissingModel())
        {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
            poseStack.translate(-0.5F, -0.5F, -0.5F);

            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
            minecraft.getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(), vertexConsumer, null, bakedModel,
                1.0F, 1.0F, 1.0F, packedLight, OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY, RenderType.cutout());

            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SlingStoneEntity entity)
    {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
