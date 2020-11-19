package zdoctor.mcskilltree.skills.properties;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.util.JSONUtils;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.api.ISkillProperty;
import zdoctor.mcskilltree.skills.Skill;

import javax.annotation.Nullable;

public class SkillTierProperty extends MinMaxBounds<Integer> implements ISkillProperty {
    public static final SkillTierProperty UNBOUNDED = new SkillTierProperty(null, null);

    protected SkillTierProperty(Integer min, Integer max) {
        super(min, max);
    }

    public static SkillTierProperty fromJson(@Nullable JsonElement element) {
        return fromJson(element, UNBOUNDED, JSONUtils::getInt, SkillTierProperty::new);
    }

    public static SkillTierProperty exactly(int min) {
        return new SkillTierProperty(min, min);
    }

    public static SkillTierProperty from(int min, int max) {
        return new SkillTierProperty(min, max);
    }

    @Override
    public SkillTierProperty deserialize(JsonElement element) {
        if (element == null || element.isJsonNull())
            return null;
        JsonObject jsonObject = JSONUtils.getJsonObject(element, "tier_object");
        return fromJson(jsonObject.get("tier"));
    }

    @Override
    public JsonElement serialize() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("tier", super.serialize());
        return jsonObject;
    }

    @Override
    public boolean test(Skill skill, ISkillHandler handler) {
        int tier = handler.getTier(skill);
        if (this.min != null && this.min > tier) {
            return false;
        } else {
            return this.max == null || !(this.max < tier);
        }
    }

}
