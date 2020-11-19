package zdoctor.mcskilltree.skills;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.IRegistryDelegate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.mcskilltree.api.ISkill;
import zdoctor.mcskilltree.api.ISkillGetter;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.registries.SkillTreeRegistries;

import javax.annotation.Nullable;
import java.util.Map;

public class SkillData extends CapabilityProvider<SkillData> {
    private static final Logger LOGGER = LogManager.getLogger();
    public static SkillData EMPTY = new SkillData(ISkillHandler.EMPTY, Skill.NONE);

    private final Skill skill;
    private final ISkillHandler handler;
    private CompoundNBT tag;
    private final CompoundNBT capNBT;
    private int tier;
    private IRegistryDelegate<Skill> delegate;
    private Map<ResourceLocation, ISkillGetter<?>> properties;

    public SkillData(ISkill skillIn) {
        this(ISkillHandler.EMPTY, skillIn, null);
    }

    public SkillData(ISkillHandler handler, ISkill skillIn) {
        this(handler, skillIn, null);
    }

    public SkillData(ISkillHandler handler, ISkill skillIn, CompoundNBT capNBT) {
        super(SkillData.class);
        this.handler = handler;
        this.skill = skillIn == null ? Skill.NONE : skillIn.getSkill();
        setTag(new CompoundNBT());
        this.capNBT = capNBT;
        skillInit();
    }

    private SkillData(ISkillHandler handler, CompoundNBT compound) {
        super(SkillData.class);
        this.handler = handler;
        this.skill = SkillTreeRegistries.SKILLS.getValue(new ResourceLocation(compound.getString("id")));
        if (this.skill == Skill.NONE) {
            LOGGER.warn("Unable to find skill {}", compound.getString("id"));
        }
        if (compound.contains("tag", 10)) {
            setTag(compound.getCompound("tag"));
        }
        this.capNBT = compound.contains("SkillCaps") ? compound.getCompound("SkillCaps") : null;

        skillInit();
    }

    protected void skillInit() {
        Skill skill = getRawSkill();
        if (skill != Skill.NONE) {
            this.delegate = skill.delegate;
            ICapabilityProvider provider = skill.initCapabilities(this, this.capNBT);
            this.gatherCapabilities(provider);
            if (this.capNBT != null)
                deserializeCaps(this.capNBT);

            properties = skill.getProperties();
        }
    }

    public <R> R getProperty(ResourceLocation property) {
        if (properties.containsKey(property))
            return (R) properties.get(property).get(getSkill(), handler);
        else
            return null;
    }

    public boolean hasProperty(ResourceLocation property) {
        return properties.containsKey(property);
    }

    public Skill getSkill() {
        return this == EMPTY || this.delegate == null ? Skill.NONE : this.delegate.get();
    }

    public Skill getRawSkill() {
        return skill;
    }

    public int getTier() {
        return getSkill().getTier(this);
    }

    public void setTier(int tier) {
        getSkill().setTier(this, tier);
    }

    public boolean isActive() {
        return getSkill().isActive(this);
    }

    public void setActive(boolean active) {
        getSkill().setActive(this, active);
    }

    public boolean hasTag() {
        return this != EMPTY && this.tag != null && !this.tag.isEmpty();
    }

    public CompoundNBT getTag() {
        return this.tag;
    }

    public void setTag(@Nullable CompoundNBT nbt) {
        this.tag = nbt;
        updateTag();
    }

    public void updateTag() {
        this.setTier(this.getTier());
        this.setActive(this.isActive());
    }

    public CompoundNBT write(CompoundNBT nbt) {
        ResourceLocation resourcelocation = SkillTreeRegistries.SKILLS.getKey(getSkill());
        resourcelocation = resourcelocation == null ? SkillTreeRegistries.SKILLS.getDefaultKey() : resourcelocation;
        nbt.putString("id", resourcelocation.toString());
        if (this.tag != null) {
            nbt.put("tag", this.tag.copy());
        }
        CompoundNBT cnbt = this.serializeCaps();
        if (cnbt != null && !cnbt.isEmpty()) {
            nbt.put("SkillCaps", cnbt);
        }
        return nbt;
    }

    public static SkillData read(ISkillHandler handler, CompoundNBT compound) {
        try {
            SkillData data = new SkillData(handler, compound);
            return data.getSkill() == null ? EMPTY : data;
        } catch (RuntimeException runtimeexception) {
            LOGGER.debug("Tried to load invalid skill: {}", compound, runtimeexception);
            return EMPTY;
        }
    }

    public boolean areSkillsEqual(SkillData other) {
        return other != EMPTY && this.getSkill() == other.getSkill();
    }

    public boolean isDataEqual(SkillData other) {
        if (!areSkillsEqual(other))
            return false;
        else if (this.tag == null && other.tag != null)
            return false;
        return this.tag == null || this.tag.equals(other.tag) && this.areCapsCompatible(other);
    }


}
