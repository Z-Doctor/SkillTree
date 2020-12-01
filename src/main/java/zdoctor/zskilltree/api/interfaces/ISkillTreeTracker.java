package zdoctor.zskilltree.api.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.trackers.ProgressTracker;

public interface ISkillTreeTracker extends IProgressTracker<CriterionTracker, CompoundNBT> {
    LivingEntity getOwner();

    void read(SCriterionTrackerSyncPacket packet);

    void reload();

    ProgressTracker startProgress(CriterionTracker tracker);

    boolean grantCriterion(CriterionTracker tracker, String criterionKey);

    boolean revokeCriterion(CriterionTracker tracker, String criterionKey);

    Iterable<CriterionTracker> getTrackers();

    ProgressTracker getOrStartProgress(CriterionTracker tracker);

    CriterionTracker getTracker(ResourceLocation key);

    boolean contains(CriterionTracker tracker);


    void flushDirty();
}
