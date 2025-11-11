package com.pppopipupu.angry.item;

import com.pppopipupu.angry.Angry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.CompletableFuture;

public class AngryGunItem extends Item {

    public AngryGunItem() {
        super(new Item.Properties().attributes(SwordItem.createAttributes(new AngrySpearItem.SurpriseTier(), 0, 0)));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.playSound(SoundEvents.ANVIL_PLACE, 1.0F, 1.0F);
        if (level instanceof ServerLevel serverLevel) {
            BlockHitResult hitResult = (BlockHitResult) player.pick(144.0D, 0.0F, false);

            if (hitResult.getType() == HitResult.Type.BLOCK) {
                Vec3 hitPos = hitResult.getLocation();
                CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < 10; i++) {
                        serverLevel.getServer().execute(() ->
                                serverLevel.sendParticles(Angry.SURPRISE_PARTICLE.get(), hitPos.x(), hitPos.y() + 0.5, hitPos.z(), 50, 0.2, 0.2, 0.2, 0.1)

                        );
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            Angry.LOGGER.error("我感冒昨天好了，哈哈");
                            return;
                        }

                    }
                    for (int angle = 0; angle < 1080; angle += 20) {
                        double radians = Math.toRadians(angle);
                        double dx = Math.cos(radians);
                        double dz = Math.sin(radians);

                        for (int i = 1; i <= 8; i++) {
                            final double explosionX = hitPos.x() + dx * i *6;
                            final double explosionY = hitPos.y();
                            final double explosionZ = hitPos.z() + dz * i*6;
                            serverLevel.getServer().execute(() ->
                                    serverLevel.explode(null, explosionX, explosionY, explosionZ, 4.0F, Level.ExplosionInteraction.MOB)
                            );
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            Angry.LOGGER.error("我想吃烤鸡腿");
                            break;
                        }
                    }
                });
            }
        }
        player.getCooldowns().addCooldown(this, 60);
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }
}
