package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.init.ParticleHandler;
import com.freefish.torchesbecomesunlight.client.util.particle.util.AdvancedParticleBase;
import com.freefish.torchesbecomesunlight.client.util.particle.util.ParticleComponent;
import com.freefish.torchesbecomesunlight.server.entity.dlc.GunKnightPatriot;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.story.dialogue.DialogueStore;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

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
        if(mode==Mode.PARTICLE){
            Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(player, new Vec3(0, 0, 2));
            if(level.isClientSide){
                //level.addParticle(ParticleHandler.TESLA_BULB_LIGHTNING.get(),player.getX(),player.getY(),player.getZ(),0,2,0);
                //level.addParticle(new CycleWindParticle.CycleWindData(player.getId()),player.getX(),player.getY(),player.getZ(),0,0,0);

                for(int i =1;i<=3;i++){
                    AdvancedParticleBase.spawnParticle(player.level(), ParticleHandler.RING_BIG.get(), player.getX(), player.getY(), player.getZ(), 0, 0, 0, false, 0, 1.57, 0, 0, 16, 1, 1, 1, 1, 0, 20, true, false, new ParticleComponent[]{
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(0.1f*i, -0.1f*i), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0,1,1},new float[]{0,0.2f*i,1}), false),
                            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, new ParticleComponent.KeyTrack(new float[]{0,8*3,16},new float[]{0,0.3f*i,1}), false)
                    });
                }
            }
        }
        if(mode==Mode.SUMMON_GROUP&&!level.isClientSide){
            GunKnightPatriot gunKnightPatriot = MathUtils.getClosestEntity(player,level.getEntitiesOfClass(GunKnightPatriot.class,player.getBoundingBox().inflate(5)));
            if(gunKnightPatriot!=null) {
                int gunMod = gunKnightPatriot.getGunMod() + 1;
                gunKnightPatriot.setGunMod(gunMod > 2 ? 0 : gunMod);
            }
            //Mangler mangler = new Mangler(EntityHandle.MANGLER.get(),level,true);
            //mangler.setPos(player.position());
            //if(!level.isClientSide){
            //    level.addFreshEntity(mangler);
            //}
            //mangler.spawnHerd();
        }else if(mode==Mode.DIALOGUE){

            Bullet abstractarrow = new Bullet(level,player,0);
            abstractarrow.setPos(player.position());
            Vec3 target = new Vec3(0,0,10).yRot((float) (-player.getYRot() / 180 * Math.PI)).add(player.position());
            double d0 = target.x - player.position().x;
            double d1 = target.y - player.position().y;
            double d2 = target.z - player.position().z;
            abstractarrow.shoot(d0, d1 , d2, 0.08F, 0);
            level.addFreshEntity(abstractarrow);
            //List<Man> entitiesOfClass = level.getEntitiesOfClass(Man.class, player.getBoundingBox().inflate(8));
            //if(entitiesOfClass.size()>=2){
            //    Man man1 = entitiesOfClass.get(0);
            //    Man man2 = entitiesOfClass.get(1);
            //    man2.setDialogueEntity(man1);
            //    man1.setDialogueEntity(man2);
            //    DialogueEntity dialogueEntity = new DialogueEntity(man1,level,DialogueStore.dialogue,man1,man2);
            //    dialogueEntity.setPos(man1.position());
            //    level.addFreshEntity(dialogueEntity);
            //}
        }
        
        return super.use(level, player, pUsedHand);
    }


    enum Mode{
        MOVE("move"),CLEAN_ENTITY("clean_entity"),PLAY_ANIMATION("play_animation"),PARTICLE("particle"),START_DIALOGUE("start_dialogue"),SUMMON_GROUP("summon_group"),DIALOGUE("dialogue");

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
                case SUMMON_GROUP -> DIALOGUE;
            };
        }
    }
}
