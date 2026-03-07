package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.block.FishingNetAnchorBlock;
import com.rustysnail.terrafirmathings.common.block.FishingNetBlock;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

//Disabled for now
public class FishingNetAnchorRenderer implements BlockEntityRenderer<FishingNetAnchorBlockEntity>
{

    private static final ResourceLocation NET_TEX = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "textures/block/fishing_net.png");

    private static final int MAX_SPAN = 64;

    private static void putQuad(PoseStack ps, VertexConsumer vc,
                                double x0, double y0, double z0,
                                double x1, double y1, double z1,
                                double x2, double y2, double z2,
                                double x3, double y3, double z3,
                                int light, int overlay)
    {

        var pose = ps.last();

        vc.addVertex(pose, (float) x0, (float) y0, (float) z0)
            .setColor(255, 255, 255, 255)
            .setUv(0f, 0f)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, 0, 1, 0);

        vc.addVertex(pose, (float) x1, (float) y1, (float) z1)
            .setColor(255, 255, 255, 255)
            .setUv(1f, 0f)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, 0, 1, 0);

        vc.addVertex(pose, (float) x2, (float) y2, (float) z2)
            .setColor(255, 255, 255, 255)
            .setUv(1f, 1f)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, 0, 1, 0);

        vc.addVertex(pose, (float) x3, (float) y3, (float) z3)
            .setColor(255, 255, 255, 255)
            .setUv(0f, 1f)
            .setOverlay(overlay)
            .setLight(light)
            .setNormal(pose, 0, 1, 0);
    }

    private static Vec3 getFlow(Level level, BlockPos pos)
    {
        FluidState fs = level.getFluidState(pos);
        return fs.getFlow(level, pos);
    }

    private static Vec3 defaultFlow(Direction.Axis axis)
    {
        return axis == Direction.Axis.X ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0);
    }

    private static int scanLength(Level level, BlockPos start, Direction.Axis axis, int sign)
    {
        for (int i = 1; i <= MAX_SPAN; i++)
        {
            BlockPos p = offset(start, axis, sign * i);
            BlockState s = level.getBlockState(p);
            if (s.getBlock() instanceof FishingNetAnchorBlock) return i;
            if (!(s.getBlock() instanceof FishingNetBlock)) return 0;
        }
        return 0;
    }

    private static boolean isAnchor(Level level, BlockPos pos, Direction.Axis axis)
    {
        BlockState s = level.getBlockState(pos);
        return s.getBlock() instanceof FishingNetAnchorBlock && s.getValue(FishingNetAnchorBlock.AXIS) == axis;
    }

    private static BlockPos offset(BlockPos pos, Direction.Axis axis, int amt)
    {
        return axis == Direction.Axis.X ? pos.offset(amt, 0, 0) : pos.offset(0, 0, amt);
    }

    public FishingNetAnchorRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(FishingNetAnchorBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        Level level = be.getLevel();
        if (level == null) return;

        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof FishingNetAnchorBlock)) return;

        Direction.Axis axis = state.getValue(FishingNetAnchorBlock.AXIS);

        int negLen = scanLength(level, be.getBlockPos(), axis, -1);
        int posLen = scanLength(level, be.getBlockPos(), axis, +1);

        if (negLen > 0 && isAnchor(level, offset(be.getBlockPos(), axis, -negLen), axis)) return;

        int totalLen = negLen + posLen;
        if (totalLen <= 0) return;

        float t = (level.getGameTime() + partialTick) * 0.12f;
        VertexConsumer vc = buffer.getBuffer(RenderType.entityTranslucent(NET_TEX));

        poseStack.pushPose();

        int mid = totalLen / 2;

        for (int i = 1; i < totalLen; i++)
        {
            BlockPos p = offset(be.getBlockPos(), axis, i);
            BlockState seg = level.getBlockState(p);
            if (!(seg.getBlock() instanceof FishingNetBlock) && !(seg.getBlock() instanceof FishingNetAnchorBlock)) break;

            Vec3 flow = getFlow(level, p);
            Vec3 dir = flow.lengthSqr() > 1e-5 ? flow.normalize() : defaultFlow(axis);

            float midFactor = 1f - (Math.abs(i - mid) / (float) Math.max(1, mid));
            float sway = (float) Math.sin(t + i * 0.65f) * (0.10f + 0.10f * midFactor);

            double ox = dir.x * sway;
            double oz = dir.z * sway;

            double w = 0.45;
            double dx = (axis == Direction.Axis.X) ? 0.0 : w;
            double dz = (axis == Direction.Axis.X) ? w : 0.0;

            double yTop = 0.95;
            double yBot = 0.15;

            double cx = (axis == Direction.Axis.X) ? i + 0.5 : 0.5;
            double cz = (axis == Direction.Axis.X) ? 0.5 : i + 0.5;

            putQuad(poseStack, vc,
                cx - dx + ox, yTop, cz - dz + oz,
                cx + dx + ox, yTop, cz + dz + oz,
                cx + dx + ox, yBot, cz + dz + oz,
                cx - dx + ox, yBot, cz - dz + oz,
                packedLight, packedOverlay);

            putQuad(poseStack, vc,
                cx - dx + ox, yTop, cz - dz + oz,
                cx - dx + ox, yBot, cz - dz + oz,
                cx + dx + ox, yBot, cz + dz + oz,
                cx + dx + ox, yTop, cz + dz + oz,
                packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}
