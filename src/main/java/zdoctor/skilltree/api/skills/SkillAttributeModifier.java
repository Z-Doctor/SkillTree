package zdoctor.skilltree.api.skills;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class SkillAttributeModifier extends AttributeModifier {
	public SkillAttributeModifier(String nameIn, double amountIn, int operationIn) {
		super(nameIn, amountIn, operationIn);
		setSaved(false);
	}

	public SkillAttributeModifier(UUID idIn, String nameIn, double amountIn, int operationIn) {
		super(idIn, nameIn, amountIn, operationIn);
		setSaved(false);
	}

}
