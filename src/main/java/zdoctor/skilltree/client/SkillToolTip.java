package zdoctor.skilltree.client;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import zdoctor.skilltree.api.skills.ISkillRequirment;

public class SkillToolTip {

	private String translateKey;
	private int color;
	private Object[] parameters;

	public SkillToolTip(EntityPlayer player, ISkillRequirment requirement) {
		this(requirement.getDescription(), requirement.getTextColor(player), requirement.getDescriptionValues());
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
