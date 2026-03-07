package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public final class RopeRenderHelper
{
    public static final int ROPE_SEGMENTS = 24;

    private static final int ROPE_RED = 96;
    private static final int ROPE_GREEN = 62;
    private static final int ROPE_BLUE = 28;
    private static final int ROPE_RED_DARK = 68;
    private static final int ROPE_GREEN_DARK = 43;
    private static final int ROPE_BLUE_DARK = 18;

    public static Vec3 getOwnerHandPosition(Entity owner, float partialTick)
    {
        double x = Mth.lerp(partialTick, owner.xOld, owner.getX());
        double y = Mth.lerp(partialTick, owner.yOld, owner.getY());
        double z = Mth.lerp(partialTick, owner.zOld, owner.getZ());

        if (owner instanceof Player player)
        {
            Minecraft mc = Minecraft.getInstance();
            boolean isFirstPerson = player == mc.player && mc.options.getCameraType().isFirstPerson();
            float yaw = (float) Math.toRadians(Mth.lerp(partialTick, owner.yRotO, owner.getYRot()));
            float handSide = player.getMainArm() == HumanoidArm.RIGHT ? 1.0F : -1.0F;

            if (isFirstPerson)
            {
                x += -Math.sin(yaw) * 0.3 * handSide;
                y += owner.getEyeHeight() - 0.3;
                z += Math.cos(yaw) * 0.3 * handSide;
            }
            else
            {
                x += -Math.sin(yaw + Math.PI / 2) * 0.35 * handSide;
                y += owner.getEyeHeight() * 0.6;
                z += Math.cos(yaw + Math.PI / 2) * 0.35 * handSide;
            }
        }
        else
        {
            y += owner.getBbHeight() * 0.5;
        }

        return new Vec3(x, y, z);
    }

    public static void renderRope(Matrix4f poseMatrix, MultiBufferSource buffer,
                                  float dx, float dy, float dz,
                                  float thickness, float sagMultiplier)
    {
        VertexConsumer vc = buffer.getBuffer(RenderType.leash());
        float sagAmount = Math.min(1.0F, (float) Math.sqrt(dx * dx + dy * dy + dz * dz) / 8.0F);

        for (int i = 0; i < ROPE_SEGMENTS; i++)
        {
            float t1 = (float) i / ROPE_SEGMENTS;
            float t2 = (float) (i + 1) / ROPE_SEGMENTS;

            float sag1 = (float) Math.sin(t1 * Math.PI) * sagMultiplier * sagAmount;
            float sag2 = (float) Math.sin(t2 * Math.PI) * sagMultiplier * sagAmount;

            float x1 = dx * t1;
            float y1 = dy * t1 - sag1;
            float z1 = dz * t1;
            float x2 = dx * t2;
            float y2 = dy * t2 - sag2;
            float z2 = dz * t2;

            // Top face (bright)
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 + thickness, z1 - thickness,
                x1 + thickness, y1 + thickness, z1 - thickness,
                x2 + thickness, y2 + thickness, z2 - thickness,
                x2 - thickness, y2 + thickness, z2 - thickness,
                ROPE_RED, ROPE_GREEN, ROPE_BLUE);
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 + thickness, z1 + thickness,
                x1 + thickness, y1 + thickness, z1 + thickness,
                x2 + thickness, y2 + thickness, z2 + thickness,
                x2 - thickness, y2 + thickness, z2 + thickness,
                ROPE_RED, ROPE_GREEN, ROPE_BLUE);

            // Bottom face (dark)
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 - thickness, z1 - thickness,
                x1 + thickness, y1 - thickness, z1 - thickness,
                x2 + thickness, y2 - thickness, z2 - thickness,
                x2 - thickness, y2 - thickness, z2 - thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 - thickness, z1 + thickness,
                x1 + thickness, y1 - thickness, z1 + thickness,
                x2 + thickness, y2 - thickness, z2 + thickness,
                x2 - thickness, y2 - thickness, z2 + thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);

            // Left face
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 - thickness, z1 - thickness,
                x1 - thickness, y1 + thickness, z1 - thickness,
                x2 - thickness, y2 + thickness, z2 - thickness,
                x2 - thickness, y2 - thickness, z2 - thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 - thickness, z1 + thickness,
                x1 - thickness, y1 + thickness, z1 + thickness,
                x2 - thickness, y2 + thickness, z2 + thickness,
                x2 - thickness, y2 - thickness, z2 + thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);

            // Right face
            addQuad(vc, poseMatrix,
                x1 + thickness, y1 - thickness, z1 - thickness,
                x1 + thickness, y1 + thickness, z1 - thickness,
                x2 + thickness, y2 + thickness, z2 - thickness,
                x2 + thickness, y2 - thickness, z2 - thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);
            addQuad(vc, poseMatrix,
                x1 + thickness, y1 - thickness, z1 + thickness,
                x1 + thickness, y1 + thickness, z1 + thickness,
                x2 + thickness, y2 + thickness, z2 + thickness,
                x2 + thickness, y2 - thickness, z2 + thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);

            // Front face (segment start cap)
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 - thickness, z1 + thickness,
                x1 + thickness, y1 - thickness, z1 + thickness,
                x1 + thickness, y1 + thickness, z1 + thickness,
                x1 - thickness, y1 + thickness, z1 + thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);

            // Back face (segment start cap)
            addQuad(vc, poseMatrix,
                x1 - thickness, y1 - thickness, z1 - thickness,
                x1 + thickness, y1 - thickness, z1 - thickness,
                x1 + thickness, y1 + thickness, z1 - thickness,
                x1 - thickness, y1 + thickness, z1 - thickness,
                ROPE_RED_DARK, ROPE_GREEN_DARK, ROPE_BLUE_DARK);
        }
    }

    private static void addQuad(VertexConsumer vc, Matrix4f pose,
                                float x1, float y1, float z1,
                                float x2, float y2, float z2,
                                float x3, float y3, float z3,
                                float x4, float y4, float z4,
                                int r, int g, int b)
    {
        addVertex(vc, pose, x1, y1, z1, r, g, b);
        addVertex(vc, pose, x2, y2, z2, r, g, b);
        addVertex(vc, pose, x3, y3, z3, r, g, b);
        addVertex(vc, pose, x4, y4, z4, r, g, b);
    }

    private static void addVertex(VertexConsumer vc, Matrix4f pose,
                                  float x, float y, float z, int r, int g, int b)
    {
        vc.addVertex(pose, x, y, z)
            .setColor(r, g, b, 255)
            .setUv(0, 0)
            .setOverlay(OverlayTexture.NO_OVERLAY)
            .setLight(0xF000F0)
            .setNormal(0, 1, 0);
    }

    private RopeRenderHelper() {}
}
