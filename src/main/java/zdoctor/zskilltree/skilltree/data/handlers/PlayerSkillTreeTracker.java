package zdoctor.zskilltree.skilltree.data.handlers;

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
import zdoctor.zskilltree.criterion.ProgressTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.events.SkillTreeEvent;
import zdoctor.zskilltree.skilltree.skill.SkillTreeDataManager;
import zdoctor.zskilltree.skilltree.skillpages.SkillTreeListener;

import java.io.File;
import java.util.*;

public class PlayerSkillTreeTracker extends SkillTreeTracker {
    // Needed to listen in on advancements, essentially creating our own invitation
    private final SkillTreeAdvancementWrapper wrapper;

    public PlayerSkillTreeTracker(ServerPlayerEntity player) {
        super(player);
        wrapper = new SkillTreeAdvancementWrapper(player, this);
    }

    @Override
    protected void startProgress(CriterionTracker tracker, ProgressTracker progress) {
        super.startProgress(tracker, progress);
        registerListeners(tracker);
    }

    @Override
    protected void onProgressCompleted(CriterionTracker trackable) {
        unregisterListeners(trackable);
        super.onProgressCompleted(trackable);
    }

    @Override
    protected void onProgressRevoked(CriterionTracker trackable) {
        registerListeners(trackable);
        super.onProgressRevoked(trackable);
    }

    public ServerPlayerEntity getPlayer() {
        return (ServerPlayerEntity) super.getOwner();
    }

    @Override
    public void flushDirty() {
        if (firstSync || !progressChanged.isEmpty()) {
            checkPageVisibility();
            LOGGER.debug("Syncing player data to client {}", getPlayer().getDisplayName().getString());
            Set<CriterionTracker> toAdd = new HashSet<>(completionChanged);
            toAdd.retainAll(completed);
            completionChanged.removeAll(toAdd);

            Set<ResourceLocation> toRemove = new HashSet<>();
            completionChanged.forEach(page -> toRemove.add(page.getRegistryName()));

            Map<ResourceLocation, ProgressTracker> progressUpdate = new HashMap<>();
            progressChanged.forEach(progressTracker -> {
                if (progressTracker.shouldClientTrack())
                    progressUpdate.put(progressTracker.getRegistryName(), getProgress(progressTracker));
            });

            // TODO Send updates to other players without overriding their skills
            //  so I'll need to make another packet
            ModMain.getInstance().getPacketChannel().send(PacketDistributor.PLAYER.with(this::getPlayer),
                    new SCriterionTrackerSyncPacket(firstSync, toAdd, toRemove, progressUpdate));

            firstSync = false;
            progressChanged.clear();
            completionChanged.clear();
        }
    }

    @Override
    public void reload() {
        dispose();
        super.reload();
        SkillTreeDataManager.getAllTrackers().values().forEach(this::registerListeners);
        MinecraftForge.EVENT_BUS.post(new SkillTreeEvent.PlayerReloadedEvent(getPlayer()));
    }

    public SkillTreeAdvancementWrapper getWrapper() {
        return wrapper;
    }

    @Override
    public void dispose() {
        // A super call is not needed because it will clear the trackers
        CriteriaTriggers.getAll().forEach(t -> t.removeAllListeners(wrapper));
    }

    protected void registerListeners(CriterionTracker trackable) {
        ProgressTracker progress = getProgress(trackable);
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
        ProgressTracker progress = getProgress(trackable);

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
