package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.interfaces.IClientSkillTreeTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeScreen;
import zdoctor.zskilltree.client.KeyBindings;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillTreeScreen extends ImageScreen implements ISkillTreeScreen {
    private static int tabPageNumber, maxPages;
    // Not static because I don't want it just sitting in memory
    private final ITextComponent SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    private final ITextComponent EMPTY = new TranslationTextComponent("advancements.empty");
    //    private final ImageDisplayInfo window = new ImageDisplayInfo(ImageAssets.SKILL_TREE_WINDOW)
//            .setAnchorPoint(AnchorPoint.CENTER).setPivotPoint(AnchorPoint.CENTER);
    private final IClientSkillTreeTracker clientTracker;
    private final HashMap<SkillPage, GuiSkillPage> tabs = new HashMap<>();
    private GuiSkillPage selectedPage;

    public SkillTreeScreen(IClientSkillTreeTracker clientTracker) {
        super(new TranslationTextComponent("zskilltree.skilltree.title"));
        this.clientTracker = clientTracker;
        setImage(ImageAssets.SKILL_TREE_WINDOW);
        setPivotPoint(AnchorPoint.CENTER);
        setAnchorPoint(AnchorPoint.CENTER);
        buttonsEnabled = false; // I do this myself
    }

    @Override
    protected void init() {
        tabs.clear();
        childScreens.clear();
        int page = tabPageNumber;
        clientTracker.setListener(this);

        if (selectedPage == null && !tabs.isEmpty()) {
            if (clientTracker.getDefaultPage() == null)
                clientTracker.setSelectedPage(tabs.values().stream().findFirst().get().getSkillPage(), true);
            else
                clientTracker.setSelectedPage(clientTracker.getDefaultPage(), true);
        } else
            clientTracker.setSelectedPage(selectedPage == null ? null : selectedPage.getSkillPage(), true);

        maxPages = Integer.max(clientTracker.getMaxVertical() / GuiSkillPage.MAX_VERTICAL,
                clientTracker.getMaxHorizontal() / GuiSkillPage.MAX_HORIZONTAL);


        if (maxPages > 0) {
            int guiLeft = (width - getImage().xSize) / 2;
            int guiTop = (height - getImage().ySize) / 2;
            addButton(new Button(guiLeft, guiTop - 50, 20, 20, new StringTextComponent("<"), b -> tabPageNumber = Math.max(tabPageNumber - 1, 0)));
            addButton(new Button(guiLeft + getImage().xSize - 20, guiTop - 50, 20, 20, new StringTextComponent(">"), b -> tabPageNumber = Math.min(tabPageNumber + 1, maxPages)));
            tabPageNumber = page > 0 ? Math.min(page, maxPages) : 0;
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (super.keyPressed(keyCode, scanCode, modifiers))
            return true;

        InputMappings.Input mouseKey = InputMappings.getInputByCode(keyCode, scanCode);
        if (KeyBindings.SKILL_TREE.isActiveAndMatches(mouseKey)) {
            closeScreen();
            minecraft.mouseHelper.grabMouse();
            return true;
        } else if (KeyBindings.RECENTER_SKILL_TREE.isActiveAndMatches(mouseKey)) {
            // TODO Add functionality, recenter
            setSelectedPage(null);
            return true;
        }
        return false;
    }

    @Override
    protected void renderMain(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.enableBlend();
        super.renderMain(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        int i = getTrueOffsetX() + getImage().xSize / 2;

        if (maxPages != 0) {
            ITextComponent page = new StringTextComponent(String.format("%d / %d", tabPageNumber + 1, maxPages + 1));
            RenderSystem.disableLighting();
            int j3 = (height - getImage().ySize) / 2 - 44;
            drawCenteredString(matrixStack, font, page, i, j3, -1);
            renderButtons(matrixStack, mouseX, mouseY, partialTicks);
        }

        if (selectedPage == null) {
            fill(matrixStack, getTrueOffsetX() + 9, getTrueOffsetY() + 18,
                    getTrueOffsetX() + getImage().xSize, getTrueOffsetY() + getImage().ySize, -16777216);
            int j1 = getTrueOffsetY() + getImage().ySize / 2;
            int j2 = getTrueOffsetY() + getImage().ySize - 18;

            drawCenteredString(matrixStack, this.font, EMPTY, i, j1, -1);
            drawCenteredString(matrixStack, this.font, SAD_LABEL, i, j2, -1);
        }

        // Draws Frame
        for (int x = 0; x < 15; x++) {
            for (int y = 0; y < 7; y++) {
                renderAt(matrixStack, getTrueOffsetX() + 9 + x * ImageAssets.DEFAULT_TILE.xSize,
                        getTrueOffsetY() + 18 + y * ImageAssets.DEFAULT_TILE.ySize, partialTicks, ImageAssets.DEFAULT_TILE);
            }
        }
        RenderSystem.enableBlend();
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Renders the tooltip of a skill page
        if (!isMouseOver(mouseX, mouseY))
            for (Map.Entry<SkillPage, GuiSkillPage> entry : tabs.entrySet()) {
                GuiSkillPage tab = entry.getValue();
                if (tab.isSelected() || tab.getPageNumber() != tabPageNumber)
                    continue;

                if (tab.isMouseOver(mouseX, mouseY)) {
                    renderWrappedToolTip(matrixStack, getTooltipFromSkillPage(entry.getKey()), mouseX, mouseY, font);
                    break;
                }
            }
    }



    @Override
    public void setSelectedPage(SkillPage pageIn) {
        if (selectedPage != null)
            selectedPage.setSelected(false);

        selectedPage = tabs.get(pageIn);
        if (selectedPage != null) {
            selectedPage.setSelected(true);
            tabPageNumber = selectedPage.getPageNumber();
            sortChildren();
        }
    }

    @Override
    public void skillPageAdded(SkillPage page) {
        GuiSkillPage guiSkillPage = new GuiSkillPage(page, this);
        addListener(guiSkillPage);
        addDisplay(guiSkillPage);
        tabs.put(page, guiSkillPage);
    }

    @Override
    public int getTabPageNumber() {
        return tabPageNumber;
    }

    @Override
    public IClientSkillTreeTracker getClientTracker() {
        return clientTracker;
    }

    @Override
    public <T extends IGuiEventListener> T addListener(T listener) {
        return super.addListener(listener);
    }

    @Override
    public void reload() {
        init(getMinecraft(), width, height);
    }

    @Override
    public void onClose() {
        if (selectedPage != null)
            tabPageNumber = selectedPage.getPageNumber();
        clientTracker.setListener(null);
    }
}
