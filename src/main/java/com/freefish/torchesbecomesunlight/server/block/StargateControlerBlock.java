package com.freefish.torchesbecomesunlight.server.block;

import com.freefish.torchesbecomesunlight.server.block.blockentity.StargateControlerBlockEntity;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class StargateControlerBlock extends BaseEntityBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");


    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    public StargateControlerBlock() {
        super(Properties.of()
                .mapColor(MapColor.COLOR_YELLOW)
                .strength(1.5f, 6.0f)
                .sound(SoundType.METAL)
                .lightLevel(state -> state.getValue(LIT) ? 15 : 0)
                .requiresCorrectToolForDrops());
        
        this.registerDefaultState(this.defaultBlockState().setValue(LIT, true));
    }
    
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
    
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_CHIME , SoundSource.BLOCKS, 0.8f, 1.2f );

            player.displayClientMessage(Component.translatable(
                    "message.examplemod.glow_block.activated"), true);

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof StargateControlerBlockEntity entity){
                entity.star();
            }

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

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new StargateControlerBlockEntity(pPos,pState);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, BlockEntityHandle.STARGATE_CONTROLER.get(),
                StargateControlerBlockEntity::tick);
    }
}