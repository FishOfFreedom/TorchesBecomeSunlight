package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.entity.AnimatedEntity;
import com.freefish.torchesbecomesunlight.server.entity.animal.Mangler;
import com.freefish.torchesbecomesunlight.server.entity.effect.AnimationBlock;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
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
                    AnimationBlock.spawnRing(player.position(),4,level);
                    //RosmontisInstallation.SpawnInstallation(level,player,0);
                    //RosmontisInstallation.SpawnInstallation(level,player,1);
                    //RosmontisInstallation.SpawnInstallation(level,player,2);
                    //RosmontisInstallation.SpawnInstallation(level,player,3);

                    //Dialogue dialogue = DialogueManager.INSTANCE.readDialogueFromData(new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "dialogue/user.json"), level);
                    //DialogueEntity dialogueEntity = new DialogueEntity(player, level, dialogue, player, player);
                    //level.addFreshEntity(dialogueEntity);

                    //EntityMultiBlock entityMultiBlock = new EntityMultiBlock(EntityHandle.MULTI_BLOCK.get(), level);
                    //entityMultiBlock.setPos(player.position().add(0,10,0));
                    //BlockPos[]  blockPos =     new BlockPos[27]  ;
                    //BlockState[] blockStates = new BlockState[27];
                    //for(int i = 0;i<3;i++){
                    //    for(int j = 0;j<3;j++){
                    //        for(int k = 0;k<3;k++){
                    //            int index = i*9+j*3+k;
                    //            blockPos[index] = (new BlockPos(i,j,k));
                    //            blockStates[index] = (Blocks.DIRT.defaultBlockState());
                    //        }
                    //    }
                    //}
                    //entityMultiBlock.setMultiBlock(blockStates,blockPos);
                    //level.addFreshEntity(entityMultiBlock);

                    //Item item = ItemHandle.COOKED_THIGH_MEAT.get();
                    //ItemStack itemStack = new ItemStack(item);
                    //FoodProperties foodProperties = item.getFoodProperties(itemStack, player);
                }
                else if(mode==Mode.CLEAN_ENTITY){
                    List<Entity> entities = level.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(6));
                    Entity livingEntity = MathUtils.getClosestEntity(player, entities);
                    if(livingEntity!=null)
                        livingEntity.kill();
                }else if(mode==Mode.START_DIALOGUE){
                    List<DialogueEntity> entities = level.getEntitiesOfClass(DialogueEntity.class, player.getBoundingBox().inflate(6));
                    DialogueEntity livingEntity = MathUtils.getClosestEntity(player, entities);
                    //livingEntity.startSpeak(DialogueStore.snownova_meet_1,100);
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
            Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(player, new Vec3(0, 0, 4));
            if(level.isClientSide){
                //ActRangeSignMessage.showSectorInter(player,player.position(),1,1,(float) (player.getYRot()),level);
                //RLCubeParticle rlCubeParticle = new RLCubeParticle(level);
                //rlCubeParticle.config.setStartColor(new Gradient(new GradientColor(0X1FFFFFFF)));
                //rlCubeParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Vertical);
                //rlCubeParticle.config.getMaterial().setMaterial(TBSMaterialHandle.NO_GLOW_PIXEL.create());
                //BlockEffect effect = new BlockEffect(level,bodyRotVec);;
                //rlCubeParticle.emmit(effect);

                //level.addParticle(ParticleHandler.TESLA_BULB_LIGHTNING.get(),player.getX(),player.getY(),player.getZ(),0,2,0);
                //level.addParticle(new CycleWindParticle.CycleWindData(player.getId()),player.getX(),player.getY(),player.getZ(),0,0,0);

                //for(int i = 0;i<4;i++){
                //    level.addParticle(new WindParticle.WindData(40, 4+i/2f, 0.3f+player.getRandom().nextFloat()*0.3f, player.getRandom().nextFloat()*3.14f), bodyRotVec.x, bodyRotVec.y,bodyRotVec.z, 0, 0, 0);
                //}
                //for(int i=0;i<10;i++){
                //    for(int j=0;j<5;j++){
                //        Vec3 vec3 = new Vec3(0, 0, 0.3).xRot((float) ((j/10f) * org.joml.Math.PI)).yRot((float) ((i/5f) * org.joml.Math.PI)+level.random.nextFloat());
                //        level.addParticle(new ParticleCloud.CloudData(ParticleHandler.CLOUD.get(), 1,1,1, (float) (30d), 100, ParticleCloud.EnumCloudBehavior.SHRINK, 1f), bodyRotVec.x, bodyRotVec.y,bodyRotVec.z, vec3.x*4, vec3.y*4, vec3.z*4);
                //    }
                //
                //}

                //for(int i =1;i<=3;i++){
                //    AdvancedParticleBase.spawnParticle(player.level(), ParticleHandler.RING_BIG.get(), player.getX(), player.getY(), player.getZ(), 0, 0, 0, false, 0, 1.57, 0, 0, 16, 1, 1, 1, 1, 0, 20, true, false, new ParticleComponent[]{
                //            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.MOTION_Y, ParticleComponent.KeyTrack.startAndEnd(0.1f*i, -0.1f*i), false),
                //            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.ALPHA, new ParticleComponent.KeyTrack(new float[]{0,1,1},new float[]{0,0.2f*i,1}), false),
                //            new ParticleComponent.PropertyControl(ParticleComponent.PropertyControl.EnumParticleProperty.SCALE, new ParticleComponent.KeyTrack(new float[]{0,8*3,16},new float[]{0,0.3f*i,1}), false)
                //    });
                //}

                //RLParticle rlParticle = new RLParticle(level);
                //rlParticle.setPos(bodyRotVec.x,bodyRotVec.y,bodyRotVec.z);
                //rlParticle.config.getShape().setShape(new Circle());
                //rlParticle.config.getVelocityOverLifetime().setEnable(true);
                //rlParticle.config.getVelocityOverLifetime().setOrbitalMode(VelocityOverLifetimeSetting.OrbitalMode.AngularVelocity);
                //rlParticle.config.getVelocityOverLifetime().setOrbital(new NumberFunction3(
                //        NumberFunction.constant(1),
                //        NumberFunction.constant(1),
                //        NumberFunction.constant(1)
                //));

                //rlParticle.emmit(null);
            }
        }
        if(mode==Mode.SUMMON_GROUP&&!level.isClientSide){
            Mangler mangler = new Mangler(EntityHandle.MANGLER.get(),level);
            mangler.setPos(player.position());
            mangler.spawnHerd();
            level.addFreshEntity(mangler);

            //LightingBoom lightingBoom = new LightingBoom(EntityHandle.LIGHT_BOOM.get(), level);
            //Vec3 bodyRotVec = FFEntityUtils.getBodyRotVec(player, new Vec3(0, 0, 1)).subtract(player.position());
            //lightingBoom.shoot(bodyRotVec.x,bodyRotVec.y,bodyRotVec.z,1.5f,0);
            //lightingBoom.setPos(player.getX(),player.getY(),player.getZ());
            //lightingBoom.setOwner(player);
            //level.addFreshEntity(lightingBoom);
        }else if(mode==Mode.DIALOGUE){

            //Bullet abstractarrow = new Bullet(level,player,0);
            Arrow abstractarrow = new Arrow(level,player);
            abstractarrow.setPos(player.position());
            Vec3 target = new Vec3(0,0,10).yRot((float) (-player.getYRot() / 180 * Math.PI)).add(player.position());
            double d0 = target.x - player.position().x;
            double d1 = target.y - player.position().y;
            double d2 = target.z - player.position().z;
            abstractarrow.shoot(d0, d1 , d2, 40F, 0);
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
