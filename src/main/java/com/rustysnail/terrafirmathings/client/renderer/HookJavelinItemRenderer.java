package com.rustysnail.terrafirmathings.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rustysnail.terrafirmathings.client.model.HookJavelinModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class HookJavelinItemRenderer extends BlockEntityWithoutLevelRenderer
{
    private static final String PREFIX = "hook_javelin/";

    private static String resolveMetal(Item item)
    {
        String path = BuiltInRegistries.ITEM.getKey(item).getPath();
        if (path.startsWith(PREFIX))
        {
            return path.substring(PREFIX.length());
        }
        return "steel";
    }

    private static ResourceLocation resolveShaftTexture(String metal)
    {
        return ResourceLocation.fromNamespaceAndPath("tfc", "textures/entity/projectiles/" + metal + "_javelin.png");
    }

    private static ResourceLocation resolveHeadTexture(String metal)
    {
        return ResourceLocation.fromNamespaceAndPath("tfcthings", "textures/entity/hook_javelin/" + metal + ".png");
    }

    private final ResourceLocation shaftTextureLocation;
    private final ResourceLocation headTextureLocation;
    private final HookJavelinModel model;

    public HookJavelinItemRenderer(Item item)
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
        this.model = new HookJavelinModel(Minecraft.getInstance().getEntityModels().bakeLayer(HookJavelinModel.LAYER_LOCATION));
        String metal = resolveMetal(item);
        this.shaftTextureLocation = resolveShaftTexture(metal);
        this.headTextureLocation = resolveHeadTexture(metal);
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffers, int packedLight, int packedOverlay)
    {
        poseStack.pushPose();
        poseStack.scale(1.0F, -1.0F, -1.0F);
        VertexConsumer shaftBuffer = ItemRenderer.getFoilBufferDirect(buffers, this.model.renderType(shaftTextureLocation), false, stack.hasFoil());
        model.renderShaft(poseStack, shaftBuffer, packedLight, packedOverlay, -1);

        VertexConsumer headBuffer = ItemRenderer.getFoilBufferDirect(buffers, this.model.renderType(headTextureLocation), false, stack.hasFoil());
        model.renderHookHead(poseStack, headBuffer, packedLight, packedOverlay, -1);
        poseStack.popPose();
    }
}
