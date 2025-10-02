package com.pppopipupu.angry.item;

import com.pppopipupu.angry.block.AngryBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.awt.*;
import java.util.List;

public class AngryLightningItem extends BlockItem {


    public AngryLightningItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state != null) {
            return state.setValue(AngryBlock.IS_LIGHTNING, true);
        }
        return null;
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack pStack) {
        return "block.angry.angry_lightning_block";
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.angry.angry_lightning_block"));
        tooltipComponents.add(Component.nullToEmpty("§k飞八分钱干飞马"));

    }
}
