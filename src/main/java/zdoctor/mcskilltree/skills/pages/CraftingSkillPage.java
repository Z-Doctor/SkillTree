package zdoctor.mcskilltree.skills.pages;

import zdoctor.mcskilltree.skills.CraftSkill;
import zdoctor.mcskilltree.skills.crafting.ArrowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.AxeCraftSkill;
import zdoctor.mcskilltree.skills.crafting.BowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.ItemCrafterSkill;
import zdoctor.mcskilltree.skills.crafting.ShovelCraftSkill;
import zdoctor.mcskilltree.skills.crafting.SpectralArrowCraftSkill;
import zdoctor.mcskilltree.skills.crafting.SwordCraftSkill;
import zdoctor.mcskilltree.skills.crafting.TippedArrowCraftSkill;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class CraftingSkillPage extends SkillPageBase {

	public static ItemCrafterSkill SWORD_CRAFTER;
	public static ItemCrafterSkill AXE_CRAFTER;
	public static ItemCrafterSkill SHOVEL_CRAFTER;
	public static ItemCrafterSkill BOW_CRAFTER;
	public static ItemCrafterSkill ARROW_CRAFTER;
	public static ItemCrafterSkill SPECTRAL_ARROW_CRAFTER;
	public static ItemCrafterSkill TIPPED_ARROW_CRAFTER;

	public CraftingSkillPage() {
		super("CraftingPage");
	}

	@Override
	public void registerSkills() {
		SWORD_CRAFTER = new SwordCraftSkill();
		AXE_CRAFTER = new AxeCraftSkill();
		SHOVEL_CRAFTER = new ShovelCraftSkill();
		BOW_CRAFTER = new BowCraftSkill();
		ARROW_CRAFTER = new ArrowCraftSkill();
		SPECTRAL_ARROW_CRAFTER = new SpectralArrowCraftSkill();
		TIPPED_ARROW_CRAFTER = new TippedArrowCraftSkill();
	}

	@Override
	public void loadPage() {
		addSkill(CraftSkill.CRAFT_SKILL, 0, 0);
		addSkill(SWORD_CRAFTER, 1, 0);
		addSkill(AXE_CRAFTER, 0, 2);
		addSkill(SHOVEL_CRAFTER, 0, 3);
		addSkill(BOW_CRAFTER, 2, 0);
		addSkill(ARROW_CRAFTER, 3, 0);
		addSkill(TIPPED_ARROW_CRAFTER, 4, 0);
		addSkill(SPECTRAL_ARROW_CRAFTER, 4, 1);
	}

}
