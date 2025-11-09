package com.pppopipupu.angry.block;

import com.mojang.serialization.MapCodec;
import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.tileentity.AngryBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class AngryBlock extends MultiPartBlock {
    public static final BooleanProperty IS_LIGHTNING = BooleanProperty.create("is_lightning");
    public static final BooleanProperty IS_ATOMIC = BooleanProperty.create("is_atomic");


    public AngryBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_LIGHTNING, false).setValue(IS_ATOMIC, false));

    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_LIGHTNING);
        builder.add(IS_ATOMIC);
    }

    @Override
    public MapCodec<AngryBlock> codec() {
        return Angry.SIMPLE_CODEC.value();
    }

    @Nullable
    @Override
    protected BlockEntity createCoreBlockEntity(BlockPos pos, BlockState state) {

        return new AngryBlockEntity(pos, state);

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
        if(!pState.getValue(IS_CORE)) {
            return null;
        }
        if (pLevel.isClientSide()) {
            return createTickerHelper(pBlockEntityType, Angry.ANGRY_BLOCK_ENTITY.get(), AngryBlockEntity::tickClient);
        } else {
            return createTickerHelper(pBlockEntityType, Angry.ANGRY_BLOCK_ENTITY.get(), AngryBlockEntity::tick);
        }
    }

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if(state.getValue(MultiPartBlock.IS_CORE)) {

            return state.getValue(AngryBlock.IS_LIGHTNING)
                    ? List.of(Angry.ANGRY_LIGHTNING_BLOCK_ITEM.get().getDefaultInstance(),Angry.ANGRY_SWORD.get().getDefaultInstance())
                    : List.of(Angry.ANGRY_BLOCK_ITEM.get().getDefaultInstance());
        }
        return List.of();
    }
}

