package zdoctor.zskilltree.handlers;

import net.minecraft.advancements.Criterion;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.ModMain;
import zdoctor.zskilltree.api.interfaces.ISkillTreeTracker;
import zdoctor.zskilltree.api.interfaces.ITrackCriterion;
import zdoctor.zskilltree.criterion.ProgressTracker;
import zdoctor.zskilltree.network.play.server.SSkillPageInfoPacket;
import zdoctor.zskilltree.skill.SkillTreeDataManager;

import java.util.*;
import java.util.stream.Collectors;

public class SkillTreeTracker implements ISkillTreeTracker {
    protected static final Logger LOGGER = LogManager.getLogger();

    protected final Set<ITrackCriterion> visible = new HashSet<>();
    protected final Set<ITrackCriterion> visibilityChanged = new HashSet<>();
    protected final Set<ITrackCriterion> progressChanged = new HashSet<>();
    protected final HashMap<ITrackCriterion, ProgressTracker> progressTracker = new HashMap<>();

    protected boolean firstSync = true;
    protected LivingEntity owner;

    public SkillTreeTracker() {
    }

    public SkillTreeTracker(LivingEntity entity) {
        owner = entity;
        LOGGER.debug("Attached Skill Tree to {}", owner);
    }

    /**
     * Checks to see if the trackable exists without registering one if missing
     */
    @Override
    public boolean contains(ITrackCriterion trackable) {
        return progressTracker.get(trackable) != null;
    }

