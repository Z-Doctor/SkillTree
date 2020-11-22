package zdoctor.zskilltree.criterion;

import com.google.common.collect.Lists;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.AbstractCriterionTrigger;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.LootContext;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.data.handlers.PlayerSkillTreeTracker;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

public abstract class AbstractSkillTreeCriterionTrigger<T extends CriterionInstance> extends AbstractCriterionTrigger<T> {
    @Override
    protected void triggerListeners(ServerPlayerEntity serverPlayer, Predicate<T> testTrigger) {
        Optional<ISkillTreeTracker> handler = ModMain.getInstance().getPlayerSkillDataManager().getSkillData(serverPlayer);
        if (!handler.isPresent())
            return;
        if (!(handler.get() instanceof PlayerSkillTreeTracker))
            return;

        PlayerAdvancements key = ((PlayerSkillTreeTracker) handler.get()).getWrapper();

        Set<ICriterionTrigger.Listener<T>> set = this.triggerListeners.get(key);
        if (set != null && !set.isEmpty()) {
            LootContext lootcontext = EntityPredicate.getLootContext(serverPlayer, serverPlayer);
            List<ICriterionTrigger.Listener<T>> list = null;

            for (ICriterionTrigger.Listener<T> listener : set) {
                T t = listener.getCriterionInstance();
                if (t.getPlayerCondition().testContext(lootcontext) && testTrigger.test(t)) {
                    if (list == null) {
                        list = Lists.newArrayList();
                    }

                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<T> listener1 : list) {
                    listener1.grantCriterion(key);
                }
            }
        }
    }
}
