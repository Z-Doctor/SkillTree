package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.enums.Layer;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.api.interfaces.ISkillTreeScreen;
import zdoctor.zskilltree.api.interfaces.ImageDisplayInfo;
import zdoctor.zskilltree.skilltree.criterion.Skill;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;

public class GuiSkillPage extends ImageScreen {
    public static final ImageAsset[][] TOP_TABS = {
            {ImageAssets.Tabs.TAB_TOP_LEFT, ImageAssets.Tabs.TAB_TOP_CENTER, ImageAssets.Tabs.TAB_TOP_RIGHT},
            {ImageAssets.Tabs.TAB_TOP_LEFT_SELECTED, ImageAssets.Tabs.TAB_TOP_CENTER_SELECTED, ImageAssets.Tabs.TAB_TOP_RIGHT_SELECTED}
    };

    public static final ImageAsset[][] BOTTOM_TABS = {
            {ImageAssets.Tabs.TAB_BOTTOM_LEFT, ImageAssets.Tabs.TAB_BOTTOM_CENTER, ImageAssets.Tabs.TAB_BOTTOM_RIGHT},
            {ImageAssets.Tabs.TAB_BOTTOM_LEFT_SELECTED, ImageAssets.Tabs.TAB_BOTTOM_CENTER_SELECTED, ImageAssets.Tabs.TAB_BOTTOM_RIGHT_SELECTED}
    };

    public static final ImageAsset[][][] VERTICAL_TABS = {TOP_TABS, BOTTOM_TABS};

    public static final ImageAsset[][] LEFT_TABS = {
            {ImageAssets.Tabs.TAB_LEFT_TOP, ImageAssets.Tabs.TAB_LEFT_CENTER, ImageAssets.Tabs.TAB_LEFT_BOTTOM},
            {ImageAssets.Tabs.TAB_LEFT_TOP_SELECTED, ImageAssets.Tabs.TAB_LEFT_CENTER_SELECTED, ImageAssets.Tabs.TAB_LEFT_BOTTOM_SELECTED}
    };

    public static final ImageAsset[][] RIGHT_TABS = {
            {ImageAssets.Tabs.TAB_RIGHT_TOP, ImageAssets.Tabs.TAB_RIGHT_CENTER, ImageAssets.Tabs.TAB_RIGHT_BOTTOM},
            {ImageAssets.Tabs.TAB_RIGHT_TOP_SELECTED, ImageAssets.Tabs.TAB_RIGHT_CENTER_SELECTED, ImageAssets.Tabs.TAB_RIGHT_BOTTOM_SELECTED}
    };

    public static final ImageAsset[][][] HORIZONTAL_TABS = {LEFT_TABS, RIGHT_TABS};

    public static final ImageAsset[][][][] ALL_TABS = {VERTICAL_TABS, HORIZONTAL_TABS};

    public static final int MAX_VERTICAL = 16;
    public static final int MAX_HORIZONTAL = 8;
    public static final int SPACING = 4;

    private final SkillPage skillPage;
    private final ISkillTreeScreen skillTreeScreen;


    private boolean isSelected;

    private float fade;

    private int index;
    private int style;
    private int pageNumber;
    private int tabGroup;
    private int tabType;

    public GuiSkillPage(SkillPage page, ISkillTreeScreen skillTreeScreen) {
        super(page.getDisplayName());
        layer = Layer.BACKGROUND;
        this.skillTreeScreen = skillTreeScreen;
        this.skillPage = page;
        this.pageNumber = page.getIndex() / (page.getAlignment() == SkillPageAlignment.VERTICAL ? MAX_VERTICAL : MAX_HORIZONTAL);
//        setImage(ImageAssets.DEFAULT_TILE);
        int i = 0;
        for (Skill skill : page.getChildSkills().values()) {
            GuiSkill guiSkill = new GuiSkill(skill, skillTreeScreen);
            guiSkill.setAnchor(skillTreeScreen);
            guiSkill.setOffsets(28 * i + 9, 18);
            i++;
            addDisplay(guiSkill);
            addListener(guiSkill);
        }
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        IGuiEventListener[] temp = getEventListeners().toArray(new IGuiEventListener[0]);
        super.init(minecraft, width, height);
        for (IGuiEventListener listener : temp) {
            addListener(listener);
        }
    }

    @Override
    public void updateOffsets() {
        super.updateOffsets();
    }

