package zdoctor.zskilltree.skilltree.data.providers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.skilltree.data.handlers.PlayerSkillTreeTracker;
import zdoctor.zskilltree.skilltree.data.handlers.SkillTreeTracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySkillTreeProvider {

    public ICapabilityProvider createProvider(Entity entity) {
        if (entity instanceof ServerPlayerEntity)
            return Provider.of(new PlayerSkillTreeTracker((ServerPlayerEntity) entity));
        else if (entity instanceof LivingEntity)
            return Provider.of(new SkillTreeTracker((LivingEntity) entity));
        else
            return null;
    }

    public static class Provider implements ICapabilitySerializable<CompoundNBT> {

        private final ISkillTreeTracker skillTreeTracker;

        protected Provider(ISkillTreeTracker skillTreeTracker) {
            this.skillTreeTracker = skillTreeTracker;
        }

        public static ICapabilityProvider of(ISkillTreeTracker skillTreeTracker) {
            return new Provider(skillTreeTracker);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap != ModMain.SKILL_TREE_CAPABILITY)
                return LazyOptional.empty();
            return LazyOptional.of(() -> skillTreeTracker).cast();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return skillTreeTracker.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            skillTreeTracker.deserializeNBT(nbt);
        }
    }
}