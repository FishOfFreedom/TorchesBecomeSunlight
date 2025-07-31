package com.freefish.torchesbecomesunlight.server.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

public class GlowBlock extends Block {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    
    public GlowBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)
                .strength(1.5f, 6.0f)
                .sound(SoundType.METAL)
                .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                .requiresCorrectToolForDrops());
        
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, false));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            boolean newState = !state.getValue(LIT);
            level.setBlock(pos, state.setValue(LIT, newState), 3);

            level.playSound(null, pos, newState ? SoundEvents.AMETHYST_BLOCK_CHIME : SoundEvents.REDSTONE_TORCH_BURNOUT,
                    SoundSource.BLOCKS, 0.8f, newState ? 1.2f : 0.8f);
            
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            // 发光时的粒子效果
            for (int i = 0; i < 3; i++) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                double y = pos.getY() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8;
                
                level.addParticle(ParticleTypes.GLOW, x, y, z, 0.0, 0.0, 0.0);
            }
        }
    }
}