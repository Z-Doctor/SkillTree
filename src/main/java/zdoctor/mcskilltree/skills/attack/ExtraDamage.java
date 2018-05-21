package zdoctor.mcskilltree.skills.attack;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import zdoctor.mcskilltree.skills.AttackSkill;

public class ExtraDamage extends AttackSkill {
	public static final AttributeModifier DAMAGE_TIER_I = new AttributeModifier("attackSkill.damageI", 1, 0);
	public static final AttributeModifier DAMAGE_TIER_II = new AttributeModifier("attackSkill.damageII", 1, 0);
	public static final AttributeModifier DAMAGE_TIER_III = new AttributeModifier("attackSkill.damageIII", 1, 0);

	public static final String[] NAME = new String[] { "damageI", "damageII", "damageIII" };
	public static final Item[] ICON = new Item[] { Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD };
	public static final AttributeModifier[] MODIFIERS = new AttributeModifier[] { DAMAGE_TIER_I, DAMAGE_TIER_II,
			DAMAGE_TIER_III };

	protected static int tier;

	public ExtraDamage(int colum, int row) {
		super(NAME[tier], colum, row, ICON[tier], MODIFIERS[tier]);
		tier++;
	}
}
