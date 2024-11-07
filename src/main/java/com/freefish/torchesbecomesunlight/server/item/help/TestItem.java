package com.freefish.torchesbecomesunlight.server.item.help;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.help.SpeedEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

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
                        SpeedEntity speedEntity = new SpeedEntity(EntityHandle.SPEED_ENTITY.get(), level,livingEntity);
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
                    if(livingEntity instanceof Patriot snowNova)
                        AnimationActHandler.INSTANCE.sendAnimationMessage(snowNova,Patriot.PIERCE2);
                }
            }
        }
        else {
            if(mode==Mode.PARTICLE){

                //level.addParticle(new BlackFlatParticle.BlackFlatData(100,0,(float) player.getX(),(float) player.getY()+20,(float) player.getZ()),player.getX(), player.getY(), player.getZ(),0,0,0);
                level.addParticle(ParticleHandler.BLACK_FLAT.get(),player.getX(), player.getY(), player.getZ(),0,0,0);
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
                case MOVE -> PARTICLE;
                case PARTICLE -> PLAY_ANIMATION;
                case PLAY_ANIMATION -> CLEAN_ENTITY;
                case CLEAN_ENTITY -> START_DIALOGUE;
                case START_DIALOGUE -> ADD_ICE;
            };
        }
    }
}