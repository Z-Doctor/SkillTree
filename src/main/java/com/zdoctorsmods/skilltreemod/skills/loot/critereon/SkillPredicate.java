package com.zdoctorsmods.skilltreemod.skills.loot.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.zdoctorsmods.skilltreemod.skills.Skill;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class SkillPredicate {
    public static final SkillPredicate ANY = new SkillPredicate();

    private final ResourceLocation id;

    public SkillPredicate() {
        this.id = null;
    }

    public SkillPredicate(ResourceLocation id) {
        this.id = id;
    }

    public SkillPredicate(Skill skill) {
        this.id = skill.getId();
    }

    public boolean matches(Skill skill) {
        if (this == ANY)
            return true;
        return skill.getId().equals(id);
    }

    public JsonElement serializeToJson() {
        if (this == ANY || id == null) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();
            jsonobject.addProperty("skill", id.toString());
            return jsonobject;
        }
    }

    public static SkillPredicate fromJson(JsonElement pJson) {
        if (pJson != null && !pJson.isJsonNull()) {
            String skillId = GsonHelper.convertToString(pJson, "skill");
            if (skillId.equalsIgnoreCase("ANY"))
                return ANY;
            ResourceLocation id = new ResourceLocation(skillId);
            return new SkillPredicate(id);
        }
        return ANY;
    }
}