    /**
     * Checks to see if the trackable exists and has progress
     */
    @Override
    public boolean hasProgress(ITrackCriterion trackable) {
        ProgressTracker progress = progressTracker.get(trackable);
        return progress != null && progress.hasProgress();
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public boolean startProgress(ITrackCriterion trackable) {
        ProgressTracker progress = getProgress(trackable);
        if (progress != null)
            return false;
        startProgress(trackable, new ProgressTracker());
        return true;
    }

    @Override
    public ProgressTracker stopTracking(ITrackCriterion trackable) {
        return progressTracker.remove(trackable);
    }

    protected void startProgress(ITrackCriterion trackable, ProgressTracker progress) {
        progress.update(trackable.getCriteria(), trackable.getRequirements());
        progressTracker.put(trackable, progress);
        progressChanged.add(trackable);
    }

    @Override
    public void update(ITrackCriterion trackable, Map<String, Criterion> criterion, String[][] requirements) {
        ProgressTracker progress = getProgress(trackable);
        if (progress != null) {
            progress.update(criterion, requirements);
        }
    }

    @Override
    public ProgressTracker getProgress(ITrackCriterion trackable) {
        return progressTracker.get(trackable);
    }

    @Override
    public boolean grant(ITrackCriterion trackable) {
        ProgressTracker progress = getProgress(trackable);
        if (progress.isDone())
            return false;
        progress.grant();
        progressChanged.add(trackable);
        if (progress.isDone())
            onProgressCompleted(trackable);
        else {
            LOGGER.error("Unable to grant {}", trackable);
            return false;
        }
        return true;
    }

    @Override
    public boolean revoke(ITrackCriterion trackable) {
        ProgressTracker progress = getProgress(trackable);
        boolean flag = progress.isDone();
        if (!progress.hasProgress())
            return false;
        progress.revoke();
        progressChanged.add(trackable);
        if (flag)
            onProgressRevoked(trackable);
        return true;
    }

    @Override
    public boolean grantCriterion(ITrackCriterion trackable, String criterionKey) {
        ProgressTracker progress = getProgress(trackable);
        if (progress.isDone())
            return false;
        if (progress.grantCriterion(criterionKey)) {
            progressChanged.add(trackable);
            if (progress.isDone())
                onProgressCompleted(trackable);
        }
        return true;
    }

    @Override
    public boolean revokeCriterion(ITrackCriterion trackable, String criterionKey) {
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

    protected void onProgressCompleted(ITrackCriterion trackable) {
        // TODO Add toast and chat announcement to options
        // TODO Event for when a skill page is unlocked
//        MinecraftForge.EVENT_BUS.post(new SkillPageEvent.SkillPageGrantedEvent(owner, page));
    }

    protected void onProgressRevoked(ITrackCriterion trackable) {
//        MinecraftForge.EVENT_BUS.post(new SkillPageEvent.SkillPageRevokedEvent(owner, page));
    }

    public <T> List<T> get(Class<T> filter) {
        return visible.stream().filter(filter::isInstance).map(filter::cast).collect(Collectors.toList());
    }

    @Override
    public Iterable<ITrackCriterion> getTrackers() {
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

        skillTreeData.put("progressList", progressList);
        for (Map.Entry<ITrackCriterion, ProgressTracker> entry : progressTracker.entrySet()) {
            ProgressTracker progress = entry.getValue();
            if (!progress.hasProgress())
                continue;
            CompoundNBT progressNbt = new CompoundNBT();
            progressNbt.putString("id", entry.getKey().getId().toString());
            progressNbt.put("criterion", progress.serializeNBT());

            progressList.add(progressNbt);
        }

        return skillTreeData;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        dispose();
        reset();
        final Map<ResourceLocation, ITrackCriterion> allEntries = new HashMap<>();
        allEntries.putAll(ModMain.getInstance().getSkillPageManager().getAllEntries());
        allEntries.putAll(ModMain.getInstance().getSkillManager().getAllEntries());

        nbt.getList("progressList", Constants.NBT.TAG_COMPOUND).stream().map(tag -> (CompoundNBT) tag).forEach(data -> {
            ResourceLocation id = new ResourceLocation(data.getString("id"));
            ITrackCriterion trackable = allEntries.get(id);
            ProgressTracker progress = getProgress(trackable);
            if (progress == null)
                startProgress(trackable);
            else {
                progress.deserializeNBT(data.getCompound("criterion"));
                progress.update(trackable.getCriteria(), trackable.getRequirements());
            }
        });
    }


    @Override
    public void dispose() {
        progressTracker.clear();
    }

    protected void reset() {
        visible.clear();
        visibilityChanged.clear();
        progressChanged.clear();

        firstSync = true;
    }

    @Override
    public void read(SSkillPageInfoPacket packetIn) {
        if (packetIn == null)
            LOGGER.trace("Server-Sided Skill Tree Tracker tried to read a null packet");
        else if (getOwner() == null)
            LOGGER.trace("Server-Sided Skill Tree Tracker has a null owner");
        else
            LOGGER.info("Server-Sided Skill Tracker owned by {} was fed a packet", getOwner().getDisplayName().getString());
    }

    protected void checkPageVisibility() {
        boolean visibleFlag;
        boolean doneFlag;
        ProgressTracker progress;

        for (ITrackCriterion trackable : progressChanged) {
            visibleFlag = visible.contains(trackable);
            progress = getProgress(trackable);
            doneFlag = progress != null && progress.isDone();

            if (visibleFlag && !doneFlag) {
                visible.remove(trackable);
                visibilityChanged.add(trackable);
            } else if (!visibleFlag && doneFlag) {
                visible.add(trackable);
                visibilityChanged.add(trackable);
            }
        }
    }

    @Override
    public void reload() {
        reset();
        HashSet<ITrackCriterion> missingTrackers = new HashSet<>(progressTracker.keySet());
        for (ITrackCriterion trackable : SkillTreeDataManager.getAllTrackers().values()) {
            missingTrackers.remove(trackable);
            ProgressTracker progress = getProgress(trackable);
            if (progress == null)
                startProgress(trackable);
            else {
                progress.update(trackable.getCriteria(), trackable.getRequirements());
                progressChanged.add(trackable);
            }
        }
        visibilityChanged.addAll(missingTrackers);
        missingTrackers.forEach(progressTracker::remove);
    }

    public void flushDirty() {
        if (owner == null)
            return;

        // TODO Send data to clients about what pages and skills the owner of this has but only if
        //  the skill or skill page is specified as one that should be tracked by other players
        //  (Default: false)
        if (firstSync || !progressChanged.isEmpty()) {
            LOGGER.info("Syncing data from {} to players", owner);
            checkPageVisibility();

            firstSync = false;
            progressChanged.clear();
            visibilityChanged.clear();
        }
    }


}
