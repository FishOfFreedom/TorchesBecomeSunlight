package com.freefish.torchesbecomesunlight.server.init.generator.advancement.criterion;

import com.freefish.torchesbecomesunlight.TorchesBecomeSunlight;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.Set;

public class ChainedAdvancementTrigger extends TBSTrigger<ChainedAdvancementTrigger.Instance, ChainedAdvancementTrigger.Listener> {
    public static final ResourceLocation ID = new ResourceLocation(TorchesBecomeSunlight.MOD_ID, "chained_advancement");

    public ChainedAdvancementTrigger() {
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public ChainedAdvancementTrigger.Instance createInstance(JsonObject object, DeserializationContext conditions) {
        JsonElement advancementsElement = object.get("advancements");
        Set<ResourceLocation> advancements = new HashSet<>();

        if (advancementsElement != null) {
            if (advancementsElement.isJsonArray()) {
                JsonArray array = advancementsElement.getAsJsonArray();
                for (JsonElement element : array) {
                    String advancementId = element.getAsString();
                    advancements.add(new ResourceLocation(advancementId));
                }
            } else if (advancementsElement.isJsonPrimitive()) {
                String advancementId = advancementsElement.getAsString();
                advancements.add(new ResourceLocation(advancementId));
            }
        }
        int len = advancements.size();
        ResourceLocation[] resourceLocations = advancements.toArray(new ResourceLocation[len]);

        return new ChainedAdvancementTrigger.Instance(resourceLocations);
    }

    @Override
    public ChainedAdvancementTrigger.Listener createListener(PlayerAdvancements playerAdvancements) {
        return new ChainedAdvancementTrigger.Listener(playerAdvancements);
    }

    public void trigger(ServerPlayer player,ResourceLocation id) {
        ChainedAdvancementTrigger.Listener listeners = this.listeners.get(player.getAdvancements());
        if (listeners != null) {
            listeners.trigger(id);
        }
    }

    static class Listener extends TBSTrigger.Listener<Instance> {
        public Listener(PlayerAdvancements playerAdvancementsIn) {
            super(playerAdvancementsIn);
        }

        public void trigger(ResourceLocation id) {
            this.listeners.stream().filter(instanceListener -> instanceListener.getTriggerInstance().targetAdvancements.contains(id)).findFirst().ifPresent(listener -> {
                listener.run(this.playerAdvancements);
            });
        }
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        private final Set<ResourceLocation> targetAdvancements;

        public Instance(ResourceLocation ... resourceLocation) {
            super(ChainedAdvancementTrigger.ID, ContextAwarePredicate.ANY);
            this.targetAdvancements = Sets.newHashSet(resourceLocation);
        }

        public Instance(Set<ResourceLocation> targetAdvancements) {
            super(ChainedAdvancementTrigger.ID, ContextAwarePredicate.ANY);
            this.targetAdvancements = targetAdvancements;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject json = super.serializeToJson(context);

            JsonArray advancementsArray = new JsonArray();
            for (ResourceLocation id : targetAdvancements) {
                advancementsArray.add(id.toString());
            }
            json.add("advancements", advancementsArray);

            return json;
        }
    }
}
