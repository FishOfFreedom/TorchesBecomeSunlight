package com.freefish.torchesbecomesunlight.server.event;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.ability.Ability;
import com.freefish.torchesbecomesunlight.server.ability.AbilityHandler;
import com.freefish.torchesbecomesunlight.server.ability.PlayerAbility;
import com.freefish.torchesbecomesunlight.server.capability.*;
import com.freefish.torchesbecomesunlight.server.command.TBSCommand;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.effect.Collapsal;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.SynCapabilityMessage;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.PlayerInteractMessage;
import com.freefish.torchesbecomesunlight.server.event.packet.toserver.SkipDialogueMessage;
import com.freefish.torchesbecomesunlight.server.init.EffectHandle;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.CustomResourceKey;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValues;
import com.freefish.torchesbecomesunlight.server.item.food.DishAttribute;
import com.freefish.torchesbecomesunlight.server.item.food.TBSFood;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import com.freefish.torchesbecomesunlight.server.story.IDialogueEntity;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import com.freefish.torchesbecomesunlight.server.util.storage.ClientStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Set;

public final class EventListener {
    @SubscribeEvent(priority = EventPriority.LOW)
    public void spawnEntityLimit(MobSpawnEvent.FinalizeSpawn event){
        Mob entity = event.getEntity();
        if(event.getSpawnType()== MobSpawnType.NATURAL) {
            ServerLevel level = (ServerLevel) event.getLevel();
            if (entity instanceof FrostNova f) {
                boolean flad = false;
                Iterable<Entity> all = level.getEntities().getAll();
                for(Entity e:all){
                    if (e instanceof FrostNova) {
                        event.setSpawnCancelled(true);
                        flad = true;
                        break;
                    }
                }

                if(!flad){
                    f.spawnIceBreaker();
                }
            } else if (entity instanceof Patriot p) {
                boolean flad = false;
                Iterable<Entity> all = level.getEntities().getAll();
                for(Entity e:all){
                    if (e instanceof Patriot) {
                        event.setSpawnCancelled(true);
                        flad = true;
                        break;
                    }
                }

                if(!flad){
                    p.spawnShield();
                }
            } else if (entity instanceof Pursuer) {
                level.getEntities().getAll().forEach(entity1 -> {
                    if (entity1 instanceof Pursuer) {
                        event.setSpawnCancelled(true);
                        return;
                    }
                });
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onEntityHurt(LivingHurtEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();



            DamageSource source = event.getSource();
            if(source.getEntity() instanceof Player player){
                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                if(capability != null) capability.playerHurt(event,player);
            }

            if(event.getEntity() instanceof Player player){
                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                if(capability != null) capability.playerByDamage(event,player);
            }

            if(event.getSource().is(CustomResourceKey.DEMON_ATTACK)){
                entity.invulnerableTime=0;
            }
        }
    }

    @SubscribeEvent
    public void onArrowSpawn(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;
        //todo
        Entity entity = event.getEntity();

        if (entity instanceof AbstractArrow arrow) {
            if (arrow.getOwner() instanceof Player player) {
                PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
                if(capability!=null&&capability.isSankta()){
                    arrow.setNoGravity(true);

                    Vec3 motion = arrow.getDeltaMovement();
                    arrow.setDeltaMovement(motion.scale(3.0));

                    arrow.moveTo(
                            arrow.getX() + motion.x,
                            arrow.getY() + motion.y,
                            arrow.getZ() + motion.z,
                            arrow.getYRot(),
                            arrow.getXRot()
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public void onEquipmentChange(LivingEquipmentChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) {
            return;
        }
        EquipmentSlot slot = event.getSlot();
        ItemStack oldItem = event.getFrom();
        ItemStack newItem = event.getTo();
        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null){
            if(!player.level().isClientSide){
                if (newItem.is(ItemHandle.ROSMONTIS_EMBRACE.get())) {
                    capability.interRosmontisInstallation(player, true);
                } else if (oldItem.is(ItemHandle.ROSMONTIS_EMBRACE.get())) {
                    capability.interRosmontisInstallation(player, false);
                }
            }
        }
    }

    @SubscribeEvent()
    public void onEntityHurt(LivingChangeTargetEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Mob mob){
            Partner<?> partner = PartnerUtil.getPartner(mob);
            if(partner!=null){
                if(partner.getPlayer() == event.getNewTarget()){
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public void onPlayerHurt(LivingAttackEvent event) {
        if (!event.isCanceled()) {
            LivingEntity entity = event.getEntity();

            if(entity instanceof IDialogueEntity iDialogueEntity&&iDialogueEntity.canDialogue()){
                DamageSource source = event.getSource();
                Entity entityPlayer = source.getEntity();
                if(entityPlayer instanceof Player player){
                    DialogueCapability.IDialogueCapability capability = CapabilityHandle.getCapability(entity, CapabilityHandle.DIALOGUE_CAPABILITY);
                    if (capability != null) {
                        int dialogueNeedTime = capability.getDialogueNeedTime();
                        if (dialogueNeedTime > 15) {
                            event.setCanceled(true);
                            if (!entity.level().isClientSide)
                                iDialogueEntity.startDialogue(player);
                            return;
                        }
                    }
                }
            }
            if(entity instanceof IDialogueEntity dialogue){
                if(!ConfigHandler.COMMON.GLOBALSETTING.canDialogueAttack.get()&&dialogue.getDialogueEntity()!=null){
                    event.setCanceled(true);
                    return;
                }
            }

            if(entity instanceof Player player){
                Ability ability = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.USE_MACHETE_ABILITY);
                DamageSource source = event.getSource();
                if(ability!=null&&ability.isUsing()){
                    Entity entity1 = source.getEntity();
                    if(entity1!=null){
                        event.setCanceled(true);
                        return;
                    }
                }
                Ability ability1 = AbilityHandler.INSTANCE.getAbility(player, AbilityHandler.USE_SHALBERD_LIGHTWIND_ABILITY);
                if(ability1!=null&&ability1.isUsing()){
                    Entity entity1 = source.getEntity();
                    if(entity1!=null){
                        event.setCanceled(true);
                        return;
                    }
                }
                if(source.getEntity() instanceof Mob mob){
                    Partner<?> partner = PartnerUtil.getPartner(mob);
                    if(partner!=null&&partner.getPlayer()==player){
                        event.setCanceled(true);
                        return;
                    }
                }
            }else {
                if(entity instanceof Mob mob){
                    Partner<?> partner = PartnerUtil.getPartner(mob);
                    if(partner!=null&&partner.getPlayer() == event.getSource().getEntity()){
                        event.setCanceled(true);
                        return;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public  void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        Entity entity = event.getTarget();
        Player player = event.getEntity();
        if(entity instanceof IDialogueEntity iDialogueEntity&&iDialogueEntity.canDialogue()){
            DialogueCapability.IDialogueCapability capability = CapabilityHandle.getCapability(entity, CapabilityHandle.DIALOGUE_CAPABILITY);
            if (capability != null) {
                int dialogueNeedTime = capability.getDialogueNeedTime();
                if (dialogueNeedTime > 15) {
                    event.setCanceled(true);
                    if (!entity.level().isClientSide)
                        iDialogueEntity.startDialogue(player);
                }
            }
        }
    }

    @SubscribeEvent
    public  void onEntityDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if(entity instanceof Player player&&event.getSource().is(DamageTypes.LAVA)){
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(entity, CapabilityHandle.PLAYER_CAPABILITY);
            if (capability != null) {
                if (capability.isSankta()) {
                    if (!entity.level().isClientSide) {
                        for (ItemStack item : player.getInventory().items) {
                            if(item.is(ItemHandle.SANKTA_RING.get())){
                                capability.setIsSankta(false);
                                ServerNetwork.toClientMessage(player, new SynCapabilityMessage(player, capability.writePlaySkillMessage()));
                                return;
                            }
                        }
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.RightClickEmpty event) {
        Player player = event.getEntity();
        CompoundTag all = new CompoundTag();
        CompoundTag interact = new CompoundTag();
        interact.putBoolean("isMainHand",event.getHand()== InteractionHand.MAIN_HAND);
        all.put("interact",interact);
        TorchesBecomeSunlight.NETWORK.sendToServer(new PlayerInteractMessage(player.getId(),all));
    }

    @SubscribeEvent
    public void onPlayerInteract(PlayerInteractEvent.LeftClickEmpty event) {
        Player player = event.getEntity();
        CompoundTag all = new CompoundTag();
        CompoundTag interact = new CompoundTag();
        interact.putBoolean("isMainHand",event.getHand()== InteractionHand.MAIN_HAND);
        all.put("interactoff",interact);
        TorchesBecomeSunlight.NETWORK.sendToServer(new PlayerInteractMessage(player.getId(),all));
    }

    @SubscribeEvent
    public void onEffectAdded(MobEffectEvent.Added event) {
        LivingEntity entity = event.getEntity();
        MobEffect effect = event.getEffectInstance().getEffect();
        if(effect instanceof Collapsal && entity instanceof Player player){
            Iterable<ItemStack> allSlots = player.getAllSlots();
            for(ItemStack itemStack:allSlots){
                if(!itemStack.isEmpty()){
                    player.getCooldowns().addCooldown(itemStack.getItem(),100);
                }
            }
        }
    }

    @SubscribeEvent
    public void registerCommand(RegisterCommandsEvent event) {
        TBSCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public void playerClone(PlayerEvent.Clone event) {
        Player original = event.getOriginal();
        original.revive();
        PlayerCapability.IPlayerCapability old = CapabilityHandle.getCapability(original, CapabilityHandle.PLAYER_CAPABILITY);
        PlayerCapability.IPlayerCapability storystate = CapabilityHandle.getCapability(event.getEntity(), CapabilityHandle.PLAYER_CAPABILITY);
        if(old!=null&&storystate!=null){
            storystate.deserializeNBT(old.serializeNBT());
        }
    }

    @SubscribeEvent
    public void levelTick(TickEvent.LevelTickEvent event){
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START || event.player == null) {
            return;
        }
        Player player = event.player;
        Level level = player.level();

        if(level.isClientSide()){
            //todo RenderDemon
            ClientStorage.INSTANCE.update();

            int skip = ClientStorage.INSTANCE.skipRadio;
            if(ClientStorage.INSTANCE.isSkip&&skip<41){
                if(skip==40){
                    DialogueEntity dialogueEntity = MathUtils.getClosestEntity(player,level.getEntitiesOfClass(DialogueEntity.class,player.getBoundingBox().inflate(5)));
                    if(dialogueEntity!=null) {
                        ServerNetwork.toServerMessage(new SkipDialogueMessage(dialogueEntity.getId()));
                        ClientStorage.INSTANCE.skipRadio = 0;
                    }
                }
                ClientStorage.INSTANCE.skipRadio+=1;
            }
            else if(!ClientStorage.INSTANCE.isSkip&&skip>0){
                ClientStorage.INSTANCE.skipRadio-=1;
            }

            if(MathUtils.isInDemon(player)){
                if(ClientStorage.INSTANCE.demonRadio<160)ClientStorage.INSTANCE.demonRadio+=1;
            }
            else
                if(ClientStorage.INSTANCE.demonRadio>0)ClientStorage.INSTANCE.demonRadio-=1;
        }

        PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
        if(capability!=null) capability.tick(event);
    }

    @SubscribeEvent
    public void livingTick(LivingEvent.LivingTickEvent event){
        LivingEntity livingEntity = event.getEntity();
        FrozenCapability.IFrozenCapability frozen = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
        if(frozen!=null){
            frozen.tick(livingEntity);
        }

        if (event.getEntity() != null) {
            LivingEntity entity = (LivingEntity) event.getEntity();
            AbilityCapability.IAbilityCapability abilityCapability = CapabilityHandle.getCapability(entity, CapabilityHandle.ABILITY_CAPABILITY);
            if (abilityCapability != null) {
                abilityCapability.tick(entity);
                Ability activeAbility = abilityCapability.getActiveAbility();
                if(activeAbility != null&& entity instanceof Player player){
                    if (activeAbility instanceof PlayerAbility playerAbility) {
                        if(playerAbility.heldItemMainHandOverride()==null){
                            AbilityHandler.INSTANCE.sendInterruptAbilityMessage(player, activeAbility.getAbilityType());
                        }
                        else {
                            if(!player.getMainHandItem().equals(playerAbility.heldItemMainHandOverride(),false))
                                AbilityHandler.INSTANCE.sendInterruptAbilityMessage(player, activeAbility.getAbilityType());
                        }
                    }
                }
            }
        }

        DialogueCapability.IDialogueCapability capability = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.DIALOGUE_CAPABILITY);
        if(capability!=null) capability.tick(event);
    }

    @SubscribeEvent
    public void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        Player player = event.getEntity();
        ResourceLocation advancementId = event.getAdvancement().getId();

        if (player instanceof ServerPlayer) TriggerHandler.CHAINED_ADVANCEMENT_TRIGGER.trigger((ServerPlayer)player,advancementId);
    }

    @SubscribeEvent
    public void healBlockEvent(LivingHealEvent event){
        LivingEntity livingEntity = event.getEntity();
        FrozenCapability.IFrozenCapability frozen = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.FROZEN_CAPABILITY);
        if(frozen!=null){
            if(frozen.hasForceEffect(ForceEffectHandle.LIGHTING_FORCE_EFFECT)) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof Player) {
            AbilityCapability.IAbilityCapability abilityCapability = AbilityHandler.INSTANCE.getAbilityCapability((LivingEntity) event.getEntity());
            if (abilityCapability != null) abilityCapability.instanceAbilities((LivingEntity) event.getEntity());
        }

        if (event.getEntity() instanceof Player) {
            PlayerCapability.IPlayerCapability playerCapability = CapabilityHandle.getCapability((Player) event.getEntity(), CapabilityHandle.PLAYER_CAPABILITY);
            if (playerCapability != null) playerCapability.addedToWorld(event);
        }

        if(event.getEntity() instanceof LivingEntity){
            FrozenCapability.IFrozenCapability playerCapability = CapabilityHandle.getCapability(event.getEntity(), CapabilityHandle.FROZEN_CAPABILITY);
            if (playerCapability != null) playerCapability.joinWorld(event);
        }
    }

    @SubscribeEvent
    public void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
        Player player = event.getEntity();
        if (!player.level().isClientSide) {
            PlayerCapability.IPlayerCapability capability = CapabilityHandle.getCapability(player, CapabilityHandle.PLAYER_CAPABILITY);
            if(capability!=null){
                ServerNetwork.toClientMessage(player, new SynCapabilityMessage(player, capability.writePlaySkillMessage()));
            }
        }
    }

    @SubscribeEvent
    public void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        ItemStack usedItem = event.getItem();
        if (!usedItem.isEdible() && usedItem.getItem() != Items.CAKE)
            return;

        eat(usedItem,usedItem.getItem(),player);
    }

    public void eat(ItemStack foodStack, Item item, Player player) {
        FoodValues foodValues;
        if(item instanceof TBSFood tbsFood){
            DishAttribute itemAdditionData = tbsFood.getItemAdditionData(foodStack, DishAttribute::new);
            foodValues = itemAdditionData.getFoodValues();
            if(!player.level().isClientSide&&(foodValues.get(FoodCategory.MEAT)!=0||foodValues.get(FoodCategory.VEGGIE)!=0||foodValues.get(FoodCategory.EGG)!=0||foodValues.get(FoodCategory.FISH)!=0)){
                FrozenCapability.IFrozenCapability data = CapabilityHandle.getCapability(player,CapabilityHandle.FROZEN_CAPABILITY);
                if(data!=null){
                    float attack = foodValues.get(FoodCategory.MEAT)*itemAdditionData.getIntegratedNutrition();
                    float speed = foodValues.get(FoodCategory.FISH)*itemAdditionData.getIntegratedNutrition();
                    float armor = foodValues.get(FoodCategory.EGG)*itemAdditionData.getIntegratedNutrition();
                    float health = foodValues.get(FoodCategory.VEGGIE)*itemAdditionData.getIntegratedNutrition();
                    player.forceAddEffect(new MobEffectInstance(EffectHandle.FULL_OF_ENERGY.get(),1200,1),player);
                    data.onEffectUpdated(player, attack, speed/15, armor, health);
                }
                Set<FoodValues.MobEffectInstance> effects = foodValues.getEffects();
                for(FoodValues.MobEffectInstance instance:effects){
                    MobEffectInstance mobEffectInstance = new MobEffectInstance(instance.mobEffect,instance.time,instance.level-1);
                    player.addEffect(mobEffectInstance);
                }
            }
        }
    }
}
