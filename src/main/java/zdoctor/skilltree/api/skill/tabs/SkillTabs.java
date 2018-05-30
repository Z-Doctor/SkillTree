package zdoctor.skilltree.api.skill.tabs;

import java.util.ArrayList;
import java.util.Random;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.skills.interfaces.ISkillPage;

/**
 * Extend this class to create a skill tab and page to display achievements
 */
public abstract class SkillTabs {
	public static final ArrayList<SkillTabs> SKILL_TABS = new ArrayList<>();

	private final String tabLabel;
	/** Texture to use. */
	private String backgroundTexture = "items.png";
	/** Whether to draw the title in the foreground of the creative GUI */
	private boolean drawTitle = true;
	private ItemStack iconItemStack;

	private ISkillPage page;

	public SkillTabs(String label, ISkillPage page) {
		this(getNextEmptyIndex(), label, page);
	}

	/**
	 * Helper function to enchant an item
	 */
	public static ItemStack enchantItem(Item item) {
		ItemStack temp = new ItemStack(item);
		EnchantmentHelper.addRandomEnchantment(new Random(), temp, 1, false);
		temp.isItemEnchanted();
		return temp;
	}

	public SkillTabs(int id, String label, ISkillPage page) {
		this.tabLabel = label;
		this.iconItemStack = ItemStack.EMPTY;
		this.page = page;
		while (SKILL_TABS.size() < id + 1)
			SKILL_TABS.add(null);

		if (SKILL_TABS.get(id) == null)
			SKILL_TABS.set(id, this);
		else
			SKILL_TABS.add(id, this);
	}

	@SideOnly(Side.CLIENT)
	public int getTabIndex() {
		return SkillTabs.SKILL_TABS.indexOf(this);
	}

	@SideOnly(Side.CLIENT)
	public SkillTabs setBackgroundImageName(String texture) {
		this.backgroundTexture = texture;
		return this;
	}

	@SideOnly(Side.CLIENT)
	public String getTabLabel() {
		return this.tabLabel;
	}

	@SideOnly(Side.CLIENT)
	public boolean showTab() {
		return true;
	}

	/**
	 * Gets the translated Label.
	 */
	@SideOnly(Side.CLIENT)
	public String getTranslatedTabLabel() {
		return I18n.format("skillTree." + this.getTabLabel());
	}

	@SideOnly(Side.CLIENT)
	public ItemStack getIconItemStack() {
		if (this.iconItemStack.isEmpty()) {
			this.iconItemStack = this.getTabIconItem();
		}

		return this.iconItemStack;
	}

	@SideOnly(Side.CLIENT)
	public abstract ItemStack getTabIconItem();

	@SideOnly(Side.CLIENT)
	public String getBackgroundImageName() {
		return this.backgroundTexture;
	}

	@SideOnly(Side.CLIENT)
	public boolean drawInForegroundOfTab() {
		return this.drawTitle;
	}

	@SideOnly(Side.CLIENT)
	public int getTabColumn() {
		return getTabIndex() % (getTabsPerPage() / 2);
	}

	@SideOnly(Side.CLIENT)
	public boolean isTabTopRow() {
		int index = getTabIndex();
		if (index > getAdjustedTabsPerPage()) {
			// index -= getAdjustedTabsPerPage();
			index %= getTabsPerPage();
		}
		return index < getTabsPerPage() / 2;
	}

	@SideOnly(Side.CLIENT)
	public boolean isAlignedRight() {
		return !this.isTabTopRow() && this.getTabColumn() == 7;
	}

	@SideOnly(Side.CLIENT)
	public boolean isAlignedLeft() {
		return this.isTabTopRow() && this.getTabColumn() == 0;
	}

	public ISkillPage getPage() {
		return page;
	}

	public int getTabPage() {
		if (getTabIndex() > getAdjustedTabsPerPage()) {
			return MathHelper.floor((float) getTabIndex() / getTabsPerPage());
		}
		return 0;
	}

	public static int getNextEmptyIndex() {
		for (int i = 0; i < SKILL_TABS.size(); i++) {
			if (SKILL_TABS.get(i) == null)
				return i;
		}
		return SKILL_TABS.size();
	}

	@SideOnly(Side.CLIENT)
	public static int getAdjustedTabsPerPage() {
		return getTabsPerPage() - 1;
	}

	@SideOnly(Side.CLIENT)
	public static int getTabsPerPage() {
		return 16;
	}

}
