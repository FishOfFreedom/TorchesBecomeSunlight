package com.freefish.torchesbecomesunlight.server.story.data.choose;

import com.freefish.rosmontislib.sync.annotation.Persisted;
import com.freefish.torchesbecomesunlight.server.story.dialogueentity.DialogueEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class IsItemInHandChoose extends Choose{
    public static final String ID = "is_item_hand";

    @Persisted
    public String playerId;
    @Persisted
    public String item;
    @Persisted
    public String nextid;

    @Override
    public String changeDialogue(DialogueEntity dialogueEntity) {
        LivingEntity chatEntities = dialogueEntity.getChatEntities(playerId);
        String[] split = item.split(":");
        if(split.length == 2){
            Item item1 = dialogueEntity.level().registryAccess().registryOrThrow(Registries.ITEM).get(new ResourceLocation(split[0], split[1]));
            if (chatEntities instanceof Player player) {
                for (ItemStack itemStack : player.getInventory().items) {
                    if(itemStack.is(item1)){
                        return nextid;
                    }
                }
            }
        }
        return "";
    }

    @Override
    public String getRegisterID() {
        return ID;
    }
}
