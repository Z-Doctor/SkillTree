package zdoctor.mcskilltree.client.gui.skilltree;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;
import zdoctor.mcskilltree.api.ISkillInfoGui;
import zdoctor.mcskilltree.api.ISkillTreeTabGui;
import zdoctor.mcskilltree.api.SkillApi;
import zdoctor.mcskilltree.client.gui.skills.SkillEntryGui;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skilltree.SkillTree;
import zdoctor.mcskilltree.skilltree.SkillTreeInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(value = Dist.CLIENT)
public class SkillTreeTabGui extends AbstractSkillTreeGui implements ISkillTreeTabGui {

    protected SkillTree skillTree;

    protected SkillTreeInfo display;
    protected double scrollX;
    protected double scrollY;
    protected float fade;

    protected boolean active;

    protected int lastMouseX, lastMouseY;
    protected int lastAdjustedMouseX, lastAdjustedMouseY;

    protected int leftScroll;
    protected int topScroll;
    protected int rightScroll;
    protected int botScroll;
    protected ISkillInfoGui selected;

    public SkillTreeTabGui(SkillTree skillTree) {
        this.skillTree = skillTree;
        this.display = skillTree.getDisplayInfo();
        buildTree();
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void buildTree() {
        children.clear();
        leftScroll = rightScroll = topScroll = botScroll = 0;
        scrollX = scrollY = 0;

        generateSkillEntries(skillTree);
        for (SkillEntryGui entry : children()) {
            entry.clearCache();
            leftScroll = Math.max(leftScroll, -entry.getLeftScroll());
            rightScroll = Math.min(rightScroll, -entry.getRightScroll() + 202);
            topScroll = Math.max(topScroll, -entry.getTopScroll());
            botScroll = Math.min(botScroll, -entry.getBotScroll() + 81);
        }

//        minScrollX =
//        if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113)
    }

    @Override
    public void reload() {
        skillTree.reload();
        buildTree();
    }

    @Override
    public double getScrollX() {
        return scrollX;
    }

    @Override
    public double getScrollY() {
        return scrollY;
    }

    protected void generateSkillEntries(SkillTree skillTree) {
        for (Skill skill : skillTree.getSkills()) {
            if (!skill.hasParents()) {
                SkillEntryGui parent = new SkillEntryGui(this, skill);
                children.add(parent);
                parent.findChildren();
            }
        }
    }

    public int getNextIndex(int from) {
        // TODO Implement index sorting
        return from;
    }

    // TODO Auto-position support
//    public Vector2 getNextPosition(Vector2 from) {
//        int scale = 0;
//
//        if (from == null)
//            from = Vector2.ZERO;
//
//        Vector2 pos = new Vector2(from);
//        while (scale < Integer.MAX_VALUE) {
//            if (scale > 0) {
//                pos.setX(from.getX() + scale);
//                pos.setY(from.getY());
//
//                for (int y = 0; y < scale; y++) {
//                    if (!skillSet.containsValue(pos))
//                        return pos;
//                    pos.setY(pos.getY() + 1);
//                }
//
//                pos.setX(from.getX());
//                pos.setY(from.getY() + scale);
//                for (int x = 0; x <= scale; x++) {
//                    if (!skillSet.containsValue(pos))
//                        return pos;
//                    pos.setX(pos.getX() + 1);
//                }
//            } else if (!skillSet.containsValue(pos))
//                return pos;
//
//            scale += 1;
//        }
//        throw new IndexOutOfBoundsException("You really shouldn't be putting them way over here.");
//
//    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button != 0)
            return false;
        scrollX = MathHelper.clamp(scrollX + deltaX, rightScroll, leftScroll);
        scrollY = MathHelper.clamp(scrollY + deltaY, botScroll, topScroll);
        return true;
    }

    @Override
    public int getPage() {
        return display.getIndex() / display.getType().getMax();
    }

    @Override
    public SkillTreeInfo getDisplayInfo() {
        return display;
    }

    @Override
    public void drawTab(int guiLeft, int guiTop, boolean isSelected) {
        display.getType().draw(this, guiLeft, guiTop, isSelected, display.getIndex());
    }

