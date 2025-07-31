package com.freefish.torchesbecomesunlight.server.event.packet.toclient;

import com.freefish.torchesbecomesunlight.server.capability.CapabilityHandle;
import com.freefish.torchesbecomesunlight.server.capability.PlayerCapability;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectHandle;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectInstance;
import com.freefish.torchesbecomesunlight.server.effect.forceeffect.ForceEffectType;
import com.freefish.torchesbecomesunlight.server.partner.IMobPartner;
import com.freefish.torchesbecomesunlight.server.partner.Partner;
import com.freefish.torchesbecomesunlight.server.partner.PartnerUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class SynCapabilityMessage {
    private int dialogueEntityID;
    private CompoundTag tag;

    public SynCapabilityMessage(){
    }

    public SynCapabilityMessage(LivingEntity dialogueEntity, CompoundTag tag) {
        this.dialogueEntityID = dialogueEntity.getId();
        this.tag = tag;
    }

    public static void serialize(final SynCapabilityMessage message, final FriendlyByteBuf buf) {
        buf.writeVarInt(message.dialogueEntityID);
        buf.writeNbt(message.tag);
    }

    public static SynCapabilityMessage deserialize(final FriendlyByteBuf buf) {
        final SynCapabilityMessage message = new SynCapabilityMessage();

        message.dialogueEntityID = buf.readVarInt();
        message.tag = buf.readNbt();

        return message;
    }

    public static class Handler implements BiConsumer<SynCapabilityMessage, Supplier<NetworkEvent.Context>> {
        @Override
        public void accept(SynCapabilityMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
            final NetworkEvent.Context context = contextSupplier.get();
            context.enqueueWork(() -> {
                ClientLevel level = Minecraft.getInstance().level;
                Entity entity = level.getEntity(message.dialogueEntityID);
                if(entity instanceof LivingEntity livingEntity){
                    CompoundTag partData = message.tag;

                    boolean forceeffct = partData.contains("forceeffct");
                    if(forceeffct){
                        ListTag compound = partData.getList("forceeffct",10);
                        for(int i =0;i<compound.size();i++){
                            ForceEffectInstance forceEffectInstance = new ForceEffectInstance();
                            forceEffectInstance.deserializeNBT(compound.getCompound(i));
                            ForceEffectHandle.addForceEffect(livingEntity, forceEffectInstance);
                        }
                    }
                    boolean removeforceeffct = partData.contains("removeforceeffct");
                    if(removeforceeffct){
                        String[] type = partData.getString("removeforceeffct").split(":");
                        ForceEffectType<?> value = ForceEffectHandle.getValue(new ResourceLocation(type[0], type[1]));
                        ForceEffectInstance instance = ForceEffectHandle.getForceEffect(livingEntity, value);
                        if(instance!=null) instance.discard(livingEntity);
                    }

                    boolean skill = partData.contains("skill");
                    if(skill&&entity instanceof Player player){
                        PlayerCapability.IPlayerCapability data = CapabilityHandle.getCapability(livingEntity, CapabilityHandle.PLAYER_CAPABILITY);
                        if (data != null) {
                            CompoundTag skillTag = partData.getCompound("skill");
                            data.readPlaySkillMessage(skillTag,player);
                        }
                    }

                    boolean partnerdata = partData.contains("partnerdata");
                    System.out.println(12);
                    if(partnerdata){
                        CompoundTag compound = partData.getCompound("partnerdata");
                        int playerid = compound.getInt("playerid");
                        Entity playerEntity = level.getEntity(playerid);
                        if(playerEntity instanceof Player player){
                            int partnerid = compound.getInt("partnerid");
                            Entity partnerEntity = level.getEntity(partnerid);
                            if(partnerEntity instanceof Mob mob){
                                System.out.println(1);
                                String[] partnertype = compound.getString("partnertype").split(":");
                                PartnerUtil.startPartner(player,mob,PartnerUtil.getValue(new ResourceLocation(partnertype[0],partnertype[1])));
                            }
                        }
                    }

                    boolean removepartnerdata = partData.contains("removepartnerdata");
                    if(removepartnerdata){
                        CompoundTag compound = partData.getCompound("removepartnerdata");
                        int partnerid = compound.getInt("partnerid");
                        Entity partnerEntity = level.getEntity(partnerid);
                        if(partnerEntity instanceof Mob mob){
                            Partner<?> partner = ((IMobPartner) mob).getPartner();
                            if(partner!=null) partner.remove();
                            ((IMobPartner) mob).setPartner(null);
                        }
                    }
                }
            });
            context.setPacketHandled(true);
        }
    }
}
