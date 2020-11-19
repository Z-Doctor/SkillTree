package zdoctor.mcskilltree.api;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillData;

import javax.annotation.Nullable;
import java.util.Objects;

public interface ISkill {

    default Skill getSkill() {
        return (Skill) this;
    }

    default int getTier(SkillData data) {
        return !data.hasTag() ? 0 : data.getTag().getInt("Tier");
    }

    default void setTier(SkillData data, int tier) {
        data.getTag().putInt("Tier", Math.max(0, tier));
    }

    default boolean isActive(SkillData data) {
        return data.hasTag() && data.getTag().getBoolean("Active");
    }

    default void setActive(SkillData data, boolean active) {
        data.getTag().putBoolean("Active", active);
    }

    default ICapabilityProvider initCapabilities(SkillData data, @Nullable CompoundNBT nbt) {
        return null;
    }

    default String getName() {
        return Objects.requireNonNull(getSkill().getRegistryName(), "Tried to get name of unregistered skill").toString();
    }

    /**
     * Applies logic after the skill is bought and cost is deducted
     *
     * @param handler - The handler that bought the skill
     */
    default void onBuy(ISkillHandler handler, boolean firstBuy) {
        if(firstBuy) {
            handler.setTier(getSkill(), 1);
            handler.setActive(getSkill(), true);
        }
    }

    enum Type {
        ACTIVE,
        PASSIVE
    }
}
