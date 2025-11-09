package com.pppopipupu.angry.item;

import com.pppopipupu.angry.Angry;
import com.pppopipupu.angry.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class AngrySpearItem extends SwordItem {
    public static class SurpriseTier implements Tier {
        @Override
        public int getUses() {
            return 99999;
        }

        @Override
        public float getSpeed() {
            return 12.0f;
        }

        @Override
        public float getAttackDamageBonus() {
            return -10f;
        }

        @Override
        public TagKey<Block> getIncorrectBlocksForDrops() {
            return null;
        }

        @Override
        public int getEnchantmentValue() {
            return 1145;
        }

        @Override
        public Ingredient getRepairIngredient() {
            return null;
        }
    }

    public AngrySpearItem() {
        super(new SurpriseTier(), new Item.Properties().attributes(SwordItem.createAttributes(new AngrySpearItem.SurpriseTier(), 0, 0)));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 60;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(itemstack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int count) {
        if (livingEntity instanceof Player player && level.getGameTime() % 60 == 0) {
            player.stopUsingItem();
            //深渊尖啸！！！
            if(!level.isClientSide) {
                MinecraftServer server = level.getServer();
                CompletableFuture.runAsync(() -> {
                    for(int l = 0; l < 2; l++) {
                        for (int i = 0; i < 5; i++) {
                            int finalI = i;
                            for (int j = 0; j < 2; j++) {
                                int finalJ = j;
                                server.execute(() -> {
                                    level.explode(player, player.xCloak, player.yCloak + 4 + finalI * 3, player.zCloak - finalJ, 4f, true, Level.ExplosionInteraction.TNT);
                                });
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    Angry.LOGGER.error("原子弹爆了", e.getCause());
                                }
                            }
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                Angry.LOGGER.error("原子弹爆了", e.getCause());
                            }
                        }
                    }
                });
            }

            else {
                CompletableFuture.runAsync(() -> {
                    Minecraft mc = Minecraft.getInstance();
                    if(ShaderManager.flag != 1) ShaderManager.flag = 1;
                    for (int i = 0; i < 15; i++) {

                        mc.execute(() -> player.playSound(SoundEvents.ENDERMAN_DEATH, 10, 1));
                        for (int j = 0; j < 3; j++) {
                            int finalJ = j;
                            mc.execute(() -> level.addParticle(Angry.SURPRISE_PARTICLE.get(), livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), finalJ, 0, 0));
                        }
                        try {
                            Thread.sleep(400);
                        } catch (InterruptedException e) {
                            Angry.LOGGER.error("氢弹爆了", e.getCause());
                        }

                    }
                    if(ShaderManager.flag != -1) ShaderManager.flag = -1;

                });
            }
        }
    }
}

