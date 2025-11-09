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

public class AngryParticle extends Particle {

    public static BakedModel ANGRY_MODEL;
    private final float rotSpeed;
    private final double centerX;
    private final double centerY;
    private final double centerZ;
    private final float orbitRadius;
    private final float orbitSpeed;
    private float currentAngle;
    private final float cosTilt;
    private final float sinTilt;

    public AngryParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd) {
        super(level, x, y, z, xd, yd, zd);

        this.lifetime = 250;
        this.gravity = 0.0F;
        this.friction = 1.0F;
        this.rotSpeed = 1F;

        this.centerX = x;
        this.centerY = y;
        this.centerZ = z;

        this.orbitRadius = 4F;
        this.orbitSpeed = 0.2F;
        this.currentAngle = this.random.nextFloat() * (float) (2 * Math.PI);

        int orbitType = this.random.nextInt(4);
        float tiltAngle = switch (orbitType) {
            case 0 -> 90.0F;
            case 1 -> 45.0F;
            case 2 -> 135.0F;
            case 3 -> 0F;
            default -> 0.0F;
        };

        this.sinTilt = Mth.sin((float) Math.toRadians(tiltAngle));
        this.cosTilt = Mth.cos((float) Math.toRadians(tiltAngle));
        double localZComponent = this.orbitRadius * Mth.sin(this.currentAngle);
        double localXComponent = this.orbitRadius * Mth.cos(this.currentAngle);
        double rotatedY = -this.sinTilt * localZComponent;
        double rotatedZ = this.cosTilt * localZComponent;

        this.x = this.centerX + localXComponent;
        this.y = this.centerY + rotatedY;
        this.z = this.centerZ + rotatedZ;
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

        this.currentAngle += this.orbitSpeed;

        double localZComponent = this.orbitRadius * Mth.sin(this.currentAngle);
        double localXComponent = this.orbitRadius * Mth.cos(this.currentAngle);
        double rotatedY = -this.sinTilt * localZComponent;
        double rotatedZ = this.cosTilt * localZComponent;

        this.x = this.centerX + localXComponent;
        this.y = this.centerY + rotatedY;
        this.z = this.centerZ + rotatedZ;
    }


    @Override
    public void render(VertexConsumer vertexConsumer, Camera camera, float partialTick) {
        BlockRenderDispatcher render = Minecraft.getInstance().getBlockRenderer();
        if (ANGRY_MODEL == null) {
            AngryParticle.ANGRY_MODEL = render.getBlockModel(Angry.ANGRY_BLOCK.get().defaultBlockState());
        }

        Vec3 camPos = camera.getPosition();
        float lerpX = (float) (Mth.lerp(partialTick, this.xo, this.x) - camPos.x());
        float lerpY = (float) (Mth.lerp(partialTick, this.yo, this.y) - camPos.y());
        float lerpZ = (float) (Mth.lerp(partialTick, this.zo, this.z) - camPos.z());

        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();
        poseStack.translate(lerpX, lerpY, lerpZ);
        float rotation = (this.age + partialTick) * this.rotSpeed * 20.0F;
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));
        float scale = 0.3F;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(-0.5, -0.5, -0.5);
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.solid());

        int light = LevelRenderer.getLightColor(this.level, BlockPos.containing(this.x, this.y, this.z));

        render.getModelRenderer()
                .renderModel(poseStack.last(), buffer, null, ANGRY_MODEL, 1.0F, 1.0F, 1.0F, light, 0xF000F0, ModelData.EMPTY, RenderType.solid());
        poseStack.popPose();

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    public static class AngryParticleProvider implements ParticleProvider<SimpleParticleType> {
        public AngryParticleProvider(SpriteSet spriteSet) {
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double xd, double yd, double zd) {
            return new AngryParticle(level, x, y, z, xd, yd, zd);
        }
    }
}
