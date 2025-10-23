package com.pppopipupu.angry.entity;

import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.item.AngrySwordItem;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Fireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;

public class AngryFireBall extends Fireball {
    @Nullable
    private LivingEntity target;
    private int life;
    public float yRot, xRot;
    public float yRotO, xRotO;

    public AngryFireBall(EntityType<? extends Fireball> entityType, Level level) {
        super(entityType, level);
    }

    public AngryFireBall(Level level, LivingEntity shooter, @Nullable LivingEntity target) {
        super(Angry.ANGRY_FIREBALL.get(), shooter, shoot(shooter, target), level);

        this.target = target;
        this.setPos(shooter.getEyePosition());
    }

    private static Vec3 shoot(LivingEntity shooter, @Nullable LivingEntity target) {
        Vec3 direction;
        if (target != null) {
            direction = target.getEyePosition().subtract(shooter.getEyePosition()).normalize();
        } else {
            direction = shooter.getLookAngle();
        }
        return direction.scale(2);
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide) {
            //操逼逼，不停操逼逼，然后爆掉
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.5F, Level.ExplosionInteraction.TNT);
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (!this.level().isClientSide && this.life++ > 451) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3.5F, Level.ExplosionInteraction.TNT);
            this.discard();
            return;
        }
        if (this.target != null && this.target.isAlive() && !this.target.isRemoved()) {
            Vec3 currentPos = this.position();
            Vec3 targetPos = this.target.getEyePosition();
            Vec3 directionToTarget = targetPos.subtract(currentPos).normalize();

            HitResult hitResult = this.level().clip(new ClipContext(currentPos, targetPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));

            Vec3 finalDirection;
            if (hitResult.getType() == HitResult.Type.MISS) {
                finalDirection = directionToTarget;
            } else {
                finalDirection = new Vec3(directionToTarget.x, directionToTarget.y + 0.75, directionToTarget.z).normalize();
            }

            Vec3 newDelta = finalDirection.scale(0.2F);
            this.setDeltaMovement(newDelta);

        } else {

            this.target = AngrySwordItem.findTarget(this, 51.4).stream()
                    .min(Comparator.comparingDouble(this::distanceToSqr))
                    .orElse(null);

        }
        this.yRotO = this.yRot;
        this.xRotO = this.xRot;
        this.yRot += 20.0F;
        this.xRot += 5.0F;

    }

    public float getRotationY(float partialTicks) {
        return Mth.lerp(partialTicks, this.yRotO, this.yRot);
    }

    public float getRotationX(float partialTicks) {
        return Mth.lerp(partialTicks, this.xRotO, this.xRot);
    }

    @Override
    protected boolean shouldBurn() {
        return true;
    }
}
