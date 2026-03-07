package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.JavelinModel;

public class RopeJavelinItemRenderer extends BlockEntityWithoutLevelRenderer
{
    private static final String SUFFIX = "_rope_javelin";

    private static ResourceLocation resolveTexture(Item item)
    {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (path.endsWith(SUFFIX))
        {
            String metal = path.substring(0, path.length() - SUFFIX.length());
            return ResourceLocation.fromNamespaceAndPath("tfc", "textures/entity/projectiles/" + metal + "_javelin.png");
        }
        return ThrownRopeJavelinRenderer.TEXTURE;
    }

    private final ResourceLocation textureLocation;
    private final JavelinModel model;

    public RopeJavelinItemRenderer(Item item)
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.model = new JavelinModel(Minecraft.getInstance().getEntityModels().bakeLayer(RenderHelpers.layerId("javelin")));
        this.textureLocation = resolveTexture(item);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer buffer = ItemRenderer.getFoilBufferDirect(buffers, this.model.renderType(textureLocation), false, stack.hasFoil());
        model.renderToBuffer(poseStack, buffer, packedLight, packedOverlay, -1);
        poseStack.popPose();
    }
}
