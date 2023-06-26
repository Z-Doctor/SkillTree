package com.zdoctorsmods.skilltreemod.client.gui.screens.skills;

import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.skills.DisplayInfo;
import com.zdoctorsmods.skilltreemod.skills.Skill;
import static com.zdoctorsmods.skilltreemod.client.gui.screens.skills.SkillScreen.Constants.*;

import java.util.Map;
import java.util.Set;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SkillTreeTab extends GuiComponent {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final static byte MAX_INDEX = Byte.MAX_VALUE;
    private final static byte MIN_INDEX = Byte.MIN_VALUE;

    private final SkillScreen screen;
    private final SkillTreeTabType type;
    private final int index;
    private final Skill skilltree;
    private final DisplayInfo display;
    private final ItemStack icon;
    private final Component title;
    private final Map<Skill, SkillWidget> children = Maps.newLinkedHashMap();
    private final Set<SkillWidget> roots = Sets.newHashSet();
    private double scrollX;
    private double scrollY;
    // private int minX = Integer.MAX_VALUE;
    // private int minY = Integer.MAX_VALUE;
    // private int maxX = Integer.MIN_VALUE;
    // private int maxY = Integer.MIN_VALUE;

    private float fade;
    private boolean centered;
    private int page;

    public SkillTreeTab(SkillScreen screen, SkillTreeTabType pType, int pIndex, Skill skill,
            DisplayInfo pDisplay) {
        this.screen = screen;
        this.type = pType;
        this.index = pIndex;
        this.skilltree = skill;
        this.display = pDisplay;
        this.icon = pDisplay.getIcon();
        this.title = pDisplay.getTitle();
    }

    public SkillTreeTab(SkillScreen screen, SkillTreeTabType type, int index, int page, Skill adv, DisplayInfo info) {
        this(screen, type, index, adv, info);
        this.page = page;
    }

    public int getPage() {
        return page;
    }

    public SkillTreeTabType getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }

    public Skill getSkilltree() {
        return this.skilltree;
    }

    public Component getTitle() {
        return this.title;
    }

    public DisplayInfo getDisplay() {
        return this.display;
    }

    public void drawTab(PoseStack pPoseStack, int pOffsetX, int pOffsetY, boolean pIsSelected) {
        this.type.draw(pPoseStack, this, pOffsetX, pOffsetY, pIsSelected,
                this.index);
    }

    public void drawIcon(int pOffsetX, int pOffsetY, ItemRenderer pRenderer) {
        this.type.drawIcon(pOffsetX, pOffsetY, this.index, pRenderer, this.icon);
    }

    private void drawBackground(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        ResourceLocation resourcelocation = this.display.getBackground();
        if (resourcelocation != null) {
            RenderSystem.setShaderTexture(0, resourcelocation);
        } else {
            RenderSystem.setShaderTexture(0, TextureManager.INTENTIONAL_MISSING_TEXTURE);
        }

        int xOffset = Mth.floor(this.scrollX = 0.0);
        int yOffset = Mth.floor(this.scrollY = 0.0);
        int col = xOffset % BACKGROUND_TILE_WIDTH;
        int row = yOffset % BACKGROUND_TILE_HEIGHT;

        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.enableDepthTest();

        fill(poseStack, WINDOW_INSIDE_WIDTH, WINDOW_INSIDE_HEIGHT, 0, 0,
                0xFF000000);
        RenderSystem.depthFunc(GL11.GL_EQUAL);

        for (int tileX = -1; tileX <= BACKGROUND_TILE_COUNT_X; ++tileX) {
            for (int tileY = -1; tileY <= 7; ++tileY) {
                blit(poseStack, col + BACKGROUND_TILE_WIDTH * tileX, row + BACKGROUND_TILE_HEIGHT * tileY, 0.0F, 0.0F,
                        BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT, BACKGROUND_TILE_WIDTH, BACKGROUND_TILE_HEIGHT);
            }
        }
    }

    private void drawSkills(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        for (SkillWidget child : this.children.values()) {
            child.draw(poseStack, mouseX, mouseY, partialTick);
        }
    }

    public void drawContents(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        // if (!this.centered) {
        // this.scrollX = (double) (117 - (this.maxX + this.minX) / 2);
        // this.scrollY = (double) (56 - (this.maxY + this.minY) / 2);
        // this.centered = true;
        // }
        poseStack.pushPose();

        drawBackground(poseStack, mouseX, mouseY, partialTick);
        drawSkills(poseStack, mouseX, mouseY, partialTick);

        // this.root.drawConnectivity(pPoseStack, i, j, true);
        // this.root.drawConnectivity(pPoseStack, i, j, false);
        // this.root.draw(pPoseStack, i, j);

        // RenderSystem.depthFunc(GL11.GL_GEQUAL);
        // poseStack.translate(0.0F, 0.0F, -950.0F);
        // RenderSystem.colorMask(false, false, false, false);
        // fill(poseStack, 4680, 2260, -4680, -2260, -0x7FFF0000);
        // RenderSystem.colorMask(true, true, true, true);
        // RenderSystem.depthFunc(GL11.GL_LEQUAL);
        poseStack.popPose();
    }

    public void drawTooltips(PoseStack pPoseStack, int pMouseX, int pMouseY, int pWidth, int pHeight) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.0F, 0.0F, -200.0F);
        fill(pPoseStack, 0, 0, 234, 113, Mth.floor(this.fade * 255.0F) << 24);
        boolean flag = false;
        int i = Mth.floor(this.scrollX);
        int j = Mth.floor(this.scrollY);
        if (pMouseX > 0 && pMouseX < 234 && pMouseY > 0 && pMouseY < 113) {
            for (SkillWidget skillWidget : this.children.values()) {
                if (skillWidget.isMouseOver(i, j, pMouseX, pMouseY)) {
                    flag = true;
                    skillWidget.drawHover(pPoseStack, i, j, this.fade, pWidth, pHeight);
                    break;
                }
            }
        }

        pPoseStack.popPose();
        if (flag) {
            this.fade = Mth.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = Mth.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }
    }

    public boolean isMouseOver(int pOffsetX, int pOffsetY, double pMouseX, double pMouseY) {
        return this.type.isMouseOver(pOffsetX, pOffsetY, this.index, pMouseX,
                pMouseY);
    }

    public static SkillTreeTab create(SkillScreen screen, int index, Skill skill) {
        if (skill.getDisplay() == null) {
            return null;
        } else {
            for (SkillTreeTabType skillTreeType : SkillTreeTabType.values()) {
                if ((index % SkillTreeTabType.MAX_TABS) < skillTreeType.getMax()) {
                    return new SkillTreeTab(screen, skillTreeType, index % SkillTreeTabType.MAX_TABS,
                            index / SkillTreeTabType.MAX_TABS,
                            skill, skill.getDisplay());
                }

                index -= skillTreeType.getMax();
            }

            return null;
        }
    }

    public void scroll(double pDragX, double pDragY) {
        // if (this.maxX - this.minX > 234) {
        // this.scrollX = Mth.clamp(this.scrollX + pDragX, (double) (-(this.maxX -
        // 234)), 0.0D);
        // }

        // if (this.maxY - this.minY > 113) {
        // this.scrollY = Mth.clamp(this.scrollY + pDragY, (double) (-(this.maxY -
        // 113)), 0.0D);
        // }

    }

    public boolean addSkill(Skill skill) {
        boolean added = false;
        SkillWidget skillWidget = new SkillWidget(this, skill, skill.getDisplay());
        if (skill.getParent() == getSkilltree()) {
            roots.add(skillWidget);
            added = true;
        } else {
            SkillWidget parentSkill = getWidget(skill.getParent());
            if (parentSkill != null) {
                parentSkill.addChild(skillWidget);
                added = true;
            } else
                LOGGER.debug("Tried to add a skill to tree {} where its parent {} does not exist",
                        getSkilltree().getId(), skill.getParent().getId());
        }
        if (added)
            addWidget(skillWidget, skill);
        return added;
    }

    private void addWidget(SkillWidget pWidget, Skill skill) {
        this.children.put(skill, pWidget);
        // int i = pWidget.getX();
        // int j = i + 28;
        // int k = pWidget.getY();
        // int l = k + 27;
        // this.minX = Math.min(this.minX, i);
        // this.maxX = Math.max(this.maxX, j);
        // this.minY = Math.min(this.minY, k);
        // this.maxY = Math.max(this.maxY, l);
    }

    public SkillWidget getWidget(Skill skill) {
        return this.children.get(skill);
    }

    public SkillScreen getScreen() {
        return this.screen;
    }

}