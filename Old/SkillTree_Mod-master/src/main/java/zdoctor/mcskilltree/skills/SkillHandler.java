package zdoctor.mcskilltree.skills;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.IEffectSkill;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.registries.SkillTreePacketRegister;
import zdoctor.mcskilltree.skilltree.packet.SkillTreePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class SkillHandler implements ISkillHandler {
    @CapabilityInject(ISkillHandler.class)
    protected static Capability<ISkillHandler> SKILL_CAPABILITY = null;

    /**
     * Holds skills player has obtained
     **/
    protected final Map<Skill, SkillData> Skill_Codex = Maps.newHashMap();
    protected LivingEntity owner;
    protected int skillPoints;

    protected boolean dirty;

    public SkillHandler() {
        this(null);
    }

    public SkillHandler(LivingEntity entity) {
        if (entity != null)
            setOwner(entity);
    }

    @Override
    public void setOwner(LivingEntity entity) {
        if (owner == null) {
            if (entity != null)
                owner = entity;
        } else
            McSkillTree.LOGGER.error("Tried to reassign owner of skill handler.");
    }

    @Override
    public LivingEntity getOwner() {
        return owner;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void markDirty() {
        dirty = true;
    }

    @Override
    public void markClean() {
        dirty = false;
    }

    @Override
    public void setSkillPoints(int amount) {
        int oldAmount = skillPoints;
        this.skillPoints = amount;
        if (oldAmount != skillPoints)
            markDirty();
    }

    @Override
    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public boolean addSkillPoints(int amount) {
        setSkillPoints(getSkillPoints() + amount);
        return true;
    }

    @Override
    public boolean deductSkillPoints(int amount, boolean force) {
        if (getSkillPoints() >= amount || force) {
            setSkillPoints(Math.max(0, getSkillPoints() - amount));
            return true;
        }
        return false;
    }

    @Override
    public boolean give(Skill skill) {
        if (hasSkill(skill))
            return false;
        SkillData skillData = new SkillData(this, skill);
        skillData.setTier(1);
        skillData.setActive(true);
        Skill_Codex.put(skill, skillData);

        if (isServer()) {
            // TODO Maybe remove this and put somewhere else
            if (skill instanceof IEffectSkill)
                ((IEffectSkill) skill).applySkill(getOwner());
        }
        markDirty();

        return true;
    }

    @Override
    public boolean revoke(Skill skill) {
        if (!hasSkill(skill))
            return false;

        if (skill instanceof IEffectSkill) {
            ((IEffectSkill) skill).removeSkill(getOwner());
        }
        markDirty();

        Skill_Codex.remove(skill);

        return true;
    }

    @Override
    public boolean hasSkill(Skill skill) {
        return Skill_Codex.containsKey(skill);
    }

    @Override
    public SkillData getData(Skill skill) {
        return Skill_Codex.getOrDefault(skill, new SkillData(this, skill));
    }

    @Override
    public boolean isActive(Skill skill) {
        return hasSkill(skill) && Skill_Codex.get(skill).isActive();
    }

    @Override
    public void setActive(Skill skill, boolean active) {
        if (!hasSkill(skill))
            return;
        Skill_Codex.get(skill).setActive(active);
    }

    @Override
    public int getTier(Skill skill) {
        return !hasSkill(skill) ? 0 : Skill_Codex.get(skill).getTier();
    }

    @Override
    public void setTier(Skill skill, int tier) {
        if (!hasSkill(skill))
            return;
        Skill_Codex.get(skill).setTier(tier);
    }

    @Override
    public boolean hasRequirements(Skill skill) {
        return skill.getRequirements().stream().allMatch(requirement -> requirement.test(this));
    }

    @Override
    public boolean buySkill(Skill skill) {
        if (!canBuySkill(skill))
            return false;
        if (!deductSkillPoints(skill.getCost(this), false))
            return false;
        if (hasSkill(skill))
            skill.onBuy(this, false);
        else {
            give(skill);
            skill.onBuy(this, true);
        }
        if (!isServer()) {
            SkillTreePacketRegister.INSTANCE.sendToServer(new SkillTreePacket(SkillTreePacket.Type.BUY, skill));
        }
        return true;
    }

    @Override
    public boolean canBuySkill(Skill skill) {
        return skill.canBuy(this);
    }

    @Override
    public void updateSkillData() {
        if (getOwner() == null)
            return;

        if (!isServer())
            SkillTreePacketRegister.INSTANCE.sendToServer(new SkillTreePacket(SkillTreePacket.Type.QUERY));
        else if (getOwner() instanceof ServerPlayerEntity) {
            if (((ServerPlayerEntity) getOwner()).connection == null) {
                McSkillTree.LOGGER.error("Tried to send an update request before connection was established.");
                return;
            }
            SkillTreePacketRegister.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) getOwner()),
                    new SkillTreePacket(SkillTreePacket.Type.UPDATE, getOwner()));
            markClean();
        }

    }

    public boolean isServer() {
        if (getOwner() == null)
            return false;

        return !getOwner().world.isRemote;
    }


    public void addPoints(int points) {

    }

    public boolean isSkillActive(Skill skill) {
        return false;
    }

    public void setSkillActive(Skill skill, boolean active) {

    }

    public int getSkillTier(Skill skill) {
        return 0;
    }

    public void addSkillTier(Skill skill) {

    }

    public void addSkillTier(Skill skill, int amount) {

    }


    @Override
    public void onPlayerRespawn(ISkillHandler oldSkillHandler) {
        deserializeNBT(oldSkillHandler.serializeNBT());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap != SkillCapability.SKILL_CAPABILITY)
            return LazyOptional.empty();
        return LazyOptional.of(() -> this).cast();
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT entityData = new CompoundNBT();
        entityData.putInt("skill_points", getSkillPoints());

        ListNBT skillDataList = new ListNBT();
        for (SkillData skillData : Skill_Codex.values()) {
            CompoundNBT tag = new CompoundNBT();
//            tag.putString("skill_name", skillData.getSkill().getName());
//            skillData.writeAdditional(tag);
            skillData.write(tag);
            skillDataList.add(tag);
        }
        entityData.put("skills", skillDataList);

        return entityData;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        List<Skill> missing = Lists.newArrayList(Skill_Codex.keySet());

        setSkillPoints(nbt.getInt("skill_points"));
        ListNBT skillDataList = nbt.getList("skills", Constants.NBT.TAG_COMPOUND);
        for (INBT inbt : skillDataList) {
            CompoundNBT skillDataTag = (CompoundNBT) inbt;
//            ResourceLocation key = new ResourceLocation(skillDataTag.getString("skill_name"));
//            Skill skill = SkillTreeRegistries.SKILLS.getValue(key);
            SkillData data = SkillData.read(this, skillDataTag);
            if (data == SkillData.EMPTY) {
                McSkillTree.LOGGER.error("Could not find skill, skipping: {}", skillDataTag);
            } else {
                Skill skill = data.getSkill();
                if (give(skill)) {
                    McSkillTree.LOGGER.debug("New Skill {}", skill.getName());
//                    SkillData skillData = Skill_Codex.get(skill);
                    Skill_Codex.put(skill, data);
                } else {
                    missing.remove(skill);
                    McSkillTree.LOGGER.debug("Reloaded Skill {}", skill.getName());
                }
            }
        }

        for (Skill skill : missing) {
            revoke(skill);
            McSkillTree.LOGGER.debug("Removing Skill {}", skill.getRegistryName());
        }

    }

    // TODO Figure out how to use these or if I need them
    @Nullable
    @Override
    public INBT writeNBT(Capability<ISkillHandler> capability, ISkillHandler instance, Direction side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<ISkillHandler> capability, ISkillHandler instance, Direction side, INBT nbt) {
        if (nbt instanceof CompoundNBT)
            instance.deserializeNBT((CompoundNBT) nbt);
    }
}
