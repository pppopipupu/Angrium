package com.pppopipupu.angry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

@EventBusSubscriber(modid = Angry.MODID, value = Dist.CLIENT)
public class ShaderManager {
    private static final ResourceLocation ANGRY_SHADER_PATH = ResourceLocation.fromNamespaceAndPath(Angry.MODID, "shaders/post/angry.json");
    public static boolean flag = false;
    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER) {
            return;
        }
        GameRenderer gameRenderer = Minecraft.getInstance().gameRenderer;
        boolean isActive = gameRenderer.currentEffect() != null && gameRenderer.currentEffect().getName().equals(ANGRY_SHADER_PATH.toString());
        if (flag && !isActive) {
            gameRenderer.loadEffect(ANGRY_SHADER_PATH);
        } else if (!flag && isActive) {
            gameRenderer.shutdownEffect();
        }
    }

}

