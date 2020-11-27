package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.enums.Layer;
import zdoctor.zskilltree.api.interfaces.ImageDisplayInfo;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO Add different rendering types (tiled, stretched, fit, true size, etc)
// TODO Perhaps make rending z-index based from the renderer and not sorting based on parent(how?)
// TODO Add system to make custom display info such as rendering text, solid colors, etc than can be registered
//  maybe even add support for datapacks to render if possible
@OnlyIn(Dist.CLIENT)
public class ImageScreen extends AbstractSkillTreeScreen implements ImageDisplayInfo {
    public static final ImageScreen MISSING = new ImageScreen();

    protected final List<ImageDisplayInfo> childScreens = new ArrayList<>();
    public Layer layer = Layer.DEFAULT;
    protected ImageDisplayInfo root;
    protected ImageDisplayInfo parentScreen;
    /**
     * Where relative to the anchor the starting point is (where it is drawn); if parent is null, is relative to screen
     */
    protected AnchorPoint anchorPoint = AnchorPoint.TOP_LEFT;
    /**
     * Where relative to the anchor point the starting point is (where it is drawn)
     */
    protected AnchorPoint pivotPoint = AnchorPoint.TOP_LEFT;
    protected boolean buttonsEnabled = true;
    protected boolean hidden = false;
    protected boolean listenMouse = true;

    protected ITextComponent title;

    protected int zIndex = 0;
    protected ImageDisplayInfo anchor;
    protected int xOffset;
    protected int yOffset;
    private ImageAsset image;
    private int xRelativeOffset;
    private int yRelativeOffset;
    private int xTrueOffset;
    private int yTrueOffset;

    private ImageScreen() {
        super(new TranslationTextComponent("title.untitled"));
        root = this;
        image = ImageAssets.MISSING;
    }

    protected ImageScreen(ITextComponent title) {
        super(title);
        root = this;
    }

    public ImageScreen(@Nonnull ImageAsset imageAsset) {
        this(imageAsset, 0, 0);
    }

    public ImageScreen(@Nonnull ImageAsset imageAsset, int xOffset, int yOffset) {
        super(new TranslationTextComponent(imageAsset.toString()));
        root = this;
        image = imageAsset;
        this.xRelativeOffset = xOffset;
        this.yRelativeOffset = yOffset;
    }

    public ImageScreen addDisplay(ImageScreen display) {
        display.root = root;
        display.parentScreen = this;
        // Should I only set when it's not already set?
        if (display.anchor == null)
            display.anchor = this;
        display.listenMouse = listenMouse;
        childScreens.add(display);
        display.updateOffsets();
        return this;
    }

