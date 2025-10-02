package com.pppopipupu.angry.block;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public abstract class MultiPartBlock extends Block implements EntityBlock {

    public static final BooleanProperty IS_CORE = BooleanProperty.create("is_core");
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final IntegerProperty OFFSET_X = IntegerProperty.create("offset_x", 0, 7);
    public static final IntegerProperty OFFSET_Y = IntegerProperty.create("offset_y", 0, 7);
    public static final IntegerProperty OFFSET_Z = IntegerProperty.create("offset_z", 0, 7);

    protected MultiPartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(IS_CORE, true)
                .setValue(FACING, Direction.NORTH)
                .setValue(OFFSET_X, 0)
                .setValue(OFFSET_Y, 0)
                .setValue(OFFSET_Z, 0));
    }

    public abstract Vec3i getStructureDimensions();

    public abstract BlockPos getCoreOffsetInStructure();

    @Nullable
    protected abstract BlockEntity createCoreBlockEntity(BlockPos pos, BlockState state);

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_CORE, FACING, OFFSET_X, OFFSET_Y, OFFSET_Z);
    }

    public static boolean canPlaceStructureAt(LevelAccessor levelAccessor, BlockPos corePos, Direction facing, Vec3i dimensions, BlockPos coreOffset) {
        for (int x = 0; x < dimensions.getX(); x++) {
            for (int y = 0; y < dimensions.getY(); y++) {
                for (int z = 0; z < dimensions.getZ(); z++) {
                    BlockPos localPos = new BlockPos(x, y, z);
                    BlockPos localOffsetFromCore = localPos.subtract(coreOffset);
                    BlockPos rotatedOffset = getRotatedOffset(localOffsetFromCore, facing);
                    BlockPos checkPos = corePos.offset(rotatedOffset);

                    if (!levelAccessor.getBlockState(checkPos).canBeReplaced()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void placeStructure(LevelAccessor level, BlockPos corePos, BlockState coreState, int flags) {
        Direction facing = coreState.getValue(FACING);
        Vec3i dimensions = getStructureDimensions();
        BlockPos coreOffset = getCoreOffsetInStructure();

        for (int x = 0; x < dimensions.getX(); x++) {
            for (int y = 0; y < dimensions.getY(); y++) {
                for (int z = 0; z < dimensions.getZ(); z++) {
                    BlockPos localPos = new BlockPos(x, y, z);
                    if (localPos.equals(coreOffset)) {
                        continue;
                    }

                    BlockPos localOffsetFromCore = localPos.subtract(coreOffset);
                    BlockPos rotatedOffset = getRotatedOffset(localOffsetFromCore, facing);
                    BlockPos partPos = corePos.offset(rotatedOffset);

                    BlockState partState = this.defaultBlockState()
                            .setValue(IS_CORE, false)
                            .setValue(FACING, facing)
                            .setValue(OFFSET_X, localPos.getX())
                            .setValue(OFFSET_Y, localPos.getY())
                            .setValue(OFFSET_Z, localPos.getZ());
                    level.setBlock(partPos, partState, flags);
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        boolean canPlace = canPlaceStructureAt(context.getLevel(), context.getClickedPos(), facing, getStructureDimensions(), getCoreOffsetInStructure());

        return canPlace ? this.defaultBlockState().setValue(FACING, facing) : null;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (level.isClientSide) {
            return;
        }
        if (state.getValue(IS_CORE)) {
            this.placeStructure(level, pos, state, Block.UPDATE_ALL);
        }
    }


    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.is(newState.getBlock())) {
            super.onRemove(state, level, pos, newState, isMoving);
            return;
        }

        Direction facing = state.getValue(FACING);
        if (state.getValue(IS_CORE)) {
            Vec3i dimensions = getStructureDimensions();
            BlockPos coreOffset = getCoreOffsetInStructure();
            for (int x = 0; x < dimensions.getX(); x++) {
                for (int y = 0; y < dimensions.getY(); y++) {
                    for (int z = 0; z < dimensions.getZ(); z++) {
                        BlockPos localPos = new BlockPos(x, y, z);
                        if (localPos.equals(coreOffset)) continue;

                        BlockPos localOffsetFromCore = localPos.subtract(coreOffset);
                        BlockPos rotatedOffset = getRotatedOffset(localOffsetFromCore, facing);
                        BlockPos partPos = pos.offset(rotatedOffset);
                        BlockState partState = level.getBlockState(partPos);

                        if (partState.is(this) && !partState.getValue(IS_CORE)) {
                            level.removeBlock(partPos, false);
                        }
                    }
                }
            }
        } else {
            BlockPos localPartPos = new BlockPos(state.getValue(OFFSET_X), state.getValue(OFFSET_Y), state.getValue(OFFSET_Z));
            BlockPos coreLocalPos = getCoreOffsetInStructure();
            BlockPos vecToCoreLocal = coreLocalPos.subtract(localPartPos);
            BlockPos vecToCoreWorld = getRotatedOffset(vecToCoreLocal, facing);
            BlockPos corePos = pos.offset(vecToCoreWorld);

            BlockState coreState = level.getBlockState(corePos);
            if (coreState.is(this) && coreState.getValue(IS_CORE)) {
                level.destroyBlock(corePos, true);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    public static BlockPos getRotatedOffset(BlockPos offset, Direction facing) {
        return switch (facing) {
            case NORTH -> offset;
            case SOUTH -> new BlockPos(-offset.getX(), offset.getY(), -offset.getZ());
            case WEST -> new BlockPos(offset.getZ(), offset.getY(), -offset.getX());
            case EAST -> new BlockPos(-offset.getZ(), offset.getY(), offset.getX());
            default -> BlockPos.ZERO;
        };
    }

    public BlockPos getCorePos(BlockState partState, BlockPos partPos) {
        if (partState.getValue(IS_CORE)) {
            return partPos;
        }
        Direction facing = partState.getValue(FACING);
        BlockPos localPartPos = new BlockPos(partState.getValue(OFFSET_X), partState.getValue(OFFSET_Y), partState.getValue(OFFSET_Z));
        BlockPos coreLocalPos = getCoreOffsetInStructure();

        BlockPos vecToCoreLocal = coreLocalPos.subtract(localPartPos);
        BlockPos vecToCoreWorld = getRotatedOffset(vecToCoreLocal, facing);
        return partPos.offset(vecToCoreWorld);
    }

    @Nullable
    @Override
    public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(IS_CORE) ? createCoreBlockEntity(pos, state) : null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return state.getValue(IS_CORE) ? RenderShape.ENTITYBLOCK_ANIMATED : RenderShape.INVISIBLE;
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState pState) {
        return false;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
        return Shapes.empty();
    }

    //steal
    @javax.annotation.Nullable
    protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> serverType, BlockEntityType<E> clientType, BlockEntityTicker<? super E> ticker) {
        return clientType == serverType ? (BlockEntityTicker<A>) ticker : null;
    }
}