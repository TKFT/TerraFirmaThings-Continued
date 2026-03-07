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

public class SlingStoneModel extends Model
{

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath("tfcthings", "sling_stone"), "main");

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("rock1", CubeListBuilder.create()
                .texOffs(12, 0).addBox(-1, -1.5F, -1, 2, 3, 2),
            PartPose.ZERO);

        partdefinition.addOrReplaceChild("rock2", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-1, -0.5F, -2, 2, 1, 4),
            PartPose.ZERO);

        partdefinition.addOrReplaceChild("rock3", CubeListBuilder.create()
                .texOffs(20, 0).addBox(-2, -0.5F, -1, 4, 1, 2),
            PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    private final ModelPart rock1;
    private final ModelPart rock2;
    private final ModelPart rock3;

    public SlingStoneModel(ModelPart root)
    {
        super(RenderType::entityCutoutNoCull);
        this.rock1 = root.getChild("rock1");
        this.rock2 = root.getChild("rock2");
        this.rock3 = root.getChild("rock3");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color)
    {
        rock1.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        rock2.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        rock3.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
