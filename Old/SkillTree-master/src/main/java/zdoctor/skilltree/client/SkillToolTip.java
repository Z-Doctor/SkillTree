package zdoctor.skilltree.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import zdoctor.skilltree.api.skills.interfaces.ISkillRequirment;

public class SkillToolTip {

	private String translateKey;
	private int color;
	private Object[] parameters;

	public SkillToolTip(EntityLivingBase entity, ISkillRequirment requirement) {
		this(requirement.getDescription(), requirement.getTextColor(entity), requirement.getDescriptionValues());
	}

	public SkillToolTip(String tranlateKey, int textColor, Object... descriptionValues) {
		this.translateKey = tranlateKey;
		this.color = textColor;
		this.parameters = descriptionValues;
	}

	public String getTransatedText() {
		return I18n.format(translateKey, parameters);
	}

	public int getTextColor() {
		return color;
	}

}
