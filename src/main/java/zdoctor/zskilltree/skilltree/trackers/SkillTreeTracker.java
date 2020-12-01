package zdoctor.zskilltree.skilltree.trackers;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;
import zdoctor.zskilltree.skilltree.events.CriterionTrackerEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillTreeTracker implements ISkillTreeTracker {
    @ObjectHolder("zskilltree:player_info")
    public static final SkillPage playerInfo = null;

    protected static final Logger LOGGER = LogManager.getLogger();

    private final Set<CriterionTracker> visible = new HashSet<>();
    private final Set<CriterionTracker> conditionallyVisible = new HashSet<>();

    private final Set<CriterionTracker> progressChanged = new HashSet<>();
    private final HashMap<CriterionTracker, ProgressTracker> progressTracker = new HashMap<>();

    private final HashMap<ResourceLocation, CriterionTracker> trackableMap = new HashMap<>();

    // TODO Make configurable
    private int updateTicks = 20;
    private int ticksSinceUpdate = -1;

    private boolean firstSync = true;
    private LivingEntity owner;

    public SkillTreeTracker() {
    }

    public SkillTreeTracker(LivingEntity entity) {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        owner = entity;
        LOGGER.debug("Attached Skill Tree to {}", owner);
    }

    protected void onProgressCompleted(CriterionTracker trackable) {
        // TODO Add toast and chat announcement to options
        MinecraftForge.EVENT_BUS.post(new CriterionTrackerEvent.ProgressGrantedEvent(owner, trackable));
    }

    protected void onProgressRevoked(CriterionTracker trackable) {
        startProgress(trackable);
        MinecraftForge.EVENT_BUS.post(new CriterionTrackerEvent.ProgressRevokedEvent(owner, trackable));
    }

    protected void onProgressChanged(CriterionTracker trackable) {
        progressChanged.add(trackable);
        ensureVisibility(trackable);
    }

    /**
     * Checks to see if the trackable exists without registering one if missing
     */
    @Override
    public boolean contains(CriterionTracker tracker) {
        return progressTracker.get(tracker) != null;
    }

    @Override
    public CriterionTracker getTracker(ResourceLocation key) {
        return trackableMap.get(key);
    }

    @Override
    public boolean isDone(CriterionTracker tracker) {
        ProgressTracker progress = getProgress(tracker);
        return progress != null && progress.isDone();
    }

    /**
     * Checks to see if the trackable exists and has progress
     */
    @Override
    public boolean hasProgress(CriterionTracker tracker) {
        ProgressTracker progress = progressTracker.get(tracker);
        return progress != null && progress.hasProgress();
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public ProgressTracker startProgress(CriterionTracker tracker) {
        ProgressTracker progress = progressTracker.get(tracker);
        if (progress != null) {
            LOGGER.trace("Tried to start duplicate progress.");
            return progress;
        }
        trackableMap.put(tracker.getRegistryName(), tracker);
        progressTracker.put(tracker, progress = new ProgressTracker());
        updateCriteria(tracker, progress);
        return progress;
    }

    // TODO Reward system for getting a skill? And maybe a way to check if they were already rewarded, maybe config
    protected void updateCriteria(CriterionTracker tracker, ProgressTracker progress) {
        if (progress.update(tracker.getCriteria(), tracker.getRequirements())) {
            onProgressChanged(tracker);
        }
    }

    @Override
    public ProgressTracker getProgress(CriterionTracker tracker) {
        return progressTracker.get(tracker);
    }

    @Override
    public ProgressTracker getOrStartProgress(CriterionTracker tracker) {
        ProgressTracker progress = progressTracker.get(tracker);
        if (progress == null)
            progress = startProgress(tracker);
        return progress;
    }

    @Override
    public boolean grant(CriterionTracker tracker) {
        ProgressTracker progress = getOrStartProgress(tracker);
        if (progress.grant()) {
            // TODO Reward, maybe check if first time, also Toast
            onProgressCompleted(tracker);
            onProgressChanged(tracker);
            return true;

        }
        return false;
    }

    @Override
    public boolean revoke(CriterionTracker tracker) {
        ProgressTracker progress = getOrStartProgress(tracker);
        if (!progress.revoke())
            return false;
        onProgressRevoked(tracker);
        onProgressChanged(tracker);
        return true;
    }

    @Override
    public boolean reset(CriterionTracker tracker) {
        ProgressTracker progress = getOrStartProgress(tracker);
        boolean obtained = isDone(tracker);
        if (!progress.resetProgress())
            return false;
        if (obtained)
            onProgressRevoked(tracker);
        onProgressChanged(tracker);
        return true;
    }

    @Override
    public boolean grantCriterion(CriterionTracker tracker, String criterionKey) {
        ProgressTracker progress = getOrStartProgress(tracker);
        if (progress.grantCriterion(criterionKey)) {
            if (progress.isDone())
                onProgressCompleted(tracker);
            onProgressChanged(tracker);
        }
        return true;
    }

    @Override
    public boolean revokeCriterion(CriterionTracker trackable, String criterionKey) {
        ProgressTracker progress = getOrStartProgress(trackable);
        boolean obtained = isDone(trackable);
        if (progress.revokeCriterion(criterionKey)) {
            if (obtained)
                onProgressRevoked(trackable);
            onProgressChanged(trackable);
            return true;
        }
        return false;
    }

    @Override
    public Iterable<CriterionTracker> getTrackers() {
        return progressTracker.keySet();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT skillTreeData = new CompoundNBT();
        ListNBT progressList = new ListNBT();

        skillTreeData.put("progress_list", progressList);
        for (Map.Entry<CriterionTracker, ProgressTracker> entry : progressTracker.entrySet()) {
            ProgressTracker progress = entry.getValue();
            if (!progress.hasProgress())
                continue;
            CompoundNBT progressNbt = new CompoundNBT();
            progressNbt.putString("id", entry.getKey().getRegistryName().toString());
            progressNbt.put("criterion", progress.serializeNBT());

            progressList.add(progressNbt);
        }

        return skillTreeData;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        dispose();
        reset();
        ImmutableMap<ResourceLocation, CriterionTracker> allTrackers = ModMain.getInstance().getSkillTreeDataManager().getAllTrackers();

        nbt.getList("progress_list", Constants.NBT.TAG_COMPOUND).stream().map(tag -> (CompoundNBT) tag).forEach(data -> {
            ResourceLocation id = new ResourceLocation(data.getString("id"));
            CriterionTracker trackable = allTrackers.get(id);
            ProgressTracker progress = getOrStartProgress(trackable);
            progress.deserializeNBT(data.getCompound("criterion"));
            onProgressChanged(trackable);
        });
//        trackableMap.values().forEach(this::ensureVisibility);
    }

    public void dispose() {
        progressTracker.clear();
    }

    protected void reset() {
        visible.clear();
        progressChanged.clear();
        conditionallyVisible.clear();
        trackableMap.clear();
        ticksSinceUpdate = -1;
        firstSync = true;
    }

    @Override
    public void read(SCriterionTrackerSyncPacket packetIn) {
        if (packetIn == null)
            LOGGER.trace("Server-Sided Skill Tree Tracker tried to read a null packet");
        else if (getOwner() == null)
            LOGGER.trace("Server-Sided Skill Tree Tracker has a null owner");
        else if (packetIn.fromServer)
            LOGGER.info("Server-Sided Skill Tracker owned by {} was fed a packet", getOwner().getDisplayName().getString());
        else {
            // Client Side Processing
            if (packetIn.isFirstSync())
                reset();

            for (CriterionTracker trackable : packetIn.getToAdd()) {//add to visible
                visible.add(trackableMap.compute(trackable.getRegistryName(), (key, old) -> trackable));
            }

            for (ResourceLocation id : packetIn.getToRemove()) {
                progressTracker.keySet().removeIf(key -> key.getRegistryName().equals(id));
                visible.remove(trackableMap.remove(id));
            }

            // TODO Not sure how I like reading progress, and seems to not be working correctly
            for (Map.Entry<ResourceLocation, ProgressTracker> entry : packetIn.getProgressChanged().entrySet()) {
                CriterionTracker trackable = trackableMap.get(entry.getKey());
                if (trackable != null) {
                    progressTracker.put(trackable, entry.getValue());
                } else
                    LOGGER.error("Unable to update progress of {}", entry.getKey());
            }
        }
    }

    @Override
    public void reload() {
        reset();
        HashSet<CriterionTracker> missingTrackers = new HashSet<>(progressTracker.keySet());
        for (CriterionTracker trackable : ModMain.getInstance().getSkillTreeDataManager().getAllTrackers().values()) {
            trackableMap.put(trackable.getRegistryName(), trackable);
            missingTrackers.remove(trackable);
            ProgressTracker progress = getProgress(trackable);
            if (progress == null)
                progress = startProgress(trackable);
            updateCriteria(trackable, progress);
            onProgressChanged(trackable);
        }
        progressChanged.addAll(missingTrackers); // To be removed from the client
        trackableMap.values().forEach(this::ensureVisibility);
        // TODO Make config whether or no to remove lost keys
        missingTrackers.forEach(progressTracker::remove);
    }

    private void validateVisibility() {
        if (ticksSinceUpdate == 0)
            return;

        ticksSinceUpdate = 0;
        conditionallyVisible.iterator().forEachRemaining(this::ensureVisibility);
    }

    private void ensureVisibility(CriterionTracker trackable) {
        boolean visibleFlag = visible.contains(trackable);
        boolean shouldBeVisible = trackable.isVisibleTo(getOwner());

        if (visibleFlag != shouldBeVisible) {
            if (visibleFlag) {
                visible.remove(trackable);
                conditionallyVisible.remove(trackable);
            } else {
                visible.add(trackable);
                if (trackable.isConditionallyVisible())
                    conditionallyVisible.add(trackable);
            }
            progressChanged.add(trackable);
        }
    }

    public void flushDirty() {
        // What is firstSync doing for us?
        if (firstSync || !progressChanged.isEmpty()) {
            LOGGER.info("Syncing data from {} to players", owner);
            validateVisibility();
            Set<CriterionTracker> toAdd = new HashSet<>(progressChanged);
            toAdd.retainAll(visible);

            Set<ResourceLocation> toRemove = new HashSet<>();
            for (CriterionTracker tracker : progressChanged) {
                if (!visible.contains(tracker))
                    toRemove.add(tracker.getRegistryName());
            }

            Map<ResourceLocation, ProgressTracker> progressUpdate = new HashMap<>();
            progressChanged.forEach(progressTracker -> {
                if (visible.contains(progressTracker))
                    progressUpdate.put(progressTracker.getRegistryName(), getProgress(progressTracker));
            });

            // TODO Alert (event?) that progress has been changed so that it can be acted upon
            // TODO Let client know about updates even if they can't see it?
            process(firstSync, toAdd, toRemove, progressUpdate);

            progressChanged.clear();
            firstSync = false;
        } else if (ticksSinceUpdate++ >= updateTicks)
            validateVisibility();
    }

    protected void process(boolean firstSync, Set<CriterionTracker> toAdd, Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressUpdate) {
    }

}
