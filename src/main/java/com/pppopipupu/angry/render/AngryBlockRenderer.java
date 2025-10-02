package com.pppopipupu.angry.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.pppopipupu.angry.AngryLightingBolt;
import com.pppopipupu.angry.block.AngryBlock;
import com.pppopipupu.angry.block.MultiPartBlock;
import com.pppopipupu.angry.tileentity.AngryBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.List;

public class AngryBlockRenderer implements BlockEntityRenderer<AngryBlockEntity> {

    private final BlockRenderDispatcher blockRenderer;
    private BakedModel model;
 //   private final RandomSource random = RandomSource.create();

    public AngryBlockRenderer(BlockEntityRendererProvider.Context context) {
        this.blockRenderer = context.getBlockRenderDispatcher();
    }

    @Override
    public void render(AngryBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
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
        if (state.getValue(AngryBlock.IS_LIGHTNING)) {
            poseStack.pushPose();

            poseStack.translate(1.0, 1.0, 1.0);

            VertexConsumer lightningConsumer = bufferSource.getBuffer(RenderType.lightning());
            List<AngryLightingBolt> bolts = blockEntity.getActiveBolts();
            for (AngryLightingBolt bolt : bolts) {
                recursivelyRenderBolt(poseStack, lightningConsumer, bolt, partialTick);
            }

            poseStack.popPose();
        }
        poseStack.popPose();
    }

    private void recursivelyRenderBolt(PoseStack poseStack, VertexConsumer builder, AngryLightingBolt bolt, float partialTick) {
        if (bolt.life + partialTick <= 0) {
            return;
        }


        Matrix4f matrix = poseStack.last().pose();
        Vector3f lastSegment = null;

        for (Vector3f segment : bolt.segments) {
            if (lastSegment != null) {
                renderVolumetricSegment(matrix, builder, lastSegment, segment, bolt.thickness, bolt.red, bolt.green, bolt.blue, 1.0f);
            }
            lastSegment = segment;
        }

        for (AngryLightingBolt child : bolt.children) {
            recursivelyRenderBolt(poseStack, builder, child, partialTick);
        }
    }

    private void renderVolumetricSegment(Matrix4f matrix, VertexConsumer builder, Vector3f start, Vector3f end, float thickness, float r, float g, float b, float a) {
        Vector3f dir = new Vector3f(end).sub(start);
        if (dir.lengthSquared() == 0) {
            return;
        }
        dir.normalize();

        Vector3f up = new Vector3f(0, 1, 0);
        if (Math.abs(dir.dot(up)) > 0.999f) {
            up = new Vector3f(1, 0, 0);
        }
        Vector3f side1 = new Vector3f(dir).cross(up).normalize().mul(thickness / 2.0f);
        Vector3f side2 = new Vector3f(dir).cross(side1).normalize().mul(thickness / 2.0f);

        Vector3f p1_1 = new Vector3f(start).sub(side1);
        Vector3f p1_2 = new Vector3f(start).add(side1);
        Vector3f p1_3 = new Vector3f(end).add(side1);
        Vector3f p1_4 = new Vector3f(end).sub(side1);

        builder.addVertex(matrix, p1_1.x(), p1_1.y(), p1_1.z()).setColor(r, g, b, a);
        builder.addVertex(matrix, p1_2.x(), p1_2.y(), p1_2.z()).setColor(r, g, b, a);
        builder.addVertex(matrix, p1_3.x(), p1_3.y(), p1_3.z()).setColor(r, g, b, a);
        builder.addVertex(matrix, p1_4.x(), p1_4.y(), p1_4.z()).setColor(r, g, b, a);

        Vector3f p2_1 = new Vector3f(start).sub(side2);
        Vector3f p2_2 = new Vector3f(start).add(side2);
        Vector3f p2_3 = new Vector3f(end).add(side2);
        Vector3f p2_4 = new Vector3f(end).sub(side2);

        float glowAlpha = a * 0.75f;
        builder.addVertex(matrix, p2_1.x(), p2_1.y(), p2_1.z()).setColor(r, g, b, glowAlpha);
        builder.addVertex(matrix, p2_2.x(), p2_2.y(), p2_2.z()).setColor(r, g, b, glowAlpha);
        builder.addVertex(matrix, p2_3.x(), p2_3.y(), p2_3.z()).setColor(r, g, b, glowAlpha);
        builder.addVertex(matrix, p2_4.x(), p2_4.y(), p2_4.z()).setColor(r, g, b, glowAlpha);
    }
}
