package zdoctor.skilltree.client.gui;

import java.io.IOException;
import java.util.List;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
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
public class GuiSkillPage extends GuiScreen {
	public static final ResourceLocation SKILL_TREE_BACKGROUND = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/skill_tree.png");

	public int xSize;
	public int ySize;
	public int guiLeft;
	public int guiTop;

	public SkillPageBase page;

	public GuiSkillPage(SkillPageBase skillPage) {
		this.page = skillPage;
	}

	@Override
	public void initGui() {
		super.initGui();
		buttonList.clear();
		if (page != null)
			page.getSkillList().forEach(skill -> {
				addButton(new GuiSkillButton(skill, guiLeft, guiTop));
			});
	}

	/**
	 * Draw here anything you want to be drawn after the background, but before the
	 * foreground. The screen has been translated.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	/**
	 * Draw anything that you need drawn last. Do not call from drawscreen. The
	 * screen has been translated.
	 */
	public void drawGuiForegroundLayer(int mouseX, int mouseY) {

	}

	public void drawConnectivity(SkillBase skill, int offsetX, int offsetY, boolean outerLine) {
		if (skill.getChildren().isEmpty())
			return;

		int color = outerLine ? -16777216 : -1;

		int initX = offsetX + 13;
		int initY = offsetY + 21;

		int parentX = 8 + initX + 19 * skill.getColumn();
		int parentHalfX = 16 + initX + 19 * skill.getColumn() + 1;
		int parentY = 8 + initY + 18 * skill.getRow();

		if (outerLine) {
			drawHorizontalLine(parentHalfX + 1, parentX, parentY - 1, color);
			drawHorizontalLine(parentHalfX + 1, parentX, parentY + 1, color);
			drawHorizontalLine(parentHalfX + 1, parentX, parentY, color);
		} else
			drawHorizontalLine(parentHalfX, parentX, parentY, color);

		for (SkillBase child : skill.getChildren()) {
			int childX = 8 + initX + 19 * child.getColumn();
			int childHalfX = 16 + initX + 19 * child.getColumn();
			int childY = 8 + initY + 18 * child.getRow();

			if (outerLine) {
				drawHorizontalLine(childX, parentHalfX - 1, childY - 1, color);
				drawHorizontalLine(childX, parentHalfX + 1, childY - 1, color);
				drawHorizontalLine(childX, parentHalfX, childY + 1, color);

				int lineY = child.getColumn() > skill.getColumn() ? 2 : -2;

				if (child.getColumn() > skill.getColumn()) {
					lineY *= child.getRow() < skill.getRow() ? -1 : 1;
				} else if (child.getColumn() < skill.getColumn()) {
					lineY *= child.getRow() < skill.getRow() ? 1 : -1;
				}

				drawVerticalLine(parentHalfX - 1, childY + lineY, parentY - lineY, color);
				drawVerticalLine(parentHalfX + 1, childY + lineY, parentY - lineY, color);

			} else {
				drawHorizontalLine(parentHalfX, childX, childY, color);
				drawVerticalLine(parentHalfX, parentY, childY, color);
			}
		}
	}

	public boolean renderSkillTooltip(SkillBase skill, int mouseX, int mouseY) {
		if (!isMouseOverSkill(skill, mouseX, mouseY))
			return false;

		this.drawHoveringText(skill, mouseX, mouseY);
		return true;
	}

	protected void drawHoveringText(SkillBase skill, int mouseX, int mouseY) {
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int i = 0;

		List<SkillToolTip> toolTip = skill.getToolTip(mc.player);

		for (SkillToolTip s : toolTip) {
			int j = this.fontRenderer.getStringWidth(s.getTransatedText());

			if (j > i) {
				i = j;
			}
		}

		int l1 = mouseX + 12;
		int i2 = mouseY - 12;
		int k = 8;

		if (toolTip.size() > 1) {
			k += 2 + (toolTip.size() - 1) * 10;
		}

		if (l1 + i > this.width) {
			l1 -= 28 + i;
		}

		if (i2 + k + 6 > this.height) {
			i2 = this.height - k - 6;
		}

		this.zLevel = 300.0F;
		this.itemRender.zLevel = 300.0F;
		int l = -267386864;
		this.drawGradientRect(l1 - 3, i2 - 4, l1 + i + 3, i2 - 3, -267386864, -267386864);
		this.drawGradientRect(l1 - 3, i2 + k + 3, l1 + i + 3, i2 + k + 4, -267386864, -267386864);
		this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 + k + 3, -267386864, -267386864);
		this.drawGradientRect(l1 - 4, i2 - 3, l1 - 3, i2 + k + 3, -267386864, -267386864);
		this.drawGradientRect(l1 + i + 3, i2 - 3, l1 + i + 4, i2 + k + 3, -267386864, -267386864);
		int i1 = 1347420415;
		int j1 = 1344798847;
		this.drawGradientRect(l1 - 3, i2 - 3 + 1, l1 - 3 + 1, i2 + k + 3 - 1, 1347420415, 1344798847);
		this.drawGradientRect(l1 + i + 2, i2 - 3 + 1, l1 + i + 3, i2 + k + 3 - 1, 1347420415, 1344798847);
		this.drawGradientRect(l1 - 3, i2 - 3, l1 + i + 3, i2 - 3 + 1, 1347420415, 1347420415);
		this.drawGradientRect(l1 - 3, i2 + k + 2, l1 + i + 3, i2 + k + 3, 1344798847, 1344798847);

