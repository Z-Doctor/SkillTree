package zdoctor.mcskilltree.skills.attack;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import zdoctor.mcskilltree.skills.AttackSkill;
import zdoctor.skilltree.api.skills.SkillAttributeModifier;
import zdoctor.skilltree.skills.SkillBase;

public class ExtraDamage extends AttackSkill {
	public static final String[] NAME = new String[] { "damageI", "damageII", "damageIII" };
	public static final Item[] ICON = new Item[] { Items.WOODEN_SWORD, Items.STONE_SWORD, Items.IRON_SWORD };

	public static final SkillAttributeModifier[] MODIFIERS = new SkillAttributeModifier[3];

	protected static int TIER;

	protected final int tier;

	public ExtraDamage() {
		super(NAME[(TIER = TIER >= NAME.length ? 0 : TIER)], ICON[TIER]);
		this.tier = TIER;

		MODIFIERS[TIER] = new SkillAttributeModifier("attackSkill." + NAME[TIER], 1, 0);

		TIER++;
	}

	@Override
	public SkillAttributeModifier getModifier(EntityLivingBase entity, SkillBase skill) {
		if (skill instanceof ExtraDamage)
			return MODIFIERS[((ExtraDamage) skill).getTier()];
		return null;
	}

	public int getTier() {
		return this.tier;
	}

}