    @Override
    public void drawIcon(int offsetX, int offsetY, ItemRenderer renderItemIn) {
        display.getType().drawIcon(offsetX, offsetY, display.getIndex(), renderItemIn, display != null ? display.getIcon() : null);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        // TODO Figure out how this masking works
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.depthFunc(518);
        fill(234, 113, 0, 0, -16777216);
        RenderSystem.depthFunc(515);
        int scrollX = MathHelper.floor(this.scrollX);
        int scrollY = MathHelper.floor(this.scrollY);
        int x = scrollX % 16;
        int y = scrollY % 16;

        if (display == null || display.getBackground() == null) {
            minecraft.getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
            for (int row = -1; row <= 15; ++row) {
                for (int col = -1; col <= 8; ++col) {
                    draw2DTex(x + 16 * row, y + 16 * col, 0f, 0f, 16, 16, 16, 16);
                }
            }
        } else {
            display.getBackground().render(minecraft, scrollX, scrollY);
        }

        this.preDrawSkills(scrollX, scrollY, mouseX, mouseY);
        this.drawSkillConnections(scrollX, scrollY, true);
        this.drawSkillConnections(scrollX, scrollY, false);
        this.drawSkills(scrollX, scrollY);
        this.postDrawSkills(scrollX, scrollY, mouseX, mouseY);


        RenderSystem.depthFunc(518);
        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
        RenderSystem.colorMask(false, false, false, false);
        fill(4680, 2260, -4680, -2260, -16777216);
        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();

        lastMouseX = mouseX;
        lastMouseY = mouseY;
    }

    @Override
    public void setFocused(@Nullable IGuiEventListener focused) {
        SkillEntryGui selected = (SkillEntryGui) focused;
        if (this.selected != null) {
            if (this.selected.getSkillEntry() == selected)
                return;
            else
                this.selected.onClose();
        }

        this.selected = selected != null ? selected.getSkillInfoGui() : null;
    }

    @Nullable
    @Override
    public ISkillInfoGui getFocused() {
        return this.selected;
    }

    // TODO make sure it is working
    public void drawSkillConnections(int left, int top, boolean outerLine) {
        for (SkillEntryGui child : children()) {
            child.drawConnectivity(left, top, outerLine);
        }
    }

    public void preDrawSkills(int posX, int posY, int mouseX, int mouseY) {
    }

    public void postDrawSkills(int posX, int posY, int mouseX, int mouseY) {
    }

    public void drawSkills(int left, int top) {
        for (SkillEntryGui child : children()) {
            child.draw(left, top, SkillApi.hasSkill(minecraft.player, child.getSkill()));
            child.drawChildren(left, top);
        }
    }

    @Override
    public void renderAdjusted(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0F, 0.0F, 200.0F);
        RenderSystem.disableDepthTest();
        fill(0, 0, 234, 113, MathHelper.floor(this.fade * 255.0F) << 24);

        boolean flag = getFocused() != null;
        if (flag)
            drawSkillInfo(mouseX, mouseY, partialTicks);

        int scrollX = MathHelper.floor(this.scrollX);
        int scrollY = MathHelper.floor(this.scrollY);
        // TODO See if needs cleanup or to be moved
        if (getFocused() == null || !getFocused().withinBounds(mouseX, mouseY))
            if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113) {
                for (SkillEntryGui child : children()) {
                    if (getFocused() != null && child == getFocused().getSkillEntry())
                        continue;
                    if (child.isMouseOver(mouseX - scrollX, mouseY - scrollY)) {
                        flag = true;
                        child.drawHovered(scrollX, scrollY, this.fade, mouseX + scrollX, mouseY + scrollY);
                        break;
                    }
                }
            }

        RenderSystem.popMatrix();
        if (flag) {
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        } else {
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
        }

        lastAdjustedMouseX = mouseX;
        lastAdjustedMouseY = mouseY;
    }


    protected void drawSkillInfo(int mouseX, int mouseY, float partialTicks) {
        if (getFocused() == null)
            return;
        getFocused().render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return display.getType().isHovered(display.getIndex(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        mouseX = MathHelper.floor(mouseX);
        mouseY = MathHelper.floor(mouseY);
        if (button == 0 && display.getType().isHovered(display.getIndex(), mouseX, mouseY))
            return true;
        if (!isActive())
            return false;

        mouseX -= 9;
        mouseY -= 18;
        // TODO Clean up this
        if(button == 1)
            setFocused(null);

        if (getFocused() != null && getFocused().withinBounds(mouseX, mouseY)) {
            getFocused().mouseClicked(mouseX, mouseY, button);
            return true;
        } else if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113) {
            if (super.mouseClicked(mouseX - scrollX, mouseY - scrollY, button)) {
                return true;
            }
        } else
            setFocused(null);
        return false;
    }

    @Override
    public void onOpen() {
        setActive(true);
    }

    @Override
    public void onClose() {
        setActive(false);
        fade = 0;
    }
}
