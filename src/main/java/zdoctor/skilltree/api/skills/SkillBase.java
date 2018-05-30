package zdoctor.skilltree.api.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.requirements.DescriptionRequirment;
import zdoctor.skilltree.api.skills.requirements.NameRequirment;
import zdoctor.skilltree.api.skills.requirements.PreviousSkillRequirement;

/**
 * Skills should extend this class.
 *
 */
public abstract class SkillBase implements ISkill {
	protected static HashMap<ResourceLocation, SkillBase> Skill_Registry = new HashMap();

	private static int nextId;

	public static ISkill getSkillById(int id) {
		for (ISkill skill : Skill_Registry.values()) {
			if (skill.getId() == id)
				return skill;
		}
		return null;
	}

	public static ArrayList<ISkill> getSkillRegistry() {
		return new ArrayList<>(Skill_Registry.values());
	}

	private String skillName;
	private ResourceLocation registryName;
	private int id;

	private final ItemStack icon;
	private SkillFrameType frameType;

	private DescriptionRequirment descReq;
	private NameRequirment nameReq;
	private PreviousSkillRequirement parentRequirement;

	private boolean drawLine = true;

	private ArrayList<ISkill> children = new ArrayList<>();
	private ArrayList<ISkillRequirment> requirements = new ArrayList<>();
	private ISkill parent;

	public SkillBase(String name, @Nullable ItemStack iconIn, ISkillRequirment... requirements) {
		this(name, SkillFrameType.NORMAL, iconIn, requirements);
	}

	public SkillBase(String name, SkillFrameType type, ItemStack iconIn, ISkillRequirment... requirements) {
		this.skillName = name;
		this.registryName = new ResourceLocation(Loader.instance().activeModContainer().getModId() + ":" + skillName);
		this.frameType = type;
		this.icon = iconIn;
		this.nameReq = new NameRequirment(this);
		Collections.addAll(this.requirements, requirements);
		this.descReq = new DescriptionRequirment(this);
		if (Skill_Registry.containsKey(registryName)) {
			throw new IllegalArgumentException("Attempt to register Skill '" + registryName + "' twice");
		}
		Skill_Registry.put(registryName, this);
		// ModMain.proxy.log.info("Registered Skill: {}", registryName);
		this.id = nextId++;
	}

	@Override
	public ISkill setParent(ISkill parent) {
		if (parent == null)
			return this;
		if (parent == this) {
			return this;
		}
		if (this.parent != null && !this.parent.removeChild(this)) {
			// ModMain.proxy.log.catching(new NullPointerException("Unable to remove '" +
			// getRegistryName()
			// + "' from previous parent '" + this.parent.getRegistryName() + "'"));
		}

		this.parent = parent;
		parentRequirement = new PreviousSkillRequirement(parent);
		parent.addChildren(this);
		return this;
	}

	@Override
	public ISkill getParent() {
		return parent;
	}

	@Override
	public boolean removeChild(ISkill skill) {
		return this.children.remove(skill);
	}

	@Override
	public NameRequirment getNameRequirement() {
		return nameReq;
	}

	@Override
	public DescriptionRequirment getDescriptionRequirement() {
		return descReq;
	}

	@Override
	public PreviousSkillRequirement getParentRequirement() {
		return parentRequirement;
	}

	@Override
	public ISkill setFrameType(SkillFrameType frameType) {
		this.frameType = frameType;
		return this;
	}

	@Override
	public ISkill addChildren(ISkill childIn) {
		if (childIn != null && !children.contains(childIn))
			children.add(childIn);
		return this;
	}

	@Override
	public ISkill setNameColor(int colorObtained, int colorNotObtained) {
		this.nameReq.setColor(colorObtained, colorNotObtained);
		return this;
	}

	@Override
	public ISkill setDescriptionColor(int color) {
		this.descReq.setColor(color);
		return this;
	}

	@Override
	public ISkill addRequirement(ISkillRequirment requirment) {
		if (requirements.contains(requirment))
			// ModMain.proxy.log.catching(new IllegalIcuArgumentException("Tried to add '" +
			// requirment + "' twice"));
			return this;
		for (ISkillRequirment r : requirements) {
			if (requirment.getClass().isInstance(r))
				// ModMain.proxy.log.catching(new IllegalIcuArgumentException("Tried to add
				// conflicting requirment class '"
				// + requirment.getClass() + "' already registered '" + r.getClass() + "'"));
				return this;
		}
		requirements.add(requirment);
		return this;
	}

	@Override
	public ArrayList<ISkill> getChildren() {
		return new ArrayList<>(children);
	}

	@Override
	public String getUnlocaizedName() {
		return "skill." + skillName;
	}

	@Override
	public ResourceLocation getRegistryName() {
		return registryName;
	}

	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		ArrayList reqList = new ArrayList<>();
		if (!hasSkill) {
			if (parentRequirement != null)
				reqList.add(parentRequirement);
			reqList.addAll(requirements);
		}
		return reqList;
	}

	@Override
	public String toString() {
		return getRegistryName().toString();
	}

	@Override
	public boolean hasParent() {
		return getParent() != null;
	}

	@Override
	public int getId() {
		return id;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public SkillFrameType getFrameType(EntityLivingBase entity) {
		return frameType;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public ItemStack getIcon(EntityLivingBase entity) {
		return icon;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getDisplayName() {
		return I18n.format(getUnlocaizedName());
	}

	/**
	 * Used when drawing skill info when hovered. You can manipulate the order the
	 * info is given
	 */
	@SideOnly(Side.CLIENT)
	@Override
	public abstract List<SkillToolTip> getToolTip(EntityLivingBase entity);

	@SideOnly(Side.CLIENT)
	@Override
	public abstract boolean shouldDrawSkill(EntityLivingBase entity);

	@SideOnly(Side.CLIENT)
	@Override
	public abstract ResourceLocation getBackroundLocation(EntityLivingBase entity);

	@SideOnly(Side.CLIENT)
	@Override
	public abstract boolean shouldRenderItem(EntityLivingBase entity);

	@Override
	public abstract void onSkillActivated(EntityLivingBase entity);

	@Override
	public abstract void onSkillDeactivated(EntityLivingBase entity);

	@Override
	public abstract boolean hasRequirments(EntityLivingBase entity);

	@Override
	public abstract void onSkillPurchase(EntityLivingBase entity);

	public static ISkill getSkillByKey(ResourceLocation key) {
		return Skill_Registry.get(key);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldDrawLineToChildren() {
		return drawLine;
	}

	@Override
	public ISkill setDrawLineToChildren(boolean doDraw) {
		drawLine = doDraw;
		return this;
	}

}
