package com.freefish.torchesbecomesunlight.server.story.data.canoption;

import com.google.gson.*;

import java.lang.reflect.Type;

public class CanOptionDeserializer implements JsonDeserializer<CanOption> {
    @Override
    public CanOption deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString(); // 获取类型字段

        // 根据类型选择对应的子类
        switch (type) {
            case HealthLargerCanOption.ID:
                return context.deserialize(json, HealthLargerCanOption.class);
            case IsSeenRosmontisCanOption.ID:
                return context.deserialize(json, IsSeenRosmontisCanOption.class);
            default:
                throw new JsonParseException("未知的canoption类型: " + type);
        }
    }
}