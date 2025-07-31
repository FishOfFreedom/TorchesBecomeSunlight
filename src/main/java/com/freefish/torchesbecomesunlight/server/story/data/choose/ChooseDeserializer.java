package com.freefish.torchesbecomesunlight.server.story.data.choose;

import com.google.gson.*;

import java.lang.reflect.Type;

public class ChooseDeserializer implements JsonDeserializer<Choose> {
    @Override
    public Choose deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString(); // 获取类型字段

        // 根据类型选择对应的子类
        switch (type) {
            case IsItemInHandChoose.ID:
                return context.deserialize(json, IsItemInHandChoose.class);
            case OpFindRosmontisChoose.ID:
                return context.deserialize(json, OpFindRosmontisChoose.class);
            default:
                throw new JsonParseException("未知的choose类型: " + type);
        }
    }
}