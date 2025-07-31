package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.rosmontislib.client.particle.advance.base.particle.RLParticle;
import com.freefish.rosmontislib.client.particle.advance.data.EmissionSetting;
import com.freefish.rosmontislib.client.particle.advance.data.RendererSetting;
import com.freefish.rosmontislib.client.particle.advance.data.material.MaterialHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction;
import com.freefish.rosmontislib.client.particle.advance.data.number.NumberFunction3;
import com.freefish.rosmontislib.client.particle.advance.data.number.RandomConstant;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.Gradient;
import com.freefish.rosmontislib.client.particle.advance.data.number.color.GradientHandle;
import com.freefish.rosmontislib.client.particle.advance.data.number.curve.Line;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Dot;
import com.freefish.rosmontislib.client.particle.advance.data.shape.Sphere;
import com.freefish.rosmontislib.client.particle.advance.effect.BlockEffect;
import com.freefish.rosmontislib.client.utils.GradientColor;
import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoPlayer;
import com.freefish.torchesbecomesunlight.client.render.gui.hud.PartnerListGui;
import com.freefish.torchesbecomesunlight.compat.rosmontis.EntityPosRotEffect;
import com.freefish.torchesbecomesunlight.compat.rosmontis.TBSMaterialHandle;
import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.block.blockentity.HookPlayerClick;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.projectile.Bullet;
import com.freefish.torchesbecomesunlight.server.entity.projectile.IceCrystal;
import com.freefish.torchesbecomesunlight.server.entity.rhodesIsland.rosmontis.RosmontisInstallation;
import com.freefish.torchesbecomesunlight.server.event.ServerNetwork;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.PartnerCommandTriggerMessage;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.CustomResourceKey;
import com.freefish.torchesbecomesunlight.server.item.weapon.WinterPass;
import com.freefish.torchesbecomesunlight.server.partner.IMobPartner;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerPlayerManager;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.BlockTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TargerTrigger;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicHandle;
import com.freefish.torchesbecomesunlight.server.partner.command.triggertype.TriggerBasicType;
import com.freefish.torchesbecomesunlight.server.story.PlayerStoryStoneData;
import com.freefish.torchesbecomesunlight.server.story.task.Task;
import com.freefish.torchesbecomesunlight.server.story.task.TaskType;
import com.freefish.torchesbecomesunlight.server.util.FFEntityUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class PlayerCapability {
    public static ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "player_cap");

    public interface IPlayerCapability extends INBTSerializable<CompoundTag> {

        int getHalberdUseTime();
        void setHalberdUseTime(int HalberdUseTime);

        int getSkillAmount();
        void setSkillAmount(int skillAmount,LivingEntity living);

        void tick(TickEvent.PlayerTickEvent event);

        void playerHurt(LivingHurtEvent event,Player player);

        void playerByDamage(LivingHurtEvent event,Player player);

        void onPlayerEmptyInteract(Player player, boolean isRightClick, boolean isMainHand);

        void addedToWorld(EntityJoinLevelEvent event);

        void addPartner(Partner<?> partner);

        void setHookPlayer(HookPlayerClick hookPlayerClick);

        void onLeftClickAir(PlayerInteractEvent player);

        long getSummonTime();
        void setSummonTime(long summonTime);

        void setIsSankta(boolean isSankta);

        boolean isSankta();

        void interRosmontisInstallation(LivingEntity entity,boolean isOpen);

        PlayerStoryStoneData getPlayerStory();

        HookPlayerClick getHookPlayer();

        PartnerPlayerManager getPartnerManager();

        <T extends Task> List<T> getTaskByType(TaskType<T> taskType);

        CompoundTag writePlaySkillMessage();
        int getSacredHalberd();

        void readPlaySkillMessage(CompoundTag compoundTag,LivingEntity living);

        @OnlyIn(Dist.CLIENT)
        GeckoPlayer.GeckoPlayerThirdPerson getGeckoPlayer();
    }

    public static class PlayerCapabilityImp implements IPlayerCapability {
        private int HalberdUseTime;
        private int triggerAttackIn;
        private int dialogueNeedTime;
        private int skillAmount;
        private boolean isSankta;
        private int sacredHalberdActTime;
        private int sacredHalberdActCoolDown;

        private long summonTime;

        private List<Task> taskList;
        private PartnerPlayerManager partnerPlayerManager = new PartnerPlayerManager();
        private final PlayerStoryStoneData playerStoneData = new PlayerStoryStoneData();
        private HookPlayerClick hookPlayerClick;
        public RosmontisInstallation[] installations = new RosmontisInstallation[4];

        @OnlyIn(Dist.CLIENT)
        private GeckoPlayer.GeckoPlayerThirdPerson geckoPlayer;

        @OnlyIn(Dist.CLIENT)
        public GeckoPlayer.GeckoPlayerThirdPerson getGeckoPlayer() {
            return geckoPlayer;
        }
        @Override
        public void addedToWorld(EntityJoinLevelEvent event) {
            // Create the geckoplayer instances when an entity joins the world
            // Normally, the animation controllers and lastModel field are only set when rendered for the first time, but this won't work for player animations
            if (event.getLevel().isClientSide()) {
                Player player = (Player) event.getEntity();
                geckoPlayer = new GeckoPlayer.GeckoPlayerThirdPerson(player);
                // Only create 1st person instance if the player joining is this client's player
                if (event.getEntity() == Minecraft.getInstance().player) {
                    PartnerListGui.INSTANCE.reset();
                    GeckoPlayer.GeckoPlayerFirstPerson geckoPlayerFirstPerson = new GeckoPlayer.GeckoPlayerFirstPerson(player);
                }
            }else {
            }
        }

        @Override
        public void addPartner(Partner<?> partner) {
            partnerPlayerManager.addPartner(partner);
            partnerPlayerManager.setCurrentPartner(partner);
        }

        @Override
        public void setHookPlayer(HookPlayerClick hookPlayerClick) {
            this.hookPlayerClick = hookPlayerClick;
        }

        @Override
        public void onLeftClickAir(PlayerInteractEvent event) {
            Player player = event.getEntity();

            PartnerPlayerManager partnerManager = getPartnerManager();
            PartnerCommandBasic partnerCommandBasic = partnerManager.currentCommandBasic();
            if(partnerCommandBasic!=null){
                TriggerBasicType triggerType = partnerCommandBasic.getTriggerType();
                if(triggerType == TriggerBasicHandle.BLOCK_TRIGGER){
                    BlockHitResult hitResult = (BlockHitResult) player.pick(20.0D, 0.0F, false);
                    BlockTrigger blockTrigger = new BlockTrigger(hitResult.getLocation());
                    int i = partnerManager.getCurrentPartner().getSkillManager().getPartnerCommandBasics().indexOf(partnerCommandBasic);
                    TorchesBecomeSunlight.NETWORK.sendToServer(new PartnerCommandTriggerMessage(player.getId(), i, blockTrigger.serializeNBT()));
                    partnerManager.getCurrentPartner().getSkillManager().setCurrentCommand(null);
                }else if(triggerType == TriggerBasicHandle.TARGER_TRIGGER){
                    Vec3 add = FFEntityUtils.getHeadRotVec(player, new Vec3(0, 0, 20)).add(0, player.getEyeHeight(), 0);
                    EntityHitResult entityHitResult = FFEntityUtils.getEntityHitResult(player.level(), player, player.getEyePosition(), add, player.getBoundingBox().expandTowards(add.subtract(player.position())).inflate(1.0D));
                    if (entityHitResult!=null&&entityHitResult.getEntity() instanceof LivingEntity living) {
                        TargerTrigger blockTrigger = new TargerTrigger(living.getId());
                        int i = partnerManager.getCurrentPartner().getSkillManager().getPartnerCommandBasics().indexOf(partnerCommandBasic);
                        TorchesBecomeSunlight.NETWORK.sendToServer(new PartnerCommandTriggerMessage(player.getId(), i, blockTrigger.serializeNBT()));
                        partnerManager.getCurrentPartner().getSkillManager().setCurrentCommand(null);
                    }
                }
            }

            if(hookPlayerClick!=null){
                if(hookPlayerClick.isLocalPlayerLooked){
                    hookPlayerClick.click(player);
                    if (!(event instanceof PlayerInteractEvent.LeftClickEmpty)) {
                        event.setCanceled(true);
                    }
                    hookPlayerClick = null;
                }
            }
        }

        @Override
        public long getSummonTime() {
            return summonTime;
        }

        @Override
        public void setSummonTime(long summonTime) {
            this.summonTime = summonTime;
        }

        @Override
        public void setIsSankta(boolean isSankta) {
            this.isSankta = isSankta;
        }

        @Override
        public boolean isSankta() {
            return isSankta;
        }

        @Override
        public void interRosmontisInstallation(LivingEntity entity,boolean isOpen) {
            if(isOpen){
                if(this.installations[2]==null) {
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 2);
                    this.installations[2] = rosmontisInstallation;
                }
                if(this.installations[3]==null) {
                    RosmontisInstallation rosmontisInstallation = RosmontisInstallation.SpawnInstallation(entity.level(), entity, 3);
                    this.installations[3] = rosmontisInstallation;
                }
            }else {
                if(this.installations[2]!=null) {
                    this.installations[2].setAnimation(-1,10);
                    this.installations[2] = null;
                }
                if(this.installations[3]!=null) {
                    this.installations[3].setAnimation(-1,10);
                    this.installations[3] = null;
                }
            }
        }

        @Override
        public PlayerStoryStoneData getPlayerStory() {
            return playerStoneData;
        }

        @Override
        public HookPlayerClick getHookPlayer() {
            return hookPlayerClick;
        }

        @Override
        public PartnerPlayerManager getPartnerManager() {
            return partnerPlayerManager;
        }

        public <T extends Task> List<T> getTaskByType(TaskType<T> taskType){
            List<T> tasks = new ArrayList<>();
            for(Task task:taskList){
                if(task.getTaskType() == taskType){
                    tasks.add((T)task);
                }
            }
            return tasks;
        };

        @Override
        public int getHalberdUseTime() {
            return HalberdUseTime;
        }

        @Override
        public void setHalberdUseTime(int HalberdUseTime) {
            this.HalberdUseTime = HalberdUseTime;
        }

        @Override
        public int getSkillAmount() {
            return skillAmount;
        }

        @Override
        public void setSkillAmount(int skillAmount,LivingEntity living) {
            this.skillAmount = skillAmount;
            if(!living.level().isClientSide){
                ServerNetwork.toClientMessage(living,new SynCapabilityMessage(living,writePlaySkillMessage()));
            }
        }

        public void tick(TickEvent.PlayerTickEvent event) {
            if(triggerAttackIn>0) triggerAttackIn--;
            if(sacredHalberdActCoolDown>0) sacredHalberdActCoolDown--;

            Player player = event.player;
            Level level = player.level();

            partnerPlayerManager.tick();
            if(!level.isClientSide){
                if(player.tickCount==1){
                    ServerNetwork.toClientMessage(player, new SynCapabilityMessage(player, writePlaySkillMessage()));
                }

                if(player.tickCount==10){
                    List<Partner<?>> partnerList = partnerPlayerManager.getPartnerList();
                    for(Partner partner:partnerList){
                        if(partner.isInited()) continue;
                        Entity entity = ((ServerLevel) level).getEntity(partner.playerUUid);
                        Entity entity1 = ((ServerLevel) level).getEntity(partner.mobUUid);
                        if(entity instanceof Player partnerPlayer&&entity1 instanceof Mob partnerMob){
                            partner.init(partnerPlayer,partnerMob);
                            ((IMobPartner) partnerMob).setPartner(partner);
                            CompoundTag all = new CompoundTag();
                            CompoundTag compoundTag = new CompoundTag();
                            compoundTag.putInt("playerid",partnerPlayer.getId());
                            compoundTag.putInt("partnerid",partnerMob.getId());
                            compoundTag.putString("partnertype",PartnerUtil.getKey(partner.getPartnerType()).toString());
                            all.put("partnerdata",compoundTag);
                            TorchesBecomeSunlight.NETWORK.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player), new SynCapabilityMessage(player,all));
                        }else {
                            partner.remove();
                        }
                    }
                }
            }
            //List<Partner<?>> partnerList = partnerPlayerManager.getPartnerList();
            //if(!partnerList.isEmpty()){
            //    partnerPlayerManager.tick();
            //    if (level.isClientSide){
            //        PartnerListGui.INSTANCE.open();
            //    }
            //}else {
            //    if (level.isClientSide){
            //        PartnerListGui.INSTANCE.close();
            //    }
            //}

            if(player.tickCount%5==0&&skillAmount<100) skillAmount++;

            if(!level.isClientSide){
                if(isSankta&&player.tickCount%20==0){
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE,120));
                    player.addEffect(new MobEffectInstance(MobEffects.REGENERATION,120));
                    player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED,120));
                    player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED,120));
                    player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,120));
                }
            }


            ItemStack usedItemHand = player.getUseItem();
            if(usedItemHand.is(ItemHandle.INFECTED_HALBERD.get())){
                int useTick = 72000 - player.getUseItemRemainingTicks();
                doHalberdFx(player,useTick,level);
            }else if(usedItemHand.is(ItemHandle.GUN.get())){
                int useTick = 72000 - player.getUseItemRemainingTicks();
                shotGun(player,useTick,level,usedItemHand);
            }else if(usedItemHand.is(ItemHandle.INFECTED_SHIELD.get())){
                int useTick = 72000 - player.getUseItemRemainingTicks();
                float range = 2;
                float arc = 60;
                List<LivingEntity> entitiesHit = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(range+5, 3, range+5), e -> e != player && player.distanceTo(e) <= range + e.getBbWidth() / 2f && e.getY() <= player.getY() + 3);
                for (LivingEntity entityHit : entitiesHit) {
                    float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - player.getZ(), entityHit.getX() - player.getX()) * (180 / Math.PI) - 90) % 360);
                    float entityAttackingAngle = player.getYRot() % 360;
                    if (entityHitAngle < 0) {
                        entityHitAngle += 360;
                    }
                    if (entityAttackingAngle < 0) {
                        entityAttackingAngle += 360;
                    }
                    float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
                    float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - player.getZ()) * (entityHit.getZ() - player.getZ()) + (entityHit.getX() - player.getX()) * (entityHit.getX() - player.getX())) - entityHit.getBbWidth() / 2f;
                    if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) || (entityRelativeAngle >= 360 - arc / 2 || entityRelativeAngle <= -360 + arc / 2)) {
                        Vec3 offset = entityHit.position().subtract(player.position()).normalize();
                        entityHit.setDeltaMovement(entityHit.getDeltaMovement().add(new Vec3(offset.x*0.2,0,offset.y*0.2)));
                    }
                }
            }else if(usedItemHand.is(ItemHandle.PHANTOM_GRASP.get())){
                int useTick = 72000 - player.getUseItemRemainingTicks();
                if(level.isClientSide&&useTick>1&&useTick%20==0){
                    BlockHitResult hitResult = (BlockHitResult) player.pick(20.0D, 0.0F, false);
                    Vec3 targetPos = hitResult.getLocation();
                    Gradient toAlpha = new Gradient(new GradientColor(0XFFFFFFFF, 0XFFFFFFFF, 0X00FFFFFF));

                    RLParticle rlParticle = new RLParticle(level);
                    rlParticle.config.setDuration(80);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle.config.setStartSize(new NumberFunction3(6));
                    rlParticle.config.setStartColor(new Gradient(new GradientColor(0XDFB2F5FF)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                    EmissionSetting.Burst burst = new EmissionSetting.Burst(); burst.setCount(NumberFunction.constant(1));
                    rlParticle.config.getEmission().addBursts(burst);
                    rlParticle.config.getShape().setShape(new Dot());
                    rlParticle.config.getMaterial().setMaterial(MaterialHandle.RING.create());
                    rlParticle.config.getRenderer().setRenderMode(RendererSetting.Particle.Mode.Horizontal);
                    rlParticle.config.getLights().open();
                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(toAlpha);
                    rlParticle.config.getSizeOverLifetime().open();
                    rlParticle.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0,0.33f,1},new float[]{0,0.7f,1})));

                    BlockEffect blockEffect = new BlockEffect(level, targetPos.add(0,0.2,0));
                    rlParticle.emmit(blockEffect);
                }
            }
        }

        boolean isright;

        @Override
        public void playerHurt(LivingHurtEvent event,Player player) {
            Level level = player.level();

            if(event.getSource().is(CustomResourceKey.NO_TRIGGER_ATTACK)|event.getSource().is(CustomResourceKey.NO_TRIGGER_WITHOUT_ARMOR_ATTACK)) return;

            if(isSankta){
                LivingEntity entity = event.getEntity();
                if(entity.distanceTo(player)>10){
                    event.setAmount(event.getAmount()*1.5f);
                }
            }

            if(player.getItemBySlot(EquipmentSlot.CHEST).is(ItemHandle.WINTER_SCRATCH.get())&&triggerAttackIn==0){
                RandomSource random = player.getRandom();
                if(!level.isClientSide) IceCrystal.spawnWaitCrystal(player.level(),new Vec3(4-random.nextInt(9),2+random.nextInt(5),4-random.nextInt(9)).add(player.position()),player,event.getEntity());
                triggerAttackIn = 20;
            }

            Vec3 offset = player.position().subtract(event.getEntity().position()).normalize();
            Vec3 position = event.getEntity().position().add(offset);

            if(isright) {
                if (this.installations[2] != null) {
                    this.installations[2].skill4(0, position);
                }
            } else {
                if (this.installations[3] != null) {
                    this.installations[3].skill4(1, position);
                }
            }
            isright = !isright;

            if(player.getMainHandItem().is(ItemHandle.SACRED_HALBERD.get())){
                if(sacredHalberdActTime>0){
                    float amount = event.getAmount();
                    event.setAmount(amount*(1+sacredHalberdActTime/10f));
                }
                if(sacredHalberdActCoolDown==0){
                    if(sacredHalberdActTime<10) sacredHalberdActTime+=1;
                    sacredHalberdActCoolDown = 20;
                }
            }
        }

        @Override
        public void playerByDamage(LivingHurtEvent event, Player player) {
            if(player.getMainHandItem().is(ItemHandle.SACRED_HALBERD.get())){
                if(sacredHalberdActTime<0){
                    float amount = event.getAmount();
                    event.setAmount(amount*(1+sacredHalberdActTime/40f));
                }
                if(sacredHalberdActCoolDown==0){
                    if(sacredHalberdActTime>-10) sacredHalberdActTime-=1;
                    sacredHalberdActCoolDown = 20;
                }
            }
            PartnerPlayerManager partnerManager = getPartnerManager();
            List<Partner<?>> partnerList = partnerPlayerManager.getPartnerList();
            for(Partner partner:partnerList){
                if(partner.playerHurt(event)){
                }else {
                    event.setAmount(0);
                }
            }
        }

        @Override
        public void onPlayerEmptyInteract(Player player, boolean isRightClick, boolean isMainHand) {
            if(isRightClick){
                int i1 = ConfigHandler.COMMON.TOOLs.SCRATCH.skillAmount1.get();
                int i2 = ConfigHandler.COMMON.TOOLs.SANKTA_RING.skillAmount1.get();

                if (player.getItemBySlot(EquipmentSlot.CHEST).is(ItemHandle.WINTER_SCRATCH.get())) {
                    if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()&&getSkillAmount()>i1) {
                        AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.USE_SCRATCH_ABILITY);
                        setSkillAmount(getSkillAmount()-i1,player);
                    }
                } else if (isSankta && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
                    if (getSkillAmount()>i2) {
                        AbilityHandler.INSTANCE.sendAbilityMessage(player, AbilityHandler.USE_SANKTA_RING_ABILITY);
                        setSkillAmount(getSkillAmount()-i2,player);
                    }
                }
            }else {
                if (player.getItemBySlot(EquipmentSlot.MAINHAND).getItem() instanceof WinterPass winterPass) {
                    Ability ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.USE_PASS_ABILITY);

                    if(ability!=null&&ability.isUsing()&&!player.getCooldowns().isOnCooldown(winterPass)){
                        player.getCooldowns().addCooldown(winterPass,30);
                        winterPass.shootIce(player);
                    }
                }
            }
        }

        private void doHalberdFx(Player player,int tick,Level level){
            if(level.isClientSide){
                if ((tick - 1) % 10 == 0 && tick <= 101) {
                    EntityPosRotEffect entityPosRotEffect = new EntityPosRotEffect(player.level(), player, new Vec3(-0.3, 1.7, 0));

                    RLParticle rlParticle = new RLParticle(player.level());
                    rlParticle.config.setDuration(10);
                    rlParticle.config.setStartLifetime(NumberFunction.constant(10));
                    rlParticle.config.setStartSpeed(NumberFunction.constant(-1.7));
                    rlParticle.config.setStartRotation(new NumberFunction3(NumberFunction.constant(0), NumberFunction.constant(0), new RandomConstant(-50, 50, true)));
                    rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0.4));
                    Sphere sphere = new Sphere();
                    sphere.setRadius(1.3f);
                    sphere.setRadiusThickness(0.3f);

                    rlParticle.config.getShape().setShape(sphere);
                    rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.PIXEL.create());
                    rlParticle.config.getColorOverLifetime().open();
                    rlParticle.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                    rlParticle.config.trails.open();
                    rlParticle.config.trails.setColorOverLifetime(new Gradient(GradientHandle.CENTER_OPAQUE));
                    rlParticle.emmit(entityPosRotEffect);

                    if (tick == 101) {
                        RLParticle rlParticle1 = new RLParticle(player.level());
                        rlParticle1.config.setDuration(10);
                        rlParticle1.config.setStartLifetime(NumberFunction.constant(10));
                        rlParticle1.config.setStartSize(new NumberFunction3(1));
                        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                        EmissionSetting.Burst burst1 = new EmissionSetting.Burst();
                        burst1.setCount(NumberFunction.constant(1));
                        rlParticle1.config.getEmission().setEmissionRate(NumberFunction.constant(0));
                        rlParticle1.config.getEmission().addBursts(burst1);

                        rlParticle1.config.getShape().setShape(new Dot());
                        rlParticle1.config.getMaterial().setMaterial(MaterialHandle.RING.create());
                        rlParticle1.config.getColorOverLifetime().open();
                        rlParticle1.config.getColorOverLifetime().setColor(new Gradient(GradientHandle.CENTER_OPAQUE));

                        rlParticle1.config.getSizeOverLifetime().open();
                        rlParticle1.config.getSizeOverLifetime().setSize(new NumberFunction3(new Line(new float[]{0, 0.5f, 1}, new float[]{0, 1, 1})));
                        rlParticle1.emmit(entityPosRotEffect);
                    }
                }
            }
        }

        private void shotGun(Player player,int tick,Level level,ItemStack itemStack){
            if(!level.isClientSide){
                if(tick<1) tick = 1;
                int inter = Mth.lerpInt(Math.min(1,tick/60.0f),6,2);

                if(tick%inter==0){
                    Bullet abstractarrow = new Bullet(level, player, 0);
                    Vec3 headRotVec1 = FFEntityUtils.getHeadRotVec(player, new Vec3(-0.45, -0.3, 1));
                    abstractarrow.setPos(headRotVec1.add(0,1.4,0));

                    Vec3 headRotVec = FFEntityUtils.getHeadRotVec(player, new Vec3(0, 0, 1)).subtract(player.position());
                    abstractarrow.shoot(headRotVec.x, headRotVec.y, headRotVec.z, 4F, 0.1f);
                    level.addFreshEntity(abstractarrow);
                }
            }else {

            }
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compound = new CompoundTag();
            compound.putInt("skill",skillAmount);
            compound.put("partnermanager",partnerPlayerManager.serializeNBT());
            compound.putBoolean("isSankta",isSankta);
            compound.putLong("sTime",summonTime);
            compound.put("playerstory",playerStoneData.serializeNBT());
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
            skillAmount = compound.getInt("skill");
            partnerPlayerManager.deserializeNBT(compound.getCompound("partnermanager"));
            isSankta = compound.getBoolean("isSankta");
            summonTime = compound.getLong("sTime");
            if(compound.contains("playerstory")){
                playerStoneData.deserializeNBT(compound.getCompound("playerstory"));
            }
        }

        @Override
        public CompoundTag writePlaySkillMessage(){
            CompoundTag compoundTag = new CompoundTag();
            CompoundTag skillLine = new CompoundTag();

            skillLine.putBoolean("sankta",isSankta);
            skillLine.putInt("time",skillAmount);
            compoundTag.put("skill",skillLine);
            return compoundTag;
        }

        @Override
        public int getSacredHalberd() {
            return sacredHalberdActTime;
        }

        @Override
        public void readPlaySkillMessage(CompoundTag compoundTag,LivingEntity living){
            int time = compoundTag.getInt("time");
            isSankta = compoundTag.getBoolean("sankta");
            this.setSkillAmount(time,living);
        }
    }

    public static class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>
    {
        private final LazyOptional<IPlayerCapability> instance = LazyOptional.of(PlayerCapabilityImp::new);

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return CapabilityHandle.PLAYER_CAPABILITY.orEmpty(cap, instance.cast());
        }
    }
}
