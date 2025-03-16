package com.freefish.torchesbecomesunlight.server.story.dialogue;


import com.freefish.torchesbecomesunlight.server.entity.guerrillas.GuerrillasEntity;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.shield.Patriot;
import com.freefish.torchesbecomesunlight.server.entity.guerrillas.snowmonster.FrostNova;
import com.freefish.torchesbecomesunlight.server.entity.ursus.Pursuer;
import com.freefish.torchesbecomesunlight.server.init.ItemHandle;
import com.freefish.torchesbecomesunlight.server.util.animation.AnimationActHandler;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogueStore {
    public static List<Dialogue> dialogueList = new ArrayList<>();
    public static int dialogueAmount = 0;

    public static Dialogue NONE = new Dialogue("",null,null,0,100);

    public static Dialogue dialogue3 = new Dialogue("少废话,滚一边去.",null,null,0,100);
    public static Dialogue dialogue2 = new Dialogue("什么?!还敢跟我顶嘴?不知道欠税在帝国是什么罪行吗?", null, dialogue3, 0,100);
    public static Dialogue dialogue1 = new Dialogue("这个......纠察官姥爷,今年荒年,我们这糊口的粮食都不够了,就求求您宽限宽限吧,真的只有这么多了.", null, dialogue2, 1,100);
    public static Dialogue dialogue = new Dialogue("规矩都懂吧,我们也不是第一次来了,赶紧交税.", null, dialogue1, 0,100);

    private static Dialogue daily_3 = new Dialogue("dialogue.torchesbecomesunlight.daily_3", null, null, 1,100);
    private static Dialogue daily_2 = new Dialogue("dialogue.torchesbecomesunlight.daily_2", null, daily_3, 1,100);
    public static Dialogue daily_1 = new Dialogue("dialogue.torchesbecomesunlight.daily_1", null, daily_2, 0,100);



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


    public static Dialogue pursuer_d_3;
    public static Dialogue pursuer_d_2;
    public static Dialogue pursuer_d_1;
    public static Dialogue pursuer_d_4;
    public static Dialogue pursuer_d_5;
    public static Dialogue pursuer_d_6;
    public static Dialogue pursuer_d_7;
    public static Dialogue pursuer_d_8;

    static  {
        pursuer_d_8 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_8", null, null, 1,80);
        pursuer_d_7 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_7", null, pursuer_d_8, 1,80);
        pursuer_d_6 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_6", null, pursuer_d_7, 1,80);
        pursuer_d_5 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_5", null, pursuer_d_6, 1,80);
        pursuer_d_4 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_4", genList(
                new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_d_4_1",0,null,null),
                new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_d_4_2",0,null,null),
                new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_d_4_3",0,pursuer_d_5,null,null,(entity -> {
                    if(entity instanceof Player player){
                        return false;
                    }
                    return true;
                }))
                ), null, 1,80);

        pursuer_d_3 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_3", null, null, 1,80);
        pursuer_d_2 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_2", genList(
                new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_d_2_1", 0, pursuer_d_3, null),
                new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_d_2_2", 0, pursuer_d_4, null),
                new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_meet_1_2", 1, entity -> {
                    Player player = MathUtils.getClosestEntity(entity, entity.level().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(5)));
                    if (player != null && !player.isCreative() && entity instanceof Pursuer mob) {
                        mob.setState(1);
                        mob.setTarget(mob.getDialogueEntity());
                    }
                })), null, 1, 80);


        pursuer_d_1 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_d_1", null, pursuer_d_2, 1,80);
    }
    public static Dialogue pursuer_meet_5 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_5", null, null, 1,80);
    public static Dialogue pursuer_meet_4 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_4", null, null, 1,80);
    public static Dialogue pursuer_meet_3 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_3", null, null, 1,80);
    public static Dialogue pursuer_meet_2 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_2", null, pursuer_meet_3, 1,80);
    public static Dialogue pursuer_meet_1 = new Dialogue("dialogue.torchesbecomesunlight.pursuer_meet_1",
            genList(new DialogueTrigger("dialogue.torchesbecomesunlight.pursuer_meet_1_1",0,pursuer_meet_4,null,entity -> {
                        if(entity instanceof Player player){
                            ItemStack mainHandItem = player.getMainHandItem();
                            if(mainHandItem.is(ItemHandle.URSUS_MACHETE.get())){
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
//frost_nova
    public static Dialogue snownova_meet_2 = new Dialogue(entity -> (entity instanceof GuerrillasEntity guerrillas?guerrillas.findVillage():"no"),null,null,1,100);
    public static Dialogue snownova_meet_1 = new Dialogue("dialogue.torchesbecomesunlight.snownova_meet_1",
            genList(new DialogueTrigger("dialogue.torchesbecomesunlight.snownova_meet_1_1",0,null),
                    new DialogueTrigger("dialogue.torchesbecomesunlight.snownova_meet_1_2",1,
            entity -> {
                Player player = MathUtils.getClosestEntity(entity,entity.level().getEntitiesOfClass(Player.class,entity.getBoundingBox().inflate(5)));
                if(player!=null&&!player.isCreative()) {
                    if (entity instanceof FrostNova snowNova) {
                        snowNova.waitAct = snowNova.getDialogueEntity();
                        AnimationActHandler.INSTANCE.sendAnimationMessage(snowNova, FrostNova.ATTACK_PREPARE);
                    }
                    else if (entity instanceof Patriot snowNova) {
                        snowNova.waitAct = snowNova.getDialogueEntity();
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
