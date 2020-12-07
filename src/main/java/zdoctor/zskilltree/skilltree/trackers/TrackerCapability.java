package zdoctor.zskilltree.skilltree.trackers;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;

import javax.annotation.Nullable;

public class TrackerCapability implements Capability.IStorage<ISkillTreeTracker> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<ISkillTreeTracker> capability, ISkillTreeTracker instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<ISkillTreeTracker> capability, ISkillTreeTracker instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT)
            instance.deserializeNBT((CompoundNBT) nbt);
    }
}
