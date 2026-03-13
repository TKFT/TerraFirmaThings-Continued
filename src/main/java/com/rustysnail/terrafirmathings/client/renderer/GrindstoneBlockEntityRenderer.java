package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.common.block.GrindstoneBlock;
import com.rustysnail.terrafirmathings.common.blockentity.GrindstoneBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GrindstoneBlockEntityRenderer implements BlockEntityRenderer<GrindstoneBlockEntity>
{
    private static final float[] FACING_TO_ANGLE = new float[] {270f, 180f, 90f, 0f};
    private static final double TOOL_INSET = 0.38d;

    public GrindstoneBlockEntityRenderer(BlockEntityRendererProvider.@NotNull Context ignoredContext)
    {
    }

    @Override
    public void render(GrindstoneBlockEntity be, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (be.getLevel() == null)
        {
            return;
        }

        final ItemStack grindstone = be.getGrindstone();
        final ItemStack tool = be.getTool();
        if (grindstone.isEmpty() && tool.isEmpty())
        {
            return;
        }

        final Direction facing = be.getBlockState().getValue(GrindstoneBlock.FACING);
        final int facingIndex = facing.get2DDataValue();
        final float facingAngle = FACING_TO_ANGLE[facingIndex];

        if (!grindstone.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(0.5d, 0.5d, 0.5d);

            final float rotationAngle = be.getRotationAngle(partialTick);
            if (facing.getAxis() == Direction.Axis.Z)
            {
                poseStack.mulPose(Axis.XP.rotation(-rotationAngle));
            }
            else
            {
                poseStack.mulPose(Axis.ZP.rotation(-rotationAngle));
            }

            poseStack.mulPose(Axis.YP.rotationDegrees(facingAngle));
            Minecraft.getInstance().getItemRenderer().renderStatic(
                grindstone, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                poseStack, buffer, be.getLevel(), 0);
            poseStack.popPose();
        }

        if (!tool.isEmpty())
        {
            poseStack.pushPose();
            poseStack.translate(
                0.5d + facing.getStepX() * TOOL_INSET,
                0.25d,
                0.5d + facing.getStepZ() * TOOL_INSET
            );
            poseStack.mulPose(Axis.XN.rotationDegrees(90f));
            float toolRoll = facingIndex * 90f;
            if (facing.getAxis() == Direction.Axis.X)
            {
                toolRoll += 180f;
            }
            poseStack.mulPose(Axis.ZP.rotationDegrees(toolRoll));
            poseStack.scale(0.65f, 0.65f, 0.65f);

            Minecraft.getInstance().getItemRenderer().renderStatic(
                tool, ItemDisplayContext.GROUND, packedLight, packedOverlay,
                poseStack, buffer, be.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
