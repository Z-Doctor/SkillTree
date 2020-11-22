package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.interfaces.IClientSkillTreeTracker;
import zdoctor.zskilltree.api.interfaces.ISkillTreeScreen;
import zdoctor.zskilltree.client.KeyBindings;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.HashMap;

public class SkillTreeScreen extends Screen implements IClientSkillTreeTracker.IListener, ISkillTreeScreen {
    private static int tabPageNumber, maxPages;
    // Not static because I don't want it just sitting in memory
    private final ITextComponent SAD_LABEL = new TranslationTextComponent("advancements.sad_label");
    private final ITextComponent EMPTY = new TranslationTextComponent("advancements.empty");
    private final ImageDisplayInfo window = new ImageDisplayInfo(ImageAssets.SKILL_TREE_WINDOW)
            .setAnchorPoint(AnchorPoint.CENTER).setPivotPoint(AnchorPoint.CENTER);
    private final IClientSkillTreeTracker clientTracker;
    private final HashMap<SkillPage, GuiSkillPage> tabs = new HashMap<>();
    private GuiSkillPage selectedPage;

    public SkillTreeScreen(IClientSkillTreeTracker clientTracker) {
        super(new TranslationTextComponent("zskilltree.skilltree.title"));
        this.clientTracker = clientTracker;
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        window.init(minecraft, width, height);
        addListener(window);
    }

    @Override
    protected void init() {
        window.dispose();
//        window.addDisplay(background);
        buttons.clear();
        tabs.clear();
        selectedPage = null;
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

        if (maxPages > 1) {
            int guiLeft = (width - window.getImage().xSize) / 2;
            int guiTop = (height - window.getImage().ySize) / 2;
            addButton(new Button(guiLeft, guiTop - 50, 20, 20, new StringTextComponent("<"), b -> tabPageNumber = Math.max(tabPageNumber - 1, 0)));
            addButton(new Button(guiLeft + window.getImage().xSize - 20, guiTop - 50, 20, 20, new StringTextComponent(">"), b -> tabPageNumber = Math.min(tabPageNumber + 1, maxPages)));
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
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        int i = window.getTrueOffsetX() + window.getImage().xSize / 2;

        if (maxPages != 0) {
            ITextComponent page = new StringTextComponent(String.format("%d / %d", tabPageNumber + 1, maxPages + 1));
            RenderSystem.disableLighting();
            int j3 = (height - window.getImage().ySize) / 2 - 44;
            drawCenteredString(matrixStack, font, page, i, j3, -1);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
        }

        if (selectedPage == null) {
            fill(matrixStack, window.getTrueOffsetX() + 9, window.getTrueOffsetY() + 18,
                    window.getTrueOffsetX() + window.getImage().xSize, window.getTrueOffsetY() + window.getImage().ySize, -16777216);
            int j1 = window.getTrueOffsetY() + window.getImage().ySize / 2;
            int j2 = window.getTrueOffsetY() + window.getImage().ySize - 18;

            drawCenteredString(matrixStack, this.font, EMPTY, i, j1, -1);
            drawCenteredString(matrixStack, this.font, SAD_LABEL, i, j2, -1);
        }

        // Draws Frame
        RenderSystem.enableBlend();
        window.render(matrixStack, mouseX, mouseY, partialTicks);
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
//        if (pageIn != null)
//            background.setImage(pageIn.getBackgroundImage());
//        else
//            background.setImage(null);
    }

    @Override
    public void skillPageAdded(SkillPage page) {
        GuiSkillPage guiSkillPage = new GuiSkillPage(page, this) {
            @Override
            public void onMouseClicked(double mouseX, double mouseY, int button) {
                if (button == 0)
                    clientTracker.setSelectedPage(page, true);
            }
        };
        // TODO Fix skill page click and mouse hovering
        window.addDisplay(guiSkillPage);
        window.addChild(guiSkillPage);
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
    public void reload() {
        int page = tabPageNumber;
        init(getMinecraft(), width, height);
        tabPageNumber = page > 0 ? Math.min(page, maxPages) : 0;
    }

    @Override
    public void onClose() {
        clientTracker.setListener(null);
    }
}
