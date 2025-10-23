package com.pppopipupu.angry.tileentity;

import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.ShaderManager;
import com.pppopipupu.angry.block.AngryFemaleBlock;
import com.pppopipupu.angry.block.MultiPartBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class AngryFemaleEntity extends BlockEntity {
    public float prevRotationAngle = 0.0f;
    public float rotationAngle = 0.0f;
    private static final float ROTATION_SPEED = 12.0f;
    private static final float LERP_FACTOR = 0.86f;
    public boolean prevTickFlag = false;

    public AngryFemaleEntity(BlockPos pos, BlockState blockState) {
        super(Angry.ANGRY_FEMALE_ENTITY.get(), pos, blockState);
    }


    public static void tick(Level level, BlockPos pos, BlockState state, AngryFemaleEntity blockEntity) {
        blockEntity.prevRotationAngle = blockEntity.rotationAngle;

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

        if (isLookingAt) {
            blockEntity.rotationAngle += ROTATION_SPEED;
        } else {
            blockEntity.rotationAngle *= LERP_FACTOR;
            if (Math.abs(blockEntity.rotationAngle) < 0.1f) {
                blockEntity.rotationAngle = 0.0f;
            }
        }
        if (state.getValue(AngryFemaleBlock.IS_LOVE)) {
            if(!ShaderManager.flag && isLookingAt) {
                ShaderManager.flag = true;
            }
            else if(ShaderManager.flag && !isLookingAt && blockEntity.prevTickFlag) {
                ShaderManager.flag = false;
            }
            double centerX = pos.getX() + 0.5;
            double centerY = pos.getY() + 0.5;
            double centerZ = pos.getZ() + 0.5;

            double randomX = centerX + (level.random.nextDouble() - 0.5) * 3.0;
            double randomY = centerY + (level.random.nextDouble() - 0.5) * 3.0;
            double randomZ = centerZ + (level.random.nextDouble() - 0.5) * 3.0;

            level.addParticle(ParticleTypes.HEART, randomX, randomY, randomZ, 0.0, 1.0, 0.0);
            blockEntity.prevTickFlag = isLookingAt;
        }

    }
}
