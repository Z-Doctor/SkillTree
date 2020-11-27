package zdoctor.zskilltree.skilltree.data.handlers;

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
import zdoctor.zskilltree.criterion.ProgressTracker;
import zdoctor.zskilltree.network.play.server.SCriterionTrackerSyncPacket;
import zdoctor.zskilltree.skilltree.events.CriterionTrackerEvent;
import zdoctor.zskilltree.skilltree.skill.SkillTreeDataManager;

import java.util.*;
import java.util.stream.Collectors;

public class SkillTreeTracker implements ISkillTreeTracker {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected final Set<CriterionTracker> completed = new HashSet<>();
    protected final Set<CriterionTracker> completionChanged = new HashSet<>();
    protected final Set<CriterionTracker> progressChanged = new HashSet<>();
    protected final HashMap<CriterionTracker, ProgressTracker> progressTracker = new HashMap<>();

    protected final HashMap<ResourceLocation, CriterionTracker> trackableMap = new HashMap<>();

    protected boolean firstSync = true;
    protected LivingEntity owner;

    public SkillTreeTracker() {
    }

    public SkillTreeTracker(LivingEntity entity) {
        if (entity == null)
            throw new NullPointerException("Entity is null");
        owner = entity;
        LOGGER.debug("Attached Skill Tree to {}", owner);
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
    public boolean startProgress(CriterionTracker tracker) {
        ProgressTracker progress = getProgress(tracker);
        if (progress != null)
            return false;
        startProgress(tracker, new ProgressTracker());
        return true;
    }

    protected void startProgress(CriterionTracker tracker, ProgressTracker progress) {
        progress.update(tracker.getCriteria(), tracker.getRequirements());
        if (tracker.shouldClientTrack())
            progress.enableUpdates();
        else
            progress.disableUpdates();

        progressTracker.put(tracker, progress);
        progressChanged.add(tracker);
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
        progressChanged.add(tracker);
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
        if (progress == null || !progress.isDone() || !progress.revoke())
            return false;
        progressChanged.add(tracker);
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
        progressChanged.add(tracker);
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
            progressChanged.add(tracker);
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
            progressChanged.add(trackable);
            if (flag)
                onProgressRevoked(trackable);
            return true;
        }
        return false;
    }

    protected void onProgressCompleted(CriterionTracker trackable) {
        // TODO Add toast and chat announcement to options
        MinecraftForge.EVENT_BUS.post(new CriterionTrackerEvent.ProgressGrantedEvent(owner, trackable));
    }

    protected void onProgressRevoked(CriterionTracker trackable) {
        startProgress(trackable);
        MinecraftForge.EVENT_BUS.post(new CriterionTrackerEvent.ProgressRevokedEvent(owner, trackable));
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
        completed.clear();
        completionChanged.clear();
        progressChanged.clear();
        trackableMap.clear();
        firstSync = true;
    }

    @Override
    public void read(SCriterionTrackerSyncPacket packetIn) {
        if (packetIn == null)
            LOGGER.trace("Server-Sided Skill Tree Tracker tried to read a null packet");
        if (getOwner() == null)
            LOGGER.trace("Server-Sided Skill Tree Tracker has a null owner");
        else
            LOGGER.info("Server-Sided Skill Tracker owned by {} was fed a packet", getOwner().getDisplayName().getString());
    }

    protected void checkPageVisibility() {
        boolean visibleFlag;
        boolean doneFlag;
        ProgressTracker progress;

        for (CriterionTracker trackable : progressChanged) {
            visibleFlag = completed.contains(trackable);
            progress = getProgress(trackable);
            doneFlag = progress != null && progress.isDone();

            if (visibleFlag && !doneFlag) {
                completed.remove(trackable);
                completionChanged.add(trackable);
            } else if (!visibleFlag && doneFlag) {
                completed.add(trackable);
                completionChanged.add(trackable);
            }
        }
    }

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
                progressChanged.add(trackable);
            }
        }
        completionChanged.addAll(missingTrackers);
        missingTrackers.forEach(progressTracker::remove);
    }

    public void flushDirty() {
        if (owner == null)
            return;

        if (firstSync || !progressChanged.isEmpty()) {
            LOGGER.info("Syncing data from {} to players", owner);
            checkPageVisibility();

            firstSync = false;
            progressChanged.clear();
            completionChanged.clear();
        }
    }

}