    @Override
    protected void renderMain(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Draws title of page
        if (isSelected()) {
            this.font.func_243248_b(matrixStack, getSkillPage().getDisplayName(), root.getTrueOffsetX() + 8,
                    root.getTrueOffsetY() + 6, getSkillPage().getLabelColor());
        }
        // Draws tab
        renderAt(matrixStack, getTrueOffsetX(), getTrueOffsetY(), partialTicks);

        // Draws page if this page number matches current page number
        RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.itemRenderer.zLevel = 50f;

        int x = getTrueOffsetX();
        int y = getTrueOffsetY();

        if (skillPage.getAlignment() == SkillPageAlignment.VERTICAL) {
            x += 6;
            y += (tabType == 0 ? 10 : 6);
        } else if (skillPage.getAlignment() == SkillPageAlignment.HORIZONTAL) {
            x += (tabType == 0 ? 10 : 6);
            y += 6;
        }

        RenderSystem.enableRescaleNormal();
        ItemStack itemstack = skillPage.getIcon();
        this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(itemstack, x, y);
        this.itemRenderer.renderItemOverlays(this.font, itemstack, x, y);
        this.itemRenderer.zLevel = 0.0F;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (skillTreeScreen.getTabPageNumber() != pageNumber)
            return;
        if (!isSelected()) {
            renderMain(matrixStack, mouseX, mouseY, partialTicks);
            return;
        }
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        for (ImageDisplayInfo child : childScreens) {
            if (child instanceof GuiSkill && child.isMouseOver(mouseX, mouseY)) {
                GuiSkill skillGui = (GuiSkill) child;
//                skillGui.drawSkillHover(matrixStack, mouseX, mouseY);
//                if (flag) {
//                    this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
//                } else {
//                    this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);
//                }
            }
        }
    }

    @Override
    public int getZIndex() {
        return isSelected() ? 2 : super.getZIndex();
    }

    public ImageAsset getBackground() {
        return skillPage.getBackgroundImage();
    }

    public SkillPage getSkillPage() {
        return skillPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY)) {
            if (button == 0 && !skillTreeScreen.isMouseOver(mouseX, mouseY)) {
                onMouseClicked(mouseX, mouseY, button);
                return true;
            }
        }
        return isSelected() && super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onMouseClicked(double mouseX, double mouseY, int button) {
        skillTreeScreen.getClientTracker().setSelectedPage(getSkillPage(), true);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getPageNumber() == skillTreeScreen.getTabPageNumber() && super.isMouseOver(mouseX, mouseY);
    }

    @Override
    protected void init() {
        SkillPageAlignment align = skillPage.getAlignment();
        this.pageNumber = skillPage.getIndex() / (align == SkillPageAlignment.VERTICAL ? MAX_VERTICAL : MAX_HORIZONTAL);

        int pos;
        if (align == SkillPageAlignment.VERTICAL) {
            tabGroup = 0;
            pos = skillPage.getIndex() % MAX_VERTICAL;
            index = pos % (MAX_VERTICAL / 2);
            style = index == 0 ? 0 : index == (MAX_VERTICAL / 2 - 1) ? 2 : 1;
            tabType = pos < (MAX_VERTICAL / 2) ? 0 : 1;

            xOffset = index * getImage().xSize + index * SPACING;
            yOffset = pos < (MAX_VERTICAL / 2) ? 4 : -4;
            anchorPoint = pos < (MAX_VERTICAL / 2) ? AnchorPoint.TOP_LEFT : AnchorPoint.BOTTOM_LEFT;
            pivotPoint = pos < (MAX_VERTICAL / 2) ? AnchorPoint.BOTTOM_LEFT : AnchorPoint.TOP_LEFT;
        } else if (align == SkillPageAlignment.HORIZONTAL) {
            tabGroup = 1;
            pos = skillPage.getIndex() % MAX_HORIZONTAL;
            index = pos % (MAX_HORIZONTAL / 2);
            style = index == 0 ? (pos < (MAX_HORIZONTAL / 2) ? 0 : 2) : 1;

            tabType = pos < (MAX_HORIZONTAL / 2) ? 0 : 1;
            xOffset = pos <= 3 ? SPACING : -SPACING;
            yOffset = (index * getImage().ySize + index * SPACING) * (pos <= 3 ? 1 : -1);
            pivotPoint = pos <= 3 ? AnchorPoint.TOP_RIGHT : AnchorPoint.BOTTOM_LEFT;
            anchorPoint = pos <= 3 ? AnchorPoint.TOP_LEFT : AnchorPoint.BOTTOM_RIGHT;
        }

        super.init();
    }

    @Override
    public ImageAsset getImage() {
        return ALL_TABS[tabGroup][tabType][isSelected ? 1 : 0][style];
    }


    public void drawToolTips(MatrixStack matrixStack, int mouseX, int mouseY) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0.0F, 0.0F, 200.0F);

        fill(matrixStack, skillTreeScreen.getTrueOffsetX(), skillTreeScreen.getTrueOffsetY(),
                skillTreeScreen.getImage().xSize, skillTreeScreen.getImage().ySize, MathHelper.floor(this.fade * 255.0F) << 24);

        boolean flag = skillTreeScreen.isMouseOver(mouseX, mouseY);
//        int i = MathHelper.floor(this.scrollX);
//        int j = MathHelper.floor(this.scrollY);
//        if (mouseX > 0 && mouseX < 234 && mouseY > 0 && mouseY < 113) {
//            for (AdvancementEntryGui advancemententrygui : this.guis.values()) {
//                if (advancemententrygui.isMouseOver(i, j, mouseX, mouseY)) {
//                    flag = true;
//                    advancemententrygui.drawAdvancementHover(matrixStack, i, j, this.fade, width, height);
//                    break;
//                }
//            }
//        }

        RenderSystem.popMatrix();
        if (flag)
            this.fade = MathHelper.clamp(this.fade + 0.02F, 0.0F, 0.3F);
        else
            this.fade = MathHelper.clamp(this.fade - 0.04F, 0.0F, 1.0F);


    }
}
