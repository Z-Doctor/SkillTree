package zdoctor.zskilltree.api.interfaces;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;

public interface ISkillTreeTracker extends IProgressTracker<CompoundNBT> {
    LivingEntity getOwner();

    void read(SCriterionTrackerSyncPacket packet);

    void reload();

    void flushDirty();
}
