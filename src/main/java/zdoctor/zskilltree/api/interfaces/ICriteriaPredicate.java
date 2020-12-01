package zdoctor.zskilltree.api.interfaces;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import net.minecraft.advancements.CriterionProgress;
import zdoctor.zskilltree.skilltree.trackers.ProgressTracker;

import java.util.function.Predicate;

public interface ICriteriaPredicate extends Predicate<ProgressTracker> {
    JsonElement serialize();

    class CompletedCriteriaPredicate implements ICriteriaPredicate {
        private final boolean completion;

        public CompletedCriteriaPredicate(boolean completion) {
            this.completion = completion;
        }

        @Override
        public JsonElement serialize() {
            return new JsonPrimitive(this.completion);
        }

        @Override
        public boolean test(ProgressTracker progress) {
            return progress != null && progress.isDone() == this.completion;
        }
    }

    class CriteriaPredicate implements ICriteriaPredicate {
        private final Object2BooleanMap<String> completion;

        public CriteriaPredicate(Object2BooleanMap<String> completion) {
            this.completion = completion;
        }

        public JsonElement serialize() {
            JsonObject jsonobject = new JsonObject();
            this.completion.forEach(jsonobject::addProperty);
            return jsonobject;
        }

        public boolean test(ProgressTracker progress) {
            for (it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<String> entry : this.completion.object2BooleanEntrySet()) {
                CriterionProgress criterionprogress = progress.getCriterionProgress(entry.getKey());
                if (criterionprogress == null || criterionprogress.isObtained() != entry.getBooleanValue()) {
                    return false;
                }
            }

            return true;
        }
    }
}
