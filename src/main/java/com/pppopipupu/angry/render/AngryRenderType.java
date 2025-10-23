//package com.pppopipupu.angry.render;
//
//import com.mojang.blaze3d.vertex.DefaultVertexFormat;
//import com.mojang.blaze3d.vertex.VertexFormat;
//import com.pppopipupu.angry.ShaderManager;
//import net.minecraft.client.renderer.RenderType;
//
//public class AngryRenderType extends RenderType {
//    private AngryRenderType(String name, VertexFormat format, VertexFormat.Mode mode, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clear) {
//        super(name, format, mode, bufferSize, affectsCrumbling, sortOnUpload, setup, clear);
//    }
//
//    public static final RenderType ANGRY = create("angry",
//            DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, 2097152, true, true,
//            RenderType.CompositeState.builder()
//                    .setShaderState(new ShaderStateShard(ShaderManager::getAngryShader))
//                    .setLightmapState(LIGHTMAP)
//                    .setTextureState(BLOCK_SHEET_MIPPED)
//                    .setCullState(CULL)
//                    .setDepthTestState(EQUAL_DEPTH_TEST)
//                    .setTransparencyState(NO_TRANSPARENCY)
//                    .setOverlayState(OVERLAY)
//                    .createCompositeState(true));
//}
//
//
