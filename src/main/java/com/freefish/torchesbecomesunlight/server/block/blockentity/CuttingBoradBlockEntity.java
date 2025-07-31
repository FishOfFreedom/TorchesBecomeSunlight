package com.freefish.torchesbecomesunlight.server.block.blockentity;

import com.freefish.torchesbecomesunlight.client.render.gui.screen.cuttingborad.CuttingBoardMenu;
import com.freefish.torchesbecomesunlight.mixin.accessor.RecipeManagerAccessor;
import com.freefish.torchesbecomesunlight.server.block.inventory.StewPotItemHandler;
import com.freefish.torchesbecomesunlight.server.init.BlockEntityHandle;
import com.freefish.torchesbecomesunlight.server.init.recipe.*;
import com.freefish.torchesbecomesunlight.server.item.food.DishAttribute;
import com.freefish.torchesbecomesunlight.server.item.food.TBSFood;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class CuttingBoradBlockEntity extends BlockEntity implements MenuProvider {

    public static final int OUTPUT_SLOT = 9;
    public static final int DECORATE_SHOT = 3;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + DECORATE_SHOT + 1;

    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inputHandler;
    private final LazyOptional<IItemHandler> decorateHandler;
    private final LazyOptional<IItemHandler> outputHandler;

    private ItemStack mealContainerStack;
    private Component customName;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;

    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;

    public CuttingBoradBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityHandle.CUTTING_BOARD.get(), pos, state);
        this.inventory = createHandler();
        this.inputHandler = LazyOptional.of(() -> new StewPotItemHandler(inventory, Direction.UP));
        this.decorateHandler = LazyOptional.of(() -> new StewPotItemHandler(inventory, Direction.WEST));
        this.outputHandler = LazyOptional.of(() -> new StewPotItemHandler(inventory, Direction.DOWN));
        this.mealContainerStack = ItemStack.EMPTY;
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        mealContainerStack = ItemStack.of(compound.getCompound("Container"));
        if (compound.contains("CustomName", 8)) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            usedRecipeTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
        }
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Container", mealContainerStack.serializeNBT());
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        compound.put("Inventory", inventory.serializeNBT());
        CompoundTag compoundRecipes = new CompoundTag();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
    }

    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Container", mealContainerStack.serializeNBT());
        compound.put("Inventory", inventory.serializeNBT());
        return compound;
    }

    public static void cookingTick(Level level, BlockPos pos, BlockState state, CuttingBoradBlockEntity cookingPot) {

    }

    public void updateResultItem(){
        boolean didInventoryChange = false;

        if (this.hasInput()) {
            Optional<CuttingBoardRecipe> recipe = this.getMatchingRecipe(new RecipeWrapper(this.inventory));
            if (recipe.isPresent() && this.canCook(recipe.get())) {
                didInventoryChange = this.processCooking(recipe.get(), this);
            }else {
                inventory.setStackInSlot(OUTPUT_SLOT+DECORATE_SHOT, ItemStack.EMPTY.copy());
            }
        }else {
            inventory.setStackInSlot(OUTPUT_SLOT+DECORATE_SHOT, ItemStack.EMPTY.copy());
        }
        if (didInventoryChange) {
            this.inventoryChanged();
        }
    }


    public static void animationTick(Level level, BlockPos pos, BlockState state, CuttingBoradBlockEntity cookingPot) {
    }

    private Optional<CuttingBoardRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();

        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessor) level.getRecipeManager())
                    .getRecipeMap(RecipesHandle.CUTTING_BOARD_RECIPE_TYPE.get())
                    .get(lastRecipeID);
            if (recipe instanceof CuttingBoardRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((CuttingBoardRecipe) recipe);
                }
                if (ItemStack.isSameItem(recipe.getResultItem(this.level.registryAccess()), getMeal())) {
                    return Optional.empty();
                }
            }
        }

        if (checkNewRecipe) {
            Optional<CuttingBoardRecipe> recipe = level.getRecipeManager().getRecipeFor(RecipesHandle.CUTTING_BOARD_RECIPE_TYPE.get(), inventoryWrapper, level);
            if (recipe.isPresent()) {
                ResourceLocation newRecipeID = recipe.get().getId();
                //if (lastRecipeID != null && !lastRecipeID.equals(newRecipeID)) {
                //    cookTime = 0;
                //}
                lastRecipeID = newRecipeID;
                return recipe;
            }
        }

        checkNewRecipe = false;
        return Optional.empty();
    }

    public ItemStack getContainer() {
        ItemStack mealStack = getMeal();
        if (mealStack.isEmpty() || mealContainerStack.isEmpty()) return mealStack.getCraftingRemainingItem();
        return mealContainerStack;
    }

    private boolean hasInput() {
        for (int i = 0; i < OUTPUT_SLOT; ++i) {
            if (!inventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    protected boolean canCook(CuttingBoardRecipe recipe) {
        if (hasInput()) {
            ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack storedMealStack = inventory.getStackInSlot(OUTPUT_SLOT+DECORATE_SHOT);
                if (storedMealStack.isEmpty()) {
                    return true;
                } else if (!ItemStack.isSameItem(storedMealStack, resultStack)) {
                    return false;
                } else if (storedMealStack.getCount() + resultStack.getCount() <= inventory.getSlotLimit(OUTPUT_SLOT+DECORATE_SHOT)) {
                    return true;
                } else {
                    return storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    private boolean processCooking(CuttingBoardRecipe recipe, CuttingBoradBlockEntity cookingPot) {
        if (level == null) return false;

        ItemStack resultStack = recipe.getResultItem(this.level.registryAccess());
        inventory.setStackInSlot(OUTPUT_SLOT+DECORATE_SHOT, resultStack.copy());

        cookingPot.setRecipeUsed(recipe);

        decorateFinalFood();

        for (int i = 0; i < OUTPUT_SLOT; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (slotStack.hasCraftingRemainingItem()) {
                ejectIngredientRemainder(slotStack.getCraftingRemainingItem());
            }
        }

        return true;
    }

    public void shrinkItem(){
        for (int i = 0; i < OUTPUT_SLOT; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (!slotStack.isEmpty())
                slotStack.shrink(1);
        }

        for (int i = OUTPUT_SLOT; i < OUTPUT_SLOT+DECORATE_SHOT; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            FoodValues foodValuesTemp = FoodValuesDefinition.getFoodValues(slotStack, level);
            if(foodValuesTemp.get(FoodCategory.VEGGIE)!=0||foodValuesTemp.get(FoodCategory.MEAT)!=0|foodValuesTemp.get(FoodCategory.FISH)!=0|foodValuesTemp.get(FoodCategory.EGG)!=0){
                if (slotStack.hasCraftingRemainingItem()) {
                    ejectIngredientRemainder(slotStack.getCraftingRemainingItem());
                }
                if (!slotStack.isEmpty())
                    slotStack.shrink(1);
            }
        }
    }

    private void decorateFinalFood(){
        FoodValues foodValues = FoodValues.create();
        FoodValues foodValues911 = null;

        for (int i = 0; i < OUTPUT_SLOT+DECORATE_SHOT; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            FoodValues foodValuesTemp = FoodValuesDefinition.getFoodValues(slotStack, level);
            for(FoodCategory category:FoodCategory.values()){
                foodValues.add(category,foodValuesTemp.get(category));
            }

            if(i<9) {
                for (FoodValues.MobEffectInstance effect : foodValuesTemp.getEffects()) {
                    foodValues.putEffect(effect);
                }
            }else {
                Set<FoodValues.MobEffectInstance> effects = foodValuesTemp.getEffects();
                if(!effects.isEmpty()){
                    foodValues911 = foodValuesTemp;
                }
            }
        }

        DishAttribute dishAttribute = new DishAttribute(foodValues,1);
        dishAttribute.resultNutrition(level.random);

        if(foodValues911!=null&&dishAttribute.getIntegratedNutrition()>=1.5){
            for (FoodValues.MobEffectInstance effect : foodValues911.getEffects()) {
                foodValues.putEffect(effect);
            }
        }

        ItemStack finalDish = inventory.getStackInSlot(OUTPUT_SLOT + DECORATE_SHOT);
        if(finalDish.getItem() instanceof TBSFood tbsFood){
            tbsFood.setItemAdditionData(finalDish,dishAttribute);
        }
    }

    protected void ejectIngredientRemainder(ItemStack remainderStack) {
        //Direction direction = getBlockState().getValue(CookingPotBlock.FACING).getCounterClockWise();
        //double x = worldPosition.getX() + 0.5 + (direction.getStepX() * 0.25);
        //double y = worldPosition.getY() + 0.7;
        //double z = worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.25);
        //ItemUtils.spawnItemEntity(level, remainderStack, x, y, z,
        //        direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F);
    }

    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    public void awardUsedRecipes(Player player, List<ItemStack> items) {
        List<Recipe<?>> usedRecipes = getUsedRecipesAndPopExperience(player.level(), player.position());
        player.awardRecipes(usedRecipes);
        usedRecipeTracker.clear();
    }

    public List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, entry.getIntValue(), ((CuttingBoardRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public ItemStack getMeal() {
        return inventory.getStackInSlot(OUTPUT_SLOT+DECORATE_SHOT);
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            if (i != OUTPUT_SLOT+DECORATE_SHOT) {
                drops.add(inventory.getStackInSlot(i));
            }
        }
        return drops;
    }

    private boolean doesMealHaveContainer(ItemStack meal) {
        return !mealContainerStack.isEmpty() || meal.hasCraftingRemainingItem();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("cutting_board");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
        return new CuttingBoardMenu(id, player, this, new SimpleContainerData(2));
    }

    @Override
    @Nonnull
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap.equals(ForgeCapabilities.ITEM_HANDLER)) {
            if (side == null || side.equals(Direction.UP)) {
                return inputHandler.cast();
            } else if(side.equals(Direction.DOWN)) {
                return outputHandler.cast();
            } else {
                return decorateHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inputHandler.invalidate();
        outputHandler.invalidate();
        decorateHandler.invalidate();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return writeItems(new CompoundTag());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(INVENTORY_SIZE)
        {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < OUTPUT_SLOT+DECORATE_SHOT) {
                    checkNewRecipe = true;
                }
                inventoryChanged();
            }
        };
    }

    protected void inventoryChanged() {
        super.setChanged();
        if (level != null)
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }
}
