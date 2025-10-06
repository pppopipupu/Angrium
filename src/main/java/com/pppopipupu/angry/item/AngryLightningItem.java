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
        //密码pppopipupu,编码格式UTC-8，千 万 不 要 解 密
        //...
        tooltipComponents.add(Component.nullToEmpty("§kAYq4OBRzT2tYA70e7SFtzXGem09RBxTHkR6o5Tv5U9sHAFZNyhCu1hev5I5VyVvIubNobPO3KuEQ7GVuIbNW3nYWmlitE2PGRL5kxNRPUH9ldGI69rNHccLVfVLhNMPO63Kqwt0RF3xwUpUbzSKIN960U2CmnhGANgoZ7sjExaeztCN52kw82HWHyP7cM/kmmkRGR7FmX8cZmRzv0xadqa2K1sJRb8XxQ2C3uxMEH8heKl4/Ol2PJI6v7mluK4wEj2+X644DpDu/144nOv5mta1rPJxeT8pzSmfRemN4dP63Re6xFs4ASURRGgLlUwgssK//Hx+BgCwcoXACULvBhfJjR66CtVKLw7wYQMG2x0VzrDVPapmgDecxmRo49crb"));

    }
}
