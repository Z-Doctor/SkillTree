package zdoctor.zskilltree.skilltree.trackers;

import com.google.gson.JsonElement;
import net.minecraft.advancements.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.network.PacketDistributor;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.criterion.SkillTreeListener;
import zdoctor.zskilltree.skilltree.events.SkillTreeEvent;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PlayerSkillTreeTracker extends SkillTreeTracker {
    // Needed to listen in on advancements, essentially creating our own invitation
    private final SkillTreeAdvancementWrapper wrapper;

    public PlayerSkillTreeTracker(ServerPlayerEntity player) {
        super(player);
        wrapper = new SkillTreeAdvancementWrapper(player, this);
    }

    @Override
    protected void updateCriteria(CriterionTracker tracker, ProgressTracker progress) {
        super.updateCriteria(tracker, progress);
        registerListeners(tracker);
    }

    @Override
    protected void onProgressCompleted(CriterionTracker trackable) {
        unregisterListeners(trackable);
        super.onProgressCompleted(trackable);
        // TODO Make a proper event
//        ExtendedCriteriaTriggers.SKILL_PAGE_TRIGGER.triggerListeners(getPlayer(), trackable.getRegistryName(), true);
//        ExtendedCriteriaTriggers.ANY_SKILL_PAGE_TRIGGER.triggerListeners(getPlayer(), trackable.getRegistryName(), true);
    }

    @Override
    protected void onProgressRevoked(CriterionTracker trackable) {
        registerListeners(trackable);
        super.onProgressRevoked(trackable);
        // TODO Make a proper event
//        ExtendedCriteriaTriggers.SKILL_PAGE_TRIGGER.triggerListeners(getPlayer(), trackable.getRegistryName(), false);
//        ExtendedCriteriaTriggers.ANY_SKILL_PAGE_TRIGGER.triggerListeners(getPlayer(), trackable.getRegistryName(), false);
    }

    public ServerPlayerEntity getPlayer() {
        return (ServerPlayerEntity) super.getOwner();
    }

    @Override
    protected void process(boolean firstSync, Set<CriterionTracker> toAdd, Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressUpdate) {
        ModMain.getInstance().getPacketChannel().send(PacketDistributor.PLAYER.with(this::getPlayer),
                new SCriterionTrackerSyncPacket(firstSync, toAdd, toRemove, progressUpdate));
    }

    @Override
    public void reload() {
        CriteriaTriggers.getAll().forEach(t -> t.removeAllListeners(wrapper));
        super.reload();
        ModMain.getInstance().getSkillTreeDataManager().getAllTrackers().values().forEach(this::registerListeners);
        MinecraftForge.EVENT_BUS.post(new SkillTreeEvent.PlayerReloadedEvent(getPlayer()));
    }

    public SkillTreeAdvancementWrapper getWrapper() {
        return wrapper;
    }

    protected void registerListeners(CriterionTracker trackable) {
        ProgressTracker progress = getOrStartProgress(trackable);
        if (!progress.isDone()) {
            for (Map.Entry<String, Criterion> entry : trackable.getCriteria().entrySet()) {
                CriterionProgress criterionprogress = progress.getCriterionProgress(entry.getKey());
                if (criterionprogress != null && !criterionprogress.isObtained()) {
                    ICriterionInstance instance = entry.getValue().getCriterionInstance();
                    if (instance != null) {
                        ICriterionTrigger<ICriterionInstance> trigger = CriteriaTriggers.get(instance.getId());
                        if (trigger != null) {
                            trigger.addListener(wrapper, new SkillTreeListener<>(instance, trackable, entry.getKey()));
                        } else
                            LOGGER.error("Unable to find trigger {}", instance.getId());
                    }
                }
            }
        }
    }

    protected void unregisterListeners(CriterionTracker trackable) {
        ProgressTracker progress = getOrStartProgress(trackable);

        for (Map.Entry<String, Criterion> entry : trackable.getCriteria().entrySet()) {
            CriterionProgress criterionProgress = progress.getCriterionProgress(entry.getKey());
            if (criterionProgress != null && (criterionProgress.isObtained() || progress.isDone())) {
                ICriterionInstance instance = entry.getValue().getCriterionInstance();
                if (instance != null) {
                    ICriterionTrigger<ICriterionInstance> trigger = CriteriaTriggers.get(instance.getId());
                    if (trigger != null) {
                        trigger.removeListener(wrapper, new SkillTreeListener<>(instance, trackable, entry.getKey()));
                    }
                }
            }
        }
    }

    public static class SkillTreeAdvancementWrapper extends PlayerAdvancements {
        private static final DummyAdvancementManager dummy = new DummyAdvancementManager();
        private final PlayerSkillTreeTracker skillTreeHandler;

        public SkillTreeAdvancementWrapper(ServerPlayerEntity player, PlayerSkillTreeTracker skillTreeHandler) {
            super(null, null, dummy, new FakeFile(), player);
            this.skillTreeHandler = skillTreeHandler;
        }

        @Override
        public boolean equals(Object o) {
            return skillTreeHandler.equals(o);
        }

        @Override
        public int hashCode() {
            return skillTreeHandler.hashCode();
        }

        public PlayerSkillTreeTracker getSkillTreeHandler() {
            return skillTreeHandler;
        }

        @Override
        public void setPlayer(ServerPlayerEntity player) {
        }

        @Override
        public void dispose() {
            super.dispose();
        }

        @Override
        public void reset(AdvancementManager manager) {
            this.dispose();
        }

        @Override
        public void save() {
        }

        @Override
        public boolean grantCriterion(Advancement advancementIn, String criterionKey) {
            return false;
        }

        @Override
        public boolean revokeCriterion(Advancement advancementIn, String criterionKey) {
            return false;
        }

        @Override
        public void flushDirty(ServerPlayerEntity serverPlayer) {
        }

        @Override
        public void setSelectedTab(Advancement advancementIn) {
        }

        @Override
        public AdvancementProgress getProgress(Advancement advancementIn) {
            return null;
        }
    }

    private static class DummyAdvancementManager extends AdvancementManager {

        public DummyAdvancementManager() {
            super(null);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
        }

        @Override
        public Collection<Advancement> getAllAdvancements() {
            return Collections.emptyList();
        }

        @Override
        public Advancement getAdvancement(ResourceLocation id) {
            return null;
        }
    }

    private static class FakeFile extends File {

        public FakeFile() {
            super("");
        }

        @Override
        public boolean isFile() {
            return false;
        }


    }
}
