package com.freefish.torchesbecomesunlight.server.item;

import com.freefish.torchesbecomesunlight.server.entity.effect.PursuerEffectEntity;
import com.freefish.torchesbecomesunlight.server.event.packet.toclient.ActRangeSignMessage;
import com.freefish.torchesbecomesunlight.server.init.EntityHandle;
import com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion.TriggerHandler;
import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class DemonEye extends Item {
    public DemonEye(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @org.jetbrains.annotations.Nullable Level level, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, level, tooltip, flagIn);
        tooltip.add(Component.translatable("text.torchesbecomesunlight.demon_eye"));
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(GLITCH_TEXT)
                .setStyle(Style.EMPTY
                        .withColor(TextColor.parseColor("#FF5555")) // 红色
                        .withObfuscated(true)  // §k 动态乱码效果
                );
    }

    private static final String GLITCH_TEXT = "ᴹᴺᴼᴾᴿᵀᵁⱽᵂᴹᴺᴼᴾᴿᵀᵁⱽᵂᴹᴺ";


    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        HitResult pick = pPlayer.pick(10, 0, true);
        Vec3 location = pick.getLocation();
        RandomSource pRandom = pPlayer.getRandom();
        float pFactor = 6f;
        if(location.distanceTo(pPlayer.position())<8){
            if(!pLevel.isClientSide){
                for(int i1 = 0;i1<10;i1++){
                    location = location.add((double)((pRandom.nextFloat() - 0.5F) * pFactor), (double)((pRandom.nextFloat() - 0.5F) * 2), (double)((pRandom.nextFloat() - 0.5F) * pFactor));
                    Vec3 firstBlockAbove = MathUtils.getFirstBlockAbove(pLevel, location, 6);
                    if(firstBlockAbove != location){
                        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
                        itemInHand.shrink(1);

                        ActRangeSignMessage.showBlockInter(pPlayer,new BlockPos((int)firstBlockAbove.x,(int)firstBlockAbove.y,(int)firstBlockAbove.z),pLevel);
                        PursuerEffectEntity pee = new PursuerEffectEntity(EntityHandle.PEE.get(), pLevel);
                        pee.setPos(pick.getLocation());
                        pee.type1Pursuer = firstBlockAbove;
                        pee.type = 1;
                        pLevel.addFreshEntity(pee);

                        if(pPlayer instanceof ServerPlayer player){
                            TriggerHandler.STRING_ADVANCEMENT_TRIGGER.trigger(player, "ursus_1");
                        }
                        break;
                    }
                }
            }
        }

        //if(!pLevel.isClientSide){
        //    if(location.distanceTo(pPlayer.position())<6){
        //        ItemStack itemInHand = pPlayer.getItemInHand(pUsedHand);
        //        itemInHand.shrink(1);
        //        Pursuer pursuer = new Pursuer(EntityHandle.PURSUER.get(), pLevel);
        //        pursuer.setPos(location);
        //        pLevel.addFreshEntity(pursuer);
        //    }
        //}else {
        //    if(location.distanceTo(pPlayer.position())<6){
        //        location = location.add(0,6,0);
        //        RLParticle rlParticle = new RLParticle(pLevel);
        //        rlParticle.config.setStartLifetime(NumberFunction.constant(400));
        //        rlParticle.config.setStartSize(new NumberFunction3(12));
        //        rlParticle.config.getEmission().setEmissionRate(NumberFunction.constant(0));
        //        EmissionSetting.Burst burst = new EmissionSetting.Burst();
        //        burst.setCount(NumberFunction.constant(1));
        //        rlParticle.config.getEmission().addBursts(burst);
        //        rlParticle.config.getMaterial().setDepthTest(false);
        //        rlParticle.config.getShape().setShape(new Dot());
        //        rlParticle.config.getMaterial().setCull(false);
        //        rlParticle.config.getMaterial().setMaterial(TBSMaterialHandle.DEMON_EYE.create());
        //        rlParticle.config.getColorOverLifetime().open();
        //        rlParticle.config.getColorOverLifetime().setColor(new Gradient(new GradientColor(new float[]{0f, 10f / 400,(400 -10f) / 400, 1}, new int[]{0X00FFFFFF, 0XFFFFFFFF, 0XFFFFFFFF,0X00FFFFFF})));
        //        rlParticle.config.getLights().open();
        //        BlockEffect blockEffect = new BlockEffect(pLevel, location);
        //        rlParticle.emmit(blockEffect);
        //    }
        //}
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
