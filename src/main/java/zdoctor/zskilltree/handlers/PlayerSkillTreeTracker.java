package zdoctor.zskilltree.handlers;

import com.google.gson.JsonElement;
import net.minecraft.advancements.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.PacketDistributor;
import zdoctor.zskilltree.api.interfaces.ITrackCriterion;
import zdoctor.zskilltree.criterion.ProgressTracker;
import zdoctor.zskilltree.network.ZSkillTreePacketHandler;
import zdoctor.zskilltree.network.play.server.SSkillPageInfoPacket;
import zdoctor.zskilltree.skill.SkillTreeDataManager;
import zdoctor.zskilltree.skillpages.SkillPage;
import zdoctor.zskilltree.skillpages.SkillTreeListener;

import java.io.File;
import java.util.*;

public class PlayerSkillTreeTracker extends SkillTreeTracker {
    private final SkillTreeAdvancementWrapper wrapper;

    public PlayerSkillTreeTracker(ServerPlayerEntity player) {
        super(player);
        wrapper = new SkillTreeAdvancementWrapper(player, this);
    }

    protected void onProgressCompleted(SkillPage page) {
        unregisterListeners(page);
    }

    protected void onPageRevoked(SkillPage page) {
        registerListeners(page);
    }

    @Override
    public void flushDirty() {
        if (owner == null) {
            LOGGER.error("Player Handler is null {}", this);
            return;
        }

        if (firstSync || !progressChanged.isEmpty()) {
            checkPageVisibility();
            if (owner instanceof ServerPlayerEntity) {
                LOGGER.debug("Syncing player data to client {}", owner.getDisplayName().getString());
                Set<ITrackCriterion> toAdd = new HashSet<>(visibilityChanged);
                toAdd.retainAll(visible);
                visibilityChanged.removeAll(toAdd);

                Set<ResourceLocation> toRemove = new HashSet<>();
                visibilityChanged.forEach(page -> toRemove.add(page.getId()));

                Map<ResourceLocation, ProgressTracker> progressUpdate = new HashMap<>();
                progressChanged.forEach(trackable -> progressUpdate.put(trackable.getId(), getProgress(trackable)));

                ZSkillTreePacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(),
                        new SSkillPageInfoPacket(firstSync, toAdd, toRemove, progressUpdate));

                progressChanged.forEach(trackable -> {
                    if (getProgress(trackable).isDone())
                        unregisterListeners(trackable);
                    else
                        registerListeners(trackable);
                });
            } else
                LOGGER.error("Trying to sync data to non-player {}", owner.getDisplayName());

            firstSync = false;
            progressChanged.clear();
            visibilityChanged.clear();
        }
    }

    @Override
    public void reload() {
        dispose();
        super.reload();
        SkillTreeDataManager.getAllTrackers().values().forEach(this::registerListeners);
    }

    public SkillTreeAdvancementWrapper getWrapper() {
        return wrapper;
    }

    public void dispose() {
        for (ICriterionTrigger<?> trigger : CriteriaTriggers.getAll()) {
            trigger.removeAllListeners(wrapper);
        }
    }

    protected void registerListeners(ITrackCriterion trackable) {
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

    protected void unregisterListeners(ITrackCriterion trackable) {
        ProgressTracker progress = getProgress(trackable);

        for (Map.Entry<String, Criterion> entry : trackable.getCriteria().entrySet()) {
            net.minecraft.advancements.CriterionProgress criterionprogress = progress.getCriterionProgress(entry.getKey());
            if (criterionprogress != null && (criterionprogress.isObtained() || progress.isDone())) {
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
            return Collections.EMPTY_LIST;
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
