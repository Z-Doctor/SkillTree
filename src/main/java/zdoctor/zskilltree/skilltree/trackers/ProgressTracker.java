package zdoctor.zskilltree.skilltree.trackers;

import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ProgressTracker implements Comparable<ProgressTracker> {
    private static final String[][] EMPTY = {};

    private final Map<String, CriterionProgress> criteria = new HashMap<>();
    private String[][] requirements = EMPTY;
    private boolean isDone, isDirty = true;

    public ProgressTracker() {
    }

    public ProgressTracker(PacketBuffer buf) {
        for (int i = buf.readVarInt(); i > 0; i--)
            this.criteria.put(buf.readString(), CriterionProgress.read(buf));
        this.requirements = new String[buf.readVarInt()][];
        for (int i = 0; i < this.requirements.length; i++) {
            this.requirements[i] = new String[buf.readVarInt()];
            for (int j = 0; j < this.requirements[i].length; j++) {
                this.requirements[i][j] = buf.readString();
            }
        }
    }

    public void writeTo(PacketBuffer buffer) {
        buffer.writeVarInt(this.criteria.size());
        for (Map.Entry<String, CriterionProgress> entry : this.criteria.entrySet()) {
            buffer.writeString(entry.getKey());
            entry.getValue().write(buffer);
        }

        buffer.writeVarInt(this.requirements.length);
        for (String[] requirements : this.requirements) {
            buffer.writeVarInt(requirements.length);
            for (String requirement : requirements) {
                buffer.writeString(requirement);
            }
        }
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
        markDirty();
    }

    public void markDirty() {
        isDirty = true;
    }

    public boolean update(Map<String, Criterion> criteriaIn, String[][] requirements) {
        Set<String> set = criteriaIn.keySet();
        // Removes criteria not in the list
        boolean changed = this.criteria.entrySet().removeIf(criterion -> !set.contains(criterion.getKey()));

        for (String key : set) {
            if (!criteria.containsKey(key)) {
                criteria.put(key, new CriterionProgress());
                changed = true;
            }
        }

        requirements = requirements == null ? EMPTY : requirements;

        this.requirements = requirements;

        if(changed)
            markDirty();
        return changed;
    }

    public boolean grant() {
        boolean flag = false;
        for (CriterionProgress progress : this.criteria.values())
            if (!progress.isObtained()) {
                flag = true;
                progress.obtain();
                markDirty();
            }
        return flag;
    }

    public boolean revoke() {
        boolean flag = false;
        for (CriterionProgress progress : this.criteria.values())
            if (progress.isObtained()) {
                flag = true;
                progress.reset();
                markDirty();
            }
        return flag;
    }

    public boolean resetProgress() {
        boolean flag = false;
        for (CriterionProgress progress : this.criteria.values())
            if (progress.isObtained()) {
                flag = true;
                markDirty();
                progress.reset();
            }
        return flag;
    }

    public boolean isDone() {
        if(isDirty) {
            isDone = checkIsDone();
            isDirty = false;
        }
        return isDone;
    }

    // TODO Remove isDoneFast and instead make an internal isDirty check so that way if a change is detected it will update
    //  and if not it will not bother calculating
    private boolean checkIsDone() {
        for (String[] requirements : this.requirements) {
            boolean flag = false;
            for (String requirement : requirements) {
                CriterionProgress progress = getCriterionProgress(requirement);
                if (progress != null && progress.isObtained()) {
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
        for (CriterionProgress criterionprogress : this.criteria.values())
            if (criterionprogress.isObtained())
                return true;

        return false;
    }

    public boolean grantCriterion(String criterionIn) {
        CriterionProgress criterionprogress = this.criteria.get(criterionIn);
        if (criterionprogress != null && !criterionprogress.isObtained()) {
            criterionprogress.obtain();
            markDirty();
            return true;
        } else
            return false;
    }

    public boolean revokeCriterion(String criterionIn) {
        CriterionProgress criterionprogress = this.criteria.get(criterionIn);
        if (criterionprogress != null && criterionprogress.isObtained()) {
            criterionprogress.reset();
            markDirty();
            return true;
        } else
            return false;
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
