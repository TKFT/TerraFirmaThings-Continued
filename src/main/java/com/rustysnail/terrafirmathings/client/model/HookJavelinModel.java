package com.rustysnail.terrafirmathings.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class HookJavelinModel extends Model
{
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath("tfcthings", "hook_javelin"), "main");

    public static LayerDefinition createBodyLayer()
    {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bbMain = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        bbMain.addOrReplaceChild("shaft", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-0.5F, -0.5F, -16.0F, 1.0F, 1.0F, 32.0F, new CubeDeformation(0.0F)),
            PartPose.offsetAndRotation(0.0F, -5.0F, 0.0F, 0.0F, 1.5708F, 1.5708F));

        bbMain.addOrReplaceChild("hook_head", CubeListBuilder.create()
                .texOffs(0, 0).addBox(0.0F, -1.5F, -0.5F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(0.5F, -1.0F, -2.0F, 1.0F, 1.0F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(8, 0).addBox(0.5F, 0.5F, -0.5F, 1.0F, 1.5F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(8, 3).addBox(0.5F, 1.5F, 0.5F, 1.0F, 0.5F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(12, 0).addBox(0.5F, -3.0F, -0.5F, 1.0F, 1.5F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 3).addBox(0.5F, -3.0F, 0.5F, 1.0F, 0.5F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(20, 0).addBox(2.0F, -1.0F, -0.5F, 1.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 2).addBox(3.0F, -1.0F, 0.5F, 0.5F, 1.0F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(26, 0).addBox(-1.5F, -1.0F, -0.5F, 1.5F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 2).addBox(-1.5F, -1.0F, 0.5F, 0.5F, 1.0F, 1.5F, new CubeDeformation(0.0F))
                .texOffs(0, 7).addBox(0.0F, -1.5F, 1.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)),

            PartPose.offsetAndRotation(-0.5F, -23.5F, 1.0F, 0.0F, 1.5708F, 1.5708F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    private final ModelPart root;
    private final ModelPart shaft;
    private final ModelPart hookHead;

    public HookJavelinModel(ModelPart root)
    {
        super(RenderType::entitySolid);
        this.root = root;

        ModelPart bbMain = root.getChild("bb_main");
        this.shaft = bbMain.getChild("shaft");
        this.hookHead = bbMain.getChild("hook_head");
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color)
    {
        renderShaft(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        renderHookHead(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    public void renderShaft(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color)
    {
        boolean shaftVisible = this.shaft.visible;
        boolean headVisible = this.hookHead.visible;

        this.shaft.visible = true;
        this.hookHead.visible = false;
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);

        this.shaft.visible = shaftVisible;
        this.hookHead.visible = headVisible;
    }

    public void renderHookHead(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color)
    {
        boolean shaftVisible = this.shaft.visible;
        boolean headVisible = this.hookHead.visible;

        this.shaft.visible = false;
        this.hookHead.visible = true;
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);

        this.shaft.visible = shaftVisible;
        this.hookHead.visible = headVisible;
    }
}
