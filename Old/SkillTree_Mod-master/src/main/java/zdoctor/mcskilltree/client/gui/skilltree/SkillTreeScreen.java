package zdoctor.mcskilltree.client.gui.skilltree;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.api.ISkillTreeTabGui;
import zdoctor.mcskilltree.client.KeyBindingHandler;
import zdoctor.mcskilltree.registries.SkillTreeRegistries;
import zdoctor.mcskilltree.skilltree.SkillTree;
import zdoctor.mcskilltree.skilltree.SkillTreeBackground;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class SkillTreeScreen extends Screen {
    protected final Map<SkillTree, ISkillTreeTabGui> tabs = Maps.newLinkedHashMap();
    protected static ISkillTreeTabGui lastSelected;
    protected ISkillTreeTabGui selected;
    protected int tabPage, maxPages;

    protected int guiLeft;
    protected int guiTop;

    protected int xSize = 252;
    protected int ySize = 140;

    public SkillTreeScreen() {
        super(NarratorChatListener.EMPTY);
        minecraft = Minecraft.getInstance();
    }

    @Override
    protected void init() {
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;

        for (SkillTree tree : SkillTreeRegistries.SKILL_TREES.getValues()) {
            tabs.put(tree, tree.getTabGui());
        }

        maxPages = getPageCount();
        if (selected != null)
            setFocused(selected);
        else if (lastSelected != null)
            setFocused(lastSelected);
        else
            setFocused(tabs.get(SkillTree.PLAYER_INFO));

        // Inits page nav buttons
        if (maxPages > 0) {
            addButton(new Button(guiLeft, guiTop - 50, 20, 20, "<", b -> tabPage = Math.max(tabPage - 1, 0)) {
                @Override
                public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
                    visible = tabPage > 0;
                    super.render(p_render_1_, p_render_2_, p_render_3_);
                }
            });
            addButton(new Button(guiLeft + xSize - 20, guiTop - 50, 20, 20, ">", b -> tabPage = Math.min(tabPage + 1, maxPages)) {
                @Override
                public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
                    visible = tabPage < maxPages;
                    super.render(p_render_1_, p_render_2_, p_render_3_);
                }
            });
        }

        children.addAll(tabs.values());
    }

    public int getPageCount() {
        int maxPage = 0;
        for (ISkillTreeTabGui tab : tabs.values()) {
            int page = tab.getPage();
            if (page > maxPage)
                maxPage = page;
        }

        return maxPage;

    }

    public ISkillTreeTabGui getSelected() {
        return (ISkillTreeTabGui) getFocused();
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener focused) {
        super.setFocused(focused);
        if (this.selected != null && focused != this.selected) {
            this.selected.onClose();
        }
        this.selected = (ISkillTreeTabGui) focused;
        if (this.selected != null) {
            lastSelected = this.selected;
            tabPage = selected.getPage();
            this.selected.onOpen();
        }
    }

    @Override
    public boolean keyPressed(int key, int scanCode, int modifiers) {
        if (KeyBindingHandler.KeyBindSkillTree.matchesKey(key, scanCode)) {
            minecraft.displayGuiScreen(null);
            minecraft.mouseHelper.grabMouse();
            return true;
        } else if (KeyBindingHandler.RECENTER_SKILL_TREE.matchesKey(key, scanCode)) {
            getSelected().reload();
            getSelected().buildTree();
            return true;
        } else
            return super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        // TODO Clean up
        this.renderBackground();
        this.renderInside(mouseX, mouseY, partialTicks);
        if (maxPages > 0) {
            String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
            int width = this.font.getStringWidth(page);
            RenderSystem.disableLighting();
            this.font.drawStringWithShadow(page, guiLeft + (xSize / 2f) - (width / 2f), guiTop - 44, -1);
        }

        this.renderWindow(mouseX, mouseY, partialTicks);
        this.renderMisc(mouseX, mouseY, partialTicks);
        super.render(mouseX, mouseY, partialTicks);
    }

    protected void renderInside(int mouseX, int mouseY, float partialTicks) {
        if (getSelected() == null) {
            // Renders null
            fill(guiLeft + 9, guiTop + 18, guiLeft + 9 + 234, guiTop + 18 + 113, -16777216);
            String s = I18n.format("skilltree.empty");
            int i = this.font.getStringWidth(s);
            this.font.drawString(s, (float) (guiLeft + 9 + 117 - i / 2), (float) (guiTop + 18 + 56 - 9 / 2), -1);
            this.font.drawString(":(", (float) (guiLeft + 9 + 117 - this.font.getStringWidth(":(") / 2), (float) (guiTop + 18 + 113 - 9), -1);
        } else {
            getSelected().preRender(mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float) (guiLeft + 9), (float) (guiTop + 18), 0.0F);
            getSelected().render(mouseX, mouseY, partialTicks);
            RenderSystem.popMatrix();
            getSelected().postRender(mouseX, mouseY, partialTicks);
            RenderSystem.depthFunc(515);
            RenderSystem.disableDepthTest();
        }
    }

    protected void renderWindow(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        this.minecraft.getTextureManager().bindTexture(SkillTreeBackground.WINDOW);
        this.blit(guiLeft, guiTop, 0, 0, xSize, ySize);
        this.minecraft.getTextureManager().bindTexture(SkillTreeBackground.TABS);

        for (ISkillTreeTabGui tabGui : tabs.values()) {
            if (tabGui.getPage() == tabPage && tabGui != getSelected())
                tabGui.drawTab(guiLeft, guiTop, false);
        }
        getSelected().drawTab(guiLeft, guiTop, true);

        RenderSystem.enableRescaleNormal();
        RenderSystem.defaultBlendFunc();

        for (ISkillTreeTabGui tabGui : tabs.values()) {
            if (tabGui.getPage() == tabPage)
                tabGui.drawIcon(guiLeft, guiTop, this.itemRenderer);
        }

        RenderSystem.disableBlend();

        if (getSelected() != null)
            this.font.drawString(getSelected().getDisplayInfo().getTitle().getFormattedText(), (float) (guiLeft + 8), (float) (guiTop + 6), 4210752);
    }

    protected void renderMisc(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (getSelected() != null) {
            int adjustedMouseX = mouseX - guiLeft - 9;
            int adjustedMouse = mouseY - guiTop - 18;

            getSelected().preRenderAdjusted(adjustedMouseX, adjustedMouse, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float) (guiLeft + 9), (float) (guiTop + 18), 400.0F);
            getSelected().renderAdjusted(adjustedMouseX, adjustedMouse, partialTicks);
            RenderSystem.popMatrix();
            getSelected().postRenderAdjusted(adjustedMouseX, adjustedMouse, partialTicks);
        }

        // TODO Sort out
        if (tabs.size() > 1) {
            Optional<IGuiEventListener> listener = getEventListenerForPos(mouseX - guiLeft, mouseY - guiTop);
            if (listener.isPresent() && listener.get() instanceof ISkillTreeTabGui) {
                if (listener.get() != getSelected()) {
                    if (getSelected() == null || getSelected().getFocused() == null ||
                            !getSelected().getFocused().withinBounds(mouseX - guiLeft - 9, mouseY - guiTop - 18)) {
                        RenderSystem.pushMatrix();
                        RenderSystem.translatef(0, 0, 400);
                        renderTooltip(((ISkillTreeTabGui) listener.get()).getDisplayInfo().getTitle().getFormattedText(), mouseX, mouseY);
                        RenderSystem.popMatrix();
                    }
                }
            }
        }

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int mx = MathHelper.floor(mouseX - guiLeft);
        int my = MathHelper.floor(mouseY - guiTop);

        for (IGuiEventListener child : this.children()) {
            if (child.mouseClicked(mx, my, button)) {
                this.setFocused(child);
                return true;
            }
        }

        mx -= 9;
        my -= 18;
        if (button == 0 && getSelected() != null)
            if (mx > 0 && mx < 234 && my > 0 && my < 113)
                setDragging(true);

        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0)
            setDragging(false);
        return this.getEventListenerForPos(mouseX, mouseY).filter((p_212931_5_) -> {
            return p_212931_5_.mouseReleased(mouseX, mouseY, button);
        }).isPresent();
    }

    @Override
    public boolean isPauseScreen() {
        // TODO Config?
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double lastMouseX, double lastMouseY) {
        return super.mouseDragged(mouseX, mouseY, button, lastMouseX, lastMouseY);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        getSelected().mouseMoved(mouseX, mouseY);
    }
}
