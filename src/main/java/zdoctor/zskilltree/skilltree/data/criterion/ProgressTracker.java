package zdoctor.zskilltree.skilltree.data.criterion;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ProgressTracker implements Comparable<ProgressTracker> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String[][] EMPTY = {};

    private final Map<String, CriterionProgress> criteria = new HashMap<>();
    private String[][] requirements;
    private boolean sendUpdatesToClient;
    private boolean cachedDone;

    public ProgressTracker() {
        requirements = EMPTY;
    }

    public static ProgressTracker fromNetwork(PacketBuffer buffer) {
        ProgressTracker progressTracker = new ProgressTracker();
        int i = buffer.readVarInt();

        for (int j = 0; j < i; ++j)
            progressTracker.criteria.put(buffer.readString(), CriterionProgress.read(buffer));

        progressTracker.isDone();

        return progressTracker;
    }

    public boolean sendsUpdatesToClient() {
        return sendUpdatesToClient;
    }

    public void enableUpdates() {
        sendUpdatesToClient = true;
    }

    public void disableUpdates() {
        sendUpdatesToClient = false;
    }

    public void update(Map<String, Criterion> criteriaIn, String[][] requirements) {
        Set<String> set = criteriaIn.keySet();
        // Removes criteria not in the list
        this.criteria.entrySet().removeIf(criterion -> !set.contains(criterion.getKey()));

        for (String key : set) {
            if (!criteria.containsKey(key)) {
                criteria.put(key, new CriterionProgress());
            }
        }

        requirements = requirements == null ? EMPTY : requirements;

        this.requirements = requirements;

        if (criteria.isEmpty() && this.requirements.length > 0) {
            LOGGER.error("Correcting: Empty Criterion with non-empty requirements: {}", Arrays.deepToString(this.requirements));
            this.requirements = EMPTY;
        } else {
            LOGGER.trace("requirements are null, is this intended? Creating from requirements");
            this.requirements = IRequirementsStrategy.AND.createRequirements(criteria.keySet());
        }

        cachedDone = isDone();
    }

    public boolean grant() {
        if (isDone())
            return false;
        criteria.values().forEach(CriterionProgress::obtain);
        return isDone();
    }


    public boolean revoke() {
        if(!isDone())
            return false;
        boolean flag = false;
        for (CriterionProgress progress : this.criteria.values())
            if (progress.isObtained()) {
                flag = true;
                progress.reset();
            }
        return flag;
    }

    public boolean resetProgress() {
        boolean flag = false;
        for (CriterionProgress progress : this.criteria.values())
            if (progress.isObtained()) {
                flag = true;
                cachedDone = false;
                progress.reset();
            }
        return flag;
    }

    public boolean lazyIsDone() {
        return cachedDone;
    }

    public boolean isDone() {
        for (String[] requirement : requirements) {
            boolean flag = false;
            for (String s : requirement) {
                CriterionProgress progress = getCriterionProgress(s);
                if (progress != null && progress.isObtained()) {
                    flag = true;
                    break;
                }
            }

            if (!flag)
                return cachedDone = false;
        }
        return cachedDone = true;
    }

    public boolean hasProgress() {
        for (CriterionProgress criterionprogress : this.criteria.values())
            if (criterionprogress.isObtained())
                return true;

        return false;
    }

    public boolean grantCriterion(String criterionIn) {
        CriterionProgress criterionprogress = this.criteria.get(criterionIn);
        if (criterionprogress != null && !criterionprogress.isObtained()) {
            criterionprogress.obtain();
            return true;
        } else
            return false;
    }

    public boolean revokeCriterion(String criterionIn) {
        CriterionProgress criterionprogress = this.criteria.get(criterionIn);
        if (criterionprogress != null && criterionprogress.isObtained()) {
            criterionprogress.reset();
            return true;
        } else
            return false;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();

        ListNBT progressList = new ListNBT();
        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            CompoundNBT data = new CompoundNBT();

            data.put("key", StringNBT.valueOf(entry.getKey()));
            CriterionProgress progress = entry.getValue();
            if (progress.isObtained()) {
                data.put("obtained", ByteNBT.valueOf(true));
                data.put("obtained-date", LongNBT.valueOf(progress.getObtained().getTime()));
            } else
                data.put("obtained", ByteNBT.valueOf(false));
            progressList.add(data);
        }
        compoundNBT.put("progress_data", progressList);

//        ListNBT requirementList = new ListNBT();
//        for (String[] requirement : requirements) {
//            ListNBT requirements = new ListNBT();
//            for (String s : requirement) {
//                requirements.add(StringNBT.valueOf(s));
//            }
//            requirementList.add(requirements);
//        }
//        compoundNBT.put("requirement_data")
        return compoundNBT;
    }

    public void deserializeNBT(CompoundNBT compoundNBT) {
        this.criteria.clear();
        ListNBT progressList = compoundNBT.getList("progress_data", Constants.NBT.TAG_COMPOUND);
        for (INBT inbt : progressList) {
            if (!(inbt instanceof CompoundNBT))
                continue;
            CompoundNBT data = (CompoundNBT) inbt;
            String key = data.getString("key");
            CriterionProgress progress = new CriterionProgress();
            if (data.getBoolean("obtained")) {
                progress.obtain();
                progress.getObtained().setTime(data.getLong("obtained-date"));
            }
            criteria.put(key, progress);
        }
    }

    public void serializeToNetwork(PacketBuffer buffer) {
        buffer.writeVarInt(this.criteria.size());

        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            buffer.writeString(entry.getKey());
            entry.getValue().write(buffer);
        }
    }

    @Nullable
    public CriterionProgress getCriterionProgress(String criterionIn) {
        return criteria.get(criterionIn);
    }

    @OnlyIn(Dist.CLIENT)
    public float getPercent() {
        if (this.criteria.isEmpty())
            return 1;
        else {
            return (float) requirements.length / countCompletedRequirements();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public String getProgressText() {
        if (criteria.isEmpty())
            return "Completed";
        else
            return requirements.length + "/" + countCompletedRequirements();
    }

    @OnlyIn(Dist.CLIENT)
    private int countCompletedRequirements() {
        int count = 0;
        for (String[] list : requirements) {
            for (String requirement : list) {
                CriterionProgress progress = getCriterionProgress(requirement);
                if (progress != null && progress.isObtained()) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public List<String> getRemainingCriteria() {
        return criteria.entrySet().stream().filter(entry -> !entry.getValue().isObtained()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<String> getCompletedCriteria() {
        return criteria.entrySet().stream().filter(entry -> entry.getValue().isObtained()).map(Map.Entry::getKey).collect(Collectors.toList());
    }

    @Nullable
    public Date getFirstProgressDate() {
        Date date = null;
        for (CriterionProgress criterionprogress : this.criteria.values()) {
            if (criterionprogress.isObtained() && (date == null || criterionprogress.getObtained().before(date))) {
                date = criterionprogress.getObtained();
            }
        }

        return date;
    }

    @Override
    public String toString() {
        return "ProgressTracker{" +
                "criteria=" + criteria +
                ", requirements=" + Arrays.deepToString(requirements) +
                '}';
    }

    @Override
    public int compareTo(ProgressTracker other) {
        Date date = this.getFirstProgressDate();
        Date date1 = other.getFirstProgressDate();
        if (date == null && date1 != null) {
            return 1;
        } else if (date != null && date1 == null) {
            return -1;
        } else {
            return date == null ? 0 : date.compareTo(date1);
        }
    }

}
