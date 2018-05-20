package zdoctor.skilltree.client.gui;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.client.GuiPageRegistry;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.pages.SkillPageBase;
import zdoctor.skilltree.tabs.SkillTabs;

public class GuiSkillTree extends GuiScreen {
	/** The location of the skill tree tabs texture */
	public static final ResourceLocation SKILL_TREE_TABS = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/tabs.png");

	public static final ResourceLocation SKILL_TREE_BACKGROUND = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/skill_tree.png");

	private static int selectedTabIndex = SkillTabs.PLAYER_INFO.getTabIndex();

	public int xSize;
	public int ySize;
	public int guiLeft;
	public int guiTop;

	private int maxPages = 0;

	private static int tabPage = 0;
	private static ArrayList<GuiSkillPage> pages = new ArrayList();

	public GuiSkillTree() {
		this.allowUserInput = true;
		this.xSize = 252;
		this.ySize = 140;
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		pages.forEach(GuiScreen::updateScreen);
	}

	@Override
	public void initGui() {
		buttonList.clear();
		this.guiLeft = (this.width - this.xSize) / 2;
		this.guiTop = (this.height - this.ySize) / 2;
		Keyboard.enableRepeatEvents(false);
		int i = selectedTabIndex;
		selectedTabIndex = -1;
		setCurrentTab(SkillTabs.SKILL_TABS.get(i));
		int tabCount = SkillTabs.SKILL_TABS.size();
		while (SkillTabs.SKILL_TABS.get(tabCount - 1) == null) {
			tabCount -= 1;
		}
		maxPages = MathHelper.ceil((float) tabCount / SkillTabs.getTabsPerPage()) - 1;
		if (maxPages > 0) {
			buttonList.add(new GuiButton(101, guiLeft, guiTop - 50, 20, 20, "<"));
			buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
		}

		pages.clear();
		while (pages.size() < tabCount) {
			pages.add(new GuiSkillPage(null));
		}
		for (SkillTabs tab : SkillTabs.SKILL_TABS) {
			if (tab == null)
				continue;
			Class<? extends GuiSkillPage> pageGuiClass = GuiPageRegistry.getGui(tab.getPage().getClass());
			try {
				Constructor<? extends GuiSkillPage> constructor = pageGuiClass.getConstructor(SkillPageBase.class);
				GuiSkillPage pageGui = constructor.newInstance(tab.getPage());
				pageGui.setWorldAndResolution(mc, width, height);
				pageGui.guiLeft = guiLeft;
				pageGui.guiTop = guiTop;
				pageGui.xSize = xSize;
				pageGui.ySize = ySize;
				pageGui.initGui();
				pages.set(tab.getTabIndex(), pageGui);
				// pages.set(tab.getTabIndex(), pageGui);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawGuiBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.pushMatrix();
		pages.get(selectedTabIndex).drawGuiBackgroundLayer(partialTicks, mouseX, mouseY);
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.pushMatrix();
		pages.get(selectedTabIndex).drawScreen(mouseX, mouseY, partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.pushMatrix();
		pages.get(selectedTabIndex).drawGuiForegroundLayer(mouseX, mouseY);
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		this.drawGuiForegroundLayer(mouseX, mouseY);

		GlStateManager.popMatrix();
		GlStateManager.enableLighting();
		GlStateManager.enableDepth();
		RenderHelper.enableStandardItemLighting();

		if (maxPages > 0) {
			String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
			int width = fontRenderer.getStringWidth(page);
			GlStateManager.disableLighting();
			this.zLevel = 300.0F;
			itemRender.zLevel = 300.0F;
			fontRenderer.drawString(page, guiLeft + (xSize / 2) - (width / 2), guiTop - 44, -1);
			this.zLevel = 0.0F;
			itemRender.zLevel = 0.0F;
		}
	}

	protected boolean renderSkillTreeHoveringText(SkillTabs tab, int mouseX, int mouseY) {
		if (isMouseOverTab(tab, mouseX, mouseY)) {
			this.drawHoveringText(I18n.format(tab.getTranslatedTabLabel()), mouseX, mouseY);
			return true;
		}
		return false;
	}

	protected boolean isPointInRegion(int rectX, int rectY, int rectWidth, int rectHeight, int pointX, int pointY) {
		int i = this.guiLeft;
		int j = this.guiTop;
		pointX = pointX - i;
		pointY = pointY - j;
		return pointX >= rectX - 1 && pointX < rectX + rectWidth + 1 && pointY >= rectY - 1
				&& pointY < rectY + rectHeight + 1;
	}

	public void drawGuiForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		this.mc.getTextureManager().bindTexture(SKILL_TREE_BACKGROUND);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();

		for (SkillTabs tab : SkillTabs.SKILL_TABS) {
			this.mc.getTextureManager().bindTexture(SKILL_TREE_TABS);

			if (tab == null || tab.getTabPage() != tabPage)
				continue;
			this.drawTab(tab);

		}

		for (SkillTabs tab : SkillTabs.SKILL_TABS) {
			if (tab == null)
				continue;
			if (this.renderSkillTreeHoveringText(tab, mouseX, mouseY)) {
				break;
			}
		}

		for (SkillBase skill : pages.get(selectedTabIndex).page.getSkillList()) {
			if (pages.get(selectedTabIndex).renderSkillTooltip(skill, mouseX, mouseY)) {
				break;
			}
		}
	}

	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
	}

	private void drawTab(SkillTabs tab) {
		boolean isSelected = tab.getTabIndex() == selectedTabIndex;
		boolean isTopRow = tab.isTabTopRow();

		int column = tab.getTabColumn();
		int type = (tab.isAlignedLeft() ? 0 : tab.isAlignedRight() ? 2 : 1) * 28;
		int tabType = 0;
		int spacer = guiLeft + 28 * column + column;
		int itemstackOffset = this.guiTop;
		int j1 = 32;

		if (isSelected) {
			tabType += 32;
		}

		if (isTopRow) {
			itemstackOffset = itemstackOffset - 28;
		} else {
			type += 84;
			itemstackOffset = itemstackOffset + (this.ySize - 4);
			spacer += 21;
		}

		GlStateManager.disableLighting();
		GlStateManager.color(1F, 1F, 1F);
		GlStateManager.enableBlend();
		this.drawTexturedModalRect(spacer, itemstackOffset, type, tabType, 28, 32);
		this.zLevel = 100.0F;
		this.itemRender.zLevel = 100.0F;
		spacer = spacer + 6;
		itemstackOffset = itemstackOffset + 8 + (isTopRow ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		ItemStack itemstack = tab.getIconItemStack();
		this.itemRender.renderItemAndEffectIntoGUI(itemstack, spacer, itemstackOffset);
		this.itemRender.renderItemOverlays(this.fontRenderer, itemstack, spacer, itemstackOffset);
		GlStateManager.disableLighting();
		this.itemRender.zLevel = 0.0F;
		this.zLevel = 0.0F;
		if (isSelected) {
			fontRenderer.drawString(tab.getTranslatedTabLabel(), this.guiLeft + 11, this.guiTop + 8, 0x404040, false);
			fontRenderer.drawString(tab.getTranslatedTabLabel(), this.guiLeft + 10, this.guiTop + 7, 0x909090, false);
		}
		// fontRenderer.drawStringWithShadow(tab.getTranslatedTabLabel(), this.guiLeft +
		// 10, this.guiTop + 5, 0);
	}

	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	public void setCurrentTab(SkillTabs tab) {
		if (tab == null)
			return;
		int i = selectedTabIndex;
		selectedTabIndex = tab.getTabIndex();
	}

	protected boolean isMouseOverTab(SkillTabs tab, int mouseX, int mouseY) {
		if (tab == null || tab.getTabPage() != tabPage)
			return false;

		mouseX -= this.guiLeft;
		mouseY -= this.guiTop;

		int column = tab.getTabColumn();
		int xOffset = 28 * column + column;
		int yOffset = 0;

		if (tab.isTabTopRow()) {
			yOffset -= 32;
		} else {
			yOffset += this.ySize;
			xOffset += 21;
		}

		return mouseX >= xOffset && mouseX <= xOffset + 28 && mouseY >= yOffset && mouseY <= yOffset + 32;
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if (mouseButton == 0) {
			for (SkillTabs tab : SkillTabs.SKILL_TABS) {
				if (this.isMouseOverTab(tab, mouseX, mouseY)) {
					return;
				}
			}
		}

		super.mouseClicked(mouseX, mouseY, mouseButton);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (state == 0) {
			for (SkillTabs tab : SkillTabs.SKILL_TABS) {
				if (tab != null && this.isMouseOverTab(tab, mouseX, mouseY)) {
					this.setCurrentTab(tab);
					return;
				}
			}
		}

		super.mouseReleased(mouseX, mouseY, state);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.player.getStatFileWriter()));
		}

		if (button.id == 101) {
			tabPage = Math.max(tabPage - 1, 0);
		} else if (button.id == 102) {
			tabPage = Math.min(tabPage + 1, maxPages);
		}

	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		pages.get(selectedTabIndex).handleMouseInput();
	}

	@Override
	public boolean handleComponentClick(ITextComponent component) {
		boolean flag = super.handleComponentClick(component);
		pages.get(selectedTabIndex).handleComponentClick(component);
		return flag;
	}

	@Override
	public void handleInput() throws IOException {
		super.handleInput();
		pages.get(selectedTabIndex).handleInput();
	}

	@Override
	public void handleKeyboardInput() throws IOException {
		super.handleKeyboardInput();
		pages.get(selectedTabIndex).handleKeyboardInput();
	}

}