		for (int k1 = 0; k1 < toolTip.size(); ++k1) {
			SkillToolTip s1 = toolTip.get(k1);
			this.fontRenderer.drawStringWithShadow(s1.getTransatedText(), l1, i2, s1.getTextColor());

			if (k1 == 0) {
				i2 += 2;
			}

			i2 += 10;
		}

		this.zLevel = 0.0F;
		this.itemRender.zLevel = 0.0F;
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
	}

	public boolean isMouseOverSkill(SkillBase skill, int mouseX, int mouseY) {
		if (skill == null)
			return false;

		int startX = this.guiLeft + 13;
		int startY = this.guiTop + 21;

		int xOffset = startX + 19 * skill.getColumn();
		int yOffset = startY + 18 * skill.getRow();

		return mouseX >= xOffset && mouseX <= xOffset + 16 && mouseY >= yOffset && mouseY <= yOffset + 16;
	}

	/**
	 * Draw anything that you need drawn in the background. Do not call from
	 * drawscreen. The screen has not been translated yet.
	 */
	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		this.mc.getTextureManager().bindTexture(SKILL_TREE_BACKGROUND);
		int i = this.guiLeft + 6;
		int j = this.guiTop + 18;
		if (page.getBackgroundType() != BackgroundType.CUSTOM) {
			int column = 176 + 16 * page.getBackgroundType().getColumn();
			int row = 212 + 16 * page.getBackgroundType().getRow();
			this.renderRepeating(i, j, 15, 7, column, row, 16, 16);
		} else {
			renderCustomBackround(partialTicks, mouseX, mouseY);
		}

		int startX = 13 + guiLeft;
		int startY = 21 + guiTop;
		int textureX = 72;
		int textureY = 140;

		for (SkillBase skill : page.getSkillList()) {
			drawConnectivity(skill, guiLeft, guiTop, true);
			drawConnectivity(skill, guiLeft, guiTop, false);
		}
		for (SkillBase skill : page.getSkillList()) {
			GlStateManager.enableAlpha();
			GlStateManager.color(1, 1, 1, 1);
			this.mc.getTextureManager().bindTexture(SKILL_TREE_BACKGROUND);
			int posX = startX + 19 * skill.getColumn();
			int posY = startY + 18 * skill.getRow();
			boolean isActive = SkillTreeApi.isSkillActive(mc.player, skill);
			drawScaledCustomSizeModalRect(posX, posY, textureX, textureY + (isActive ? 0 : 26), 26, 26, 16, 16, 256,
					256);
			GlStateManager.pushMatrix();
			GlStateManager.translate(posX, posY, 0);
			GlStateManager.scale(0.5, 0.5, 1);
			itemRender.renderItemAndEffectIntoGUI(skill.getIcon(), 8, 8);
			GlStateManager.popMatrix();
		}
	}

	protected void renderCustomBackround(float partialTicks, int mouseX, int mouseY) {

	}

	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		int i = this.guiLeft;
		int j = this.guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX && pointX < rectX + rectWidth && pointY >= rectY && pointY < rectY + rectHeight;
	}

	protected void renderRepeating(int startX, int startY, int repeatX, int repeatY, int textureX, int textureY,
			int textureWidth, int textureHeight) {
		for (int i = 0; i < repeatX; i++) {
			int j = startX + i * textureWidth;

			for (int l = 0; l < repeatY; l++) {
				int i1 = startY + l * textureHeight;
				this.drawTexturedModalRect(j, i1, textureX, textureY, textureWidth, textureHeight);
			}
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (!(button instanceof GuiSkillButton))
			return;
		// GuiSkillButton skillButton = (GuiSkillButton) button;
	}

}
