package com.rustysnail.terrafirmathings.client.renderer;

import java.util.LinkedHashMap;
import java.util.Map;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rustysnail.terrafirmathings.common.block.GemDisplayBlock;
import com.rustysnail.terrafirmathings.common.blockentity.GemDisplayBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class GemDisplayBlockEntityRenderer implements BlockEntityRenderer<GemDisplayBlockEntity>
{
    private static final float[] FACING_TO_ANGLE = new float[] {180f, 0f, 270f, 90f};
    private static final float BLOCK_SCALE = 0.5F;

    private static final float[][] GEM_POSITIONS = {
        {0.25F, 1.1875F, 0.25F},
        {0.75F, 1.1875F, 0.25F},
        {0.50F, 1.1F, 0.775F}
    };

    private static final Map<TagKey<Item>, String> C_GEMS_TAG_TO_TYPE = new LinkedHashMap<>();

    static
    {
        addCGemTag("amethyst", "amethyst");
        addCGemTag("diamond", "diamond");
        addCGemTag("emerald", "emerald");
        addCGemTag("lapis", "lapis_lazuli");
        addCGemTag("lapis_lazuli", "lapis_lazuli");
        addCGemTag("opal", "opal");
        addCGemTag("pyrite", "pyrite");
        addCGemTag("ruby", "ruby");
        addCGemTag("sapphire", "sapphire");
        addCGemTag("topaz", "topaz");
        addCGemTag("agate", "agate");
        addCGemTag("beryl", "beryl");
        addCGemTag("garnet", "garnet");
        addCGemTag("jade", "jade");
        addCGemTag("jasper", "jasper");
        addCGemTag("tourmaline", "tourmaline");
        addCGemTag("quartz", "nether_quartz");
        addCGemTag("prismarine", "prismarine_crystals");
    }

    private static void addCGemTag(String tagSuffix, String modelName)
    {
        C_GEMS_TAG_TO_TYPE.put(
            ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "gems/" + tagSuffix)),
            modelName
        );
    }

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
        for (Map.Entry<TagKey<Item>, String> entry : C_GEMS_TAG_TO_TYPE.entrySet())
        {
            if (stack.is(entry.getKey()))
            {
                return entry.getValue();
            }
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

    public GemDisplayBlockEntityRenderer(BlockEntityRendererProvider.Context ignoredCtx)
    {
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

            float[] pos = rotatePositionForFacing(GEM_POSITIONS[slot], facing);
            int gemLight = resolveGemLight(be, packedLight, pos[1]);

            ModelResourceLocation modelId = resolveGemModel(gem);
            if (modelId != null && renderGemJsonModel(poseStack, buffer, gemLight, facingIndex, pos[0], pos[1], pos[2], modelId))
            {
                continue;
            }
            renderGemItem(be, poseStack, buffer, gemLight, packedOverlay, gem, facingIndex, pos[0], pos[1], pos[2]);
        }
    }

    /**
     * Resolves a gem-display baked model for the given gem item stack.
     * Lookup order:
     *   1. {item_namespace}:block/gem_display/gems/{item_path_last_segment}
     *      — addon/resource-pack path; must be registered as a standalone model
     *   2. tfcthings:block/gem_display/gems/{gem_type}
     *      — built-in compat: TFC gems (tfc:gem/*) by path, vanilla/cross-mod gems by c:gems/* tag
     *   3. none found
     *      — falls back to item rendering (no purple/missing-model artifacts)
     * mymod:gems/emerald_shard → last segment "emerald_shard" → tier-1 tries mymod:block/gem_display/gems/emerald_shard
     * Addon authors: place model JSON at
     * assets/{modid}/models/block/gem_display/gems/{item_path_last_segment}.json
     * and register it via ModelEvent.RegisterAdditional in your mod's client setup.
     */
    @Nullable
    private static ModelResourceLocation resolveGemModel(ItemStack gem)
    {
        ModelManager mm = Minecraft.getInstance().getModelManager();

        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(gem.getItem());
        String path = itemId.getPath();
        String lastSegment = path.contains("/") ? path.substring(path.lastIndexOf('/') + 1) : path;

        ModelResourceLocation tier1 = ModelResourceLocation.standalone(
            ResourceLocation.fromNamespaceAndPath(
                itemId.getNamespace(), "block/gem_display/gems/" + lastSegment));
        if (mm.getModel(tier1) != mm.getMissingModel())
        {
            return tier1;
        }

        String gemType = getGemType(gem);
        if (gemType != null)
        {
            ModelResourceLocation tier2 = ModelResourceLocation.standalone(
                ResourceLocation.fromNamespaceAndPath("tfcthings", "block/gem_display/gems/" + gemType));
            if (mm.getModel(tier2) != mm.getMissingModel())
            {
                return tier2;
            }
        }

        return null;
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

    private boolean renderGemJsonModel(PoseStack poseStack, MultiBufferSource buffer,
                                       int packedLight,
                                       int facingIndex, float x, float y, float z,
                                       ModelResourceLocation modelId)
    {
        Minecraft minecraft = Minecraft.getInstance();
        var modelManager = minecraft.getModelManager();
        var bakedModel = modelManager.getModel(modelId);

        if (bakedModel == modelManager.getMissingModel())
        {
            return false;
        }

        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(Axis.YP.rotationDegrees(FACING_TO_ANGLE[facingIndex]));
        poseStack.scale(BLOCK_SCALE, BLOCK_SCALE, BLOCK_SCALE);
        poseStack.translate(-0.5F, -0.2F, -0.5F);

        var blockRenderer = minecraft.getBlockRenderer();
        var modelRenderer = blockRenderer.getModelRenderer();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.translucent());

        modelRenderer.renderModel(
            poseStack.last(),
            vertexConsumer,
            null,
            bakedModel,
            1.0F, 1.0F, 1.0F,
            packedLight,
            OverlayTexture.NO_OVERLAY,
            ModelData.EMPTY,
            RenderType.translucent()
        );

        poseStack.popPose();
        return true;
    }
}
