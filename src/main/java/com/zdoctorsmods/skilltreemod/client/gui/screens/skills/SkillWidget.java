package com.zdoctorsmods.skilltreemod.client.gui.screens.skills;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zdoctorsmods.skilltreemod.SkillTree;
import com.zdoctorsmods.skilltreemod.client.ClientMain;
import com.zdoctorsmods.skilltreemod.network.packets.ServerboundClientSkillPacket;
import com.zdoctorsmods.skilltreemod.skills.DisplayInfo;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillAction;
import com.zdoctorsmods.skilltreemod.skills.SkillProgress;

import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.level.storage.loot.PredicateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkillWidget extends GuiComponent {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    private final SkillTreeTab tab;
    private final Skill skill;
    private final DisplayInfo display;
    private final FormattedCharSequence title;
    private final int width;
    private final List<FormattedCharSequence> description;
    // private SkillWidget parent;
    private final List<SkillWidget> children = Lists.newArrayList();
    private final int x;
    private final int y;

    // TODO Auto placing support
    public SkillWidget(SkillTreeTab tab, Skill skill, DisplayInfo display) {
        this.tab = tab;
        this.skill = skill;
        this.display = display;
        this.title = Language.getInstance().getVisualOrder(MINECRAFT.font.substrByWidth(display.getTitle(),
                163));
        this.x = Mth.floor(display.getPosition().x * 28.0F);
        this.y = Mth.floor(display.getPosition().y * 27.0F);
        int maxCriteria = skill.getMaxCriteraRequired();
        int creteriaCountWidth = String.valueOf(maxCriteria).length();
        int titleCenterOffset = maxCriteria > 1
                ? MINECRAFT.font.width(" ") + MINECRAFT.font.width("0") * creteriaCountWidth * 2
                        + MINECRAFT.font.width("/")
                : 0;
        int titleOffset = 29 + MINECRAFT.font.width(this.title) + titleCenterOffset;
        this.description = Language.getInstance()
                .getVisualOrder(this.findOptimalLines(ComponentUtils.mergeStyles(display.getDescription().copy(),
                        Style.EMPTY.withColor(display.getFrame().getChatColor())), titleOffset));

        for (FormattedCharSequence formattedcharsequence : this.description) {
            titleOffset = Math.max(titleOffset, MINECRAFT.font.width(formattedcharsequence));
        }

        this.width = titleOffset + 3 + 5;
    }

    public Skill getSkill() {
        return skill;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public void addChild(SkillWidget skillWidget) {
        this.children.add(skillWidget);
    }

    public boolean isMouseOver(int pX, int pY, double pMouseX, double pMouseY) {
        if (!this.display.isHidden()) {
            int left = pX + this.x;
            int right = left + Constants.ICON_WIDTH;
            int top = pY + this.y;
            int bot = top + Constants.ICON_WIDTH;
            return pMouseX >= left && pMouseX <= right && pMouseY >= top && pMouseY <= bot;
        } else {
            return false;
        }
    }

    public boolean onMouseClick(int button) {
        if (button == 0) {
            ServerboundClientSkillPacket packet = null;
            if (ClientMain.SKILLS.hasSkill(getSkill())) {
                packet = new ServerboundClientSkillPacket(getSkill(), SkillAction.ACTIVE);
            } else {
                // TODO Make packet to query if a skill can be bought and/or cached the result
                // and listen for changes
                packet = new ServerboundClientSkillPacket(getSkill(), SkillAction.BUY);
            }
            SkillTree.CHANNEL.sendToServer(packet);
            return true;
        }
        return false;
    }

    private static float getMaxWidth(StringSplitter pManager, List<FormattedText> pText) {
        return (float) pText.stream().mapToDouble(pManager::stringWidth).max().orElse(0.0D);
    }

    private List<FormattedText> findOptimalLines(Component pComponent, int pMaxWidth) {
        StringSplitter stringsplitter = MINECRAFT.font.getSplitter();
        List<FormattedText> list = null;
        float f = Float.MAX_VALUE;

        for (int i : Constants.TEST_SPLIT_OFFSETS) {
            List<FormattedText> list1 = stringsplitter.splitLines(pComponent, pMaxWidth -
                    i, Style.EMPTY);
            float f1 = Math.abs(getMaxWidth(stringsplitter, list1) - (float) pMaxWidth);
            if (f1 <= 10.0F) {
                return list1;
            }

            if (f1 < f) {
                f = f1;
                list = list1;
            }
        }

        return list;
    }

    // private SkillWidget getFirstVisibleParent(Skill pAdvancement) {
    // do {
    // pAdvancement = pAdvancement.getParent();
    // } while (pAdvancement != null && pAdvancement.getDisplay() == null);

    // return pAdvancement != null && pAdvancement.getDisplay() != null ?
    // this.tab.getWidget(pAdvancement) : null;
    // }

    // public void drawConnectivity(PoseStack pPoseStack, int pX, int pY, boolean
    // pDropShadow) {
    // if (this.parent != null) {
    // int i = pX + this.parent.x + 13;
    // int j = pX + this.parent.x + 26 + 4;
    // int k = pY + this.parent.y + 13;
    // int l = pX + this.x + 13;
    // int i1 = pY + this.y + 13;
    // int j1 = pDropShadow ? -16777216 : -1;
    // if (pDropShadow) {
    // this.hLine(pPoseStack, j, i, k - 1, j1);
    // this.hLine(pPoseStack, j + 1, i, k, j1);
    // this.hLine(pPoseStack, j, i, k + 1, j1);
    // this.hLine(pPoseStack, l, j - 1, i1 - 1, j1);
    // this.hLine(pPoseStack, l, j - 1, i1, j1);
    // this.hLine(pPoseStack, l, j - 1, i1 + 1, j1);
    // this.vLine(pPoseStack, j - 1, i1, k, j1);
    // this.vLine(pPoseStack, j + 1, i1, k, j1);
    // } else {
    // this.hLine(pPoseStack, j, i, k, j1);
    // this.hLine(pPoseStack, l, j, i1, j1);
    // this.vLine(pPoseStack, j, i1, k, j1);
    // }
    // }

    // for (SkillWidget advancementwidget : this.children) {
    // advancementwidget.drawConnectivity(pPoseStack, pX, pY, pDropShadow);
    // }

    // }

    public void draw(PoseStack pPoseStack, int mouseX, int mouseY, float partialTick, int scrollX, int scrollY) {
        if (!this.display.isHidden()) {
            SkillWidgetType skillWidgetType;
            if (ClientMain.SKILLS.hasSkill(skill))
                skillWidgetType = SkillWidgetType.OBTAINED;
            else
                skillWidgetType = SkillWidgetType.UNOBTAINED;

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, Constants.WIDGETS_LOCATION);
            this.blit(pPoseStack, scrollX + x + Constants.TITLE_PADDING_LEFT, scrollY + y,
                    display.getFrame().getTexture(),
                    128 + skillWidgetType.getIndex() * Constants.ICON_WIDTH, Constants.ICON_WIDTH,
                    Constants.ICON_WIDTH);
            Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(),
                    scrollX + x + Constants.ICON_X, scrollY + y + Constants.ICON_Y);

            for (SkillWidget skillWidget : this.children) {
                skillWidget.draw(pPoseStack, mouseX, mouseY, partialTick, scrollX, scrollY);
            }
        }
    }

    public void drawHover(PoseStack pPoseStack, int pX, int pY, float pFade, int pWidth, int pHeight) {
        boolean flag = pWidth + pX + this.x + this.width + 26 >= this.tab.getScreen().width;
        SkillProgress progress = ClientMain.SKILLS.getOrStartProgress(skill);
        String s = progress == null ? null : progress.getProgressText();
        int i = s == null ? 0 : MINECRAFT.font.width(s);
        boolean flag1 = 113 - pY - this.y - 26 <= 6 + this.description.size() * 9;
        float f = progress == null ? 0.0F : progress.getPercent();
        int j = Mth.floor(f * (float) this.width);
        SkillWidgetType advancementwidgettype;
        SkillWidgetType advancementwidgettype1;
        SkillWidgetType advancementwidgettype2;
        if (f >= 1.0F) {
            j = this.width / 2;
            advancementwidgettype = SkillWidgetType.OBTAINED;
            advancementwidgettype1 = SkillWidgetType.OBTAINED;
            advancementwidgettype2 = SkillWidgetType.OBTAINED;
        } else if (j < 2) {
            j = this.width / 2;
            advancementwidgettype = SkillWidgetType.UNOBTAINED;
            advancementwidgettype1 = SkillWidgetType.UNOBTAINED;
            advancementwidgettype2 = SkillWidgetType.UNOBTAINED;
        } else if (j > this.width - 2) {
            j = this.width / 2;
            advancementwidgettype = SkillWidgetType.OBTAINED;
            advancementwidgettype1 = SkillWidgetType.OBTAINED;
            advancementwidgettype2 = SkillWidgetType.UNOBTAINED;
        } else {
            advancementwidgettype = SkillWidgetType.OBTAINED;
            advancementwidgettype1 = SkillWidgetType.UNOBTAINED;
            advancementwidgettype2 = SkillWidgetType.UNOBTAINED;
        }

        int k = this.width - j;
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Constants.WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        int l = pY + this.y;
        int i1;
        if (flag) {
            i1 = pX + this.x - this.width + 26 + 6;
        } else {
            i1 = pX + this.x;
        }

        int j1 = 32 + this.description.size() * 9;
        if (!this.description.isEmpty()) {
            if (flag1) {
                this.render9Sprite(pPoseStack, i1, l + 26 - j1, this.width, j1, 10, 200, 26,
                        0, 52);
            } else {
                this.render9Sprite(pPoseStack, i1, l, this.width, j1, 10, 200, 26, 0, 52);
            }
        }

        this.blit(pPoseStack, i1, l, 0, advancementwidgettype.getIndex() * 26, j,
                26);
        this.blit(pPoseStack, i1 + j, l, 200 - k, advancementwidgettype1.getIndex() *
                26, k, 26);
        this.blit(pPoseStack, pX + this.x + 3, pY + this.y,
                this.display.getFrame().getTexture(),
                128 + advancementwidgettype2.getIndex() * 26, 26, 26);
        if (flag) {
            MINECRAFT.font.drawShadow(pPoseStack, this.title, (float) (i1 + 5),
                    (float) (pY + this.y + 9), -1);
            if (s != null) {
                MINECRAFT.font.drawShadow(pPoseStack, s, (float) (pX + this.x - i),
                        (float) (pY + this.y + 9), -1);
            }
        } else {
            MINECRAFT.font.drawShadow(pPoseStack, this.title, (float) (pX + this.x +
                    32),
                    (float) (pY + this.y + 9), -1);
            if (s != null) {
                MINECRAFT.font.drawShadow(pPoseStack, s, (float) (pX + this.x +
                        this.width - i - 5),
                        (float) (pY + this.y + 9), -1);
            }
        }

        if (flag1) {
            for (int k1 = 0; k1 < this.description.size(); ++k1) {
                MINECRAFT.font.draw(pPoseStack, this.description.get(k1), (float) (i1 +
                        5),
                        (float) (l + 26 - j1 + 7 + k1 * 9), -5592406);
            }
        } else {
            for (int l1 = 0; l1 < this.description.size(); ++l1) {
                MINECRAFT.font.draw(pPoseStack, this.description.get(l1), (float) (i1 +
                        5),
                        (float) (pY + this.y + 9 + 17 + l1 * 9), -5592406);
            }
        }

        MINECRAFT.getItemRenderer().renderAndDecorateFakeItem(this.display.getIcon(),
                pX + this.x + 8,
                pY + this.y + 5);
    }

    protected void render9Sprite(PoseStack pPoseStack, int pX, int pY, int pWidth, int pHeight, int pPadding,
            int pUWidth, int pVHeight, int pUOffset, int pVOffset) {
        this.blit(pPoseStack, pX, pY, pUOffset, pVOffset, pPadding, pPadding);
        this.renderRepeating(pPoseStack, pX + pPadding, pY, pWidth - pPadding -
                pPadding, pPadding, pUOffset + pPadding,
                pVOffset, pUWidth - pPadding - pPadding, pVHeight);
        this.blit(pPoseStack, pX + pWidth - pPadding, pY, pUOffset + pUWidth -
                pPadding, pVOffset, pPadding, pPadding);
        this.blit(pPoseStack, pX, pY + pHeight - pPadding, pUOffset, pVOffset +
                pVHeight - pPadding, pPadding,
                pPadding);
        this.renderRepeating(pPoseStack, pX + pPadding, pY + pHeight - pPadding,
                pWidth - pPadding - pPadding, pPadding,
                pUOffset + pPadding, pVOffset + pVHeight - pPadding, pUWidth - pPadding -
                        pPadding,
                pVHeight);
        this.blit(pPoseStack, pX + pWidth - pPadding, pY + pHeight - pPadding,
                pUOffset + pUWidth - pPadding,
                pVOffset + pVHeight - pPadding, pPadding, pPadding);
        this.renderRepeating(pPoseStack, pX, pY + pPadding, pPadding, pHeight -
                pPadding - pPadding, pUOffset,
                pVOffset + pPadding, pUWidth, pVHeight - pPadding - pPadding);
        this.renderRepeating(pPoseStack, pX + pPadding, pY + pPadding, pWidth -
                pPadding - pPadding,
                pHeight - pPadding - pPadding, pUOffset + pPadding, pVOffset + pPadding,
                pUWidth - pPadding - pPadding,
                pVHeight - pPadding - pPadding);
        this.renderRepeating(pPoseStack, pX + pWidth - pPadding, pY + pPadding,
                pPadding, pHeight - pPadding - pPadding,
                pUOffset + pUWidth - pPadding, pVOffset + pPadding, pUWidth, pVHeight -
                        pPadding - pPadding);
    }

    protected void renderRepeating(PoseStack pPoseStack, int pX, int pY, int pBorderToU, int pBorderToV, int pUOffset,
            int pVOffset, int pUWidth, int pVHeight) {
        for (int i = 0; i < pBorderToU; i += pUWidth) {
            int j = pX + i;
            int k = Math.min(pUWidth, pBorderToU - i);

            for (int l = 0; l < pBorderToV; l += pVHeight) {
                int i1 = pY + l;
                int j1 = Math.min(pVHeight, pBorderToV - l);
                this.blit(pPoseStack, j, i1, pUOffset, pVOffset, k, j1);
            }
        }

    }

    public static class Constants {
        private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(
                "textures/gui/advancements/widgets.png");
        private static final int HEIGHT = 26;
        private static final int BOX_X = 0;
        private static final int BOX_WIDTH = 200;
        private static final int FRAME_WIDTH = 26;
        private static final int ICON_X = 8;
        private static final int ICON_Y = 5;
        private static final int ICON_WIDTH = 26;
        private static final int TITLE_PADDING_LEFT = 3;
        private static final int TITLE_PADDING_RIGHT = 5;
        private static final int TITLE_X = 32;
        private static final int TITLE_Y = 9;
        private static final int TITLE_MAX_WIDTH = 163;
        private static final int[] TEST_SPLIT_OFFSETS = new int[] { 0, 10, -10, 25, -25 };
    }
}
