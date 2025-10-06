package com.pppopipupu.angry.item;

import com.pppopipupu.angry.entity.AngryFireBall;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class AngrySwordItem extends SwordItem {
    public static class AngryTier implements Tier {

        @Override
        public int getUses() {
            return 114514;
        }

        @Override
        public float getSpeed() {
            return 12.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return 11.45f;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return null;
        }

        @Override
        public int getEnchantmentValue() {
            return 114;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return null;
        }
    }

    public AngrySwordItem() {
        super(new AngryTier(), new Item.Properties().attributes(SwordItem.createAttributes(new AngryTier(), 0, 0)));
    }
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        tooltipComponents.add(Component.translatable("tooltip.angry.angry_sword"));

    }
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 114514;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (!level.isClientSide && livingEntity instanceof Player player) {
            if (level.getGameTime() % 20 == 0) {
                findTarget(player, 51.4f).forEach(target -> {
                    level.playSound(
                            null,
                            player.getX(),
                            player.getY(),
                            player.getZ(),
                            SoundEvents.GHAST_SHOOT,
                            SoundSource.PLAYERS,
                            1.0F,
                            1.0F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
                    );

                    AngryFireBall fireball = new AngryFireBall(level, player, target);
                    level.addFreshEntity(fireball);

                    player.awardStat(Stats.ITEM_USED.get(this));
                });
            }
        }
    }

    public static List<LivingEntity> findTarget(Entity player, double range) {
        Level level = player.level();
        AABB searchArea = player.getBoundingBox().inflate(range);

        return level.getEntitiesOfClass(LivingEntity.class, searchArea,
                entity -> entity != player && entity.isAlive()
        );
    }

}
