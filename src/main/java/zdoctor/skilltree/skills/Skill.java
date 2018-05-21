package zdoctor.skilltree.skills;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.ISkillRequirment;
import zdoctor.skilltree.client.SkillToolTip;

public class Skill extends SkillBase {

	public Skill(String name, int column, int row, Item iconIn) {
		this(name, column, row, new ItemStack(iconIn), new ISkillRequirment[0]);
	}

	public Skill(String name, int column, int row, SkillFrameType type, Item iconIn) {
		this(name, column, row, type, new ItemStack(iconIn), new ISkillRequirment[0]);
	}

	public Skill(String name, int column, int row, ItemStack iconIn) {
		this(name, column, row, iconIn, new ISkillRequirment[0]);
	}

	public Skill(String name, int column, int row, SkillFrameType type, ItemStack iconIn) {
		this(name, column, row, type, iconIn, new ISkillRequirment[0]);
	}

	public Skill(String name, int column, int row, ItemStack iconIn, ISkillRequirment... requirements) {
		super(name, column, row, iconIn, requirements);
	}

	public Skill(String name, int column, int row, SkillFrameType type, ItemStack iconIn,
			ISkillRequirment... requirements) {
		super(name, column, row, type, iconIn, requirements);
	}

	@Override
	public List<SkillToolTip> getToolTip(EntityLivingBase player) {
		List<SkillToolTip> toolTip = new ArrayList<>();
		toolTip.add(new SkillToolTip(player, getNameRequirement()));
		getRequirments(SkillTreeApi.hasSkill(player, this))
				.forEach(requirement -> toolTip.add(new SkillToolTip(player, requirement)));
		toolTip.add(new SkillToolTip(player, getDescriptionRequirement()));
		return toolTip;
	}

	@Override
	public void onSkillActivated(EntityLivingBase player) {

	}

	@Override
	public void onSkillDeactivated(EntityLivingBase player) {

	}

	@Override
	public boolean hasRequirments(EntityLivingBase player) {
		if (hasParent() && !SkillTreeApi.hasSkill(player, getParent()))
			return false;

		for (ISkillRequirment requirement : getRequirments(false)) {
			if (!requirement.test(player))
				return false;
		}
		return true;
	}

}
