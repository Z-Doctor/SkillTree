package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.SkillTreeApi;
import zdoctor.zskilltree.api.interfaces.ISkillTreeScreen;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skill.SkillDisplayInfo;

import java.util.function.Supplier;

public class GuiSkill extends ImageScreen {
    protected static final float xSpacing = 28f;
    protected static final float ySpacing = 27f;

    private final Skill skill;
    private final ISkillTreeScreen skillScreen;
    private final SkillDisplayInfo displayInfo;

    public GuiSkill(Skill skill, ISkillTreeScreen skillScreen) {
        super(skill.getDisplayInfo().getSkillName());
        this.skill = skill;
        this.skillScreen = skillScreen;
        this.displayInfo = skill.getDisplayInfo();
//        setAnchorPoint(AnchorPoint.ABSOLUTE);
//        setPivotPoint(AnchorPoint.ABSOLUTE);
//        setOffsets((int)xSpacing * xCount, displayInfo.getY());
        // TODO Setup child skills
        // TODO Get skill progress, prob through method from listener
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // TODO Check if obtained or not
        int x = getTrueOffsetX();
        int y = getTrueOffsetY();
        renderAt(matrixStack, x, y, partialTicks);

        RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.itemRenderer.zLevel = 50f;

        x += 5;
        y += 5;
        RenderSystem.enableRescaleNormal();
        ItemStack itemstack = displayInfo.getIcon();
        this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(itemstack, x, y);
        this.itemRenderer.renderItemOverlays(this.font, itemstack, x, y);
        this.itemRenderer.zLevel = 0.0F;

        if (isMouseOver(mouseX, mouseY)) {
            // TODO Draw hover text
            renderWrappedToolTip(matrixStack, getTooltipFromSkill(getSkill()), mouseX, mouseY, font);
        }
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int button) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            skillScreen.getClientTracker().setSelectedSkill(getSkill(), true);
            return true;
        }
        return false;
    }

    @Override
    public ImageAsset getImage() {
        if (SkillTreeApi.Client.hasSkill(skill))
            return ImageAssets.COMMON_FRAME_OWNED;
        return ImageAssets.COMMON_FRAME_UNOWNED;
    }

    public void drawSkillHover(MatrixStack matrixStack, int x, int y, float fade, int width, int height) {
//        boolean flag = width + x + this.x + this.width + 26 >= this.guiAdvancementTab.getScreen().width;
//
//        String s = this.advancementProgress == null ? null : this.advancementProgress.getProgressText();
//        int i = s == null ? 0 : this.minecraft.fontRenderer.getStringWidth(s);
//        boolean flag1 = 113 - y - this.y - 26 <= 6 + this.description.size() * 9;
//        float f = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
//        int j = MathHelper.floor(f * (float) this.width);
//        AdvancementState advancementstate;
//        AdvancementState advancementstate1;
//        AdvancementState advancementstate2;
//        if (f >= 1.0F) {
//            j = this.width / 2;
//            advancementstate = AdvancementState.OBTAINED;
//            advancementstate1 = AdvancementState.OBTAINED;
//            advancementstate2 = AdvancementState.OBTAINED;
//        } else if (j < 2) {
//            j = this.width / 2;
//            advancementstate = AdvancementState.UNOBTAINED;
//            advancementstate1 = AdvancementState.UNOBTAINED;
//            advancementstate2 = AdvancementState.UNOBTAINED;
//        } else if (j > this.width - 2) {
//            j = this.width / 2;
//            advancementstate = AdvancementState.OBTAINED;
//            advancementstate1 = AdvancementState.OBTAINED;
//            advancementstate2 = AdvancementState.UNOBTAINED;
//        } else {
//            advancementstate = AdvancementState.OBTAINED;
//            advancementstate1 = AdvancementState.UNOBTAINED;
//            advancementstate2 = AdvancementState.UNOBTAINED;
//        }
//
//        int k = this.width - j;
//        this.minecraft.getTextureManager().bindTexture(WIDGETS);
//        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
//        RenderSystem.enableBlend();
//        int l = y + this.y;
//        int i1;
//        if (flag) {
//            i1 = x + this.x - this.width + 26 + 6;
//        } else {
//            i1 = x + this.x;
//        }
//
//        int j1 = 32 + this.description.size() * 9;
//        if (!this.description.isEmpty()) {
//            if (flag1) {
//                this.drawDescriptionBox(matrixStack, i1, l + 26 - j1, this.width, j1, 10, 200, 26, 0, 52);
//            } else {
//                this.drawDescriptionBox(matrixStack, i1, l, this.width, j1, 10, 200, 26, 0, 52);
//            }
//        }
//
//        this.blit(matrixStack, i1, l, 0, advancementstate.getId() * 26, j, 26);
//        this.blit(matrixStack, i1 + j, l, 200 - k, advancementstate1.getId() * 26, k, 26);
//        this.blit(matrixStack, x + this.x + 3, y + this.y, this.displayInfo.getFrame().getIcon(), 128 + advancementstate2.getId() * 26, 26, 26);
//        if (flag) {
//            this.minecraft.fontRenderer.func_238407_a_(matrixStack, this.title, (float) (i1 + 5), (float) (y + this.y + 9), -1);
//            if (s != null) {
//                this.minecraft.fontRenderer.drawStringWithShadow(matrixStack, s, (float) (x + this.x - i), (float) (y + this.y + 9), -1);
//            }
//        } else {
//            this.minecraft.fontRenderer.func_238407_a_(matrixStack, this.title, (float) (x + this.x + 32), (float) (y + this.y + 9), -1);
//            if (s != null) {
//                this.minecraft.fontRenderer.drawStringWithShadow(matrixStack, s, (float) (x + this.x + this.width - i - 5), (float) (y + this.y + 9), -1);
//            }
//        }
//
//        if (flag1) {
//            for (int k1 = 0; k1 < this.description.size(); ++k1) {
//                this.minecraft.fontRenderer.func_238422_b_(matrixStack, this.description.get(k1), (float) (i1 + 5), (float) (l + 26 - j1 + 7 + k1 * 9), -5592406);
//            }
//        } else {
//            for (int l1 = 0; l1 < this.description.size(); ++l1) {
//                this.minecraft.fontRenderer.func_238422_b_(matrixStack, this.description.get(l1), (float) (i1 + 5), (float) (y + this.y + 9 + 17 + l1 * 9), -5592406);
//            }
//        }
//
//        this.minecraft.getItemRenderer().renderItemAndEffectIntoGuiWithoutEntity(this.displayInfo.getIcon(), x + this.x + 8, y + this.y + 5);
    }

}
