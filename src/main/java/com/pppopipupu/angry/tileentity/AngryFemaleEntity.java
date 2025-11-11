package com.pppopipupu.angry.tileentity;

import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.ShaderManager;
import com.pppopipupu.angry.block.AngryBlock;
import com.pppopipupu.angry.block.AngryFemaleBlock;
import com.pppopipupu.angry.block.MultiPartBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

public class AngryFemaleEntity extends BlockEntity {
    public float prevRotationAngle = 0.0f;
    public float rotationAngle = 0.0f;
    private static final float ROTATION_SPEED = 12.0f;
    private static final float LERP_FACTOR = 0.86f;
    public boolean prevTickFlag = false;
    public boolean is_jiaopei = false;
    public int jiaopeiTime =0;
    public AngryFemaleEntity(BlockPos pos, BlockState blockState) {
        super(Angry.ANGRY_FEMALE_ENTITY.get(), pos, blockState);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, AngryFemaleEntity blockEntity) {
        blockEntity.prevRotationAngle = blockEntity.rotationAngle;
        if(blockEntity.is_jiaopei && blockEntity.jiaopeiTime > 0) {
            blockEntity.jiaopeiTime--;
        }
        else if(blockEntity.is_jiaopei) {
            int radius = 2;
            BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius),
                    pos.offset(radius, radius, radius)).forEach(blockPos -> {
                if ((level.getBlockState(blockPos).getBlock() instanceof AngryBlock&& level.getBlockState(blockPos).getValue(AngryBlock.IS_CORE) )) {
                    BlockState blockState = level.getBlockState(blockPos).setValue(AngryBlock.IS_ATOMIC, true);
                    level.createFireworks(pos.getX(),pos.getY(),pos.getZ(),0,0,0,List.of(FireworkExplosion.DEFAULT));
                    level.setBlockAndUpdate(blockPos, blockState);
                    ((AngryFemaleBlock) state.getBlock()).onRemove(state,level,pos, Blocks.AIR.defaultBlockState(),false);
                    level.destroyBlock(pos, false);

                }
            });
        }
        Minecraft mc = Minecraft.getInstance();
        HitResult hitResult = mc.hitResult;

        boolean isLookingAt = false;
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos lookedAtPos = blockHitResult.getBlockPos();
            BlockState lookedAtState = level.getBlockState(lookedAtPos);
            Block lookedAtBlock = lookedAtState.getBlock();
            if (lookedAtBlock instanceof MultiPartBlock multiPartBlock) {
                BlockPos coreOfLookedAtBlock = multiPartBlock.getCorePos(lookedAtState, lookedAtPos);
                if (coreOfLookedAtBlock.equals(pos)) {
                    isLookingAt = true;
                }
            }
        }

        if (blockEntity.is_jiaopei) {
            blockEntity.rotationAngle += ROTATION_SPEED * 5;
        }
        else if (isLookingAt) {
            blockEntity.rotationAngle += ROTATION_SPEED;
        }
        else {
            blockEntity.rotationAngle *= LERP_FACTOR;
            if (Math.abs(blockEntity.rotationAngle) < 0.1f) {
                blockEntity.rotationAngle = 0.0f;
            }
        }
        if (state.getValue(AngryFemaleBlock.IS_LOVE)) {
            if (ShaderManager.flag != 0 && isLookingAt) {
                ShaderManager.flag = 0;
            } else if (ShaderManager.flag == 0 && !isLookingAt && blockEntity.prevTickFlag) {
                ShaderManager.flag = -1;
            }
            blockEntity.prevTickFlag = ShaderManager.flag == 0;
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double randomX = centerX + (level.random.nextDouble() - 0.5) * 3.0;
            double randomY = centerY + (level.random.nextDouble() - 0.5) * 3.0;
            double randomZ = centerZ + (level.random.nextDouble() - 0.5) * 3.0;
            if (level.getGameTime() % 20 == 0 && !blockEntity.is_jiaopei) {
                int radius = 2;
                BlockPos.betweenClosed(pos.offset(-radius, -radius, -radius),
                        pos.offset(radius, radius, radius)).forEach(blockPos -> {
                    if ((level.getBlockState(blockPos).getBlock() instanceof AngryBlock && !level.getBlockState(blockPos).getValue(AngryBlock.IS_ATOMIC) && level.getBlockState(blockPos).getValue(AngryBlock.IS_CORE) )) {
                        blockEntity.is_jiaopei = true;
                        blockEntity.jiaopeiTime = 100;
                        }
                });

            }
            level.addParticle(ParticleTypes.HEART, randomX, randomY, randomZ, 0.0, 1.0, 0.0);
            blockEntity.prevTickFlag = isLookingAt;
        }

    }
}
