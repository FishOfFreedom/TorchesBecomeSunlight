package com.freefish.torchesbecomesunlight.client.render.gui.screen.cuttingborad;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.server.block.blockentity.CuttingBoradBlockEntity;
import com.freefish.torchesbecomesunlight.server.init.BlockHandle;
import com.freefish.torchesbecomesunlight.server.init.MenuHandle;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import java.util.Objects;

public class CuttingBoardMenu extends RecipeBookMenu<RecipeWrapper> {
    public final CuttingBoradBlockEntity blockEntity;
    public final ItemStackHandler inventory;
    private final ContainerLevelAccess canInteractWithCallable;
    protected final Level level;

    public CuttingBoardMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(5));
    }

    public CuttingBoardMenu(final int windowId, final Inventory playerInventory, final CuttingBoradBlockEntity blockEntity, ContainerData cookingPotDataIn) {
        super(MenuHandle.CUTTING_BORAD_MENU.get(), windowId);
        this.blockEntity = blockEntity;
        this.inventory = blockEntity.getInventory();
        this.level = playerInventory.player.level();
        this.canInteractWithCallable = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        this.addSlotListener(new ContainerListener() {
            public void slotChanged(AbstractContainerMenu p_97973_, int slot, ItemStack p_97975_) {
                if (slot >= 0 && slot < 12) {
                    blockEntity.updateResultItem();
                }

            }

            public void dataChanged(AbstractContainerMenu p_169628_, int p_169629_, int p_169630_) {
            }
        });

        // Ingredient Slots - 3 Rows x 3 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 30;
        int inputStartY = 17;
        int borderSlotSize = 18;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                this.addSlot(new SlotItemHandler(inventory, (row * 3) + column,
                        inputStartX + (column * borderSlotSize),
                        inputStartY + (row * borderSlotSize)));
            }
        }

        // Decorate
        inputStartX = 6;
        inputStartY = 17;
        for (int row = 0; row < 3; ++row) {
            this.addSlot(new SlotItemHandler(inventory, 9+row,
                    inputStartX,
                    inputStartY + (row * borderSlotSize)));
        }

        // Output
        this.addSlot(new CuttingBoardResultSlot(playerInventory.player, blockEntity, inventory, 12, 124, 35));

        // Main Player Inventory
        int startPlayerInvY = startY * 4 + 12;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }

        // Hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
        }

        this.addDataSlots(cookingPotDataIn);
    }

    @Override
    public void slotsChanged(Container pContainer) {
        super.slotsChanged(pContainer);
    }

    @Override
    public void setItem(int pSlotId, int pStateId, ItemStack pStack) {
        super.setItem(pSlotId, pStateId, pStack);
    }

    private static CuttingBoradBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level().getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof CuttingBoradBlockEntity) {
            return (CuttingBoradBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(canInteractWithCallable, playerIn, BlockHandle.CUTTING_BOARD.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexMealDisplay = 9;
        int indexOutput = 12;
        int startPlayerInv = indexOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack slotStackCopy = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            slotStackCopy = slotStack.copy();
            if (index == indexOutput) {
                if (!this.moveItemStackTo(slotStack, startPlayerInv, endPlayerInv, true)) {
                    return ItemStack.EMPTY;
                }else {
                    blockEntity.shrinkItem();
                    blockEntity.updateResultItem();
                }
            } else if (index > indexOutput) {
                if (!this.moveItemStackTo(slotStack, 0, indexMealDisplay, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(slotStack, startPlayerInv, endPlayerInv, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == slotStackCopy.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }
        return slotStackCopy;
    }

    public boolean isHeated() {
        return true;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents helper) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            helper.accountSimpleStack(inventory.getStackInSlot(i));
        }
    }

    @Override
    public void clearCraftingContent() {
        for (int i = 0; i < 12; i++) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean recipeMatches(Recipe<? super RecipeWrapper> recipe) {
        return recipe.matches(new RecipeWrapper(inventory), level);
    }

    @Override
    public int getResultSlotIndex() {
        return 12;
    }

    @Override
    public int getGridWidth() {
        return 3;
    }

    @Override
    public int getGridHeight() {
        return 4;
    }

    @Override
    public int getSize() {
        return 13;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return TorchesBecomeSunlight.RECIPE_TYPE_COOKING;
    }

    @Override
    public boolean shouldMoveToInventory(int slot) {
        return slot < (getGridWidth() * getGridHeight());
    }
}
