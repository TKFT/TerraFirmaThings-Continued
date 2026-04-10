package com.rustysnail.terrafirmathings.compat.curios.client;

import java.util.List;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.client.ICurioRenderer;

public class CrownCurioRenderer implements ICurioRenderer
{

    private HumanoidArmorModel<LivingEntity> armorModel;

    private HumanoidArmorModel<LivingEntity> getArmorModel()
    {
        if (armorModel == null)
        {
            armorModel = new HumanoidArmorModel<>(
                Minecraft.getInstance().getEntityModels().bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR));
        }
        return armorModel;
    }

    @Override
    public <T extends LivingEntity, M extends EntityModel<T>> void render(
        ItemStack stack,
        SlotContext slotContext,
        PoseStack poseStack,
        RenderLayerParent<T, M> renderLayerParent,
        MultiBufferSource buffer,
        int packedLight,
        float limbSwing,
        float limbSwingAmount,
        float partialTick,
        float ageInTicks,
        float netHeadYaw,
        float headPitch)
    {
        if (!(stack.getItem() instanceof ArmorItem armorItem))
        {
            return;
        }

        if (!(renderLayerParent.getModel() instanceof HumanoidModel<?>))
        {
            return;
        }

        List<ArmorMaterial.Layer> layers = armorItem.getMaterial().value().layers();
        if (layers.isEmpty())
        {
            return;
        }
        ResourceLocation texture = layers.getFirst().texture(false);

        LivingEntity entity = slotContext.entity();
        HumanoidArmorModel<LivingEntity> model = getArmorModel();

        ICurioRenderer.followBodyRotations(entity, model);

        model.setAllVisible(false);
        model.head.visible = true;
        model.hat.visible = true;

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(
            buffer, RenderType.armorCutoutNoCull(texture), stack.hasFoil());
        model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, -1);
    }
}
