package zdoctor.skilltree.api.skills;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.client.SkillToolTip;
import zdoctor.skilltree.skills.SkillBase;

public class Skill extends SkillBase {

	public Skill(String name, Item iconIn) {
		this(name, new ItemStack(iconIn), new ISkillRequirment[0]);
	}

	public Skill(String name, SkillFrameType type, Item iconIn) {
		this(name, type, new ItemStack(iconIn), new ISkillRequirment[0]);
	}

	public Skill(String name, ItemStack iconIn) {
		this(name, iconIn, new ISkillRequirment[0]);
	}

	public Skill(String name, SkillFrameType type, ItemStack iconIn) {
		this(name, type, iconIn, new ISkillRequirment[0]);
	}

	public Skill(String name, ItemStack iconIn, ISkillRequirment... requirements) {
		super(name, iconIn, requirements);
	}

	public Skill(String name, SkillFrameType type, ItemStack iconIn, ISkillRequirment... requirements) {
		super(name, type, iconIn, requirements);
	}

	@Override
	public List<SkillToolTip> getToolTip(EntityLivingBase entity) {
		List<SkillToolTip> toolTip = new ArrayList<>();
		toolTip.add(new SkillToolTip(entity, getNameRequirement()));
		getRequirments(entity, SkillTreeApi.hasSkill(entity, this))
				.forEach(requirement -> toolTip.add(new SkillToolTip(entity, requirement)));
		toolTip.add(new SkillToolTip(entity, getDescriptionRequirement()));
		return toolTip;
	}

	@Override
	public void onSkillActivated(EntityLivingBase entity) {

	}

	@Override
	public void onSkillDeactivated(EntityLivingBase entity) {

	}

	@Override
	public boolean hasRequirments(EntityLivingBase entity) {
		for (ISkillRequirment requirement : getRequirments(entity, SkillTreeApi.hasSkill(entity, this))) {
			if (!requirement.test(entity))
				return false;
		}
		return true;
	}

	@Override
	public void onSkillPurchase(EntityLivingBase entity) {
		// TODO Toast
	}

	@Override
	public boolean shouldDrawSkill(EntityLivingBase entity) {
		return true;
	}

	@Override
	public ResourceLocation getBackroundLocation(EntityLivingBase entity) {
		return null;
	}

	@Override
	public boolean shouldRenderItem(EntityLivingBase entity) {
		ItemStack icon = getIcon(entity);
		return icon != null && !icon.isEmpty();
	}

}
