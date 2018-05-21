package zdoctor.skilltree.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.client.SkillToolTip;
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
		if (page != null)
			page.getSkillList().forEach(skill -> {
				addButton(new GuiSkillButton(skill, guiLeft, guiTop));
			});
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

//		int startX = 13 + guiLeft;
//		int startY = 21 + guiTop;
//		int textureX = 72;
//		int textureY = 140;

		for (SkillBase skill : page.getSkillList()) {
			drawConnectivity(skill, guiLeft, guiTop, true);
			drawConnectivity(skill, guiLeft, guiTop, false);
		}
//		for (SkillBase skill : page.getSkillList()) {
//			GlStateManager.enableAlpha();
//			GlStateManager.color(1, 1, 1, 1);
//			this.mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
//			int posX = startX + 19 * skill.getColumn();
//			int posY = startY + 18 * skill.getRow();
//			boolean isActive = SkillTreeApi.isSkillActive(mc.player, skill);
//			drawScaledCustomSizeModalRect(posX, posY, textureX, textureY + (isActive ? 0 : 26), 26, 26, 16, 16, 256,
//					256);
//			GlStateManager.pushMatrix();
//			GlStateManager.translate(posX, posY, 0);
//			GlStateManager.scale(0.5, 0.5, 1);
//			GlStateManager.enableDepth();
//			itemRender.renderItemAndEffectIntoGUI(skill.getIcon(), 8, 8);
//			GlStateManager.popMatrix();
//
//		}
	}

	protected void renderCustomBackround(float partialTicks, int mouseX, int mouseY) {

	}

}
