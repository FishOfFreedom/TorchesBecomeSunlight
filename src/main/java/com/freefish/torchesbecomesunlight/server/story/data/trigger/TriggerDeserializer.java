package com.freefish.torchesbecomesunlight.server.story.data.trigger;

import com.google.gson.*;

import java.lang.reflect.Type;

public class TriggerDeserializer implements JsonDeserializer<Trigger> {
    @Override
    public Trigger deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString(); // 获取类型字段

        // 根据类型选择对应的子类
        switch (type) {
            case EventTrigger.ID:
                return context.deserialize(json, TimerTrigger.class);
            case TimerTrigger.ID:
                return context.deserialize(json, EventTrigger.class);
            default:
                throw new JsonParseException("未知的trigger类型: " + type);
        }
    }
}