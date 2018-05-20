package zdoctor.skilltree.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import zdoctor.skilltree.ModMain;
import zdoctor.skilltree.api.SkillTreeApi;
import zdoctor.skilltree.api.enums.EnumSkillInteractType;
import zdoctor.skilltree.api.skills.IToggleSkill;
import zdoctor.skilltree.network.SkillTreePacketHandler;
import zdoctor.skilltree.network.play.server.SPacketSkillSlotInteract;
import zdoctor.skilltree.skills.SkillBase;

public class GuiSkillButton extends GuiButton {
	public static final ResourceLocation SKILL_TREE_BACKGROUND = new ResourceLocation(
			ModMain.MODID + ":textures/gui/skilltree/skill_tree.png");

	private SkillBase skill;

	public GuiSkillButton(SkillBase skill, int startX, int startY) {
		this(0, skill, startX + 13 + 19 * skill.getColumn(), startY + 21 + 18 * skill.getRow(), 16, 16, "");
	}

	public GuiSkillButton(int buttonId, SkillBase skill, int x, int y, int widthIn, int heightIn, String buttonText) {
		super(buttonId, x, y, widthIn, heightIn, buttonText);
		this.skill = skill;
		this.enabled = true;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (this.visible) {
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
					&& mouseY < this.y + this.height;
			if (!SkillTreeApi.hasSkill(mc.player, skill)) {
				boolean hasRequirements = SkillTreeApi.hasSkillRequirements(mc.player, skill);
				if (hasRequirements && hovered) {

				} else {
					GlStateManager.pushMatrix();
					drawRect(this.x, this.y, this.x + width, this.y + height, 0xCC505050);
					mc.getTextureManager().bindTexture(SKILL_TREE_BACKGROUND);
					GlStateManager.enableAlpha();
					GlStateManager.color(1, 1, 1, 1);
					int lockType = hasRequirements ? 18 : 0;
					drawScaledCustomSizeModalRect(this.x, this.y, lockType, 176, 18, 18, 16, 16, 256, 256);
					GlStateManager.popMatrix();
				}
			}
		}
	}

	public SkillBase getSkill() {
		return skill;
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY) {
		Minecraft mc = Minecraft.getMinecraft();
		if (SkillTreeApi.hasSkill(mc.player, this.getSkill())) {
			if (this.getSkill() instanceof IToggleSkill) {
				SPacketSkillSlotInteract message = new SPacketSkillSlotInteract(this.getSkill(),
						EnumSkillInteractType.TOGGLE);
				SkillTreePacketHandler.INSTANCE.sendToServer(message);
			}
		} else if (!SkillTreeApi.hasSkill(mc.player, this.getSkill())) {
			SPacketSkillSlotInteract message = new SPacketSkillSlotInteract(this.getSkill(), EnumSkillInteractType.BUY);
			SkillTreePacketHandler.INSTANCE.sendToServer(message);
		}
	}

}
