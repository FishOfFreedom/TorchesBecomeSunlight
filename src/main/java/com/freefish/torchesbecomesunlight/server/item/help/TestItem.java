package com.freefish.torchesbecomesunlight.server.item.help;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.bobmowzie.mowziesmobs.client.particle.util.AdvancedParticleBase;
import com.bobmowzie.mowziesmobs.client.particle.util.ParticleComponent;
import com.bobmowzie.mowziesmobs.client.particle.util.RibbonComponent;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.EntityRegistry;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.SnowNova;
import com.freefish.torchesbecomesunlight.server.entity.help.EntityCameraShake;
import com.freefish.torchesbecomesunlight.server.entity.help.SpeedEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.StartDialogueMessage;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.storage.RegionFile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class TestItem extends Item {
    private Mode mode = Mode.MOVE;

    public TestItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand pUsedHand) {
        if (!level.isClientSide) {
            if(player.isShiftKeyDown()) {
                mode = mode.changeMode();
                player.sendSystemMessage(Component.translatable(mode.getName()));
            }
            else {
                if(mode==Mode.MOVE) {
                    List<PathfinderMob> entities = level.getEntitiesOfClass(PathfinderMob.class, player.getBoundingBox().inflate(6));
                    PathfinderMob livingEntity = MathUtils.getClosestEntity(player, entities);
                    if (livingEntity != null) {
                        SpeedEntity speedEntity = new SpeedEntity(EntityRegistry.SPEED_ENTITY.get(), level,livingEntity);
                        speedEntity.setPos(livingEntity.position());
                        level.addFreshEntity(speedEntity);
                    }
                }
                else if(mode==Mode.CLEAN_ENTITY){
                    List<Entity> entities = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(6));
                    Entity livingEntity = MathUtils.getClosestEntity(player, entities);
                    if(livingEntity!=null)
                        livingEntity.kill();
                }else if(mode==Mode.START_DIALOGUE){
                    List<DialogueEntity> entities = level.getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(6));
                    DialogueEntity livingEntity = MathUtils.getClosestEntity(player, entities);
                    livingEntity.startSpeak(DialogueStore.snownova_meet_1,100);
                    //TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> livingEntity), new StartDialogueMessage(livingEntity.getId(),livingEntity.getId(), DialogueStore.snownova_meet_1));
                }
                else if(mode==Mode.PLAY_ANIMATION){
                    List<AnimatedEntity> entities = level.getEntitiesOfClass(AnimatedEntity.class, player.getBoundingBox().inflate(6));
                    AnimatedEntity livingEntity = MathUtils.getClosestEntity(player, entities);
                    if(livingEntity instanceof SnowNova snowNova)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(snowNova,SnowNova.REMOTE_2);
                }
            }
        }
        else {
            if(mode==Mode.PARTICLE){
                ParticleComponent.KeyTrack keyTrack1 = ParticleComponent.KeyTrack.oscillate(0, 2, 24);
                ParticleComponent.KeyTrack keyTrack2 = new ParticleComponent.KeyTrack(new float[]{0, 18, 18, 0}, new float[]{0, 0.2f, 0.8f, 1});
                AdvancedParticleBase.spawnParticle(level, ParticleHandler.ICEBOMB_1.get(), player.getX(), player.getY()+3, player.getZ(), 0, 0, 0, true, 0, 0, 0, 0, 0F, 1, 1, 1, 1, 1, 15, true, false, new ParticleComponent[]{
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack2, false),
                        new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, keyTrack1, true)
                });
            }
        }
        if(mode==Mode.ADD_ICE){
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(6));
            LivingEntity livingEntity = MathUtils.getClosestEntity(player, entities);
            if(livingEntity!=null){
                livingEntity.getCapability(FrozenCapabilityProvider.FROZEN_CAPABILITY).ifPresent(frozenCapability -> frozenCapability.setFrozen(livingEntity,100));
            }
        }
        
        return super.use(level, player, pUsedHand);
    }


    enum Mode{
        MOVE("move"),CLEAN_ENTITY("clean_entity"),PLAY_ANIMATION("play_animation"),PARTICLE("particle"),START_DIALOGUE("start_dialogue"),ADD_ICE("add_ice");

        Mode(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        final String name;

        public Mode changeMode(){
            return switch (this){
                default -> MOVE;
                case MOVE -> CLEAN_ENTITY;
                case CLEAN_ENTITY -> PLAY_ANIMATION;
                case PLAY_ANIMATION -> PARTICLE;
                case PARTICLE -> START_DIALOGUE;
                case START_DIALOGUE -> ADD_ICE;
            };
        }
    }
}
