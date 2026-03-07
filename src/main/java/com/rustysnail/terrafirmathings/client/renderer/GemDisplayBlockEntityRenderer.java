package com.rustysnail.terrafirmathings.client.renderer;

import java.util.HashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.client.model.GemNormalModel;
import com.rustysnail.terrafirmathings.common.block.GemDisplayBlock;
import com.rustysnail.terrafirmathings.common.blockentity.GemDisplayBlockEntity;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GemDisplayBlockEntityRenderer implements BlockEntityRenderer<GemDisplayBlockEntity>
{
    private static final float[] FACING_TO_ANGLE = new float[] {180f, 0f, 270f, 90f};
    private static final float BLOCK_SCALE = 0.5F;

    private static final float[][] GEM_POSITIONS = {
        {0.25F, 1.1875F, 0.25F},
        {0.75F, 1.1875F, 0.25F},
        {0.50F, 1.1F, 0.775F}
    };

    private static final Map<String, ResourceLocation> GEM_TEXTURES = new HashMap<>();

    @Nullable
    private static String getGemType(ItemStack stack)
    {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if ("tfc".equals(itemId.getNamespace()))
        {
            String path = itemId.getPath();
            if (path.startsWith("gem/"))
            {
                return path.substring("gem/".length());
            }
        }
        return null;
    }

    private static ResourceLocation getGemTexture(String gemType)
    {
        return GEM_TEXTURES.computeIfAbsent(gemType, type ->
            ResourceLocation.fromNamespaceAndPath("tfcthings", "textures/block/gem_display/normal/" + type + ".png"));
    }

    @Nullable
    private static ResourceLocation resolveGemTexture(String gemType)
    {
        ResourceLocation primary = getGemTexture(gemType);
        if (Minecraft.getInstance().getResourceManager().getResource(primary).isPresent())
        {
            return primary;
        }
        return null;
    }

    private static float[] rotatePositionForFacing(float[] localPos, Direction facing)
    {
        float x = localPos[0];
        float y = localPos[1];
        float z = localPos[2];

        float rx;
        float rz;
        switch (facing)
        {
            case SOUTH ->
            {
                rx = 1.0F - x;
                rz = 1.0F - z;
            }
            case EAST ->
            {
                rx = 1.0F - z;
                rz = x;
            }
            case WEST ->
            {
                rx = z;
                rz = 1.0F - x;
            }
            default ->
            {
                rx = x;
                rz = z;
            }
        }
        return new float[] {rx, y, rz};
    }

    private static int resolveGemLight(GemDisplayBlockEntity be, int fallbackLight, float localY)
    {
        if (be.getLevel() == null)
        {
            return fallbackLight;
        }

        BlockPos samplePos = be.getBlockPos().above(localY >= 1.0F ? 1 : 0);
        int sampled = LevelRenderer.getLightColor(be.getLevel(), samplePos);
        return mergeMaxLight(fallbackLight, sampled);
    }

    private static int mergeMaxLight(int a, int b)
    {
        int block = Math.max(LightTexture.block(a), LightTexture.block(b));
        int sky = Math.max(LightTexture.sky(a), LightTexture.sky(b));
        return LightTexture.pack(block, sky);
    }

    private final GemNormalModel gemModel;

    public GemDisplayBlockEntityRenderer(BlockEntityRendererProvider.@NotNull Context ctx)
    {
        this.gemModel = new GemNormalModel(ctx.bakeLayer(GemNormalModel.LAYER_LOCATION));
    }

    @Override
    public void render(GemDisplayBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay)
    {
        if (be.getLevel() == null)
        {
            return;
        }

        Direction facing = be.getBlockState().getValue(GemDisplayBlock.FACING);
        int facingIndex = facing.get2DDataValue();
        for (int slot = 0; slot < GemDisplayBlockEntity.MAX_GEMS; slot++)
        {
            ItemStack gem = be.getGem(slot);
            if (gem.isEmpty())
            {
                continue;
            }

            String gemType = getGemType(gem);
            float[] pos = rotatePositionForFacing(GEM_POSITIONS[slot], facing);
            int gemLight = resolveGemLight(be, packedLight, pos[1]);
            if (gemType != null)
            {
                ResourceLocation gemTexture = resolveGemTexture(gemType);
                if (gemTexture != null)
                {
                    renderGemModel(poseStack, buffer, gemLight, facingIndex, pos[0], pos[1], pos[2], gemTexture);
                }
                else
                {
                    renderGemItem(be, poseStack, buffer, gemLight, packedOverlay, gem, facingIndex, pos[0], pos[1], pos[2]);
                }
            }
            else
            {
                renderGemItem(be, poseStack, buffer, gemLight, packedOverlay, gem, facingIndex, pos[0], pos[1], pos[2]);
            }
        }
    }

    private void renderGemModel(PoseStack poseStack, MultiBufferSource buffer,
                                int packedLight,
                                int facingIndex, float x, float y, float z, ResourceLocation texture)
    {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(FACING_TO_ANGLE[facingIndex]));
        poseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));
        poseStack.translate(0, -22.0 / 16.0, 0);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        gemModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);

        poseStack.popPose();
    }

    private void renderGemItem(GemDisplayBlockEntity be, PoseStack poseStack, MultiBufferSource buffer,
                               int packedLight, int packedOverlay,
                               ItemStack gem, int facingIndex, float x, float y, float z)
    {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(FACING_TO_ANGLE[facingIndex]));
        poseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);

        Minecraft.getInstance().getItemRenderer().renderStatic(
            gem, ItemDisplayContext.FIXED,
            packedLight, packedOverlay,
            poseStack, buffer, be.getLevel(), 0);
        poseStack.popPose();
    }
}
