package com.freefish.torchesbecomesunlight.mixin.accessor;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * 1.21 把 BlockBehaviour 的这几个方法收成了 protected。
 * VerticalSlabBlock 需要把调用转发给它所包装的另一个 Block 实例，
 * 而 Java 不允许跨实例访问父类的 protected 成员。
 * <p>
 * 这里用 Invoker 而不是 AT：放开 BlockBehaviour 上的方法会要求同时放开所有
 * vanilla 子类的覆写（参见 accesstransformer.cfg 中 actuallyHurt 的注释），
 * 波及面过大。
 */
@Mixin(BlockBehaviour.class)
public interface BlockBehaviourAccessor {
    @Invoker("onPlace")
    void callOnPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston);

    @Invoker("isRandomlyTicking")
    boolean callIsRandomlyTicking(BlockState state);

    @Invoker("randomTick")
    void callRandomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);

    @Invoker("tick")
    void callTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random);
}