    @Override
    public void updateOffsets() {
        xRelativeOffset = xOffset;
        yRelativeOffset = yOffset;

        switch (anchorPoint) {
            case TOP_RIGHT:
                xRelativeOffset += getAnchor() != null ? getAnchor().getImage().xSize : width;
                break;
            case TOP_CENTER:
                xRelativeOffset += (getAnchor() != null ? getAnchor().getImage().xSize : width) / 2;
                break;
            case CENTER:
                xRelativeOffset += (getAnchor() != null ? getAnchor().getImage().xSize : width) / 2;
                yRelativeOffset += (getAnchor() != null ? getAnchor().getImage().ySize : height) / 2;
                break;
            case LEFT_CENTER:
                yRelativeOffset += (getAnchor() != null ? getAnchor().getImage().ySize : height) / 2;
                break;
            case RIGHT_CENTER:
                xRelativeOffset += getAnchor() != null ? getAnchor().getImage().xSize : width;
                yRelativeOffset += (getAnchor() != null ? getAnchor().getImage().ySize : height) / 2;
                break;
            case BOTTOM_LEFT:
                yRelativeOffset += getAnchor() != null ? getAnchor().getImage().ySize : height;
                break;
            case BOTTOM_CENTER:
                xRelativeOffset += (getAnchor() != null ? getAnchor().getImage().xSize : width) / 2;
                yRelativeOffset += getAnchor() != null ? getAnchor().getImage().ySize : height;
                break;
            case BOTTOM_RIGHT:
                xRelativeOffset += getAnchor() != null ? getAnchor().getImage().xSize : width;
                yRelativeOffset += getAnchor() != null ? getAnchor().getImage().ySize : height;
                break;
            case ABSOLUTE:
                xRelativeOffset = xOffset;
                yRelativeOffset = yOffset;
                break;
            case TOP_LEFT:
            default:
                break;
        }

        switch (pivotPoint) {
            case TOP_RIGHT:
                xRelativeOffset -= getImage().xSize;
                break;
            case TOP_CENTER:
                xRelativeOffset -= getImage().xSize / 2;
                break;
            case CENTER:
                xRelativeOffset -= getImage().xSize / 2;
                yRelativeOffset -= getImage().ySize / 2;
                break;
            case LEFT_CENTER:
                yRelativeOffset -= getImage().ySize / 2;
                break;
            case RIGHT_CENTER:
                xRelativeOffset -= getImage().xSize;
                yRelativeOffset -= getImage().ySize / 2;
                break;
            case BOTTOM_LEFT:
                yRelativeOffset -= getImage().ySize;
                break;
            case BOTTOM_CENTER:
                xRelativeOffset -= getImage().xSize / 2;
                yRelativeOffset -= getImage().ySize;
                break;
            case BOTTOM_RIGHT:
                xRelativeOffset -= getImage().xSize;
                yRelativeOffset -= getImage().ySize;
                break;
            case ABSOLUTE:
                xRelativeOffset = xOffset;
                yRelativeOffset = yOffset;
                break;
            case TOP_LEFT:
            default:
                break;
        }


        if (pivotPoint != AnchorPoint.ABSOLUTE) {
            xTrueOffset = getAnchor() != null ? getAnchor().getTrueOffsetX() + xRelativeOffset : xRelativeOffset;
            yTrueOffset = getAnchor() != null ? getAnchor().getTrueOffsetY() + yRelativeOffset : yRelativeOffset;
        } else {
            xTrueOffset = xRelativeOffset;
            yTrueOffset = yRelativeOffset;
        }

        childScreens.forEach(ImageDisplayInfo::updateOffsets);
    }

    @Override
    public ImageAsset getImage() {
        return image;
    }

    public void setImage(ImageAsset image) {
        this.image = image;
    }

    @Override
    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    @Override
    public int getTrueOffsetX() {
        return xTrueOffset;
    }

    @Override
    public int getTrueOffsetY() {
        return yTrueOffset;
    }

    @Override
    public int getRelativeOffsetX() {
        return xRelativeOffset;
    }

    @Override
    public int getRelativeOffsetY() {
        return yRelativeOffset;
    }

    @Override
    public ImageDisplayInfo getParentScreen() {
        return parentScreen;
    }

    @Override
    public ImageDisplayInfo getAnchor() {
        return anchor;
    }

    public void setAnchor(ImageDisplayInfo anchor) {
        this.anchor = anchor;
    }

    public Layer getLayer() {
        return layer;
    }

    public void setOffsets(int x, int y) {
        this.xOffset = x;
        this.yOffset = y;
    }

    public void setAnchorPoint(AnchorPoint anchorPoint) {
        this.anchorPoint = anchorPoint;
    }

    public void setPivotPoint(AnchorPoint pivotPoint) {
        this.pivotPoint = pivotPoint;
    }

    public void dispose() {
        childScreens.clear();
        children.clear();
    }

    @Override
    public void init(Minecraft minecraft, int width, int height) {
        super.init(minecraft, width, height);
        childScreens.forEach(display -> display.init(minecraft, width, height));
        updateOffsets();
    }

    @Override
    public ITextComponent getTitle() {
        return super.getTitle();
    }

