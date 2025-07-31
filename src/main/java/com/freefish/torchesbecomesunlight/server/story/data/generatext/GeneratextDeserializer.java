package com.freefish.torchesbecomesunlight.server.story.data.generatext;

import com.google.gson.*;

import java.lang.reflect.Type;

public class GeneratextDeserializer implements JsonDeserializer<Generatext> {
    @Override
    public Generatext deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString(); // 获取类型字段
        // 根据类型选择对应的子类
        switch (type) {
            case FindVillagerGeneratext.ID:
                return context.deserialize(json, FindVillagerGeneratext.class);
            default:
                throw new JsonParseException("未知的generatext类型: " + type);
        }
    }
}
