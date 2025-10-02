package com.pppopipupu.angry.block;

import com.mojang.serialization.MapCodec;
import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.tileentity.AngryBlockEntity;
import com.pppopipupu.angry.tileentity.AngryFemaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AngryFemaleBlock extends MultiPartBlock{
    public AngryFemaleBlock(Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<AngryBlock> codec() {
        return Angry.SIMPLE_CODEC.value();
    }
    @Nullable
    @Override
    protected BlockEntity createCoreBlockEntity(BlockPos pos, BlockState state) {

        return new AngryFemaleEntity(pos, state);

    }
    @Override
    public Vec3i getStructureDimensions() {
        return new Vec3i(2, 2, 2);
    }

    @Override
    public BlockPos getCoreOffsetInStructure() {
        return BlockPos.ZERO;
    }
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if (pLevel.isClientSide()) {
            return createTickerHelper(pBlockEntityType, Angry.ANGRY_FEMALE_ENTITY.get(), AngryFemaleEntity::tick);
        }
        return null;
    }

}
