//package com.pppopipupu.angry.render;
//
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.VertexFormat;
//import net.minecraft.client.renderer.RenderStateShard;
//import net.minecraft.client.renderer.RenderType;
//
//public class AngryRenderType extends RenderType {
//    private AngryRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
//        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
//    }
//
//    public static final RenderType LIGHTNING_NO_DEPTH = create("lightning_no_depth",
//            DefaultVertexFormat.POSITION_COLOR,
//            VertexFormat.Mode.QUADS,
//            256,
//            false,
//            false,
//            RenderType.CompositeState.builder()
//                    .setShaderState(RenderStateShard.RENDERTYPE_LIGHTNING_SHADER)
//                    .setTransparencyState(RenderStateShard.NO_TRANSPARENCY)
//                    .setDepthTestState(RenderStateShard.NO_DEPTH_TEST)
//                    .setCullState(RenderStateShard.NO_CULL)
//                    .setLightmapState(RenderStateShard.NO_LIGHTMAP)
//                    .setOverlayState(RenderStateShard.NO_OVERLAY)
//                    .createCompositeState(false));
//}
//
