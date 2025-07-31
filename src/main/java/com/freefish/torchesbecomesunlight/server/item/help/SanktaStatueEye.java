package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.init.TBSTagKey;
import net.minecraft.world.item.Rarity;

public class SanktaStatueEye extends ItemFindStructureEye{
    public SanktaStatueEye() {
        super(new Properties().rarity(Rarity.RARE).stacksTo(16).fireResistant(), TBSTagKey.EYE_OF_SANKTA_STATUE, 1F, 9.55F, 0.8F);
    }
}
