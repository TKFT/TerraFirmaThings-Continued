package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.common.entity.ThrownRopeJavelin;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.JavelinModel;

public class ThrownRopeJavelinRenderer extends EntityRenderer<ThrownRopeJavelin>
{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(
        "tfc", "textures/entity/projectiles/stone_javelin.png");
    private static final float ROPE_THICKNESS = 0.012F;
    private static final float ROPE_SAG = 0.4F;
    private static final String SUFFIX = "_rope_javelin";
    private final JavelinModel model;

    public ThrownRopeJavelinRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new JavelinModel(context.bakeLayer(RenderHelpers.layerId("javelin")));
    }

    @Override
    public boolean shouldRender(ThrownRopeJavelin entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double camX, double camY, double camZ)
    {
        return true;
    }

    @Override
    public void render(ThrownRopeJavelin entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight)
    {
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));

        VertexConsumer vertexConsumer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(getTextureLocation(entity)), false, false);
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
        poseStack.popPose();

        Entity owner = entity.getOwner();
        if (owner != null)
        {
            renderRope(entity, owner, partialTick, poseStack, buffer);
        }

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownRopeJavelin entity)
    {
        String path = BuiltInRegistries.ITEM.getKey(entity.getItem().getItem()).getPath();
        if (path.endsWith(SUFFIX))
        {
            String metal = path.substring(0, path.length() - SUFFIX.length());
            return ResourceLocation.fromNamespaceAndPath("tfc", "textures/entity/projectiles/" + metal + "_javelin.png");
        }
        return net.dries007.tfc.client.render.entity.ThrownJavelinRenderer.JAVELIN_TEXTURES
            .getOrDefault(entity.getItem().getItem(), TEXTURE);
    }

    private void renderRope(ThrownRopeJavelin javelin, Entity owner, float partialTick,
                            PoseStack poseStack, MultiBufferSource buffer)
    {
        poseStack.pushPose();

        Vec3 handPos = RopeRenderHelper.getOwnerHandPosition(owner, partialTick);

        double javelinX = Mth.lerp(partialTick, javelin.xOld, javelin.getX());
        double javelinY = Mth.lerp(partialTick, javelin.yOld, javelin.getY());
        double javelinZ = Mth.lerp(partialTick, javelin.zOld, javelin.getZ());

        Vec3 forward = javelin.getViewVector(partialTick);
        javelinX -= forward.x * 0.35;
        javelinY -= forward.y * 0.35;
        javelinZ -= forward.z * 0.35;

        float dx = (float) (handPos.x - javelinX);
        float dy = (float) (handPos.y - javelinY);
        float dz = (float) (handPos.z - javelinZ);

        RopeRenderHelper.renderRope(poseStack.last().pose(), buffer, dx, dy, dz, ROPE_THICKNESS, ROPE_SAG);

        poseStack.popPose();
    }
}
