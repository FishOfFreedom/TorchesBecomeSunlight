package com.freefish.torchesbecomesunlight.server.item.help;

import com.freefish.torchesbecomesunlight.server.util.MathUtils;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class SpeedItem extends Item {
    public SpeedItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (!context.getLevel().isClientSide) {
            List<PathfinderMob> entities = context.getLevel().getEntitiesOfClass(PathfinderMob.class,context.getPlayer().getBoundingBox().inflate(6));
            PathfinderMob livingEntity = MathUtils.getClosestEntity(context.getPlayer(),entities);
            Vec3 vec3 = LandRandomPos.getPos(livingEntity, 15, 7);
            livingEntity.getNavigation().moveTo(vec3.x,vec3.y,vec3.z,1);
        }
        return super.useOn(context);
    }
}
