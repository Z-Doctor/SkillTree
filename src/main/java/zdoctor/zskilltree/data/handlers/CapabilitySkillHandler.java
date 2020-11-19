package zdoctor.zskilltree.data.handlers;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.client.multiplayer.ClientPlayerProgressTracker;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilitySkillHandler {
    public static void register() {
        CapabilityManager.INSTANCE.register(ISkillTreeTracker.class, new Capability.IStorage<ISkillTreeTracker>() {
            @Override
            public INBT writeNBT(Capability<ISkillTreeTracker> capability, ISkillTreeTracker instance, Direction side) {
                return instance.serializeNBT();
            }

            @Override
            public void readNBT(Capability<ISkillTreeTracker> capability, ISkillTreeTracker instance, Direction side, INBT nbt) {
                if (nbt instanceof CompoundNBT)
                    instance.deserializeNBT((CompoundNBT) nbt);
            }
        }, SkillTreeTracker::new);

        MinecraftForge.EVENT_BUS.register(CapabilitySkillHandler.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void attachCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof LivingEntity)
            e.addCapability(new ResourceLocation(ModMain.MODID, "skill_capability"),
                    new CapabilitySkillProvider((LivingEntity) e.getObject()));

    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath())
            return;
        LazyOptional<ISkillTreeTracker> oldCap = event.getPlayer().getCapability(ModMain.SKILL_TREE_CAPABILITY);
        if (!oldCap.isPresent())
            return;
        LazyOptional<ISkillTreeTracker> newCap = event.getPlayer().getCapability(ModMain.SKILL_TREE_CAPABILITY);
        if (!newCap.isPresent())
            return;
        // TODO Add config for keep on death(Default: true)

        oldCap.ifPresent(oldHandler -> newCap.ifPresent(newHandler ->
                newHandler.deserializeNBT(oldHandler.serializeNBT())));
    }

    public static class CapabilitySkillProvider implements ICapabilitySerializable<CompoundNBT> {

        final ISkillTreeTracker skillHandler;

        public CapabilitySkillProvider(LivingEntity entity) {
            if (entity instanceof ClientPlayerEntity)
                skillHandler = new ClientPlayerProgressTracker((ClientPlayerEntity) entity);
            else if (entity instanceof ServerPlayerEntity) {
                skillHandler = new PlayerSkillTreeTracker((ServerPlayerEntity) entity);
            } else
                skillHandler = new SkillTreeTracker(entity);
        }

        public CapabilitySkillProvider(@Nonnull ISkillTreeTracker handler) {
            skillHandler = handler;
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            if (cap != ModMain.SKILL_TREE_CAPABILITY)
                return LazyOptional.empty();
            return LazyOptional.of(() -> skillHandler).cast();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return skillHandler.serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            skillHandler.deserializeNBT(nbt);
        }
    }

}
