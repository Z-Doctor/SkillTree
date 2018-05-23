package zdoctor.skilltree.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.EnumSkillInteractType;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.ISkillStackable;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.network.play.server.SPacketSkillSlotInteract;
import zdoctor.skilltree.skills.SkillBase;
import zdoctor.skilltree.skills.pages.SkillPageBase;

public class GuiSkillButton extends GuiButton {
	public static final ResourceLocation SKILL_TREE_BACKGROUND = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/skill_tree.png");

	public int textureX = 72;
	public int textureY = 140;

	public SkillBase skill;
	// public boolean hasRequirements;
	// public boolean hasSkill;

	public GuiSkillScreen parent;

	public boolean mouseDown;

	public int orginalX;
	public int oringalY;

	public SkillPageBase page;

	public GuiSkillButton(SkillPageBase page, GuiSkillScreen parent, SkillBase skill, int column, int row, int startX,
			int startY) {
		this(page, parent, 0, skill, startX + 13 + 19 * column, startY + 21 + 18 * row, 16, 16, "");
	}

	public GuiSkillButton(SkillPageBase page, GuiSkillScreen parent, int buttonId, SkillBase skill, int x, int y,
			int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.page = page;
		this.parent = parent;
		this.skill = skill;
		this.enabled = true;
		orginalX = this.x;
		oringalY = this.y;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		GlStateManager.pushMatrix();
		this.x = orginalX + parent.offsetX;
		this.y = oringalY + parent.offsetY;

		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
				&& mouseY < this.y + this.height;

		this.visible = this.x >= parent.minBoundryX && this.y >= parent.minBoundryY
				&& this.x + this.width < parent.maxBoundryX && this.y + this.height < parent.maxBoundryY;
		boolean childVisible = false;

		GlStateManager.enableDepth();
		if (!skill.getChildren().isEmpty()) {
			this.zLevel = 0;

			GlStateManager.translate(0, 0, this.zLevel);
			parent.drawConnectivity(page, skill, parent.offsetX, parent.offsetY, true);
			parent.drawConnectivity(page, skill, parent.offsetX, parent.offsetY, false);

		}
		this.zLevel = 1;
		if (this.visible) {
			GlStateManager.translate(0, 0, this.zLevel);
			drawSkillBackground(mc, this.x, this.y);

			drawSkillIcon(mc, this.x, this.y);

			GlStateManager.disableDepth();
			GlStateManager.enableAlpha();
			if (!SkillTreeApi.hasSkill(mc.player, skill))
				renderSkillLockOverlay(mc, this.x, this.y);

		}

		GlStateManager.disableDepth();
		GlStateManager.popMatrix();

	}

	protected void drawSkillBackground(Minecraft mc, int posX, int posY) {
		if (skill.getFrameType() == SkillFrameType.NONE)
			return;

		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
		boolean isActive = SkillTreeApi.isSkillActive(mc.player, skill);
		int xOffset = textureX + (skill.getFrameType().ordinal() % 4) * 26;
		int yOffset = textureY + (skill.getFrameType().ordinal() / 4) * 26;
		drawScaledCustomSizeModalRect(posX, posY, xOffset, yOffset + (isActive ? 0 : 26), 26, 26, 16, 16, 256, 256);
	}

	protected void drawSkillIcon(Minecraft mc, int posX, int posY) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(posX, posY, 0);
		GlStateManager.scale(0.5, 0.5, 1);
		GlStateManager.enableDepth();
		mc.getRenderItem().renderItemAndEffectIntoGUI(skill.getIcon(), 8, 8);
		GlStateManager.disableDepth();
		GlStateManager.popMatrix();
		if (skill instanceof ISkillStackable) {
			GlStateManager.pushMatrix();
			String tier = String.valueOf(SkillTreeApi.getSkillTier(mc.player, skill));
			GlStateManager.translate(this.x, this.y, 0);
			GlStateManager.scale(0.5, 0.5, 0);
			drawString(mc.fontRenderer, tier, 18 + 10 - mc.fontRenderer.getStringWidth(tier),
					18 + mc.fontRenderer.FONT_HEIGHT / 2, -1);
			GlStateManager.popMatrix();
		}

	}

	protected void renderSkillLockOverlay(Minecraft mc, int posX, int posY) {
		boolean hasRequirements = SkillTreeApi.hasSkillRequirements(mc.player, skill);
		if (hasRequirements && hovered)
			return;

		drawRect(posX, posY, posX + width, posY + height, 0xCC505050);
		mc.getTextureManager().bindTexture(GuiReference.SKILL_TREE_BACKGROUND);
		int lockType = hasRequirements ? 18 : 0;
		GlStateManager.color(1, 1, 1, 1);
		drawScaledCustomSizeModalRect(posX, posY, lockType, 176, 18, 18, 16, 16, 256, 256);
	}

	public SkillBase getSkill() {
		return skill;
	}

	@Override
	public boolean isMouseOver() {
		return visible && super.isMouseOver();
	}

	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		return super.mousePressed(mc, mouseX, mouseY);
	}

	@Override
	protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {

	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		if (!isMouseOver())
			return;
		Minecraft mc = Minecraft.getMinecraft();
		if (!SkillTreeApi.hasSkill(mc.player, this.getSkill()) || skill instanceof ISkillStackable) {
			SPacketSkillSlotInteract message = new SPacketSkillSlotInteract(this.getSkill(), EnumSkillInteractType.BUY);
			SkillTreePacketHandler.INSTANCE.sendToServer(message);
		}
	}

}
