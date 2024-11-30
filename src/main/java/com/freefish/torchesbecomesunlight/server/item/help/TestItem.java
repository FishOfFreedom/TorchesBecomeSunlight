package com.freefish.torchesbecomesunlight.server.item.help;

import com.bobmowzie.mowziesmobs.client.particle.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.particle.BlackFlatParticle;
import com.freefish.torchesbecomesunlight.client.particle.BladeParticle;
import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.capability.frozen.FrozenCapabilityProvider;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.effect.SpeedEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
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
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        if (entity instanceof LivingEntity livingEntity) {
            if (!entity.level().isClientSide) {
                MobEffectInstance effect = livingEntity.getEffect(EffectHandle.COLLAPSAL.get());
                if(effect!=null){
                    //System.out.println(effect.getAmplifier());
                }
            }
            return true;
        }
        return false;
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
                level.addParticle(new BlackFlatParticle.BlackFlatData(100,3),player.getX(), player.getY(), player.getZ(),player.getX(), player.getY()+10, player.getZ()+10);
                //level.addParticle(new BladeParticle.BladeData(100,10,0,0,1),player.getX(), player.getY(), player.getZ(),0,0,0);
            }
        }
        if(mode==Mode.SUMMON_GROUP){
            Mangler mangler = new Mangler(EntityHandle.MANGLER.get(),level,true);
            mangler.setPos(player.position());
            if(!level.isClientSide){
                level.addFreshEntity(mangler);
            }
            mangler.spawnHerd();
        }
        
        return super.use(level, player, pUsedHand);
    }


    enum Mode{
        MOVE("move"),CLEAN_ENTITY("clean_entity"),PLAY_ANIMATION("play_animation"),PARTICLE("particle"),START_DIALOGUE("start_dialogue"),SUMMON_GROUP("summon_group");

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
                case START_DIALOGUE -> SUMMON_GROUP;
            };
        }
    }
}
