package zdoctor.skilltree.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.EnumSkillInteractType;
import zdoctor.skilltree.api.skills.interfaces.ISkillToggle;
import zdoctor.skilltree.client.KeyHandler;
import zdoctor.skilltree.events.SkillEvent;
import zdoctor.skilltree.events.SkillEvent.ReloadPages.Pre;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.network.play.server.SPacketSkillSlotInteract;
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

	int minIndexX;
	int minIndexY;
	int maxIndexX;
	int maxIndexY;

	public SkillPageBase page;

	public GuiSkillPage(SkillPageBase skillPage) {
		this.page = skillPage;
	}

	@Override
	public void initGui() {
		super.initGui();
		if (page != null) {
			this.minBoundryX = this.guiLeft + 4;
			this.minBoundryY = this.guiTop + 9;
			this.maxBoundryX = this.guiLeft + 247;
			this.maxBoundryY = this.guiTop + 138;

			minIndexX = 0;
			minIndexY = 0;
			maxIndexX = 0;
			maxIndexY = 0;

			for (SkillBase skill : page.getSkillList()) {
				GuiSkillButton button;
				addButton(button = new GuiSkillButton(page, this, skill, page.getColumn(skill), page.getRow(skill),
						guiLeft, guiTop));
				minIndexX = Math.min(minIndexX, page.getColumn(skill));
				minIndexY = Math.min(minIndexY, page.getRow(skill));
				maxIndexX = Math.max(maxIndexX, page.getColumn(skill));
				maxIndexY = Math.max(maxIndexY, page.getRow(skill));
			}

			maxOffsetXNeg = minIndexX < 0 ? -19 * minIndexX : 0;
			maxOffsetYNeg = minIndexY < 0 ? -18 * minIndexY : 0;
			maxOffsetX = maxIndexX > 11 ? -19 * maxIndexX + 209 : 0;
			maxOffsetY = maxIndexY > 5 ? -18 * maxIndexY + 90 : 0;
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
		for (GuiButton button : buttonList) {
			if (button.isMouseOver() && button instanceof GuiSkillButton) {
				GuiSkillButton skillButton = (GuiSkillButton) button;
				drawSkillToolTip(skillButton.page, skillButton.skill, mouseX, mouseY);
			}
		}
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

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (keyCode == KeyHandler.RECENTER_SKILL_TREE.getKeyCode()) {
			SkillEvent.RecenterPage event = new SkillEvent.RecenterPage(page);
			MinecraftForge.EVENT_BUS.post(event);
			if (!event.isCanceled())
				this.initGui();
			Pre event1 = new SkillEvent.ReloadPages.Pre(this.page);
			MinecraftForge.EVENT_BUS.post(event1);
			if (!event1.isCanceled()) {
				this.initGui();
				MinecraftForge.EVENT_BUS.post(new SkillEvent.ReloadPages.Post(this.page));
			}

		} else
			super.keyTyped(typedChar, keyCode);
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		if (this.selectedButton != null && state == 1 && this.selectedButton instanceof GuiSkillButton) {
			GuiSkillButton skillButton = (GuiSkillButton) this.selectedButton;
			SkillBase skill = skillButton.getSkill();
			if (skill instanceof ISkillToggle && SkillTreeApi.hasSkill(mc.player, skill)) {
				SkillTreeApi.toggleSkill(mc.player, skill);
			}
			this.selectedButton = null;
		}
		super.mouseReleased(mouseX, mouseY, state);

	}

}
