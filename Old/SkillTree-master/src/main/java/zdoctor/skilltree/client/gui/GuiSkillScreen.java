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

@SideOnly(Side.CLIENT)
public class GuiSkillScreen extends GuiScreen {
	public int xSize;
	public int ySize;
	public int guiLeft;
	public int guiTop;

	protected final ArrayList<GuiSkillPage> pageList = new ArrayList();

	public int offsetX;
	public int offsetY;

	public int maxOffsetX;
	public int maxOffsetXNeg;
	public int maxOffsetY;
	public int maxOffsetYNeg;

	public boolean mouseDown;
	public int lastX;
	public int lastY;

	public int minBoundryX;
	public int minBoundryY;
	public int maxBoundryX;
	public int maxBoundryY;

	@Override
	public void updateScreen() {
		super.updateScreen();
		pageList.forEach(GuiScreen::updateScreen);
	}

	@Override
	public void initGui() {
		offsetX = 0;
		offsetY = 0;

		maxOffsetX = 0;
		maxOffsetXNeg = 0;
		maxOffsetY = 0;
		maxOffsetYNeg = 0;

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

	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

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

	protected void drawSkillToolTip(SkillPageBase page, SkillBase skill, int mouseX, int mouseY) {
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		int i = 0;

		List<SkillToolTip> tempTip = skill.getToolTip(mc.player);
		List<SkillToolTip> toolTip = new ArrayList<>();

		int maxWidth = 0;
		for (int index = 0; index < tempTip.size(); index++) {
			SkillToolTip originalTip = tempTip.get(index);
			int width = this.fontRenderer.getStringWidth(originalTip.getTransatedText());
			if (index == 0) {
				maxWidth = width < 125 ? 125 : width;
				toolTip.add(originalTip);
				continue;
			}

			if (width > maxWidth) {
				String[] text = originalTip.getTransatedText().split(" ");
				if (text.length == 1) {
					SkillToolTip tip = new SkillToolTip(text[0], originalTip.getTextColor(), new Object[0]);
					toolTip.add(tip);
					continue;
				}

				String cache = "";
				for (int index1 = 0; index1 < text.length; index1++) {
					String nextString = text[index1];
					int newWidth = this.fontRenderer.getStringWidth(cache + (cache.equals("") ? "" : " ") + nextString);
					if (newWidth <= maxWidth)
						cache += (cache.equals("") ? "" : " ") + nextString;
					else {
						if (!cache.equals("")) {
							SkillToolTip tip = new SkillToolTip(cache, originalTip.getTextColor(), new Object[0]);
							toolTip.add(tip);
						}
						if (newWidth > maxWidth)
							cache = nextString;
						else
							cache = "";
					}

					if (index1 + 1 == text.length) {
						SkillToolTip tip = new SkillToolTip(cache, originalTip.getTextColor(), new Object[0]);
						toolTip.add(tip);
					}

				}
			} else {
				toolTip.add(originalTip);
			}
		}

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

	public void drawConnectivity(SkillPageBase page, SkillBase skill, int offsetX, int offsetY, boolean outerLine) {
		if (skill.getChildren().isEmpty())
			return;

		int color = outerLine ? -16777216 : -1;

		int initX = this.guiLeft + offsetX + 13;
		int initY = this.guiTop + offsetY + 21;

		int parentX = 8 + initX + 19 * page.getColumn(skill);
		int parentHalfX = 16 + initX + 19 * page.getColumn(skill) + 1;
		int parentY = 8 + initY + 18 * page.getRow(skill);

		parentX = Math.max(this.minBoundryX, Math.min(this.maxBoundryX, parentX));
		parentHalfX = Math.max(this.minBoundryX, Math.min(this.maxBoundryX, parentHalfX));
		parentY = Math.max(this.minBoundryY, Math.min(this.maxBoundryY, parentY));

		int parentHalfX1 = Math.max(this.minBoundryX, Math.min(this.maxBoundryX, parentHalfX + 1));
		int parentHalfX2 = Math.max(this.minBoundryX, Math.min(this.maxBoundryX, parentHalfX - 1));

		int parentY1 = Math.max(this.minBoundryY, Math.min(this.maxBoundryY, parentY + 1));
		int parentY2 = Math.max(this.minBoundryY, Math.min(this.maxBoundryY, parentY - 1));

		boolean lineDrawn = false;

		for (SkillBase child : skill.getChildren()) {
			if (child == null) {
				ModMain.proxy.log.catching(new NullPointerException("Tried to draw null child for parent '"
						+ skill.getRegistryName() + "' on page '" + page.getRegistryName() + "'"));
				continue;
			}
			if (!page.hasSkillInPage(child))
				continue;

			if (!child.shouldDrawSkill(mc.player))
				continue;

			int childX = 8 + initX + 19 * page.getColumn(child);
			int childHalfX = 16 + initX + 19 * page.getColumn(child);
			int childY = 8 + initY + 18 * page.getRow(child);

			childX = Math.max(this.minBoundryX, Math.min(this.maxBoundryX, childX));
			childHalfX = Math.max(this.minBoundryX, Math.min(this.maxBoundryX, childHalfX));
			childY = Math.max(this.minBoundryY, Math.min(this.maxBoundryY, childY));

			if (outerLine) {
				drawHorizontalLine(childX, parentHalfX2,
						Math.max(this.minBoundryY, Math.min(this.maxBoundryY, childY - 1)), color);
				drawHorizontalLine(childX, parentHalfX1,
						Math.max(this.minBoundryY, Math.min(this.maxBoundryY, childY - 1)), color);
				drawHorizontalLine(childX, parentHalfX,
						Math.max(this.minBoundryY, Math.min(this.maxBoundryY, childY + 1)), color);

				int lineY = page.getColumn(child) > page.getColumn(skill) ? 2 : -2;

				if (page.getColumn(child) > page.getColumn(skill)) {
					lineY *= page.getRow(child) < page.getRow(skill) ? -1 : 1;
				} else if (page.getColumn(child) < page.getColumn(skill)) {
					lineY *= page.getRow(child) < page.getRow(skill) ? 1 : -1;
				}

				int parentY3 = Math.max(this.minBoundryY, Math.min(this.maxBoundryY, parentY - lineY));

				drawVerticalLine(parentHalfX2, childY + lineY, parentY3, color);
				drawVerticalLine(parentHalfX1, childY + lineY, parentY3, color);

			} else {
				drawHorizontalLine(parentHalfX, childX, childY, color);
				drawVerticalLine(parentHalfX, parentY, childY, color);
			}
			lineDrawn = true;
		}

		if (lineDrawn) {
			if (outerLine) {
				drawHorizontalLine(parentHalfX1, parentX, parentY2, color);
				drawHorizontalLine(parentHalfX1, parentX, parentY1, color);
				drawHorizontalLine(parentHalfX1, parentX, parentY, color);
			} else
				drawHorizontalLine(parentHalfX, parentX, parentY, color);
		}
	}

	public boolean isPointWithinBounds(int pointX, int pointY) {

		return false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if (mouseDown && isPointInRegion(9, 18, xSize, ySize, mouseX, mouseY)) {
			offsetX += mouseX - lastX;
			offsetY += mouseY - lastY;

			offsetX = offsetX > maxOffsetXNeg ? maxOffsetXNeg : offsetX < maxOffsetX ? maxOffsetX : offsetX;
			offsetY = offsetY > maxOffsetYNeg ? maxOffsetYNeg : offsetY < maxOffsetY ? maxOffsetY : offsetY;
		}
		lastX = mouseX;
		lastY = mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	// Gui Inputs

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		lastX = mouseX;
		lastY = mouseY;
		mouseDown = true;

		for (int i = 0; i < this.buttonList.size(); ++i) {
			GuiButton guibutton = this.buttonList.get(i);

			if (guibutton.isMouseOver()) {
				mouseDown = false;
			}

			if (guibutton.mousePressed(this.mc, mouseX, mouseY)) {
				net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(
						this, guibutton, this.buttonList);
				if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
					break;
				guibutton = event.getButton();
				this.selectedButton = guibutton;
				guibutton.playPressSound(this.mc.getSoundHandler());
				this.actionPerformed(guibutton);
				if (this.equals(this.mc.currentScreen))
					net.minecraftforge.common.MinecraftForge.EVENT_BUS
							.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this,
									event.getButton(), this.buttonList));
			}
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);
		mouseDown = false;
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
