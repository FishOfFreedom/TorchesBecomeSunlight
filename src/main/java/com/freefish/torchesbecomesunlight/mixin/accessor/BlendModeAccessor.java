package com.freefish.torchesbecomesunlight.mixin.accessor;

import com.mojang.blaze3d.shaders.BlendMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * @author KilaBash
 * @date 2022/7/10
 * @implNote BlendModeMixin
 */
@Mixin(BlendMode.class)
public interface BlendModeAccessor {
    @Accessor
    static void setLastApplied(BlendMode mode) {}
    @Accessor
    static BlendMode getLastApplied() { return null;}
}
