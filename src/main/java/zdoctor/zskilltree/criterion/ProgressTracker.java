package zdoctor.zskilltree.criterion;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
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
    private static final String[][] EMPTY = {{""}};

    private final Map<String, net.minecraft.advancements.CriterionProgress> criteria = new HashMap<>();
    private String[][] requirements;
    private boolean sendUpdatesToClient;

    public ProgressTracker() {
        criteria.put("", new net.minecraft.advancements.CriterionProgress());
        requirements = EMPTY;
    }

    public static ProgressTracker fromNetwork(PacketBuffer buffer) {
        ProgressTracker progressTracker = new ProgressTracker();
        int i = buffer.readVarInt();

        for (int j = 0; j < i; ++j) {
            progressTracker.criteria.put(buffer.readString(), net.minecraft.advancements.CriterionProgress.read(buffer));
        }

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
            if (!this.criteria.containsKey(key)) {
                this.criteria.put(key, new net.minecraft.advancements.CriterionProgress());
            }
        }

        requirements = requirements == null ? EMPTY : requirements;

        this.requirements = requirements;

        if (criteria.isEmpty()) {
            criteria.put("", new net.minecraft.advancements.CriterionProgress());
            if (Arrays.deepEquals(EMPTY, this.requirements)) {
                LOGGER.error("Correcting: Empty Criterion with non-empty requirements");
                this.requirements = EMPTY;
            }
        }
    }

    public boolean grant() {
        if (isDone())
            return false;
        criteria.values().forEach(net.minecraft.advancements.CriterionProgress::obtain);
        return true;
    }

    public boolean revoke() {
        boolean flag = false;
        for (net.minecraft.advancements.CriterionProgress criterionprogress : this.criteria.values())
            if (criterionprogress.isObtained()) {
                flag = true;
                criterionprogress.reset();
            }
        return flag;
    }

    public boolean isDone() {
        for (String[] requirement : requirements) {
            boolean flag = false;
            for (String s : requirement) {
                net.minecraft.advancements.CriterionProgress criterionprogress = this.getCriterionProgress(s);
                if (criterionprogress != null && criterionprogress.isObtained()) {
                    flag = true;
                    break;
                }
            }

            if (!flag)
                return false;
        }
        return true;
    }

    public boolean hasProgress() {
        for (net.minecraft.advancements.CriterionProgress criterionprogress : this.criteria.values())
            if (criterionprogress.isObtained())
                return true;

        return false;
    }

    public boolean grantCriterion(String criterionIn) {
        net.minecraft.advancements.CriterionProgress criterionprogress = this.criteria.get(criterionIn);
        if (criterionprogress != null && !criterionprogress.isObtained()) {
            criterionprogress.obtain();
            return true;
        } else
            return false;
    }

    public boolean revokeCriterion(String criterionIn) {
        net.minecraft.advancements.CriterionProgress criterionprogress = this.criteria.get(criterionIn);
        if (criterionprogress != null && criterionprogress.isObtained()) {
            criterionprogress.reset();
            return true;
        } else
            return false;
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT compoundNBT = new CompoundNBT();

        ListNBT progressList = new ListNBT();
        for (Map.Entry<String, net.minecraft.advancements.CriterionProgress> entry : this.criteria.entrySet()) {
            CompoundNBT data = new CompoundNBT();

            data.put("key", StringNBT.valueOf(entry.getKey()));
            net.minecraft.advancements.CriterionProgress progress = entry.getValue();
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
            net.minecraft.advancements.CriterionProgress progress = new net.minecraft.advancements.CriterionProgress();
            if (data.getBoolean("obtained")) {
                progress.obtain();
                progress.getObtained().setTime(data.getLong("obtained-date"));
            }
            criteria.put(key, progress);
        }
    }

    public void serializeToNetwork(PacketBuffer buffer) {
        buffer.writeVarInt(this.criteria.size());

        for (Map.Entry<String, net.minecraft.advancements.CriterionProgress> entry : this.criteria.entrySet()) {
            buffer.writeString(entry.getKey());
            entry.getValue().write(buffer);
        }
    }

    @Nullable
    public CriterionProgress getCriterionProgress(String criterionIn) {
        return this.criteria.get(criterionIn);
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
