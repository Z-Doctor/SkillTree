package com.zdoctorsmods.skilltreemod.skills;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class SkillInfo {
    private final SkillType type;

    public SkillInfo() {
        type = SkillType.NONE;
    }

    public JsonElement serializeToJson() {
        return null;
    }

    public static SkillInfo deserialize(JsonObject pJson) throws JsonParseException {
        return null;
    }
}
