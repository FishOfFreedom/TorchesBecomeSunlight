package com.freefish.torchesbecomesunlight.server.item.food;

import com.freefish.rosmontislib.item.ItemAdditionData;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodCategory;
import com.freefish.torchesbecomesunlight.server.init.recipe.FoodValues;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TBSFood extends Item implements ItemAdditionData<DishAttribute> {
    public TBSFood(int nutrition,float saturation,int during) {
        super(new Item.Properties().food((new FoodProperties.Builder()).nutrition(nutrition).saturationMod(saturation).alwaysEat().build()).durability(during));
    }

    @Override
    public boolean isEdible() {
        return super.isEdible();
    }

    @Override
    public String getSaveDataName() {
        return "dish_attribute";
    }

    @Override
    public void addAdditionTextTool(ItemTooltipEvent itemTooltipEvent) {
        //List<Component> toolTip = itemTooltipEvent.getToolTip();
        //ItemStack itemStack = itemTooltipEvent.getItemStack();
        //if(itemStack instanceof TBSFood tbsFood) {
        //    toolTip.add(Component.literal());
        //}
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        if(pStack.getItem() instanceof TBSFood tbsFood){
            DishAttribute itemAdditionData = getItemAdditionData(pStack, DishAttribute::new);
            FoodValues foodValues = itemAdditionData.getFoodValues();
            float health = foodValues.get(FoodCategory.VEGGIE);
            if(health >0){
                pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.foodhealth").withStyle(ChatFormatting.GREEN)
                        .append(Component.literal(String.format("%.1f",health)).withStyle(ChatFormatting.GREEN)));
            }
            float act = foodValues.get(FoodCategory.MEAT);
            if(act >0){
                pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.foodact").withStyle(ChatFormatting.RED)
                        .append(Component.literal(String.format("%.1f",act)).withStyle(ChatFormatting.RED)));
            }
            float egg = foodValues.get(FoodCategory.EGG);
            if(egg >0){
                pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.foodarmor").withStyle(ChatFormatting.WHITE)
                        .append(Component.literal(String.format("%.1f",egg)).withStyle(ChatFormatting.WHITE)));
            }
            float move = foodValues.get(FoodCategory.FISH);
            if(move >0){
                pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.foodfish").withStyle(ChatFormatting.BLUE)
                        .append(Component.literal(String.format("%.1f",move)).withStyle(ChatFormatting.BLUE)));
            }
            Set<FoodValues.MobEffectInstance> effects = foodValues.getEffects();
            if(!effects.isEmpty()){
                String collect = effects.stream()
                        .map(this::toS)
                        .collect(Collectors.joining(", "));
                //String collect = effects.stream()
                //        .map(in -> ForgeRegistries.MOB_EFFECTS.getKey(in.mobEffect).toString())
                //        .collect(Collectors.joining(","));
                pTooltipComponents.add(Component.translatable("text.torchesbecomesunlight.foodbuff").append(Component.literal(collect)));
            }
        }
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    private String toS(FoodValues.MobEffectInstance m){
        int amplifier = m.level;
        String romanLevel = String.valueOf(amplifier + 1);
        if (amplifier > 0) {
            romanLevel = switch (amplifier) {
                case 1 -> "I";
                case 2 -> "II";
                case 3 -> "III";
                case 4 -> "IV";
                case 5 -> "V";
                default -> String.valueOf(amplifier + 1);
            };
        }
        return Component.translatable(m.mobEffect.getDescriptionId()).append(" "+romanLevel).getString();
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pLivingEntity) {
        return super.finishUsingItem(pStack, pLevel, pLivingEntity);
    }
}
