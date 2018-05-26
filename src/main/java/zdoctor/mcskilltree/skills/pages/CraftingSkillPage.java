package zdoctor.mcskilltree.skills.pages;

import net.minecraft.init.Items;
import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.mcskilltree.skills.crafting.WeaponCrafter;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class CraftingSkillPage extends SkillPageBase {

	public static WeaponCrafter SWORD_CRAFTER;
	public static WeaponCrafter AXE_CRAFTER;
	public static WeaponCrafter SHOVEL_CRAFTER;
	public static WeaponCrafter BOW_CRAFTER;

	public CraftingSkillPage() {
		super("CraftingPage");
	}

	@Override
	public void registerSkills() {
		SWORD_CRAFTER = new WeaponCrafter("SwordCrafter", Items.DIAMOND_SWORD);
		AXE_CRAFTER = new WeaponCrafter("AxeCrafter", Items.DIAMOND_AXE);
		SHOVEL_CRAFTER = new WeaponCrafter("ShovelCrafter", Items.DIAMOND_SHOVEL);
		BOW_CRAFTER = new WeaponCrafter("BowCrafter", Items.BOW);
	}

	@Override
	public void loadPage() {
		addSkill(CraftSkill.CRAFT_SKILL, 0, 0);
		addSkill(SWORD_CRAFTER, 0, 1);
		addSkill(AXE_CRAFTER, 0, 2);
		addSkill(SHOVEL_CRAFTER, 0, 3);
		addSkill(BOW_CRAFTER, 0, 4);
	}

}
