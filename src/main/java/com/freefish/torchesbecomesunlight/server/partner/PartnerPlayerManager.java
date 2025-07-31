package com.freefish.torchesbecomesunlight.server.partner;

import com.freefish.rosmontislib.sync.ITagSerializable;
import com.freefish.torchesbecomesunlight.server.partner.command.PartnerCommandBasic;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PartnerPlayerManager implements ITagSerializable<CompoundTag> {
    @Getter
    private List<Partner<?>> partnerList = new ArrayList<>();

    @Getter@Setter
    private Partner<?> currentPartner;


    public void tick(){
        Iterator<Partner<?>> iterator = partnerList.iterator();
        while (iterator.hasNext()){
            Partner<?> next = iterator.next();
            Mob partnerMob = next.getPartnerMob();
            if(next.isRemoved()) {
                if(next == currentPartner){
                    currentPartner = null;
                }
                next.removeFromWorld();
                iterator.remove();
            } else {
                if ((partnerMob == null || !partnerMob.isAlive())&&next.isInited()) {
                    if (currentPartner == next) currentPartner = null;
                    next.removeFromWorld();
                    iterator.remove();
                }
            }
        }
    }

    public void addPartner(Partner<?> partner){
        partnerList.add(partner);
    }

    public PartnerCommandBasic currentCommandBasic(){
        if(currentPartner==null){
            return null;
        }else {
            return currentPartner.getSkillManager().getCurrentCommand();
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        ListTag listTag = new ListTag();
        for(Partner partner:partnerList){
            CompoundTag partnerTag = partner.serializeNBT();
            PartnerType partnerType = partner.getPartnerType();
            ResourceLocation key = PartnerUtil.getKey(partnerType);
            partnerTag.putString("type",key.toString());
            listTag.add(partnerTag);
        }
        compoundTag.put("list",listTag);

        if(currentPartner!=null){
            compoundTag.putInt("current", partnerList.indexOf(currentPartner));
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag compoundTag) {
        partnerList.clear();

        ListTag list = compoundTag.getList("list", 10);
        for(int i=0;i<list.size();i++){
            CompoundTag partnerTag = list.getCompound(i);
            String[] split = partnerTag.getString("type").split(":");
            PartnerType key = PartnerUtil.getValue(new ResourceLocation(split[0],split[1]));
            Partner partner = key.create();
            partner.deserializeNBT(partnerTag);
            partnerList.add(partner);
        }

        if(compoundTag.contains("current")){
            int current = compoundTag.getInt("current");
            if(partnerList.size()>current&&current>=0){
                currentPartner = partnerList.get(current);
            }
        }
    }
}
