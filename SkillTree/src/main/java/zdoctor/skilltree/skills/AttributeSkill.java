package zdoctor.skilltree.skills;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * For information about Attribute check
 * {@link https://minecraft.gamepedia.com/Attribute}
 *
 */
public class AttributeSkill extends Skill {
	protected final AttributeModifier modifier;
	protected final IAttribute attribute;

	public AttributeSkill(String name, int column, int row, Item icon, IAttribute attributeIn,
			AttributeModifier modifierIn) {
		this(name, column, row, new ItemStack(icon), attributeIn, modifierIn);
	}

	public AttributeSkill(String name, int column, int row, ItemStack icon, IAttribute attributeIn,
			AttributeModifier modifierIn) {
		super(name, column, row, icon);
		this.attribute = attributeIn;
		this.modifier = modifierIn;
		modifierIn.setSaved(false);
	}

	public void modifyPlayer(EntityPlayer player) {
		if (!player.getEntityAttribute(getAttribute()).hasModifier(getModifier())) {
			player.getEntityAttribute(getAttribute()).applyModifier(getModifier());
			// System.out.println("Added Modifier: " + getModifier());
		}
	}

	public void removePlayerModify(EntityPlayer player) {
		// System.out.println("Modifiyer: " + getModifier());
		if (player.getEntityAttribute(getAttribute()).hasModifier(getModifier())) {
			player.getEntityAttribute(getAttribute()).removeModifier(getModifier());
			// System.out.println("Removed Modifier: " + getModifier());
		}
	}

	public AttributeModifier getModifier() {
		return modifier;
	}

	public IAttribute getAttribute() {
		return attribute;
	}

	@Override
	public void onSkillActivated(EntityPlayer player) {
		modifyPlayer(player);
	}

	@Override
	public void onSkillDeactivated(EntityPlayer player) {
		removePlayerModify(player);
	}
}
