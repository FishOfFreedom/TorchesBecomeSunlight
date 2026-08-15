package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;

import java.util.Optional;

/**
 * 1.21 重写了进度触发器：CriterionTrigger 改为基于 Codec，监听器管理下沉到
 * {@link SimpleCriterionTrigger}，触发实例只需实现 SimpleInstance（提供 player()）。
 * 原先自建的 TBSTrigger 基类与 JSON 手写序列化（DeserializationContext /
 * SerializationContext / AbstractCriterionTriggerInstance）在 1.21 均已不存在。
 */
public class StringAdvancementTrigger extends SimpleCriterionTrigger<StringAdvancementTrigger.Instance> {

    @Override
    public Codec<Instance> codec() {
        return Instance.CODEC;
    }

    public void trigger(ServerPlayer player, String id) {
        this.trigger(player, instance -> instance.matches(id));
    }

    public record Instance(Optional<ContextAwarePredicate> player, String targetAdvancements)
            implements SimpleCriterionTrigger.SimpleInstance {

        public static final Codec<Instance> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter(Instance::player),
                Codec.STRING.fieldOf("id").forGetter(Instance::targetAdvancements)
        ).apply(inst, Instance::new));

        public Instance(String targetAdvancements) {
            this(Optional.empty(), targetAdvancements);
        }

        public boolean matches(String id) {
            return this.targetAdvancements.contains(id);
        }
    }
}
