package com.pppopipupu.angry.render.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.pppopipupu.angry.Angry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.model.data.ModelData;

public class SurpriseParticle extends Particle {

    public static BakedModel SURPRISE_MODEL;

    public SurpriseParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, 0.0, 0.0, 0.0);

        this.lifetime = 200;
        this.gravity = 0.0F;
        this.hasPhysics = false;


        float angleDeg = switch ((int) xd) {
            case 0 -> 0.0f;
            case 1 -> 30.0f;
            case 2 -> -30.0f;
            default -> 0.0f;
        };
        float angleRad = (float) Math.toRadians(angleDeg);

        float radius = 30.0f;
        float upwardMovement = 40.0f;

        double targetX = x + radius * Mth.sin(angleRad);
        double targetY = y + upwardMovement;
        double targetZ = z + radius * Mth.cos(angleRad);

        this.xd = (targetX - x) / this.lifetime;
        this.yd = (targetY - y) / this.lifetime;
        this.zd = (targetZ - z) / this.lifetime;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd, this.zd);
    }


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        BlockRenderDispatcher render = Minecraft.getInstance().getBlockRenderer();
        if (SURPRISE_MODEL == null) {
            SurpriseParticle.SURPRISE_MODEL = render.getBlockModel(Angry.SURPRISE_BLOCK.get().defaultBlockState());
        }

        Vec3 camPos = camera.getPosition();
        float lerpX = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
        float lerpY = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
        float lerpZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(lerpX, lerpY, lerpZ);

        poseStack.mulPose(camera.rotation());
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-90.0F));

        float progress = (this.age + partialTick) / (float) this.lifetime;
        float scale = Mth.lerp(progress, 1f, 10f);
        poseStack.scale(scale, scale, scale);

        poseStack.translate(-0.5, -0.5, -0.5);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());

        int light = LevelRenderer.getLightColor(this.level, BlockPos.containing(this.x, this.y, this.z));

        render.getModelRenderer()
                .renderModel(poseStack.last(), buffer, null, SURPRISE_MODEL, 1.0F, 1.0F, 1.0F, light, 0xF000F0, ModelData.EMPTY, RenderType.solid());
        poseStack.popPose();

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class SurpriseParticleProvider implements ParticleProvider<SimpleParticleType> {
        public SurpriseParticleProvider(SpriteSet spriteSet) {
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new SurpriseParticle(level, x, y, z, xd, yd, zd);
        }
    }
}