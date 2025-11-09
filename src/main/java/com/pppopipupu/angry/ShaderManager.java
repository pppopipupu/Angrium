package com.pppopipupu.angry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@EventBusSubscriber(modid = Angry.MODID, value = Dist.CLIENT)
public class ShaderManager {
    public static List<ResourceLocation> shaders = new ArrayList<>();
    public static int flag = -1;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        switch (flag) {
            case -1 -> {
                AtomicBoolean isActive = new AtomicBoolean(false);
                shaders.forEach(resourceLocation -> {
                    if (gameRenderer.currentEffect() != null && resourceLocation.toString().equals(gameRenderer.currentEffect().getName()))
                        isActive.set(true);
                });
                if (isActive.get()) gameRenderer.shutdownEffect();
            }
            case 0 -> {
                boolean isActive = gameRenderer.currentEffect() != null && gameRenderer.currentEffect().getName().equals(shaders.get(0).toString());
                if (!isActive) gameRenderer.loadEffect(shaders.get(0));
            }
            case 1 -> {
                boolean isActive = gameRenderer.currentEffect() != null && gameRenderer.currentEffect().getName().equals(shaders.get(1).toString());
                if (!isActive) gameRenderer.loadEffect(shaders.get(1));
            }

        }
        ;
    }

}

