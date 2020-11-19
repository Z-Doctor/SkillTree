package zdoctor.mcskilltree.api;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface ISkillHandler extends ICapabilitySerializable<CompoundNBT>, Capability.IStorage<ISkillHandler> {

    void setOwner(LivingEntity entity);

    LivingEntity getOwner();

    boolean isDirty();

    void markDirty();

    void markClean();

    void setSkillPoints(int amount);

    int getSkillPoints();

    boolean addSkillPoints(int amount);

    /**
     * Should attempt to deduct skill points from handler and only effect the amount
     * if there is enough or if @force is true
     *
     * @param amount - The Amount to deduct
     * @param force  - Whether to force deduction
     * @return If points were taken
     */
    boolean deductSkillPoints(int amount, boolean force);

    boolean give(Skill skill);

    boolean revoke(Skill skill);

    boolean hasSkill(Skill skill);

    SkillData getData(Skill skill);

    boolean isActive(Skill skill);

    void setActive(Skill skill, boolean active);

    int getTier(Skill skill);

    void setTier(Skill skill, int tier);

    boolean hasRequirements(Skill skill);

    void updateSkillData();

    boolean canBuySkill(Skill skill);

    boolean buySkill(Skill skill);

    void onPlayerRespawn(ISkillHandler oldSkillHandler);

    ISkillHandler EMPTY = new ISkillHandler() {
        @Override
        public void setOwner(LivingEntity entity) {

        }

        @Override
        public LivingEntity getOwner() {
            return null;
        }

        @Override
        public boolean isDirty() {
            return false;
        }

        @Override
        public void markDirty() {

        }

        @Override
        public void markClean() {

        }

        @Override
        public void setSkillPoints(int amount) {

        }

        @Override
        public int getSkillPoints() {
            return 0;
        }

        @Override
        public boolean addSkillPoints(int amount) {
            return false;
        }

        @Override
        public boolean deductSkillPoints(int amount, boolean force) {
            return force;
        }

        @Override
        public boolean give(Skill skill) {
            return false;
        }

        @Override
        public boolean revoke(Skill skill) {
            return false;
        }

        @Override
        public boolean hasSkill(Skill skill) {
            return false;
        }

        @Override
        public SkillData getData(Skill skill) {
            return null;
        }

        @Override
        public int getTier(Skill skill) {
            return 0;
        }

        @Override
        public void setTier(Skill skill, int tier) {

        }

        @Override
        public boolean isActive(Skill skill) {
            return false;
        }

        @Override
        public void setActive(Skill skill, boolean active) {

        }


        @Override
        public boolean hasRequirements(Skill skill) {
            return false;
        }

        @Override
        public void updateSkillData() {

        }

        @Override
        public boolean canBuySkill(Skill skill) {
            return false;
        }

        @Override
        public boolean buySkill(Skill skill) {
            return false;
        }

        @Override
        public void onPlayerRespawn(ISkillHandler oldSkillHandler) {

        }

        @Nullable
        @Override
        public INBT writeNBT(Capability<ISkillHandler> capability, ISkillHandler instance, Direction side) {
            return null;
        }

        @Override
        public void readNBT(Capability<ISkillHandler> capability, ISkillHandler instance, Direction side, INBT nbt) {

        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return null;
        }

        @Override
        public CompoundNBT serializeNBT() {
            return null;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {

        }
    };

}
