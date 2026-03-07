package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.client.model.SlingStoneModel;
import com.rustysnail.terrafirmathings.common.entity.SlingStoneEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class SlingStoneRenderer extends EntityRenderer<SlingStoneEntity>
{

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "tfcthings", "textures/entity/slingstone.png");

    private final SlingStoneModel model;

    public SlingStoneRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new SlingStoneModel(context.bakeLayer(SlingStoneModel.LAYER_LOCATION));
    }

    @Override
    public void render(SlingStoneEntity entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight)
    {
        poseStack.pushPose();

        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));

        poseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(TEXTURE), false, false);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SlingStoneEntity entity)
    {
        return TEXTURE;
    }
}
