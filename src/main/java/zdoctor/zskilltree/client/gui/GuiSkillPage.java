package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.extra.ImageAsset;
import zdoctor.zskilltree.skillpages.SkillPage;

public class GuiSkillPage extends ImageDisplayInfo {
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
    private final GuiSkillTreeScreen skillScreen;


    private boolean isSelected;

    private boolean hasDisplay;
    private int index;
    private int style;
    private int pageNumber;
    private int tabGroup;
    private int tabType;

    public GuiSkillPage(SkillPage page, GuiSkillTreeScreen skillScreen) {
        super(page.getPageName());
        this.skillScreen = skillScreen;
        this.skillPage = page;
        this.pageNumber = page.getIndex() / (page.getAlignment() == SkillPageAlignment.VERTICAL ? MAX_VERTICAL : MAX_HORIZONTAL);
        // TODO Add skills
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // Draws title of page
        if (isSelected() && getSkillPage().drawInForegroundOfTab()) {
            this.font.func_243248_b(matrixStack, getSkillPage().getPageName(), root.getTrueOffsetX() + 8,
                    root.getTrueOffsetY() + 6, getSkillPage().getLabelColor());
        }
        // Draws children
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Draws page if this page number matches current page number
        if (skillScreen.getTabPage() == pageNumber) {
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
    public boolean isMouseOver(double mouseX, double mouseY) {
        return getPageNumber() == skillScreen.getTabPage() && super.isMouseOver(mouseX, mouseY);
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
            pivotPoint = pos < (MAX_VERTICAL / 2) ? AnchorPoint.BOTTOM_LEFT : AnchorPoint.TOP_LEFT;
            anchorPoint = pos < (MAX_VERTICAL / 2) ? AnchorPoint.TOP_LEFT : AnchorPoint.BOTTOM_LEFT;
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


}
