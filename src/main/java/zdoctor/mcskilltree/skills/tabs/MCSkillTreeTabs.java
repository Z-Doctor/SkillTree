package zdoctor.mcskilltree.skills.tabs;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.pages.AttackSkillPage;
import zdoctor.mcskilltree.skills.pages.CraftingSkillPage;
import zdoctor.skilltree.tabs.SkillTabs;

public class MCSkillTreeTabs {
	// public static final SkillTabs ATTACK_TAB = new SkillTabs("AttackTab", new
	// AttackSkillPage()) {
	//
	// @Override
	// public ItemStack getTabIconItem() {
	// return SkillTabs.enchantItem(Items.DIAMOND_SWORD);
	// }
	// };

	// public static final SkillTreeTabs DEF_TAB = new SkillTreeTabs("DefenseTab",
	// new AttackSkillPage()) {
	//
	// @Override
	// public ItemStack getTabIconItem() {
	// return new ItemStack(Items.SHIELD);
	// }
	// };
	//
	public static final SkillTabs MINING_TAB = new SkillTabs("CraftingTab", new CraftingSkillPage()) {
		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Blocks.CRAFTING_TABLE);
		}

	};

	public static void init() {
	};
}
