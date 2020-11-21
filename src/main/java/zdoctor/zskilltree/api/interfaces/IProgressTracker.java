package zdoctor.zskilltree.api.interfaces;

import net.minecraft.advancements.Criterion;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.zskilltree.criterion.ProgressTracker;

import java.util.Map;

public interface IProgressTracker<T extends INBT> extends INBTSerializable<T> {
    boolean grant(CriterionTracker page);

    boolean revoke(CriterionTracker page);

    boolean grantCriterion(CriterionTracker trackable, String criterionKey);

    boolean revokeCriterion(CriterionTracker page, String criterionKey);

    boolean startProgress(CriterionTracker trackable);

    void update(CriterionTracker trackable, Map<String, Criterion> criterion, String[][] requirements);

    boolean hasProgress(CriterionTracker trackable);

    ProgressTracker getProgress(CriterionTracker trackable);

    Iterable<CriterionTracker> getTrackers();

    Iterable<ProgressTracker> getAllProgress();

    boolean contains(CriterionTracker trackable);

}
