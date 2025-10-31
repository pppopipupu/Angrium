package com.pppopipupu.angry.tileentity;

import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.AngryLightingBolt;
import com.pppopipupu.angry.block.AngryBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AngryBlockEntity extends BlockEntity {
    private final List<AngryLightingBolt> activeBolts = new ArrayList<>();
    public boolean IS_LIGHTNING = true;
    private final RandomSource random = RandomSource.create();

    public AngryBlockEntity(BlockPos pos, BlockState state) {
        super(Angry.ANGRY_BLOCK_ENTITY.get(), pos, state);
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, AngryBlockEntity blockEntity) {
        if (state.getValue(AngryBlock.IS_LIGHTNING)) {
            blockEntity.tickLightingBolts();
        }
        level.addParticle(
                Angry.ANGRY_PARTICLE.get(),
                pos.getX(),
                pos.getY(),
                pos.getZ(),
                0,
                0,
                0
        );

    }

    public static void tick(Level level, BlockPos pos, BlockState state, AngryBlockEntity blockEntity) {
        AABB aabb = new AABB(
                pos.getX() - 3, level.getMinBuildHeight(), pos.getZ() - 3,
                pos.getX() + 3, level.getMaxBuildHeight(), pos.getZ() + 3
        );
        List<LightningBolt> lightningBolts = level.getEntitiesOfClass(LightningBolt.class, aabb);
        if (!lightningBolts.isEmpty() && !state.getValue(AngryBlock.IS_LIGHTNING)) {
            BlockState newState = state.setValue(AngryBlock.IS_LIGHTNING, true);
            level.setBlock(pos, newState, 3);
        }
        if (state.getValue(AngryBlock.IS_LIGHTNING)) {
            final double radius = 6.0f;
            final double pushStrength = 1.0f;
            final float damageAmount = 1.5f;

            AABB forceFieldAABB = new AABB(pos).inflate(radius);
            List<LivingEntity> nearbyEntities = level.getEntitiesOfClass(LivingEntity.class, forceFieldAABB, entity ->
                    !(entity instanceof Player player && (player.isCreative() || player.isSpectator()))
            );

            Vec3 blockCenter = Vec3.atCenterOf(pos);
            for (LivingEntity entity : nearbyEntities) {
                Vec3 entityPos = entity.position();
                Vec3 pushVector = entityPos.subtract(blockCenter).normalize();
                entity.push(pushVector.x * pushStrength, pushVector.y * pushStrength * 2, pushVector.z * pushStrength);
                entity.hurtMarked = true;
                entity.setHealth(entity.getHealth() - damageAmount);
            }
        }
    }

    private void tickLightingBolts() {
        for (AngryLightingBolt bolt : activeBolts) {
            bolt.update();
        }
        activeBolts.removeIf(bolt -> bolt.life <= 0);

        int boltsToSpawn = 2 + this.random.nextInt(3);
        for (int i = 0; i < boltsToSpawn; i++) {
            if (activeBolts.size() < 40) {
                spawnNewBolt();
            }
        }
    }

    private void spawnNewBolt() {
        float distance = 2.8f + random.nextFloat() * 3.0f;
        Vector3f endPoint = new Vector3f(
                (random.nextFloat() - 0.5f),
                (random.nextFloat() - 0.5f),
                (random.nextFloat() - 0.5f)
        ).normalize().mul(distance);

        Color color = Color.getHSBColor(random.nextFloat(), 0.9f, 1.0f);
        float r = color.getRed() / 255f;
        float g = color.getGreen() / 255f;
        float b = color.getBlue() / 255f;

        int life = 15 + random.nextInt(20);
        float thickness = 0.06f + random.nextFloat() * 0.04f;
        int segments = 15;
        float jitter = 0.4f;
        int maxRecursion = 3;

        AngryLightingBolt newBolt = new AngryLightingBolt(
                new Vector3f(0, 0, 0), endPoint, segments, jitter, thickness, life, r, g, b, maxRecursion, this.random
        );
        activeBolts.add(newBolt);
    }

    public List<AngryLightingBolt> getActiveBolts() {
        return activeBolts;
    }

}