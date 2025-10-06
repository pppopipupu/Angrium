package com.pppopipupu.angry;

import com.pppopipupu.angry.render.AngryBlockRenderer;
import com.pppopipupu.angry.render.AngryFemaleBlockRenderer;
import com.pppopipupu.angry.render.AngryFireBallRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = Angry.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = Angry.MODID, value = Dist.CLIENT)
public class AngryClient {

    public AngryClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }


    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(Angry.ANGRY_BLOCK_ENTITY.get(), AngryBlockRenderer::new);
        event.registerBlockEntityRenderer(Angry.ANGRY_FEMALE_ENTITY.get(), AngryFemaleBlockRenderer::new);
        event.registerEntityRenderer(Angry.ANGRY_FIREBALL.get(), AngryFireBallRenderer::new);
    }
}

