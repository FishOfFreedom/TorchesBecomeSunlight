package com.freefish.torchesbecomesunlight.server.story.dialogue;

import com.freefish.torchesbecomesunlight.server.entity.effect.dialogueentity.IDialogue;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogueStore {
    public static List<Dialogue> dialogueList = new ArrayList<>();
    public static int dialogueAmount = 0;

    public static Dialogue NONE = new Dialogue("",null,null,0,100);

    public static Dialogue dialogue3 = new Dialogue("may be you should end dialogue.",null,null,0,40);
    public static Dialogue dialogue2 = new Dialogue("this is third dialogue!!!", withoutTrigger(Arrays.asList("hello","end")), dialogue3, 1,100);
    public static Dialogue dialogue1 = new Dialogue("this is second dialogue!!!", withoutTrigger(Arrays.asList("hello","end","goodbye")), dialogue2, 0,100);
    public static Dialogue dialogue = new Dialogue("hello,this is torchesbecomesunlight", null, dialogue1, 0,200);



    public static Dialogue state_1_10 = new Dialogue("或者你可以加入我们的庆祝活动.", null, null, 1,100);
    public static Dialogue state_1_9 = new Dialogue("如果你愿意帮忙,我们还可以教你怎么使用这些工具.", null, state_1_10, 1,100);
    public static Dialogue state_1_8 = new Dialogue("当然,我们这里的人都很热情好客.", withoutTrigger(Arrays.asList("不赖啊","真不错","听起来正好","不赖啊","真不错")), state_1_9, 1,100);
    public static Dialogue state_1_7 = new Dialogue("那我可以帮上什么忙吗?", withoutTrigger(Arrays.asList("...","...","...")), state_1_8, 0,100);
    public static Dialogue state_1_6 = new Dialogue("家家户户都会拿出自家的食物来庆祝,还会举行一些传统的比赛和舞蹈.", withoutTrigger(Arrays.asList("不赖啊","真不错","听起来真好","真不错")), state_1_7, 1,100);
    public static Dialogue state_1_5 = new Dialogue("哦,我们这里的收获日可热闹了.", null, state_1_6, 1,100);
    public static Dialogue state_1_4 = new Dialogue("请问一下,你们这里的收获日有什么特别的习俗吗?", null, state_1_5, 0,100);
    public static Dialogue state_1_3 = new Dialogue("听起来真是令人兴奋.", null, state_1_4, 0,100);
    public static Dialogue state_1_2 = new Dialogue("我们这里的麦子长得特别好,今年应该会有个好收成.", withoutTrigger(Arrays.asList("我可以帮忙吗")), state_1_3, 1,100);
    public static Dialogue state_1_1 = new Dialogue("是啊,再过几天就是收获的大日子了.", withoutTrigger(Arrays.asList("不赖啊","真不错")), state_1_2, 1,100);
    public static Dialogue state_1_0 = new Dialogue("今年的庄稼长得可真好,看起来收获的日子不远了吧?", null, state_1_1, 0,100);

    public static Dialogue pursuer_meet_5 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_5", null, null, 1,80);
    public static Dialogue pursuer_meet_4 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_4", null, null, 1,80);
    public static Dialogue pursuer_meet_3 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_3", null, null, 1,80);
    public static Dialogue pursuer_meet_2 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_2", null, pursuer_meet_3, 1,80);
    public static Dialogue pursuer_meet_1 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_1",
            genList(new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_meet_1_1",0,pursuer_meet_4,null,entity -> {
                        if(entity instanceof Player player){
                            ItemStack mainHandItem = player.getMainHandItem();
                            if(mainHandItem.is(Items.GOLD_BLOCK)){
                                return pursuer_meet_5;
                            }
                        }
                        return pursuer_meet_4;
                    }),
                    new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_meet_1_2",1,entity -> {
                        Player player = MathUtils.getClosestEntity(entity,entity.level().getEntitiesOfClass(Player.class,entity.getBoundingBox().inflate(5)));
                        if(player!=null&&!player.isCreative()&&entity instanceof Pursuer mob) {
                            mob.setTarget(mob.getDialogueEntity());
                        }
                    }))
            , pursuer_meet_2, 1,100);

    public static Dialogue snownova_meet_2 = new Dialogue(entity -> (entity instanceof GuerrillasEntity guerrillas?guerrillas.findVillage():"no"),null,null,1,100);
    public static Dialogue snownova_meet_1 = new Dialogue("dialogue.torchesbecomesunlight.snownova_meet_1",
            genList(new DialogueTrigger("dialogue.torchesbecomesunlight.snownova_meet_1_1",0,null),
                    new DialogueTrigger("dialogue.torchesbecomesunlight.snownova_meet_1_2",1,
            entity -> {
                Player player = MathUtils.getClosestEntity(entity,entity.level().getEntitiesOfClass(Player.class,entity.getBoundingBox().inflate(5)));
                if(player!=null&&!player.isCreative()) {
                    if (entity instanceof FrostNova snowNova && !snowNova.level().isClientSide) {
                        AnimationActHandler.INSTANCE.sendAnimationMessage(snowNova, FrostNova.ATTACK_PREPARE);
                    }
                }
            }),new DialogueTrigger("dialogue.torchesbecomesunlight.snownova_meet_1_3",0,snownova_meet_2,null)), null, 1,100);

    public static List<DialogueTrigger> withoutTrigger(List<String> strings){
        List<DialogueTrigger> dialogueTriggers = new ArrayList<>();
        for (String string : strings) {
            dialogueTriggers.add(new DialogueTrigger(string,0,null));
        }
        return dialogueTriggers;
    }

    public static List<DialogueTrigger> genList(DialogueTrigger ... dialogueTriggers){
        return new ArrayList<>(Arrays.asList(dialogueTriggers));
    }
}
