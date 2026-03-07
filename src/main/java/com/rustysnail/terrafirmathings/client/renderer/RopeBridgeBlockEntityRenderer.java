package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.common.block.RopeBridgeBlock;
import com.rustysnail.terrafirmathings.common.blockentity.RopeBridgeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

public class RopeBridgeBlockEntityRenderer implements BlockEntityRenderer<RopeBridgeBlockEntity>
{

    private static final RandomSource RAND = RandomSource.create();

    private static void renderBakedBlockModel(BlockState state, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay)
    {
        var dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel model = dispatcher.getBlockModel(state);

        ModelBlockRenderer modelRenderer = dispatcher.getModelRenderer();
        RAND.setSeed(42L);

        for (var rt : model.getRenderTypes(state, RAND, ModelData.EMPTY))
        {
            modelRenderer.renderModel(
                poseStack.last(),
                buffer.getBuffer(rt),
                state,
                model,
                1.0f, 1.0f, 1.0f,
                light,
                overlay,
                ModelData.EMPTY,
                rt
            );
        }
    }

    public RopeBridgeBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredCtx) {}

    @Override
    public void render(RopeBridgeBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay)
    {

        Level level = be.getLevel();
        if (level == null) return;

        BlockState state = be.getBlockState();
        if (!(state.getBlock() instanceof RopeBridgeBlock)) return;

        float t = level.getGameTime() + partialTick;

        boolean axisNS = state.getValue(RopeBridgeBlock.AXIS);

        float seed = (be.getBlockPos().getX() * 31 + be.getBlockPos().getZ() * 17) * 0.02f;
        float idleSway = (float) Math.sin(t * 0.06f + seed) * 0.03f;
        float idleBob = (float) Math.cos(t * 0.05f + seed) * 0.02f;

        float impulse = 0f;
        if (be.getLastStepTick() >= 0)
        {
            float dt = (t - be.getLastStepTick());
            if (dt >= 0f && dt < 40f)
            {
                float damping = (float) Math.exp(-dt * 0.12f);
                impulse = (float) (Math.sin(dt * 0.9f) * damping) * (0.07f * be.getStepStrength());
            }
        }

        poseStack.pushPose();

        poseStack.translate(0.5, 0.0, 0.5);

        poseStack.translate(0.0, idleBob + impulse, 0.0);

        if (axisNS) poseStack.translate(idleSway, 0.0, 0.0);
        else poseStack.translate(0.0, 0.0, idleSway);

        float tilt = idleSway * 0.6f;
        if (axisNS) poseStack.mulPose(Axis.ZP.rotation(tilt));
        else poseStack.mulPose(Axis.XP.rotation(tilt));

        poseStack.translate(-0.5, 0.0, -0.5);

        renderBakedBlockModel(state, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