    public void sortChildren() {
        childScreens.sort((a, b) -> {
            if (a == null || b == null)
                return a == null ? 1 : -1;
                // For non-ImageDisplayInfo, they are rendered based on when they were added and are not sorted
            else if (!(a instanceof ImageScreen) || !(b instanceof ImageScreen))
                return 0;
            ImageScreen first = (ImageScreen) a;
            ImageScreen second = (ImageScreen) b;
            if (first.layer == Layer.DEFAULT || second.layer == Layer.DEFAULT) {
                if (first.layer == Layer.BACKGROUND)
                    return -1;
                else if (first.layer == Layer.FOREGROUND)
                    return 1;
                else if (second.layer == Layer.BACKGROUND)
                    return 1;
                else if (second.layer == Layer.FOREGROUND)
                    return -1;
                else
                    return Integer.compare(first.getZIndex(), second.getZIndex());
            } else if (first.layer == Layer.BACKGROUND && second.layer == Layer.FOREGROUND)
                return -1;
            else if (first.layer == Layer.FOREGROUND && second.layer == Layer.BACKGROUND)
                return 1;
            else
                return Integer.compare(first.getZIndex(), second.getZIndex());
        });
    }

    @Override
    protected void init() {
        sortChildren();
    }

    public void bindTexture() {
        getMinecraft().getTextureManager().bindTexture(getImage().texture);
    }

    public void bindTexture(ImageAsset imageAsset) {
        getMinecraft().getTextureManager().bindTexture(imageAsset.texture);
    }

    public void renderAt(MatrixStack matrixStack, int x, int y, float partialTicks) {
        bindTexture();
        this.blit(matrixStack, x, y, getImage().uOffset, getImage().vOffset, getImage().xSize, getImage().ySize);
    }

    public void renderAt(MatrixStack matrixStack, int x, int y, float partialTicks, ImageAsset imageAsset) {
        bindTexture(imageAsset);
        this.blit(matrixStack, x, y, imageAsset.uOffset, imageAsset.vOffset, imageAsset.xSize, imageAsset.ySize);
    }

    public void renderRepeating(MatrixStack matrixStack, int cols, int rows, float partialTicks) {
        renderRepeating(matrixStack, 0, 0, cols, rows, partialTicks);
    }

    public void renderRepeating(MatrixStack matrixStack, int startX, int startY, int cols, int rows, float partialTicks) {
        for (int x = 0; x < cols; x++) {
            for (int y = 0; y < rows; y++) {
                renderAt(matrixStack, startX + xTrueOffset + x * getImage().xSize, startY + yTrueOffset + y * getImage().ySize, partialTicks);
            }
        }
    }

    protected final ImageDisplayInfo renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Iterator<ImageDisplayInfo> itr) {
        ImageDisplayInfo child = null;
        while (itr.hasNext()) {
            child = itr.next();
            if (child.getLayer() == Layer.DEFAULT)
                break;
            child.render(matrixStack, mouseX, mouseY, partialTicks);
            child = null;
        }
        return child;
    }

    protected final void renderForeground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks, Iterator<ImageDisplayInfo> itr, ImageDisplayInfo child) {
        while (child != null) {
            child.render(matrixStack, mouseX, mouseY, partialTicks);
            child = itr.hasNext() ? itr.next() : null;
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (hidden)
            return;

        Iterator<ImageDisplayInfo> itr = childScreens.iterator();
        ImageDisplayInfo child = renderBackground(matrixStack, mouseX, mouseY, partialTicks, itr);
        renderMain(matrixStack, mouseX, mouseY, partialTicks);
        renderForeground(matrixStack, mouseX, mouseY, partialTicks, itr, child);
    }

    protected void renderMain(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderAt(matrixStack, xTrueOffset, yTrueOffset, partialTicks);
        if (buttonsEnabled)
            renderButtons(matrixStack, mouseX, mouseY, partialTicks);
    }

    protected void renderButtons(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        for (Widget button : this.buttons)
            button.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (getImage() == null)
            return false;
        return (mouseX >= xTrueOffset && mouseX <= xTrueOffset + getImage().xSize) &&
                (mouseY >= yTrueOffset && mouseY <= yTrueOffset + getImage().ySize);
    }


}
