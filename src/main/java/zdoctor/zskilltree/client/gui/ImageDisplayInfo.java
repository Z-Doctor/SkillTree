package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.enums.Layer;
import zdoctor.zskilltree.api.interfaces.IRenderableHandler;
import zdoctor.zskilltree.extra.ImageAsset;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// TODO Add different rendering types (tiled, stretched, fit, true size, etc)
// TODO Perhaps make rending z-index based from the renderer and not sorting based on parent(how?)
// TODO Add system to make custom display info such as rendering text, solid colors, etc than can be registered
//  maybe even add support for datapacks to render if possible
public class ImageDisplayInfo extends AbstractGuiSkillTreeScreen {
    private static final ITextComponent EMPTY = new TranslationTextComponent("title.untitled");
    protected final List<IGuiEventListener> children = new ArrayList<>();
    public Layer layer = Layer.DEFAULT;
    /**
     * Where relative to the parent the starting point is (where it is drawn); if parent is null, is relative to screen
     */
    public AnchorPoint anchorPoint = AnchorPoint.TOP_LEFT;
    /**
     * Where relative to the anchor point the starting point is (where it is drawn)
     */
    public AnchorPoint pivotPoint = AnchorPoint.TOP_LEFT;
    public int xOffset;
    public int yOffset;
    public boolean listenMouse = true;
    public final boolean buttonsEnabled = true;
    public final boolean hidden = false;
    public IRenderableHandler preRender;
    protected ImageDisplayInfo root;
    protected ImageDisplayInfo parent;
    protected ITextComponent title;
    protected int zIndex = 0;
    private ImageAsset image;
    private int xRelativeOffset;
    private int yRelativeOffset;
    private int xTrueOffset;
    private int yTrueOffset;

    public ImageDisplayInfo() {
        super(EMPTY);
        root = this;
    }

    protected ImageDisplayInfo(ITextComponent title) {
        super(title);
        root = this;
    }

    public ImageDisplayInfo(@Nonnull ImageAsset imageAsset) {
        this(imageAsset, 0, 0);
    }

    public ImageDisplayInfo(@Nonnull ImageAsset imageAsset, int xOffset, int yOffset) {
        super(new TranslationTextComponent(imageAsset.toString()));
        root = this;
        image = imageAsset;
        this.xRelativeOffset = xOffset;
        this.yRelativeOffset = yOffset;
    }

    public ImageDisplayInfo addChild(ImageDisplayInfo child) {
        child.root = root;
        child.parent = this;
        child.listenMouse = listenMouse;
        children.add(child);
        return this;
    }

    public ImageDisplayInfo addMiscChild(IGuiEventListener child) {
        children.add(child);
        return this;
    }

    public void clearChildren() {
        children.clear();
        super.children.clear();
    }

    public void updateOffsets() {
        xRelativeOffset = xOffset;
        yRelativeOffset = yOffset;

        switch (anchorPoint) {
            case TOP_RIGHT:
                xRelativeOffset += parent != null ? parent.getImage().xSize : width;
                break;
            case TOP_CENTER:
                xRelativeOffset += (parent != null ? parent.getImage().xSize : width) / 2;
                break;
            case CENTER:
                xRelativeOffset += (parent != null ? parent.getImage().xSize : width) / 2;
                yRelativeOffset += (parent != null ? parent.getImage().ySize : height) / 2;
                break;
            case LEFT_CENTER:
                yRelativeOffset += (parent != null ? parent.getImage().ySize : height) / 2;
                break;
            case RIGHT_CENTER:
                xRelativeOffset += parent != null ? parent.getImage().xSize : width;
                yRelativeOffset += (parent != null ? parent.getImage().ySize : height) / 2;
                break;
            case BOTTOM_LEFT:
                yRelativeOffset += parent != null ? parent.getImage().ySize : height;
                break;
            case BOTTOM_CENTER:
                xRelativeOffset += (parent != null ? parent.getImage().xSize : width) / 2;
                yRelativeOffset += parent != null ? parent.getImage().ySize : height;
                break;
            case BOTTOM_RIGHT:
                xRelativeOffset += parent != null ? parent.getImage().xSize : width;
                yRelativeOffset += parent != null ? parent.getImage().ySize : height;
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
            case TOP_LEFT:
            default:
                break;
        }

        xTrueOffset = parent != null ? parent.xTrueOffset + xRelativeOffset : xRelativeOffset;
        yTrueOffset = parent != null ? parent.yTrueOffset + yRelativeOffset : yRelativeOffset;
    }

    @Override
    public ITextComponent getTitle() {
        return super.getTitle();
    }

    public void sortChildren() {
        children.sort((a, b) -> {
            if (a == null || b == null)
                return a == null ? 1 : -1;
                // For non-ImageDisplayInfo, they are rendered based on when they were added and are not sorted
            else if (!(a instanceof ImageDisplayInfo) || !(b instanceof ImageDisplayInfo))
                return 0;
            ImageDisplayInfo first = (ImageDisplayInfo) a;
            ImageDisplayInfo second = (ImageDisplayInfo) b;
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
        super.children.addAll(children);
        updateOffsets();
        super.children.forEach(child -> {
            if (child instanceof Screen)
                ((Screen) child).init(minecraft, width, height);
        });
    }

    public void bindTexture() {
        this.minecraft.getTextureManager().bindTexture(getImage().texture);
    }

    public ImageAsset getImage() {
        return image;
    }

    public void setImage(ImageAsset image) {
        this.image = image;
    }

    public int getTrueOffsetX() {
        return xTrueOffset;
    }

    public int getTrueOffsetY() {
        return yTrueOffset;
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }

    public int getRelativeOffsetX() {
        return xRelativeOffset;
    }

    public int getRelativeOffsetY() {
        return yRelativeOffset;
    }

    public void renderAt(MatrixStack matrixStack, int x, int y, float partialTicks) {
        bindTexture();
        this.blit(matrixStack, x, y, getImage().uOffset, getImage().vOffset, getImage().xSize, getImage().ySize);
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

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (hidden)
            return;

        Iterator<IRenderable> itr = children.stream().filter(child -> child instanceof IRenderable)
                .map(child -> (IRenderable) child).iterator();

        // Renders background
        IRenderable child = null;
        while (itr.hasNext()) {
            child = itr.next();
            if (child instanceof ImageDisplayInfo)
                if (((ImageDisplayInfo) child).layer == Layer.DEFAULT)
                    break;
            child.render(matrixStack, mouseX, mouseY, partialTicks);
            child = null;
        }

        // Renders main window
        if (preRender == null || preRender.preRender(matrixStack, mouseX, mouseY, partialTicks))
            renderAt(matrixStack, xTrueOffset, yTrueOffset, partialTicks);
        if (buttonsEnabled)
            super.render(matrixStack, mouseX, mouseY, partialTicks);

        // Renders everything else (foreground layers should be at the end of the stream)
        while (child != null) {
            child.render(matrixStack, mouseX, mouseY, partialTicks);
            child = itr.hasNext() ? itr.next() : null;
        }

    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (getImage() == null)
            return false;
        return (mouseX >= xTrueOffset && mouseX <= xTrueOffset + getImage().xSize) &&
                (mouseY >= yTrueOffset && mouseY <= yTrueOffset + getImage().ySize);
    }

    // Classes that extend this class should be able to just override what they want to happen when their
    // class is clicked as long as there class isn't a root
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!root.listenMouse)
            return false;
        if (listenMouse && isMouseOver(mouseX, mouseY)) {
            onMouseClicked(mouseX, mouseY, button);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void onMouseClicked(double mouseX, double mouseY, int button) {
    }


}
