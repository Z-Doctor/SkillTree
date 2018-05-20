package zdoctor.skilltree.skills;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.ibm.icu.impl.IllegalIcuArgumentException;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.skills.ISkillRequirment;
import zdoctor.skilltree.api.skills.requirements.DescriptionRequirment;
import zdoctor.skilltree.api.skills.requirements.NameRequirment;
import zdoctor.skilltree.api.skills.requirements.PreviousSkillRequirement;
import zdoctor.skilltree.client.SkillToolTip;
import zdoctor.skilltree.skills.pages.SkillPageBase;

/**
 * Skills should extend this class.
 *
 */
public abstract class SkillBase {
	protected static HashMap<ResourceLocation, SkillBase> Skill_Registry = new HashMap();

	private static int nextId;

	private String skillName;
	private ResourceLocation registryName;

	public int column;
	public int row;

	private ArrayList<SkillBase> children = new ArrayList<>();
	private ArrayList<ISkillRequirment> requirements = new ArrayList<>();
	private SkillBase parent;

	private SkillPageBase page;
	private final ItemStack icon;

	private int id;

	private DescriptionRequirment descReq;
	private NameRequirment nameReq;

	public SkillBase(String name, int column, int row, ItemStack iconIn, ISkillRequirment... requirements) {
		this.skillName = name;
		this.registryName = new ResourceLocation(Loader.instance().activeModContainer().getModId() + ":" + skillName);
		this.column = Math.min(Math.max(0, column), 12);
		this.row = Math.min(Math.max(0, row), 5);
		this.icon = iconIn;
		nameReq = new NameRequirment(this);
		Collections.addAll(this.requirements, requirements);
		descReq = new DescriptionRequirment(this);
		if (Skill_Registry.containsKey(registryName)) {
			throw new IllegalArgumentException("Attempt to register Skill '" + registryName + "' twice");
		}
		Skill_Registry.put(registryName, this);
		this.id = nextId++;
	}

	public SkillBase setParent(SkillBase parent) {
		if (parent == null)
			return this;
		this.parent = parent;
		requirements.add(new PreviousSkillRequirement(parent));
		parent.addChildren(this);
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
			throw new IllegalIcuArgumentException("Tried to add '" + requirment + "' twice");
		for (ISkillRequirment r : requirements) {
			if (requirment.getClass().isInstance(r))
				throw new IllegalIcuArgumentException("Tried to add conflicting requirment class '"
						+ requirment.getClass() + "' already registered '" + r.getClass() + "'");
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

	public ArrayList<ISkillRequirment> getRequirments(boolean hasSkill) {
		ArrayList reqList = new ArrayList<>();
		// reqList.add(nameReq);
		if (!hasSkill)
			reqList.addAll(requirements);
		// reqList.add(descReq);
		return reqList;
	}

	@Override
	public String toString() {
		return getRegistryName().toString();
	}

	public ItemStack getIcon() {
		return icon;
	}

	public void setPage(SkillPageBase page) {
		this.page = page;
	}

	public SkillPageBase getPage() {
		return page;
	}

	public boolean hasParent() {
		return getParent() != null;
	}

	@SideOnly(Side.CLIENT)
	public int getColumn() {
		return column;
	}

	@SideOnly(Side.CLIENT)
	public int getRow() {
		return row;
	}

	public int getId() {
		return id;
	}

	public abstract List<SkillToolTip> getToolTip(EntityLivingBase entity);

	public abstract void onSkillActivated(EntityLivingBase entity);

	public abstract void onSkillDeactivated(EntityLivingBase entity);

	public abstract boolean hasRequirments(EntityLivingBase entity);

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
