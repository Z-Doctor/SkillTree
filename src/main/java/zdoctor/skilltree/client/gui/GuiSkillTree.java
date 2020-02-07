package zdoctor.skilltree.client.gui;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.client.GuiPageRegistry;
import zdoctor.skilltree.skills.pages.SkillPageBase;
import zdoctor.skilltree.tabs.SkillTabs;

@SideOnly(Side.CLIENT)
public class GuiSkillTree extends GuiSkillScreen {

	private static int selectedTabIndex = SkillTabs.PLAYER_INFO.getTabIndex();

	private int maxPages = 0;

	private static int tabPage = 0;

	public GuiSkillTree() {
		this.allowUserInput = true;
		this.xSize = 252;
		this.ySize = 140;
	}

	@Override
	public void initGui() {
		super.initGui();

		int tabCount = SkillTabs.SKILL_TABS.size();
		while (SkillTabs.SKILL_TABS.get(tabCount - 1) == null) {
			tabCount -= 1;
		}
		maxPages = MathHelper.ceil((float) tabCount / SkillTabs.getTabsPerPage()) - 1;
		if (maxPages > 0) {
			buttonList.add(new GuiButton(101, guiLeft, guiTop - 50, 20, 20, "<"));
			buttonList.add(new GuiButton(102, guiLeft + xSize - 20, guiTop - 50, 20, 20, ">"));
		}

		while (pageList.size() < tabCount) {
			pageList.add(new GuiSkillPage(null));
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
				pageList.set(tab.getTabIndex(), pageGui);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		setCurrentTab(SkillTabs.SKILL_TABS.get(selectedTabIndex));

	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		int i = this.guiLeft;
		int j = this.guiTop;
		this.drawGuiBackgroundLayer(partialTicks, mouseX, mouseY);
		this.drawPageBackgroundLayer(selectedTabIndex, partialTicks, mouseX, mouseY);
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();
		super.drawScreen(mouseX, mouseY, partialTicks);
		RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableRescaleNormal();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.disableLighting();
		RenderHelper.disableStandardItemLighting();
		this.drawPageScreen(selectedTabIndex, mouseX, mouseY, partialTicks);
		RenderHelper.disableStandardItemLighting();
		this.drawGuiForegroundLayer(mouseX, mouseY);
		this.drawPageForeground(selectedTabIndex, mouseX, mouseY);

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

	@Override
	public void drawGuiForegroundLayer(int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		this.mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
		this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		RenderHelper.enableGUIStandardItemLighting();

		for (SkillTabs tab : SkillTabs.SKILL_TABS) {
			this.mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_TABS);

			if (tab == null || tab.getTabPage() != tabPage)
				continue;
			this.drawTab(tab);
		}
		for (SkillTabs tab : SkillTabs.SKILL_TABS) {
			if (isMouseOverTab(tab, mouseX, mouseY)) {
				this.drawHoveringText(tab.getTranslatedTabLabel(), mouseX, mouseY);
				break;
			}
		}

	}

	@Override
	public void drawGuiBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		drawDefaultBackground();
	}

	public boolean isMouseOverTab(SkillTabs tab, int mouseX, int mouseY) {
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

	public void setCurrentTab(SkillTabs tab) {
		if (tab == null)
			return;
		int i = selectedTabIndex;
		selectedTabIndex = tab.getTabIndex();
		getGuiPage(i).allowUserInput = false;
		getGuiPage(selectedTabIndex).allowUserInput = true;
	}

	protected void drawTab(SkillTabs tab) {
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
		spacer = spacer + 6;
		itemstackOffset = itemstackOffset + 8 + (isTopRow ? 1 : -1);
		GlStateManager.enableLighting();
		GlStateManager.enableRescaleNormal();
		GlStateManager.enableBlend();
		ItemStack itemstack = tab.getIconItemStack();
		GlStateManager.enableDepth();
		this.itemRender.zLevel = 300;
		this.itemRender.renderItemAndEffectIntoGUI(itemstack, spacer, itemstackOffset);
		this.itemRender.zLevel = 0;
		GlStateManager.disableDepth();
		GlStateManager.disableLighting();
		if (isSelected) {
			fontRenderer.drawString(tab.getTranslatedTabLabel(), this.guiLeft + 11, this.guiTop + 8, 0x404040, false);
			fontRenderer.drawString(tab.getTranslatedTabLabel(), this.guiLeft + 10, this.guiTop + 7, 0x909090, false);
		}
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.id == 101) {
			tabPage = Math.max(tabPage - 1, 0);
		} else if (button.id == 102) {
			tabPage = Math.min(tabPage + 1, maxPages);
		}

	}
	
	@Override
	protected void keyTyped(char par1, int par2) throws IOException {
		if (par2 == KeyHandler.OPEN_SKILL_TREE.getKeyCode()) {
			this.mc.player.closeScreen();
		} else {
			super.keyTyped(par1, par2);
		} 
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

}
