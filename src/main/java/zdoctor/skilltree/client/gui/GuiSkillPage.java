package zdoctor.skilltree.client.gui;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.pages.SkillPageBase;
import zdoctor.skilltree.skills.pages.SkillPageBase.BackgroundType;

/**
 * @author Z_Doctor Custom Skill pages need to extend this class. Return a new
 *         instance of this class in the {@link SkillPage} The GuiSkillTree will
 *         handle init and rendering these classes
 */
@SideOnly(Side.CLIENT)
public class GuiSkillPage extends GuiSkillScreen {

	public SkillPageBase page;

	public GuiSkillPage(SkillPageBase skillPage) {
		this.page = skillPage;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (page != null) {
			page.getSkillList().forEach(skill -> {
				addButton(new GuiSkillButton(this, skill, guiLeft, guiTop));
			});

			this.minX = this.guiLeft + 4;
			this.minY = this.guiTop + 9;
			this.maxX = this.guiLeft + 247;
			this.maxY = this.guiTop + 139;
		}
	}

	/**
	 * Draw here anything you want to be drawn after the background, but before the
	 * foreground.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw anything that you need drawn last. Do not call from drawscreen.
	 */
	@Override
	public void drawGuiForegroundLayer(int mouseX, int mouseY) {

	}

	/**
	 * Draw anything that you need drawn in the background. Do not call from
	 * drawscreen.
	 */
	@Override
	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		this.mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
		int i = this.guiLeft + 6;
		int j = this.guiTop + 18;
		if (page.getBackgroundType() != BackgroundType.CUSTOM) {
			int column = 176 + 16 * page.getBackgroundType().getColumn();
			int row = 212 + 16 * page.getBackgroundType().getRow();
			this.renderRepeating(i, j, 15, 7, column, row, 16, 16);
		} else {
			renderCustomBackround(partialTicks, mouseX, mouseY);
		}

	}

	protected void renderCustomBackround(float partialTicks, int mouseX, int mouseY) {

	}

}
