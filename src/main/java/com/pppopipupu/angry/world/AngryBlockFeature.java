package com.pppopipupu.angry.world;

import com.mojang.serialization.Codec;
import com.pppopipupu.angry.block.MultiPartBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class AngryBlockFeature extends Feature<NoneFeatureConfiguration> {

    private final List<MultiPartBlock> blocks;

    public AngryBlockFeature(Codec<NoneFeatureConfiguration> codec, List<MultiPartBlock> blocks) {
        super(codec);
        this.blocks = blocks;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (blocks.isEmpty()) {
            return false;
        }

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        MultiPartBlock blockToPlace = blocks.get(random.nextInt(0,8)>5 ? 1:0);
        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(random);

        if (!MultiPartBlock.canPlaceStructureAt(level, origin, facing, blockToPlace.getStructureDimensions(), blockToPlace.getCoreOffsetInStructure())) {
            return false;
        }
        BlockState coreState = blockToPlace.defaultBlockState().setValue(MultiPartBlock.FACING, facing);
        level.setBlock(origin, coreState, 2);

        blockToPlace.placeStructure( level, origin, coreState, 2);

        return true;
    }

}