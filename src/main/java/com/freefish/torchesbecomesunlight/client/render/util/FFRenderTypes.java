package com.freefish.torchesbecomesunlight.client.render.util;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.ShaderRegistry;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.resources.ResourceLocation;

public class FFRenderTypes extends RenderType{
    private static ResourceLocation DEMON_1 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/effects/demon.png");
    private static ResourceLocation DEMON_BACK = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/effects/demon_back.png");

    private static ResourceLocation GALLRY = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/shader/gallry.png");
    private static ResourceLocation NOSIC = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/shader/nosic.png");
    private static ResourceLocation NOISE = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/shader/noise.png");

    public FFRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType getIce(ResourceLocation locationIn) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(locationIn, false, false);
        return create("ice_texture", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, CompositeState.builder().setShaderState(RenderType.RENDERTYPE_BEACON_BEAM_SHADER).setTextureState(lvt_1_1_).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    }

    public static RenderType getGlowingEffect(ResourceLocation location) {
        TextureStateShard shard = new TextureStateShard(location, false, false);
        CompositeState rendertype$state = CompositeState.builder().setTextureState(shard).setShaderState(RENDERTYPE_BEACON_BEAM_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
        return create("glow_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static final RenderType PORTAL = create("starlight_portal", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true, CompositeState.builder()
            .setShaderState(new ShaderStateShard(ShaderRegistry::getRenderTypeStarlightPortal))
            .setTextureState(NO_TEXTURE)
            .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
            .setCullState(NO_CULL)
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .createCompositeState(true));


    public static final RenderType DEMON = create("demon", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false,
            CompositeState.builder().setShaderState(new ShaderStateShard(ShaderRegistry::getRenderTypeDemon)).setTextureState(MultiTextureStateShard.builder()
                    .add(DEMON_BACK, false, false)
                    .add(DEMON_1, false, false).build()).createCompositeState(false));

    public static final RenderType HOLE = create("hole", DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS, TRANSIENT_BUFFER_SIZE, true, true,
            CompositeState.builder().setShaderState(new ShaderStateShard(ShaderRegistry::getRenderTypeHole)).setTextureState(MultiTextureStateShard.builder()
                    .add(NOISE, false, false)
                    .add(DEMON_1, false, false).build())
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setCullState(NO_CULL)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(OVERLAY)
                    .createCompositeState(true));
}