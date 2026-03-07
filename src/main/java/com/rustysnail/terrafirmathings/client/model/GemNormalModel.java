package com.rustysnail.terrafirmathings.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class GemNormalModel extends Model
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath("tfcthings", "gem_normal"), "main");

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("core", CubeListBuilder.create()
                .texOffs(0, 25).addBox(-1.0F, -1.0F, -3.0F, 2.0F, 1.0F, 6.0F)
                .texOffs(16, 24).addBox(-3.0F, -1.0F, -1.0F, 6.0F, 1.0F, 2.0F)
                .texOffs(16, 27).addBox(-2.0F, -1.0F, -2.0F, 4.0F, 1.0F, 4.0F)
                .texOffs(0, 22).addBox(-2.0F, 0.0F, -1.0F, 4.0F, 1.0F, 2.0F)
                .texOffs(20, 19).addBox(-1.0F, 0.0F, -2.0F, 2.0F, 1.0F, 4.0F)
                .texOffs(12, 21).addBox(-1.0F, 1.0F, -1.0F, 2.0F, 1.0F, 2.0F)
                .texOffs(0, 17).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 1.0F, 4.0F)
                .texOffs(12, 18).addBox(-2.0F, -2.0F, -1.0F, 4.0F, 1.0F, 2.0F),
            PartPose.offset(0.0F, 22.0F, 0.0F));

        return LayerDefinition.create(meshDefinition, 32, 32);
    }

    private final ModelPart core;

    public GemNormalModel(ModelPart root)
    {
        super(RenderType::entityCutoutNoCull);
        this.core = root.getChild("core");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color)
    {
        core.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
