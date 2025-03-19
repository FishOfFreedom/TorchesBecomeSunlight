package com.freefish.torchesbecomesunlight.client.shader.postprocessing;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.shader.rendertarget.CopyDepthColorTarget;
import com.freefish.torchesbecomesunlight.client.shader.rendertarget.ProxyTarget;
import com.freefish.torchesbecomesunlight.client.shader.shader.RenderUtils;
import com.freefish.torchesbecomesunlight.mixin.accessor.BlendModeAccessor;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.BlendMode;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.GameRules;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @author KilaBash
 * @date 2022/05/02
 * @implNote Custom PostProcessing
 */
@SuppressWarnings("unused")
public class PostProcessing implements ResourceManagerReloadListener {

    private static final Map<String, PostProcessing> POST_PROCESSING_MAP = new HashMap<>();
    public static final PostProcessing BLOOM_UNREAL = new PostProcessing("bloom_unreal", new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "shaders/post/bloom_unreal.json"));

    public static AtomicBoolean enableBloomFilter = new AtomicBoolean(false);
    private static final Minecraft mc = Minecraft.getInstance();
    public final String name;
    private CopyDepthColorTarget postTargetWithoutColor;
    private CopyDepthColorTarget postTargetWithColor;
    private PostChain postChain = null;
    private boolean loadFailed = false;
    private final ResourceLocation shader;
    private final List<Consumer<MultiBufferSource>> postEntityDrawFilter = Lists.newArrayList();
    private final List<Consumer<MultiBufferSource>> postEntityDrawForce = Lists.newArrayList();
    private boolean hasParticle;

    private PostProcessing(String name, ResourceLocation shader) {
        this.shader = shader;
        this.name = name;
        POST_PROCESSING_MAP.put(name, this);
    }

    /**
     * register your custom postprocessing or replace the original one
     * @param name post name
     * @param shader post shader
     * @return PostProcessing
     */
    public static PostProcessing registerPost(String name, ResourceLocation shader) {
        return new PostProcessing(name, shader);
    }

    @Nullable
    public static PostProcessing getPost(String name) {
        return POST_PROCESSING_MAP.get(name);
    }

    public static Collection<PostProcessing> values() {
        return POST_PROCESSING_MAP.values();
    }

    public static PostProcessing getBlockBloom() {
        return BLOOM_UNREAL;
    }

    public static float getITime(float pPartialTicks) {
        if (mc.level == null || !mc.level.getGameRules().getRule(GameRules.RULE_DAYLIGHT).get()) {
            return System.currentTimeMillis() % 1200000 / 1000f;
        } else {
            return ((mc.level.dayTime() % 24000) + pPartialTicks) / 20f;
        }
    }

    public CopyDepthColorTarget getPostTarget(boolean hookColorAttachment) {
        if (hookColorAttachment) {
            if (postTargetWithColor == null) {
                postTargetWithColor = new CopyDepthColorTarget(mc.getMainRenderTarget(), true);
                postTargetWithColor.setClearColor(0, 0, 0, 0);
                postTargetWithColor.clear(Minecraft.ON_OSX);
            }
            return postTargetWithColor;
        }else {
            if (postTargetWithoutColor == null) {
                postTargetWithoutColor = new CopyDepthColorTarget(mc.getMainRenderTarget(), false);
                postTargetWithoutColor.setClearColor(0, 0, 0, 0);
                postTargetWithoutColor.clear(Minecraft.ON_OSX);
            }
            return postTargetWithoutColor;
        }
    }

    private PostChain getPostChain() {
        if (loadFailed) return null;
        try {
            if (postChain == null) {
                postChain = new PostChain(mc.getTextureManager(), mc.getResourceManager(), mc.getMainRenderTarget(), shader);
                postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            }
        } catch (IOException e) {
            loadFailed = true;
        }
        return postChain;
    }

    public void renderPost(@NotNull PostChain postChain, RenderTarget post, RenderTarget output) {
        RenderTarget target = postChain.getTempTarget("torchesbecomesunlight:input");
        if (target instanceof ProxyTarget) {
            ((ProxyTarget) target).setParent(post);
        }
        BlendMode lastBlendMode = BlendModeAccessor.getLastApplied();
        RenderSystem.depthMask(false);
        RenderSystem.disableDepthTest();
        postChain.process(mc.getFrameTime());
        RenderUtils.fastBlit(postChain.getTempTarget("torchesbecomesunlight:output"), output);
        BlendModeAccessor.setLastApplied(lastBlendMode);
    }

    public boolean allowPost() {
        return true;
    }

    public void renderParticlePost() {
        if (hasParticle) {
            hasParticle = false;
            RenderTarget mainTarget = mc.getMainRenderTarget();
            CopyDepthColorTarget postTarget = getPostTarget(false);
            postTarget.bindWrite(false);

            PostChain postChain = getPostChain();

            if (postChain == null) return;
            if (allowPost()) {
                renderPost(postChain, postTarget, mainTarget);
            } else {
                RenderUtils.fastBlit(postTarget, mainTarget);
            }
            postTarget.clear(Minecraft.ON_OSX);
            mainTarget.bindWrite(false);
        }
    }

    @Override
    public void onResourceManagerReload(@Nullable ResourceManager pResourceManager) {
        if (postChain != null) {
            postChain.close();
        }
        if (postTargetWithoutColor != null) {
            postTargetWithoutColor.destroyBuffers();
        }
        if (postTargetWithColor != null) {
            postTargetWithColor.destroyBuffers();
        }
        postTargetWithoutColor = null;
        postTargetWithColor = null;
        postChain = null;
        loadFailed = false;
    }

    public static void resize(int width, int height) {
        for (PostProcessing postProcessing : PostProcessing.values()) {
            if (postProcessing.postChain != null) {
                postProcessing.postChain.resize(width, height);
            }
        }

        for (PostProcessing postProcessing : PostProcessing.values()) {
            if (postProcessing.postTargetWithColor != null) {
                postProcessing.postTargetWithColor.resize(mc.getMainRenderTarget(), Minecraft.ON_OSX);
            }
            if (postProcessing.postTargetWithoutColor != null) {
                postProcessing.postTargetWithoutColor.resize(mc.getMainRenderTarget(), Minecraft.ON_OSX);
            }
        }
    }

    public void hasParticle() {
        hasParticle = true;
    }
}
