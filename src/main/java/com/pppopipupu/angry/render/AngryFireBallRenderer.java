package com.pppopipupu.angry.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.entity.AngryFireBall;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.neoforged.neoforge.client.model.data.ModelData;

public class AngryFireBallRenderer extends EntityRenderer<AngryFireBall> {
    private BakedModel bakedModel;

    public AngryFireBallRenderer(EntityRendererProvider.Context context) {
        super(context);
      bakedModel =  context.getBlockRenderDispatcher().getBlockModel(Angry.ANGRY_BLOCK.get().defaultBlockState());
        this.shadowRadius = 0.2F;
    }

    @Override
    public void render(AngryFireBall entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (bakedModel == null) {
            return;
        }
        poseStack.pushPose();

        poseStack.translate(0.0, 0.25, 0.0);
        poseStack.scale(0.3f, 0.3f, 0.3f);

        float rotY = entity.getRotationY(partialTicks);
        float rotX = entity.getRotationX(partialTicks);

        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.5F, -0.5F, -0.5F);

        VertexConsumer vertexconsumer = buffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(entity)));
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(
                poseStack.last(),
                vertexconsumer,
                null,
                bakedModel
                ,
                1.0F, 1.0F, 1.0F,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.solid()
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AngryFireBall angryFireBall) {
        return ResourceLocation.fromNamespaceAndPath(Angry.MODID, "textures/block/face_angry.png");
    }

}