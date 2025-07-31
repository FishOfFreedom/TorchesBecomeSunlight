package com.freefish.torchesbecomesunlight.compat;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import net.minecraftforge.fml.ModList;

public class ModDetectionCache {
    private static final String STEVE_MOD_ID = "ysm"; // 替换为实际模组ID
    private static Boolean hasSteveModelMod = null; // 使用Boolean对象方便null检查
    
    /**
     * 检测并缓存"是，史蒂夫模型"模组是否加载
     */
    public static boolean hasSteveModelMod() {
        if (hasSteveModelMod == null) {
            hasSteveModelMod = ModList.get().isLoaded(STEVE_MOD_ID);

            if (hasSteveModelMod) {
                TorchesBecomeSunlight.LOGGER.info("[检测到] '是，史蒂夫模型'模组已加载");
            } else {
                TorchesBecomeSunlight.LOGGER.info("[未检测到] '是，史蒂夫模型'模组未安装");
            }
        }
        return hasSteveModelMod;
    }

    public static boolean isLoadSteveModelMod() {
        if(hasSteveModelMod==null){
            return false;
        }
        else
            return hasSteveModelMod;
    }
    
    /**
     * 重置缓存（用于开发环境重新加载）
     */
    public static void resetCache() {
        hasSteveModelMod = null;
    }
    
    /**
     * 在游戏初始化时调用，确保缓存已设置
     */
    public static void init() {
        // 强制初始化缓存
        hasSteveModelMod();
    }
}