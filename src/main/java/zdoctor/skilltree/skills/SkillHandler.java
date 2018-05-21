package zdoctor.skilltree.skills;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.skills.ISkillHandler;
import zdoctor.skilltree.api.skills.ISkillWatcher;
import zdoctor.skilltree.events.SkillDeseralizeEvent;
import zdoctor.skilltree.events.SkillEvent;
import zdoctor.skilltree.events.SkillEvent.ActiveTick;
import zdoctor.skilltree.tabs.SkillTabs;

public class SkillHandler implements ISkillHandler {

	protected ArrayList<SkillBase> trackerCodex = new ArrayList<>();

	protected HashMap<SkillBase, SkillSlot> skillsCodex = new HashMap();
	private EntityLivingBase owner;
	private int skillPoints;

	public SkillHandler() {
		for (SkillTabs tab : SkillTabs.SKILL_TABS) {
			if (tab == null)
				continue;
			for (SkillBase skill : tab.getPage().getSkillList()) {
				if (skill == null)
					continue;
				SkillSlot slot = new SkillSlot(skill);
				skillsCodex.put(skill, slot);
				if (skill instanceof ISkillWatcher)
					trackerCodex.add(skill);
			}
		}
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagList nbtTagList = new NBTTagList();
		skillsCodex.forEach((skill, skillSlot) -> {
			NBTTagCompound skillTag = new NBTTagCompound();
			skillSlot.writeToNBT(skillTag);
			nbtTagList.appendTag(skillTag);
		});
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setTag("Skills", nbtTagList);
		nbt.setInteger("SkillPoints", skillPoints);
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		NBTTagList skillCodex = nbt.getTagList("Skills", Constants.NBT.TAG_COMPOUND);
		for (NBTBase skillBase : skillCodex) {
			NBTTagCompound skillTag = (NBTTagCompound) skillBase;
			SkillSlot skillSlot = new SkillSlot(skillTag);
			SkillDeseralizeEvent event = new SkillDeseralizeEvent(skillTag, skillSlot.getSkill(), skillSlot);
			MinecraftForge.EVENT_BUS.post(event);
			if (event.isCanceled() || event.getResult() == Result.DENY || event.getSkill() == null) {
				ModMain.proxy.log.debug("Did not add skill {}. Event Cancled: {} Result Deny: {} Skill null: {}",
						skillTag, event.isCanceled(), event.getResult() == Result.DENY, event.getSkill() == null);
				return;
			}
			this.skillsCodex.put(skillSlot.getSkill(), skillSlot);
		}
		skillPoints = nbt.getInteger("SkillPoints");
		if (getOwner() != null)
			reloadHandler();
	}

	@Override
	public void setSkillObtained(SkillBase skill, boolean obtained) {
		SkillSlot skillSlot = skillsCodex.get(skill);
		boolean orignalState = skillSlot.isObtained();
		skillSlot.setObtained(obtained);
		if (orignalState != skillSlot.isObtained())
			onSkillChange(skillSlot);
	}

	@Override
	public void setSkillActive(SkillBase skill, boolean active) {
		SkillSlot skillSlot = skillsCodex.get(skill);
		boolean orignalState = skillSlot.isActive();
		skillSlot.setActive(active);
		if (orignalState != skillSlot.isActive())
			onSkillChange(skillSlot);
	}

	@Override
	public void onSkillChange(SkillSlot skillSlot) {
		if (skillSlot.getSkill().getParent() != null) {
			if (!hasSkill(skillSlot.getSkill().getParent())) {
				skillSlot.setObtained(false);
				setSkillActive(skillSlot.getSkill(), false);
			}
		}

		skillSlot.getSkill().getChildren().forEach(skill -> {
			onSkillChange(getSkillSlot(skill));
		});

		if (skillSlot.isActive())
			skillSlot.getSkill().onSkillActivated(getOwner());
		else
			skillSlot.getSkill().onSkillDeactivated(getOwner());
	}

	@Override
	public void reloadHandler() {
		skillsCodex.values().forEach(skillSlot -> {
			if (skillSlot.isActive())
				skillSlot.getSkill().onSkillActivated(getOwner());
			else
				skillSlot.getSkill().onSkillDeactivated(getOwner());
		});
	}

	@Override
	public boolean hasSkill(SkillBase skill) {
		return getSkillSlot(skill).isObtained();
	}

	@Override
	public SkillSlot getSkillSlot(SkillBase skill) {
		return skillsCodex.get(skill);
	}

	@Override
	public boolean hasRequirements(SkillBase skill) {
		return skill.hasRequirments(getOwner());
	}

	@Override
	public ArrayList<SkillSlot> getSkillSlots() {
		return new ArrayList<>(skillsCodex.values());
	}

	@Override
	public void setOwner(EntityLivingBase player) {
		if (player != null)
			MinecraftForge.EVENT_BUS.unregister(this);
		this.owner = player;
		reloadHandler();

		if (owner != null)
			MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public EntityLivingBase getOwner() {
		return owner;
	}

	@Override
	public boolean canBuySkill(SkillBase skill) {
		return skill.hasParent() ? hasSkill(skill.getParent()) : true && skill.hasRequirments(getOwner());
	}

	@Override
	public void buySkill(SkillBase skill) {
		if (canBuySkill(skill)) {
			setSkillObtained(skill, true);
			setSkillActive(skill, true);
			skill.getRequirments(false).forEach(requirement -> requirement.onFufillment(getOwner()));
		}
	}

	@Override
	public int getSkillPoints() {
		return skillPoints;
	}

	@Override
	public void addPoints(int points) {
		skillPoints += points;
		skillPoints = Math.max(skillPoints, 0);
	}

	@Override
	public boolean isSkillActive(SkillBase skill) {
		return getSkillSlot(skill).isActive();
	}

	@SubscribeEvent
	public void onTick(SkillEvent.SkillTick e) {
		trackerCodex.forEach(skill -> {
			SkillSlot skillSlot = skillsCodex.get(skill);
			if (skill instanceof ISkillWatcher && skillSlot.isObtained()) {
				ActiveTick event = new SkillEvent.ActiveTick(owner, skillSlot, skill);
				MinecraftForge.EVENT_BUS.post(event);
				if (!event.isCanceled() && (skillSlot.isActive() || event.getResult() == Result.ALLOW))
					((ISkillWatcher) skill).onActiveTick(owner, skillSlot.getSkill(), skillSlot);
			}
		});
	}

}
