package com.freefish.torchesbecomesunlight.server.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

/**
 * 1.20.5 起 ItemStack 不再直接挂 NBT，自定义数据改由 CUSTOM_DATA 组件承载，
 * {@code getTag()} / {@code getOrCreateTag()} / {@code hasTag()} 均已移除。
 * <p>
 * 这里给出等价的读写入口，保留本 Mod 原有的"取出标签 → 读/改 → 写回"用法。
 * <p>
 * <b>注意语义差异</b>：组件里取出的标签是<b>副本</b>，修改它不会自动作用回物品。
 * 因此写入必须走 {@link #mutate}（或自行 {@link #setTag}），
 * 不能像 1.20.1 的 {@code getOrCreateTag()} 那样改完就生效。
 */
public final class TBSItemData {
    private TBSItemData() {
    }

    /** 读取自定义数据的副本；不存在时返回空标签 */
    public static CompoundTag getTag(ItemStack stack) {
        return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static boolean hasTag(ItemStack stack) {
        return stack.has(DataComponents.CUSTOM_DATA);
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    /** 取出、修改并写回，等价于 1.20.1 中对 getOrCreateTag() 的就地修改 */
    public static void putBoolean(ItemStack stack, String key, boolean value) {
        CompoundTag tag = getTag(stack);
        tag.putBoolean(key, value);
        setTag(stack, tag);
    }

    public static void putInt(ItemStack stack, String key, int value) {
        CompoundTag tag = getTag(stack);
        tag.putInt(key, value);
        setTag(stack, tag);
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        return getTag(stack).getBoolean(key);
    }

    public static int getInt(ItemStack stack, String key) {
        return getTag(stack).getInt(key);
    }

    public static boolean contains(ItemStack stack, String key) {
        return getTag(stack).contains(key);
    }
}
