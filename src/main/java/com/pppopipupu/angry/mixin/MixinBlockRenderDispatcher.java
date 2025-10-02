package com.pppopipupu.angry.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pppopipupu.angry.block.MultiPartBlock;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderDispatcher.class)
public abstract class MixinBlockRenderDispatcher {
    @Final
    @Shadow
    private RandomSource random;

    @Inject(method = "renderBreakingTexture(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/neoforged/neoforge/client/model/data/ModelData;)V"
            , at = @At("HEAD"),
            cancellable = true
    )
    private void renderBreakingTexture(BlockState state, BlockPos pos, BlockAndTintGetter level, PoseStack poseStack, VertexConsumer consumer, ModelData modelData, CallbackInfo cir) {

        if (state.getBlock() instanceof MultiPartBlock block) {
            if (state.getValue(MultiPartBlock.IS_CORE)) {
                return;
            }
            cir.cancel();
            BlockPos corePos = block.getCorePos(state, pos);
            BlockState coreBlock = level.getBlockState(corePos);
            BakedModel bakedmodel = ((BlockRenderDispatcher) (Object) this).getBlockModelShaper().getBlockModel(coreBlock);
            long i = state.getSeed(pos);
            modelData = bakedmodel.getModelData(level, corePos, coreBlock, level.getModelData(corePos));
            poseStack.pushPose();
            BlockPos offset = corePos.subtract(pos);
            //:)
            poseStack.translate(offset.getX()-1, offset.getY(), offset.getZ()-1);
            ((BlockRenderDispatcher) (Object) this).getModelRenderer().tesselateBlock(level, bakedmodel, coreBlock, corePos, poseStack, consumer, true, random, i, OverlayTexture.NO_OVERLAY, modelData, (RenderType) null);
            poseStack.popPose();
        }
    }
}
