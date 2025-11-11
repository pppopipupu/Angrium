package com.pppopipupu.angry.block;

import com.mojang.serialization.MapCodec;
import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.tileentity.AngryBlockEntity;
import com.pppopipupu.angry.tileentity.AngryFemaleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AngryFemaleBlock extends MultiPartBlock {
    public static final BooleanProperty IS_LOVE = BooleanProperty.create("is_love");
    public AngryFemaleBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(IS_LOVE, false));
    }
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(IS_LOVE);
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

    @Override
    protected @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(MultiPartBlock.IS_CORE)) {
            return List.of(Angry.ANGRY_FEMALE_ITEM.get().getDefaultInstance());
        }
        return List.of();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        if(!pState.getValue(IS_CORE)) {
            return null;
        }
        if (pLevel.isClientSide()) {
            return createTickerHelper(pBlockEntityType, Angry.ANGRY_FEMALE_ENTITY.get(), AngryFemaleEntity::tickClient);
        }
        else {
            return createTickerHelper(pBlockEntityType, Angry.ANGRY_FEMALE_ENTITY.get(), AngryFemaleEntity::tick);
        }

    }

}
