package zdoctor.skilltree.api.skills;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class SkillAttributeModifier extends AttributeModifier {

	private ISkillAtribute skill;

	public SkillAttributeModifier(ISkillAtribute skill, String nameIn, double amountIn, int operationIn) {
		super(nameIn, amountIn, operationIn);
		this.skill = skill;
	}

	public SkillAttributeModifier(ISkillAtribute skill, UUID idIn, String nameIn, double amountIn, int operationIn) {
		super(idIn, nameIn, amountIn, operationIn);
		this.skill = skill;
	}

	

}
