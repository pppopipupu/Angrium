package com.pppopipupu.angry.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pppopipupu.angry.AngryLightingBolt;
import com.pppopipupu.angry.block.MultiPartBlock;
import com.pppopipupu.angry.tileentity.AngryBlockEntity;
import com.pppopipupu.angry.tileentity.AngryFemaleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class AngryFemaleBlockRenderer implements BlockEntityRenderer<AngryFemaleEntity> {

    private final BlockRenderDispatcher blockRenderer;
    private BakedModel model;
    private final RandomSource random = RandomSource.create();

    public AngryFemaleBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AngryFemaleEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        BlockState state = blockEntity.getBlockState();
        if (!state.getValue(MultiPartBlock.IS_CORE)) {
            return;
        }
        if (model == null) {
            model = this.blockRenderer.getBlockModel(state);
        }
        poseStack.pushPose();
        Direction facing = state.getValue(MultiPartBlock.FACING);
        switch (facing) {
            case WEST:
                poseStack.translate(2.0, 0.0, 1.0);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                break;
            case NORTH:
                poseStack.translate(0.0, 0.0, 2.0);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                break;
            case SOUTH:
                poseStack.translate(1.0, 0.0, -1.0);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                break;
            case EAST:
                poseStack.translate(-1.0, 0.0, 0.0);
            default:
                break;
        }
        float interpolatedAngle = Mth.lerp(partialTick, blockEntity.prevRotationAngle, blockEntity.rotationAngle);

        if (interpolatedAngle != 0.0f) {
            poseStack.translate(1.0, 1.0, 1.0);
            poseStack.mulPose(Axis.YP.rotationDegrees(interpolatedAngle));
            poseStack.translate(-1.0, -1.0, -1.0);
        }

        var vertexConsumer = bufferSource.getBuffer(RenderType.solid());
        this.blockRenderer.getModelRenderer().renderModel(
                poseStack.last(),
                vertexConsumer,
                state,
                model,
                1.0f, 1.0f, 1.0f,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }
}
