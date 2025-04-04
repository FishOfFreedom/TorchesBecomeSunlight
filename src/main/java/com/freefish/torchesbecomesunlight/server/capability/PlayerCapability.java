package com.freefish.torchesbecomesunlight.server.capability;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.freefish.torchesbecomesunlight.client.render.entity.player.GeckoPlayer;
import com.freefish.torchesbecomesunlight.server.config.ConfigHandler;
import com.freefish.torchesbecomesunlight.server.entity.IDialogueEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import javax.annotation.Nonnull;
import java.util.List;

import static com.freefish.torchesbecomesunlight.server.util.FFEntityUtils.isLookingAtMe;

public class PlayerCapability {
    public static ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "player_cap");

    public interface IPlayerCapability extends INBTSerializable<CompoundTag> {

        void tick(TickEvent.PlayerTickEvent event);

        void addedToWorld(EntityJoinLevelEvent event);

        @OnlyIn(Dist.CLIENT)
        GeckoPlayer.GeckoPlayerThirdPerson getGeckoPlayer();
    }

    public static class PlayerCapabilityImp implements IPlayerCapability {
        private int dialogueNeedTime;

        @OnlyIn(Dist.CLIENT)
        private GeckoPlayer.GeckoPlayerThirdPerson geckoPlayer;

        @OnlyIn(Dist.CLIENT)
        public GeckoPlayer.GeckoPlayerThirdPerson getGeckoPlayer() {
            return geckoPlayer;
        }
        @Override
        public void addedToWorld(EntityJoinLevelEvent event) {
            // Create the geckoplayer instances when an entity joins the world
            // Normally, the animation controllers and lastModel field are only set when rendered for the first time, but this won't work for player animations
            if (event.getLevel().isClientSide()) {
                Player player = (Player) event.getEntity();
                geckoPlayer = new GeckoPlayer.GeckoPlayerThirdPerson(player);
                // Only create 1st person instance if the player joining is this client's player
                if (event.getEntity() == Minecraft.getInstance().player) {
                    GeckoPlayer.GeckoPlayerFirstPerson geckoPlayerFirstPerson = new GeckoPlayer.GeckoPlayerFirstPerson(player);
                }
            }
        }

        public void tick(TickEvent.PlayerTickEvent event) {
            Player player = event.player;
            Level level = player.level();
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag compound = new CompoundTag();
            return compound;
        }

        @Override
        public void deserializeNBT(CompoundTag compound) {
        }
    }

    public static class Provider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag>
    {
        private final LazyOptional<IPlayerCapability> instance = LazyOptional.of(PlayerCapabilityImp::new);

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return CapabilityHandle.PLAYER_CAPABILITY.orEmpty(cap, instance.cast());
        }
    }
}
