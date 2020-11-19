package zdoctor.zskilltree.api.interfaces;

import net.minecraft.advancements.Criterion;
import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.zskilltree.criterion.ProgressTracker;

import java.util.Map;

public interface IProgressTracker<T extends INBT> extends INBTSerializable<T> {
    boolean grant(ITrackCriterion page);

    boolean revoke(ITrackCriterion page);

    boolean grantCriterion(ITrackCriterion trackable, String criterionKey);

    boolean revokeCriterion(ITrackCriterion page, String criterionKey);

    boolean startProgress(ITrackCriterion trackable);

    ProgressTracker stopTracking(ITrackCriterion trackable);

    void update(ITrackCriterion trackable, Map<String, Criterion> criterion, String[][] requirements);

    boolean hasProgress(ITrackCriterion trackable);

    ProgressTracker getProgress(ITrackCriterion trackable);

    Iterable<ITrackCriterion> getTrackers();

    Iterable<ProgressTracker> getAllProgress();

    boolean contains(ITrackCriterion trackable);

    void dispose();

}
