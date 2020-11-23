package zdoctor.zskilltree.advancements.criterion;

import com.google.gson.JsonObject;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.Objects;

public class SkillPageUnlockedTrigger extends AbstractCriterionTrigger<SkillPageUnlockedTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(ModMain.MODID, "skill_page_unlocked");

    @Override
    protected Instance deserializeTrigger(JsonObject json, EntityPredicate.AndPredicate entityPredicate, ConditionArrayParser conditionsParser) {
        return null;
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    public void trigger() {

    }

    public static class Instance extends CriterionInstance {
        private final ResourceLocation skillPageId;

        public Instance(EntityPredicate.AndPredicate player, ResourceLocation skillPageId) {
            super(ID, player);
            this.skillPageId = skillPageId;
        }

        public boolean test(SkillPage skillPage) {
            return Objects.equals(skillPage.getRegistryName(), skillPageId);
        }
    }
}
