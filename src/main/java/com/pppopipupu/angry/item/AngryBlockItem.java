package com.pppopipupu.angry.item;

import com.pppopipupu.angry.block.AngryBlock;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AngryBlockItem extends BlockItem {
    public int mode;

    public AngryBlockItem(Block block, Properties properties,int mode) {
        super(block, properties);
        this.mode = mode;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        switch (mode) {
            case 0:
                tooltipComponents.add(Component.translatable("tooltip.angry.angry_block"));
                return;
            case 1:
                tooltipComponents.add(Component.translatable("tooltip.angry.angry_lightning_block"));
                tooltipComponents.add(Component.nullToEmpty("§kAYq4OBRzT2tYA70e7SFtzXGem09RBxTHkR6o5Tv5U9sHAFZNyhCu1hev5I5VyVvIubNobPO3KuEQ7GVuIbNW3nYWmlitE2PGRL5kxNRPUH9ldGI69rNHccLVfVLhNMPO63Kqwt0RF3xwUpUbzSKIN960U2CmnhGANgoZ7sjExaeztCN52kw82HWHyP7cM/kmmkRGR7FmX8cZmRzv0xadqa2K1sJRb8XxQ2C3uxMEH8heKl4/Ol2PJI6v7mluK4wEj2+X644DpDu/144nOv5mta1rPJxeT8pzSmfRemN4dP63Re6xFs4ASURRGgLlUwgssK//Hx+BgCwcoXACULvBhfJjR66CtVKLw7wYQMG2x0VzrDVPapmgDecxmRo49crb"));
                return;
            case 2:
                tooltipComponents.add(Component.translatable("tooltip.angry.angry_atomic_block"));
        }


    }

    @Override
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        if (state != null) {
            switch (mode) {
                case 0:
                    return state;
                case 1:
                    return state.setValue(AngryBlock.IS_LIGHTNING, true);
                case 2:
                    return state.setValue(AngryBlock.IS_ATOMIC, true);
            }

        }
        return state;
    }
}
