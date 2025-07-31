package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

public class StringAdvancementTrigger extends TBSTrigger<StringAdvancementTrigger.Instance, StringAdvancementTrigger.Listener> {
    public static final ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "string_advancement");

    public StringAdvancementTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public StringAdvancementTrigger.Instance createInstance(JsonObject object, DeserializationContext conditions) {
        JsonElement advancementsElement = object.get("id");

        return new StringAdvancementTrigger.Instance(advancementsElement.getAsString());
    }

    @Override
    public StringAdvancementTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new StringAdvancementTrigger.Listener(playerAdvancements);
    }

    public void trigger(ServerPlayer player,String id) {
        StringAdvancementTrigger.Listener listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(id);
        }
    }

    static class Listener extends TBSTrigger.Listener<Instance> {
        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(String id) {
            this.listeners.stream().filter(instanceListener -> instanceListener.getTriggerInstance().targetAdvancements.contains(id)).findFirst().ifPresent(listener -> {
                listener.run(this.playerAdvancements);
            });
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final String targetAdvancements;

        public Instance(String resourceLocation) {
            super(StringAdvancementTrigger.ID, ContextAwarePredicate.ANY);
            this.targetAdvancements = resourceLocation;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);

            json.addProperty("id",targetAdvancements);
            return json;
        }
    }
}
