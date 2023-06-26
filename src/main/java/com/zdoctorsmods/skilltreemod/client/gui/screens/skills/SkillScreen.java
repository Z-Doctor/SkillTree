package com.zdoctorsmods.skilltreemod.client.gui.screens.skills;

import com.zdoctorsmods.skilltreemod.skills.Skill;
import com.zdoctorsmods.skilltreemod.skills.SkillList;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.zdoctorsmods.skilltreemod.client.ClientMain;

import net.minecraft.advancements.Advancement;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.advancements.AdvancementTab;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import com.google.common.collect.Maps;

public class SkillScreen extends Screen implements SkillList.Listener {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Map<Skill, SkillTreeTab> tabs = Maps.newLinkedHashMap();
    private SkillTreeTab selectedTab;
    private int tabPage, maxPages;
    private int guiLeft, guiTop;
    private boolean isScrolling;

    public SkillScreen() {
        super(Component.literal("Skill Tree"));
        for (var skill : ClientMain.SKILLS.getSkills().getAllSkills()) {
            LOGGER.debug("Found Client Skill: {}", skill);
        }
    }

    @Override
    protected void init() {
        this.tabs.clear();
        this.selectedTab = null;
        ClientMain.SKILLS.setListener(this);

        if (this.selectedTab == null && !this.tabs.isEmpty()) {
            ClientMain.SKILLS.setSelectedTab(this.tabs.values().iterator().next().getSkilltree(),
                    true);
        } else {
            ClientMain.SKILLS.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getSkilltree(), true);
        }

        guiLeft = (this.width - Constants.WINDOW_WIDTH) / 2;
        guiTop = (this.height - Constants.WINDOW_HEIGHT) / 2;

