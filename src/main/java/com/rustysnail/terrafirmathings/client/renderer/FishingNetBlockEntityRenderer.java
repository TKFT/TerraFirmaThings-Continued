package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rustysnail.terrafirmathings.common.block.FishingNetBlock;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

import static net.minecraft.core.Direction.*;

//Disabled for now
public class FishingNetBlockEntityRenderer implements BlockEntityRenderer<FishingNetBlockEntity>
{

    private static final int THICK_PX = 2;
    private static final int MAX_SHIFT_PX = 6;
    private static final float OMEGA = 0.10f;

    private static int computeShiftPx(Level level, BlockPos pos, int sag)
    {
        float amp = sag / 7.0f;
        float t = (level.getGameTime() + (pos.asLong() & 15)) * OMEGA;
        float tri = 0.5f - 0.5f * Mth.cos(t);
        float px = amp * MAX_SHIFT_PX * tri;
        return Mth.clamp(Math.round(px), 0, MAX_SHIFT_PX);
    }

    private static void renderBakedBlockModel(Level level, BlockState state, BlockPos pos,
                                              PoseStack ps, MultiBufferSource buffers,
                                              int light, int overlay)
    {

        Minecraft mc = Minecraft.getInstance();
        BlockRenderDispatcher brd = mc.getBlockRenderer();
        BakedModel model = brd.getBlockModel(state);

        ModelBlockRenderer mbr = brd.getModelRenderer();

        for (RenderType rt : model.getRenderTypes(state, RandomSource.create(Mth.getSeed(pos)), ModelData.EMPTY))
        {
            VertexConsumer vc = buffers.getBuffer(rt);
            mbr.renderModel(ps.last(), vc, state, model, 1f, 1f, 1f, light, overlay, ModelData.EMPTY, rt);
        }
    }

    private static Vec3 sampleFlow(Level level, BlockPos pos)
    {
        Vec3 sum = Vec3.ZERO;
        int n = 0;
        for (Direction d : new Direction[] {NORTH, SOUTH, EAST, WEST})
        {
            BlockPos p = pos.relative(d);
            FluidState fs = level.getFluidState(p);
            Vec3 f = fs.getFlow(level, p);
            if (f.lengthSqr() > 1.0e-6)
            {
                sum = sum.add(f);
                n++;
            }
        }
        return n == 0 ? Vec3.ZERO : sum.scale(1.0 / n);
    }

    public FishingNetBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(FishingNetBlockEntity be, float partialTick, PoseStack ps,
                       MultiBufferSource buffers, int light, int overlay)
    {

        Level level = be.getLevel();
        if (level == null) return;

        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof FishingNetBlock)) return;
        BlockPos pos = be.getBlockPos();
        Direction.Axis axis = state.getValue(FishingNetBlock.AXIS);
        int sag = state.getValue(FishingNetBlock.SAG);
        int shiftPx = computeShiftPx(level, pos, sag); // your motion function
        int basePx = shiftPx;
        float shift = basePx / 16f;
        float t = level.getGameTime() + partialTick;
        float seed = (be.getBlockPos().getX() * 31 + be.getBlockPos().getZ() * 17) * 0.13f;

        //Flow bow: sample neighbor fluid flow
        Vec3 flow = sampleFlow(level, pos);
        boolean positive = (axis == Direction.Axis.X) ? flow.z >= 0 : flow.x >= 0;
        basePx = positive ? shiftPx : (16 - THICK_PX - shiftPx);
        // Perpendicular component bows the net "downstream"
        float perp = (axis == Direction.Axis.X) ? (float) flow.z : (float) flow.x;
        perp = Mth.clamp(perp, -1f, 1f);

        float bow = perp * 0.12f; // tune

        float rippleY = (float) Math.sin(t * 0.10f + seed) * 0.01f;
        float rippleSide = (float) Math.sin(t * 0.14f + seed * 1.7f) * 0.008f;

        ps.pushPose();

        ps.translate(0.5, 0.0, 0.5);


        ps.translate(0.5, 0, 0.5);
        if (axis == Direction.Axis.X) ps.translate(0, 0, shift);
        else ps.translate(shift, 0, 0);
        ps.translate(-0.5, 0, -0.5);


        ps.translate(0.5, 0, 0.5);
        if (axis == Direction.Axis.X) ps.translate(0, 0, shift);
        else ps.translate(shift, 0, 0);
        ps.translate(-0.5, 0, -0.5);

        renderBakedBlockModel(level, state, be.getBlockPos(), ps, buffers, light, overlay);

        ps.popPose();
    }

}
