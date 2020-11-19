package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.enums.Layer;
import zdoctor.zskilltree.api.interfaces.IClientProgressTracker;
import zdoctor.zskilltree.client.KeyBindings;
import zdoctor.zskilltree.skillpages.SkillPage;

import java.util.HashMap;
import java.util.Map;

public class GuiSkillTreeScreen extends AbstractGuiSkillTreeScreen implements IClientProgressTracker.IListener {
    // TODO Make my own sad_label and empty
    protected static final ITextComponent SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    protected static final ITextComponent EMPTY = new TranslationTextComponent("advancements.empty");
    private static int tabPageNumber = 0;
    private final HashMap<SkillPage, GuiSkillPage> tabs = new HashMap<>();
    private final IClientProgressTracker skillTreeHandler;
    protected ImageDisplayInfo window = new ImageDisplayInfo(ImageAssets.SKILL_TREE_WINDOW) {
        {
            anchorPoint = AnchorPoint.CENTER;
            pivotPoint = AnchorPoint.CENTER;

            preRender = (matrixStack, mouseX, mouseY, partialTicks) -> {
                RenderSystem.enableBlend();
                return true;
            };
        }
    };
    protected ImageDisplayInfo background = new ImageDisplayInfo() {
        {
            layer = Layer.BACKGROUND;
            xOffset = 6;
            yOffset = 6;
        }

        @Override
        public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
            if (getImage() == null)
                return;
            background.renderRepeating(matrixStack, 15, 8, partialTicks);
        }
    };
    private int maxPages = 0;

    private GuiSkillPage selectedPage;

    public GuiSkillTreeScreen(IClientProgressTracker skillTreeHandler) {
        super(new TranslationTextComponent("zskilltree.skilltree.title"));
        this.skillTreeHandler = skillTreeHandler;
        window.addChild(background);
    }

    public int getTabPage() {
        return tabPageNumber;
    }

    @Override
    public void setSelectedPage(SkillPage pageIn) {
        if (selectedPage != null)
            selectedPage.setSelected(false);

        selectedPage = tabs.get(pageIn);
        if (selectedPage != null) {
            selectedPage.setSelected(true);
            tabPageNumber = selectedPage.getPageNumber();
            window.sortChildren();
        }
        if (pageIn != null)
            background.setImage(pageIn.getBackgroundImage());
        else
            background.setImage(null);
    }

    public GuiSkillPage getSkillPage(SkillPage skillPage) {
        return this.tabs.get(skillPage);
    }

    @Override
    public void skillPageAdded(SkillPage page) {
        GuiSkillPage guiSkillPage = new GuiSkillPage(page, this) {
            @Override
            public void onMouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0)
                    skillTreeHandler.setSelectedPage(page, true);
            }
        };
        window.addChild(guiSkillPage);
        guiSkillPage.preRender = (matrixStack, mouseX, mouseY, partialTicks) -> tabPageNumber == guiSkillPage.getPageNumber();
        tabs.put(page, guiSkillPage);
    }

    @Override
    public void reload() {
        int page = tabPageNumber;
        init();
        tabPageNumber = page > 0 ? Math.min(page, maxPages) : 0;
    }

    @Override
    protected void init() {
        children.add(window);
        window.clearChildren();
        window.addChild(background);
        buttons.clear();
        tabs.clear();
        this.selectedPage = null;
        this.skillTreeHandler.setListener(this);

        if (selectedPage == null && !tabs.isEmpty())
            skillTreeHandler.setSelectedPage(tabs.values().stream().findFirst().get().getSkillPage(), true);
        else
            skillTreeHandler.setSelectedPage(selectedPage == null ? null : selectedPage.getSkillPage(), true);

        maxPages = Integer.max(skillTreeHandler.getMaxVertical() / GuiSkillPage.MAX_VERTICAL,
                skillTreeHandler.getMaxHorizontal() / GuiSkillPage.MAX_HORIZONTAL) - 1;


        if (maxPages > 1) {
            int guiLeft = (width - window.getImage().xSize) / 2;
            int guiTop = (height - window.getImage().ySize) / 2;
            addButton(new Button(guiLeft, guiTop - 50, 20, 20, new StringTextComponent("<"), b -> tabPageNumber = Math.max(tabPageNumber - 1, 0)));
            addButton(new Button(guiLeft + window.getImage().xSize - 20, guiTop - 50, 20, 20, new StringTextComponent(">"), b -> tabPageNumber = Math.min(tabPageNumber + 1, maxPages)));
        }

        window.init(minecraft, width, height);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        int i = window.getTrueOffsetX() + window.getImage().xSize / 2;

        if (tabs.values().isEmpty()) {
            fill(matrixStack, window.getTrueOffsetX() + 9, window.getTrueOffsetY() + 18,
                    window.getTrueOffsetX() + window.getImage().xSize, window.getTrueOffsetY() + window.getImage().ySize, -16777216);
            int j1 = window.getTrueOffsetY() + window.getImage().ySize / 2;
            int j2 = window.getTrueOffsetY() + window.getImage().ySize - 18;

            drawCenteredString(matrixStack, this.font, EMPTY, i, j1, -1);
            drawCenteredString(matrixStack, this.font, SAD_LABEL, i, j2, -1);
        }
        window.render(matrixStack, mouseX, mouseY, partialTicks);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (maxPages != 0) {
            ITextComponent page = new StringTextComponent(String.format("%d / %d", tabPageNumber + 1, maxPages + 1));
            RenderSystem.disableLighting();
            int j3 = (height - window.getImage().ySize) / 2 - 44;
            drawCenteredString(matrixStack, font, page, i, j3, -1);
        }

        // Renders the tooltip of a skill page
        for (Map.Entry<SkillPage, GuiSkillPage> entry : tabs.entrySet()) {
            GuiSkillPage tab = entry.getValue();
            if(tab.isSelected() || tab.getPageNumber() != tabPageNumber)
                continue;

            if (tab.isMouseOver(mouseX, mouseY)) {
                renderWrappedToolTip(matrixStack, getTooltipFromSkillPage(entry.getKey()), mouseX, mouseY, font);
                break;
            }
        }
    }

    @Override
    public void onClose() {
        skillTreeHandler.setListener(null);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (KeyBindings.SKILL_TREE.isActiveAndMatches(mouseKey)) {
            closeScreen();
            this.minecraft.mouseHelper.grabMouse();
            return true;
        } else if (KeyBindings.RECENTER_SKILL_TREE.isActiveAndMatches(mouseKey)) {
            // TODO Add functionality, recenter
            setSelectedPage(null);
            return true;
        }
        return false;
    }

}