        // TODO make button visibilty dynamic
        if (this.tabs.size() > SkillTreeTabType.MAX_TABS) {
            addRenderableWidget(Button.builder(Component.literal("<"), b -> tabPage = Math.max(tabPage - 1, 0))
                    .pos(guiLeft, guiTop - 50).size(20, 20).build());
            addRenderableWidget(net.minecraft.client.gui.components.Button
                    .builder(Component.literal(">"), b -> tabPage = Math.min(tabPage + 1,
                            maxPages))
                    .pos(guiLeft + Constants.WINDOW_WIDTH - 20, guiTop - 50).size(20, 20).build());
            maxPages = this.tabs.size() / SkillTreeTabType.MAX_TABS;
        }
    }

    @Override
    public void removed() {
        ClientMain.SKILLS.setListener(null);
        // ModMain.CHANNEL.sendPacketTo(null, null);
        // clientpacketlistener.send(ServerboundSeenSkillsPacket.closedScreen());
    }

    @Override
    public void render(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(pose);
        renderInside(pose, mouseX, mouseY, partialTick);
        renderWindow(pose, mouseX, mouseY, partialTick);
        renderTooltips(pose, mouseX, mouseY, partialTick);
        super.render(pose, mouseX, mouseY, partialTick);

        if (maxPages != 0) {
            Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
            int width = this.font.width(page);
            this.font.drawShadow(pose, page.getVisualOrderText(), guiLeft + (Constants.WINDOW_WIDTH / 2) - (width / 2),
                    guiTop - 44, -1);
        }
    }

    public int getGuiTop() {
        return guiTop;
    }

    public int getGuiLeft() {
        return guiLeft;
    }

    // TODO Custom rendering for Skill Player Info
    // TODO Understand RenderSystem.getModelViewStack();
    private void renderInside(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        SkillTreeTab skillTree = this.selectedTab;
        if (skillTree == null) {
            fill(pose, guiLeft + Constants.WINDOW_INSIDE_X, guiTop + Constants.WINDOW_INSIDE_Y,
                    guiLeft + Constants.WINDOW_INSIDE_X + Constants.WINDOW_INSIDE_WIDTH,
                    guiTop + Constants.WINDOW_INSIDE_Y + Constants.WINDOW_INSIDE_HEIGHT,
                    0xFF000000);
            int i = guiLeft + Constants.WINDOW_INSIDE_X + 117;
            drawCenteredString(pose, this.font, Constants.NO_SKILLS_LABEL, i,
                    guiTop + Constants.WINDOW_INSIDE_Y + 56 - Constants.WINDOW_INSIDE_X / 2, 0xFFFFFFFF);
            drawCenteredString(pose, this.font, Constants.VERY_SAD_LABEL, i,
                    guiTop + Constants.WINDOW_INSIDE_Y + Constants.WINDOW_INSIDE_HEIGHT - Constants.WINDOW_INSIDE_X,
                    0xFFFFFFFF);
        } else {
            PoseStack modelViewStack = RenderSystem.getModelViewStack();
            modelViewStack.pushPose();
            modelViewStack.translate(guiLeft + Constants.WINDOW_INSIDE_X, guiTop + Constants.WINDOW_INSIDE_Y, 0.0F);
            RenderSystem.applyModelViewMatrix();
            skillTree.drawContents(pose, mouseX, mouseY, partialTick);
            modelViewStack.popPose();

            RenderSystem.applyModelViewMatrix();
            RenderSystem.disableDepthTest();
        }
    }

    // TODO Implement tab positioning
    private void renderWindow(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, Constants.WINDOW_LOCATION);
        this.blit(pose, guiLeft, guiTop, 0, 0, Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        if (this.tabs.size() > 0) {

            RenderSystem.setShaderTexture(0, Constants.TABS_LOCATION);
            for (SkillTreeTab tree : this.tabs.values()) {
                if (tree.getPage() == tabPage) {
                    tree.drawTab(pose, guiLeft, guiTop, tree == this.selectedTab);
                }
            }

            RenderSystem.defaultBlendFunc();
            for (SkillTreeTab tree : this.tabs.values()) {
                if (tree.getPage() == tabPage) {
                    tree.drawIcon(guiLeft, guiTop, this.itemRenderer);
                }
            }

            RenderSystem.disableBlend();
        }

        if (selectedTab != null)
            this.font.draw(pose, selectedTab.getTitle(), (float) (guiLeft + 8), (float) (guiTop + 6), 0x404040);
    }

    private void renderTooltips(PoseStack pose, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        if (this.selectedTab != null) {
            PoseStack posestack = RenderSystem.getModelViewStack();
            posestack.pushPose();
            posestack.translate(guiLeft + 9, guiTop + 18, 400.0F);
            RenderSystem.applyModelViewMatrix();
            RenderSystem.enableDepthTest();
            this.selectedTab.drawTooltips(pose, mouseX - guiLeft - 9, mouseY - guiTop - 18, guiLeft, guiTop);
            RenderSystem.disableDepthTest();
            posestack.popPose();
            RenderSystem.applyModelViewMatrix();
        }

        if (this.tabs.size() > 1) {
            for (SkillTreeTab skillTreeTab : this.tabs.values()) {
                if (skillTreeTab.getPage() == tabPage
                        && skillTreeTab.isMouseOver(guiLeft, guiTop, mouseX, mouseY)) {
                    this.renderTooltip(pose, skillTreeTab.getTitle(), mouseX, mouseY);
                }
            }
        }

    }

    private SkillTreeTab getTreeTab(Skill skill) {
        while (skill.getParent() != null) {
            skill = skill.getParent();
        }

        return this.tabs.getOrDefault(skill, null);
    }

    @Override
    public void onAddSkill(Skill skill) {
        LOGGER.debug("Added skill {}: {}", skill.getId(), skill.getDisplay().getIcon());
        SkillTreeTab tree = getTreeTab(skill);
        if (tree != null)
            tree.addSkill(skill);
    }

    @Override
    public void onRemoveSkill(Skill skill) {
    }

    @Override
    public void onSkillsCleared() {
        tabs.clear();
        selectedTab = null;
    }

    @Override
    public void onSelectedTreeChanged(Skill skill) {
        selectedTab = tabs.get(skill);
    }

    @Override
    public void onTreeAdded(Skill skill) {
        LOGGER.debug("Added tree {}: {}", skill.getId(), skill.getDisplay().getIcon());
        SkillTreeTab tree = SkillTreeTab.create(this, tabs.size(), skill);
        if (tree != null) {
            tabs.put(skill, tree);
            if (selectedTab == null)
                ClientMain.SKILLS.setSelectedTab(skill, false);
        }
    }

    @Override
    public void onTreeRemoved(Skill skill) {
        SkillTreeTab tree = tabs.get(skill);
        if (tree != null) {
            tabs.remove(skill);
        }
    }

    public static class Constants {
        private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation(
                "textures/gui/advancements/window.png");
        private static final ResourceLocation TABS_LOCATION = new ResourceLocation(
                "textures/gui/advancements/tabs.png");
        public static final int WINDOW_WIDTH = 252;
        public static final int WINDOW_HEIGHT = 140;
        private static final int WINDOW_INSIDE_X = 9;
        private static final int WINDOW_INSIDE_Y = 18;
        public static final int WINDOW_INSIDE_WIDTH = 234;
        public static final int WINDOW_INSIDE_HEIGHT = 113;
        private static final int WINDOW_TITLE_X = 8;
        private static final int WINDOW_TITLE_Y = 6;
        public static final int BACKGROUND_TILE_WIDTH = 16;
        public static final int BACKGROUND_TILE_HEIGHT = 16;
        public static final int BACKGROUND_TILE_COUNT_X = 14;
        public static final int BACKGROUND_TILE_COUNT_Y = 7;
        private static final Component VERY_SAD_LABEL = Component.translatable("advancements.sad_label");
        private static final Component NO_SKILLS_LABEL = Component.translatable("advancements.empty");
        private static final Component TITLE = Component.translatable("gui.advancements");
    }

}
