package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 见 {@link StringAdvancementTrigger} 的说明：1.21 的进度触发器改为基于 Codec。
 */
public class ChainedAdvancementTrigger extends SimpleCriterionTrigger<ChainedAdvancementTrigger.Instance> {

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, ResourceLocation id) {
        this.trigger(player, instance -> instance.matches(id));
    }

    public record Instance(Optional<ContextAwarePredicate> player, Set<ResourceLocation> targetAdvancements)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                ResourceLocation.CODEC.listOf().fieldOf("advancements")
                        .forGetter(i -> List.copyOf(i.targetAdvancements()))
        ).apply(inst, (p, list) -> new Instance(p, Sets.newHashSet(list))));

        public Instance(ResourceLocation... targetAdvancements) {
            this(Optional.empty(), Sets.newHashSet(targetAdvancements));
        }

        public Instance(Set<ResourceLocation> targetAdvancements) {
            this(Optional.empty(), targetAdvancements);
        }

        public boolean matches(ResourceLocation id) {
            return this.targetAdvancements.contains(id);
        }
    }
}
