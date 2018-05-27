package zdoctor.mcskilltree.skills;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.Skill;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;
import zdoctor.skilltree.api.skills.requirements.LevelRequirement;
import zdoctor.skilltree.api.skills.requirements.PreviousSkillRequirement;
import zdoctor.skilltree.api.skills.requirements.SkillPointRequirement;
import zdoctor.skilltree.skills.SkillBase;

public class CraftSkill extends Skill {
	public static final SkillBase CRAFT_SKILL = new Skill("CraftersSkill", Item.getItemFromBlock(Blocks.CRAFTING_TABLE))
			.setDrawLineToChildren(false).addRequirement(new LevelRequirement(10))
			.addRequirement(new SkillPointRequirement(3)).setFrameType(SkillFrameType.SPECIAL);

	public CraftSkill(String name, Item icon) {
		this(name, new ItemStack(icon));
	}

	public CraftSkill(String name, ItemStack icon) {
		super(name, icon);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill) {
		List<ISkillRequirment> req = new ArrayList<>();
		if (!SkillTreeApi.hasSkill(entity, CRAFT_SKILL) && !hasParent())
			req.add(new PreviousSkillRequirement(CRAFT_SKILL));
		req.addAll(super.getRequirments(entity, hasSkill));
		return req;
	}

}
