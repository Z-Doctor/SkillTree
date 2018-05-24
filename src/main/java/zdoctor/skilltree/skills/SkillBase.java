package zdoctor.skilltree.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ibm.icu.impl.IllegalIcuArgumentException;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.interfaces.ISkillTickable;
import zdoctor.skilltree.api.skills.requirements.DescriptionRequirment;
import zdoctor.skilltree.api.skills.requirements.NameRequirment;
import zdoctor.skilltree.api.skills.requirements.PreviousSkillRequirement;
import zdoctor.skilltree.client.SkillToolTip;
import zdoctor.skilltree.proxy.CommonProxy;

/**
 * Skills should extend this class.
 *
 */
public abstract class SkillBase {
	protected static HashMap<ResourceLocation, SkillBase> Skill_Registry = new HashMap();

	private static int nextId;

	private String skillName;
	private ResourceLocation registryName;

	private SkillFrameType frameType;

	private ArrayList<SkillBase> children = new ArrayList<>();
	private ArrayList<ISkillRequirment> requirements = new ArrayList<>();
	private SkillBase parent;

	private final ItemStack icon;

	private int id;

	private DescriptionRequirment descReq;
	private NameRequirment nameReq;

	private PreviousSkillRequirement parentRequirement;

	public SkillBase(String name, ItemStack iconIn, ISkillRequirment... requirements) {
		this(name, SkillFrameType.NORMAL, iconIn, requirements);
	}

	public SkillBase(String name, SkillFrameType type, ItemStack iconIn, ISkillRequirment... requirements) {
		this.skillName = name;
		this.registryName = new ResourceLocation(Loader.instance().activeModContainer().getModId() + ":" + skillName);
		this.frameType = type;
		this.icon = iconIn;
		nameReq = new NameRequirment(this);
		Collections.addAll(this.requirements, requirements);
		descReq = new DescriptionRequirment(this);
		if (Skill_Registry.containsKey(registryName)) {
			ModMain.proxy.log
					.catching(new IllegalArgumentException("Attempt to register Skill '" + registryName + "' twice"));
		}
		Skill_Registry.put(registryName, this);
		this.id = nextId++;
		if (this instanceof ISkillTickable)
			CommonProxy.SkillWatcher_Registry.add(this);
	}

	public SkillBase setParent(SkillBase parent) {
		if (parent == null)
			return this;
		if (parent == this) {
			ModMain.proxy.log.catching(new IllegalArgumentException(
					"Tried to register skill '" + getRegistryName() + "' parent as itself!"));
		}
		// TODO have a way to change the parent
		if (this.parent == null) {
			this.parent = parent;
			parentRequirement = new PreviousSkillRequirement(parent);
			parent.addChildren(this);
		}
		return this;
	}

	public SkillBase getParent() {
		return parent;
	}

	public NameRequirment getNameRequirement() {
		return nameReq;
	}

	public DescriptionRequirment getDescriptionRequirement() {
		return descReq;
	}
	
	public PreviousSkillRequirement getParentRequirement() {
		return parentRequirement;
	}

	public SkillFrameType getFrameType() {
		return frameType;
	}

	public SkillBase setFrameType(SkillFrameType frameType) {
		this.frameType = frameType;
		return this;
	}

	protected SkillBase addChildren(SkillBase childIn) {
		if (childIn != null && !children.contains(childIn))
			children.add(childIn);
		return this;
	}

	public SkillBase setNameColor(int colorObtained, int colorNotObtained) {
		this.nameReq.setColor(colorObtained, colorNotObtained);
		return this;
	}

	public SkillBase setDescriptionColor(int color) {
		this.descReq.setColor(color);
		return this;
	}

	public SkillBase addRequirement(ISkillRequirment requirment) {
		if (requirements.contains(requirment))
			ModMain.proxy.log.catching(new IllegalIcuArgumentException("Tried to add '" + requirment + "' twice"));
		for (ISkillRequirment r : requirements) {
			if (requirment.getClass().isInstance(r))
				ModMain.proxy.log.catching(new IllegalIcuArgumentException("Tried to add conflicting requirment class '"
						+ requirment.getClass() + "' already registered '" + r.getClass() + "'"));
		}
		requirements.add(requirment);
		return this;
	}

	public ArrayList<SkillBase> getChildren() {
		return new ArrayList<>(children);
	}

	public String getDisplayName() {
		return I18n.format(getUnlocaizedName());
	}

	public String getUnlocaizedName() {
		return "skill." + skillName;
	}

	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		ArrayList reqList = new ArrayList<>();
		if (!hasSkill)
			reqList.addAll(requirements);
		return reqList;
	}

	@Override
	public String toString() {
		return getRegistryName().toString();
	}

	public ItemStack getIcon() {
		return icon;
	}

	public boolean hasParent() {
		return getParent() != null;
	}

	public int getId() {
		return id;
	}

	/**
	 * Used when drawing skill info when hovered. You can manipulate the order the
	 * info is given
	 */
	public abstract List<SkillToolTip> getToolTip(EntityLivingBase entity);

	public abstract void onSkillActivated(EntityLivingBase entity);

	public abstract void onSkillDeactivated(EntityLivingBase entity);

	public abstract boolean hasRequirments(EntityLivingBase entity);
	
	public abstract void onSkillPurchase(EntityLivingBase entity);

	public static SkillBase getSkillByKey(ResourceLocation key) {
		return Skill_Registry.get(key);
	}

	public static SkillBase getSkillById(int id) {
		for (SkillBase skill : Skill_Registry.values()) {
			if (skill.getId() == id)
				return skill;
		}
		return null;
	}

	public static ArrayList<SkillBase> getSkillRegistry() {
		return new ArrayList<>(Skill_Registry.values());
	}

}
