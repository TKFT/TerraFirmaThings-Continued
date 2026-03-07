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
import net.minecraft.world.phys.Vec3;

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

    private static ResourceLocation getShaftTextureLocation(String metal)
    {
        return ResourceLocation.fromNamespaceAndPath("tfc", "textures/entity/projectiles/" + metal + "_javelin.png");
    }

    private static ResourceLocation getHeadTextureLocation(String metal)
    {
        return ResourceLocation.fromNamespaceAndPath("tfcthings", "textures/entity/hook_javelin/" + metal + ".png");
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
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot()) + 90.0F));

        String metal = resolveMetal(entity);
        VertexConsumer shaftBuffer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(getShaftTextureLocation(metal)), false, false);
        this.model.renderShaft(poseStack, shaftBuffer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        VertexConsumer headBuffer = ItemRenderer.getFoilBufferDirect(
            buffer, this.model.renderType(getHeadTextureLocation(metal)), false, false);
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
        return getShaftTextureLocation(resolveMetal(entity));
    }

    private void renderRope(ThrownHookJavelin javelin, Entity owner, float partialTick,
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

        float sag = javelin.isInGroundSynced() ? ROPE_SAG_GROUNDED : ROPE_SAG_FLYING;
        RopeRenderHelper.renderRope(poseStack.last().pose(), buffer, dx, dy, dz, ROPE_THICKNESS, sag);

        poseStack.popPose();
    }
}
