package zdoctor.zskilltree.skilltree.trackers;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.Criterion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.CriterionTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.criterion.ProgressTracker;
import zdoctor.zskilltree.skilltree.events.CriterionTrackerEvent;
import zdoctor.zskilltree.skilltree.managers.SkillTreeDataManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SkillTreeTracker implements ISkillTreeTracker {
    protected static final Logger LOGGER = LogManager.getLogger();

    private final Set<CriterionTracker> visible = new HashSet<>();
    private final Set<CriterionTracker> visibilityChanged = new HashSet<>();
    private final Set<CriterionTracker> conditionallyVisible = new HashSet<>();

    private final Set<CriterionTracker> progressChanged = new HashSet<>();
    private final HashMap<CriterionTracker, ProgressTracker> progressTracker = new HashMap<>();

    private final HashMap<ResourceLocation, CriterionTracker> trackableMap = new HashMap<>();

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

    protected void onProgressChanged(CriterionTracker tracker) {
        progressChanged.add(tracker);
    }

    // TODO Perhaps add triggers to the visibility for checking so we don't have to check every tick or something
    //  Or I can delay the time between checks
    private void checkVisibility() {
        boolean visibleFlag;
        boolean shouldBeVisible;
        for (CriterionTracker trackable : conditionallyVisible) {
            visibleFlag = visible.contains(trackable);
            shouldBeVisible = trackable.isVisibleTo(getOwner());

            if (visibleFlag && !shouldBeVisible) {
                visible.remove(trackable);
                visibilityChanged.add(trackable);
            } else if (!visibleFlag && shouldBeVisible) {
                visible.add(trackable);
                visibilityChanged.add(trackable);
            }
        }
    }

    public Set<CriterionTracker> getVisible() {
        return ImmutableSet.copyOf(visible);
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
        return getProgress(tracker).isDone();
    }

    /**
     * Checks to see if the trackable exists and has progress
     */
    @Override
    public boolean has(CriterionTracker tracker) {
        ProgressTracker progress = progressTracker.get(tracker);
        return progress != null && progress.hasProgress();
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public boolean startProgress(CriterionTracker tracker) {
        ProgressTracker progress = getProgress(tracker);
        if (progress != null)
            return false;
        startProgress(tracker, new ProgressTracker());
        return true;
    }

    protected void startProgress(CriterionTracker tracker, ProgressTracker progress) {
        progress.update(tracker.getCriteria(), tracker.getRequirements());
        if (tracker.isConditionallyVisible())
            progress.enableUpdates();
        else
            progress.disableUpdates();

        progressTracker.put(tracker, progress);
        onProgressChanged(tracker);
    }

    @Override
    public void update(CriterionTracker trackable, Map<String, Criterion> criterion, String[][] requirements) {
        ProgressTracker progress = getProgress(trackable);
        if (progress != null) {
            progress.update(criterion, requirements);
        }
    }

    @Override
    public ProgressTracker getProgress(CriterionTracker tracker) {
        return progressTracker.get(tracker);
    }

    @Override
    public boolean grant(CriterionTracker tracker) {
        ProgressTracker progress = getProgress(tracker);
        if (progress.isDone())
            return false;
        progress.grant();
        onProgressChanged(tracker);
        if (progress.isDone())
            onProgressCompleted(tracker);
        else {
            LOGGER.error("Unable to grant {}", tracker);
            return false;
        }
        return true;
    }

    @Override
    public boolean revoke(CriterionTracker tracker) {
        ProgressTracker progress = getProgress(tracker);
        if (progress == null || !progress.revoke())
            return false;
        onProgressChanged(tracker);
        onProgressRevoked(tracker);
        return true;
    }

    @Override
    public boolean reset(CriterionTracker tracker) {
        ProgressTracker progress = getProgress(tracker);
        if (progress == null || !progress.hasProgress())
            return false;
        boolean flag = progress.isDone();
        if (!progress.resetProgress())
            return false;
        onProgressChanged(tracker);
        if (flag)
            onProgressRevoked(tracker);
        return true;
    }

    @Override
    public boolean grantCriterion(CriterionTracker tracker, String criterionKey) {
        ProgressTracker progress = getProgress(tracker);
        if (progress.isDone())
            return false;
        if (progress.grantCriterion(criterionKey)) {
            onProgressChanged(tracker);
            if (progress.isDone())
                onProgressCompleted(tracker);
        }
        return true;
    }

    @Override
    public boolean revokeCriterion(CriterionTracker trackable, String criterionKey) {
        ProgressTracker progress = getProgress(trackable);
        boolean flag = progress.isDone();
        if (progress.revokeCriterion(criterionKey)) {
            onProgressChanged(trackable);
            if (flag)
                onProgressRevoked(trackable);
            return true;
        }
        return false;
    }

    @Override
    public Iterable<CriterionTracker> getTrackers() {
        return progressTracker.keySet();
    }

    @Override
    public Iterable<ProgressTracker> getAllProgress() {
        return progressTracker.values();
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
        final Map<ResourceLocation, CriterionTracker> allEntries = new HashMap<>();
        allEntries.putAll(ModMain.getInstance().getSkillPageManager().getAllEntries());
        allEntries.putAll(ModMain.getInstance().getSkillManager().getAllEntries());

        nbt.getList("progress_list", Constants.NBT.TAG_COMPOUND).stream().map(tag -> (CompoundNBT) tag).forEach(data -> {
            ResourceLocation id = new ResourceLocation(data.getString("id"));
            CriterionTracker trackable = allEntries.get(id);
            ProgressTracker progress = new ProgressTracker();
            progress.deserializeNBT(data.getCompound("criterion"));
            startProgress(trackable, progress);
        });
    }


    public void dispose() {
        progressTracker.clear();
    }

    protected void reset() {
        visible.clear();
        visibilityChanged.clear();
        progressChanged.clear();
        conditionallyVisible.clear();
        trackableMap.clear();
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

            for (CriterionTracker trackable : packetIn.getToAdd()) {
                visible.add(trackableMap.compute(trackable.getRegistryName(), (key, old) -> trackable));
            }

            for (ResourceLocation id : packetIn.getToRemove()) {
                progressTracker.keySet().removeIf(key -> key.getRegistryName().equals(id));
                visible.remove(trackableMap.remove(id));
            }

            for (Map.Entry<ResourceLocation, ProgressTracker> entry : packetIn.getProgressChanged().entrySet()) {
                CriterionTracker trackable = trackableMap.get(entry.getKey());
                if (trackable != null)
                    progressTracker.put(trackable, entry.getValue());
                else
                    LOGGER.error("Unable to update progress of {}", entry.getKey());
            }
        }
    }
    // TODO Fix problem when checking if a page is owned because it might be farther down the queue than a skill checking for it

    @Override
    public void reload() {
        reset();
        HashSet<CriterionTracker> missingTrackers = new HashSet<>(progressTracker.keySet());
        for (CriterionTracker trackable : SkillTreeDataManager.getAllTrackers().values()) {
            trackableMap.put(trackable.getRegistryName(), trackable);
            missingTrackers.remove(trackable);
            ProgressTracker progress = getProgress(trackable);
            if (progress == null)
                startProgress(trackable);
            else {
                progress.update(trackable.getCriteria(), trackable.getRequirements());
                onProgressChanged(trackable);
            }
            if (trackable.isConditionallyVisible())
                conditionallyVisible.add(trackable);
            else
                visible.add(trackable);
            visibilityChanged.add(trackable);
        }
        visibilityChanged.addAll(missingTrackers);
        missingTrackers.forEach(progressTracker::remove);
    }

    public void flushDirty() {
        checkVisibility();
        if (firstSync || !visibilityChanged.isEmpty() || !progressChanged.isEmpty()) {
            LOGGER.info("Syncing data from {} to players", owner);

            Set<CriterionTracker> toAdd = new HashSet<>(visibilityChanged);
            toAdd.retainAll(visible);
            visibilityChanged.removeAll(toAdd);

            Set<ResourceLocation> toRemove = new HashSet<>();
            visibilityChanged.forEach(page -> toRemove.add(page.getRegistryName()));

            Map<ResourceLocation, ProgressTracker> progressUpdate = new HashMap<>();
            progressChanged.forEach(progressTracker -> {
                if (visible.contains(progressTracker))
                    progressUpdate.put(progressTracker.getRegistryName(), getProgress(progressTracker));
            });

            toAdd.forEach(tracker -> {
                if (!progressUpdate.containsKey(tracker.getRegistryName()))
                    progressUpdate.put(tracker.getRegistryName(), getProgress(tracker));
            });

//            toAdd.forEach(pr;

            process(firstSync, toAdd, toRemove, progressUpdate);

            firstSync = false;
            progressChanged.clear();
            visibilityChanged.clear();
        }
    }

    protected void process(boolean firstSync, Set<CriterionTracker> toAdd, Set<ResourceLocation> toRemove, Map<ResourceLocation, ProgressTracker> progressUpdate) {
    }

}
