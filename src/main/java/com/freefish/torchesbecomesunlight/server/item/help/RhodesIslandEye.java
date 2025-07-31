package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.init.TBSTagKey;
import net.minecraft.world.item.Rarity;

public class RhodesIslandEye extends ItemFindStructureEye{
    public RhodesIslandEye() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(16).fireResistant(), TBSTagKey.EYE_OF_RHODES_ISLAND_BANSHICHU, 0.05F, 0.15F, 0.255F);
    }
}
