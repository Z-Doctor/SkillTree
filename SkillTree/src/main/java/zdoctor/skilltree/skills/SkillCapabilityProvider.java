package zdoctor.skilltree.skills;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import zdoctor.skilltree.api.skills.ISkillHandler;

public class SkillCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
	@CapabilityInject(ISkillHandler.class)
	public static Capability<ISkillHandler> SKILL_CAPABILITY = null;

	private Object cap;

	public SkillCapabilityProvider() {
		cap = new SkillHandler();
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == SKILL_CAPABILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability != SKILL_CAPABILITY)
			return null;

		return cap != null ? (T) cap : null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound capTag = ((ISkillHandler) cap).serializeNBT();
		return capTag;
	}

	@Override
	public void deserializeNBT(NBTTagCompound capTag) {
		((ISkillHandler) cap).deserializeNBT(capTag);
	}

}
