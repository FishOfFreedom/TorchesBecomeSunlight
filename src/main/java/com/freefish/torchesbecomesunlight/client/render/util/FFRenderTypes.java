package com.freefish.torchesbecomesunlight.client.render.util;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.ShaderHandle;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class FFRenderTypes extends RenderType{
    public static ResourceLocation DEMON_1 = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/effects/demon.png");
    public static ResourceLocation DEMON_BACK = new ResourceLocation(TorchesBecomeSunlight.MOD_ID,"textures/effects/demon_back.png");

    public FFRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType getIce(ResourceLocation locationIn) {
        TextureStateShard lvt_1_1_ = new TextureStateShard(locationIn, false, false);
        return create("ice_texture", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderType.RENDERTYPE_BEACON_BEAM_SHADER).setTextureState(lvt_1_1_).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(CULL).setLightmapState(LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(true));
    }

    public static RenderType getGlowingEffect(ResourceLocation location) {
        RenderStateShard.TextureStateShard shard = new RenderStateShard.TextureStateShard(location, false, false);
        RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder().setTextureState(shard).setShaderState(RENDERTYPE_BEACON_BEAM_SHADER).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setOverlayState(OVERLAY).setWriteMaskState(COLOR_WRITE).createCompositeState(false);
        return create("glow_effect", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
    }

    public static final RenderType DEMON = create("demon", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder().setShaderState(new ShaderStateShard(ShaderHandle::getRenderTypeDemon)).setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                    .add(DEMON_BACK, false, false)
                    .add(DEMON_1, false, false).build()).createCompositeState(false));
}
