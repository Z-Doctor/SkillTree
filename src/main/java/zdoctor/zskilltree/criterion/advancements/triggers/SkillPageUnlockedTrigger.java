package zdoctor.zskilltree.criterion.advancements.triggers;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.Entity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

public class SkillPageUnlockedTrigger extends AbstractCriterionTrigger<SkillPageUnlockedTrigger.Instance> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "skill_page_unlocked");

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        ResourceLocation skillPage = new ResourceLocation(JSONUtils.getString(json, "skillPageId"));
        boolean hasPage = JSONUtils.getBoolean(json, "hasSkill");
        return new Instance(entityPredicate, skillPage, hasPage);
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger() {

    }

    public static class Instance extends CriterionInstance {
        private final ResourceLocation skillPageId;
        private final boolean hasPage;

        public Instance(EntityPredicate.AndPredicate player, ResourceLocation skillPageId, boolean hasPage) {
            super(ID, player);
            this.skillPageId = skillPageId;
            this.hasPage = hasPage;
        }

        public static Instance with(ResourceLocation skillPageId, boolean hasPage) {
            return new Instance(EntityPredicate.AndPredicate.ANY_AND, skillPageId, hasPage);
        }

        @Override
        public JsonObject serialize(ConditionArraySerializer conditions) {
            JsonObject jsonObject = super.serialize(conditions);
            jsonObject.addProperty("skillPageId", skillPageId.toString());
            jsonObject.addProperty("hasSkill", hasPage);
            return jsonObject;
        }

        public boolean test(Entity entity) {
            SkillPage page = SkillTreeApi.getPage(skillPageId);
            if (page == null) {
                LOGGER.error("Skill page {} not found when testing for it.", hasPage);
                return !hasPage;
            }
            return SkillTreeApi.hasPage(entity, page) == hasPage;
        }
    }
}
