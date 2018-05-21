package zdoctor.skilltree.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.client.SkillToolTip;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.pages.SkillPageBase;
import zdoctor.skilltree.tabs.SkillTabs;

@SideOnly(Side.CLIENT)
public class GuiSkillScreen extends GuiScreen {
	public int xSize;
	public int ySize;
	public int guiLeft;
	public int guiTop;

	protected final ArrayList<GuiSkillPage> pageList = new ArrayList();

	@Override
	public void updateScreen() {
		super.updateScreen();
		pageList.forEach(GuiScreen::updateScreen);
	}

	@Override
	public void initGui() {
		buttonList.clear();
		pageList.clear();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		Keyboard.enableRepeatEvents(false);
	}

	protected void drawPageBackgroundLayer(int pageIndex, float partialTicks, int mouseX, int mouseY) {
		if (pageList.isEmpty())
			return;

		if (pageIndex >= pageList.size() || pageIndex < 0 || pageList.get(pageIndex) == null) {
			ModMain.proxy.log.debug(
					"Tried to draw invalid child page. GuiScreen: {} Type: {} Index: {} Partial Ticks: {} MouseX: {} MouseY: {} PageList: {}",
					this, "Background", pageIndex, partialTicks, mouseX, mouseY, pageList.toArray());
			return;
		}
		pageList.get(pageIndex).drawGuiBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	protected void drawPageScreen(int pageIndex, int mouseX, int mouseY, float partialTicks) {
		if (pageList.isEmpty())
			return;

		if (pageIndex >= pageList.size() || pageIndex < 0 || pageList.get(pageIndex) == null) {
			ModMain.proxy.log.debug(
					"Tried to draw invalid child page. GuiScreen: {} Type: {} Index: {} Partial Ticks: {} MouseX: {} MouseY: {} PageList: {}",
					this, "Screen", pageIndex, partialTicks, mouseX, mouseY, pageList.toArray());
			return;
		}
		pageList.get(pageIndex).drawScreen(mouseX, mouseY, partialTicks);
	}

	protected void drawPageForeground(int pageIndex, int mouseX, int mouseY) {
		if (pageList.isEmpty())
			return;

		if (pageIndex >= pageList.size() || pageIndex < 0 || pageList.get(pageIndex) == null) {
			ModMain.proxy.log.debug(
					"Tried to draw invalid child page. GuiScreen: {} Type: {} Index: {} MouseX: {} MouseY: {} PageList: {}",
					this, "Foreground", pageIndex, mouseX, mouseY);
			return;
		}
		pageList.get(pageIndex).drawGuiForegroundLayer(mouseX, mouseY);
	}

	public void drawGuiForegroundLayer(int mouseX, int mouseY) {

	}

	public GuiSkillPage getGuiPage(int pageIndex) {
		if (pageList.isEmpty())
			return new GuiSkillPage(null);

		if (pageIndex >= pageList.size() || pageIndex < 0 || pageList.get(pageIndex) == null) {
			ModMain.proxy.log.debug("Tried to get invalid page. GuiScreen: {} Index: PageList: {}", this, pageIndex,
					pageList);
			return null;
		}

		return pageList.get(pageIndex);
	}

	public SkillPageBase getSkillPage(int pageIndex) {
		if (pageList.isEmpty())
			return SkillPageBase.EMPTY;

		if (pageIndex >= pageList.size() || pageIndex < 0 || pageList.get(pageIndex) == null) {
			ModMain.proxy.log.debug("Tried to get invalid page. GuiScreen: {} Index: PageList: {}", this, pageIndex,
					pageList);
			return null;
		}

		return getGuiPage(pageIndex).page;
	}

	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

	}

	protected void renderSkillTreeHoveringText(SkillTabs tab, int mouseX, int mouseY) {
		// if (isMouseOverTab(tab, mouseX, mouseY)) {
		this.drawHoveringText(tab.getTranslatedTabLabel(), mouseX, mouseY);
		// return true;
		// }
		// return false;
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

	public boolean renderSkillTooltip(SkillBase skill, int mouseX, int mouseY) {
		if (!isMouseOverSkill(skill, mouseX, mouseY))
			return false;

		this.drawSkillSlot(skill, mouseX, mouseY);
		return true;
	}

	protected void drawSkillSlot(SkillBase skill, int mouseX, int mouseY) {
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

		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableRescaleNormal();
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

	// Gui Inputs

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		pageList.forEach(child -> {
			if (child.allowUserInput)
				try {
					child.handleMouseInput();
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
	}

	@Override
	public boolean handleComponentClick(ITextComponent component) {
		boolean flag = super.handleComponentClick(component);
		pageList.forEach(child -> {
			if (child.allowUserInput)
				child.handleComponentClick(component);
		});
		return flag;
	}

	@Override
	public void handleInput() throws IOException {
		super.handleInput();
		pageList.forEach(child -> {
			if (child.allowUserInput)
				try {
					child.handleInput();
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		super.handleKeyboardInput();
		pageList.forEach(child -> {
			if (child.allowUserInput)
				try {
					child.handleKeyboardInput();
				} catch (IOException e) {
					e.printStackTrace();
				}
		});
	}

}
