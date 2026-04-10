package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.client.model.HookJavelinModel;
import com.rustysnail.terrafirmathings.common.entity.ThrownHookJavelin;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class ThrownHookJavelinRenderer extends EntityRenderer<ThrownHookJavelin>
{
    private static final float ROPE_THICKNESS = 0.012F;
    private static final float ROPE_SAG_FLYING = 0.4F;
    private static final float ROPE_SAG_GROUNDED = 0.15F;
    private static final String PREFIX = "hook_javelin/";

    private static String resolveMetal(ThrownHookJavelin entity)
    {
        ItemStack weapon = entity.getWeapon();
        if (!weapon.isEmpty())
        {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(weapon.getItem());
            String path = itemId.getPath();
            if (path.startsWith(PREFIX))
            {
                return path.substring(PREFIX.length());
            }
        }
        return "steel";
    }

    private final HookJavelinModel model;

    public ThrownHookJavelinRenderer(EntityRendererProvider.Context context)
    {
        super(context);
        this.model = new HookJavelinModel(context.bakeLayer(HookJavelinModel.LAYER_LOCATION));
    }

    @Override
    public boolean shouldRender(ThrownHookJavelin entity, net.minecraft.client.renderer.culling.Frustum frustum,
                                double camX, double camY, double camZ)
    {
        return true;
    }

    @Override
    public void render(ThrownHookJavelin entity, float entityYaw, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight)
    {
        String metal = resolveMetal(entity);
        ResourceLocation shaftTexture = ResourceLocation.fromNamespaceAndPath(
            "tfc", "textures/entity/projectiles/" + metal + "_javelin.png");
        ResourceLocation headTexture = ResourceLocation.fromNamespaceAndPath(
            "tfcthings", "textures/entity/hook_javelin/" + metal + ".png");

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));

        VertexConsumer shaftBuffer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(shaftTexture), false, false);
        this.model.renderShaft(poseStack, shaftBuffer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        VertexConsumer headBuffer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(headTexture), false, false);
        this.model.renderHookHead(poseStack, headBuffer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();

        Entity owner = entity.getOwner();
        if (owner != null)
        {
            renderRope(entity, owner, partialTick, poseStack, buffer);
        }

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(ThrownHookJavelin entity)
    {
        return ResourceLocation.fromNamespaceAndPath(
            "tfcthings", "textures/entity/hook_javelin/" + resolveMetal(entity) + ".png");
    }

    private void renderRope(ThrownHookJavelin javelin, Entity owner, float partialTick,
                            PoseStack poseStack, MultiBufferSource buffer)
    {
        poseStack.pushPose();
        RopeRenderHelper.RopeRenderData data = RopeRenderHelper.computeRopeRenderData(owner, javelin, partialTick);
        float sag = javelin.isAnchored() ? ROPE_SAG_GROUNDED : ROPE_SAG_FLYING;
        RopeRenderHelper.renderRope(poseStack.last().pose(), buffer, data.dx(), data.dy(), data.dz(), ROPE_THICKNESS, sag);
        poseStack.popPose();
    }
}
