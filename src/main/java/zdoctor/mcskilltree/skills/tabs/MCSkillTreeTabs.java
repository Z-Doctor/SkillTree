package zdoctor.mcskilltree.skills.tabs;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import zdoctor.mcskilltree.skills.pages.AttackSkillPage;
import zdoctor.skilltree.tabs.SkillTabs;

public class MCSkillTreeTabs {
	public static final SkillTabs ATTACK_TAB = new SkillTabs("AttackTab", new AttackSkillPage()) {

		@Override
		public ItemStack getTabIconItem() {
			return new ItemStack(Items.DIAMOND_SWORD);
		}
	};

	// public static final SkillTreeTabs DEF_TAB = new SkillTreeTabs("DefenseTab",
	// new AttackSkillPage()) {
	//
	// @Override
	// public ItemStack getTabIconItem() {
	// return new ItemStack(Items.SHIELD);
	// }
	// };
	//
	// public static final SkillTreeTabs MINING_TAB = new SkillTreeTabs("MiningTab",
	// new AttackSkillPage()) {
	//
	// @Override
	// public ItemStack getTabIconItem() {
	// return new ItemStack(Items.DIAMOND_PICKAXE);
	// }
	// };

	public static void init() {
	};
}
