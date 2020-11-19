package zdoctor.mcskilltree.api;

import com.google.gson.JsonElement;
import zdoctor.mcskilltree.skills.Skill;

import java.util.function.BiPredicate;

public interface ISkillProperty extends BiPredicate<Skill, ISkillHandler> {

    JsonElement serialize();
    ISkillProperty deserialize(JsonElement element);

}
