package zdoctor.zskilltree.api.interfaces;

import net.minecraft.advancements.Criterion;
import net.minecraft.nbt.INBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.zskilltree.skilltree.data.criterion.ProgressTracker;
import zdoctor.zskilltree.skilltree.skill.Skill;

import java.util.Map;

public interface IProgressTracker<T extends INBT> extends INBTSerializable<T> {
    boolean grant(CriterionTracker tracker);

    boolean revoke(CriterionTracker tracker);

    boolean reset(CriterionTracker tracker);

    boolean grantCriterion(CriterionTracker tracker, String criterionKey);

    boolean revokeCriterion(CriterionTracker tracker, String criterionKey);

    boolean startProgress(CriterionTracker tracker);

    void update(CriterionTracker trackable, Map<String, Criterion> criterion, String[][] requirements);

    boolean has(CriterionTracker tracker);

    ProgressTracker getProgress(CriterionTracker tracker);

    Iterable<CriterionTracker> getTrackers();

    Iterable<ProgressTracker> getAllProgress();

    boolean contains(CriterionTracker tracker);

    CriterionTracker getTracker(ResourceLocation key);

    boolean isDone(CriterionTracker tracker);
}
