package zdoctor.zskilltree.api.interfaces;

import net.minecraft.nbt.INBT;
import net.minecraftforge.common.util.INBTSerializable;
import zdoctor.zskilltree.skilltree.trackers.ProgressTracker;

import java.util.Map;

public interface IProgressTracker<T, K extends INBT> extends INBTSerializable<K> {
    boolean grant(T tracker);

    boolean revoke(T tracker);

    boolean reset(T tracker);

    boolean hasProgress(T tracker);

    ProgressTracker getProgress(T tracker);

    boolean isDone(T tracker);

    Map<CriterionTracker, ProgressTracker> getProgressTracker();
}
