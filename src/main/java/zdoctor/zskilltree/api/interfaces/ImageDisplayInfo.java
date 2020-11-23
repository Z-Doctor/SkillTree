package zdoctor.zskilltree.api.interfaces;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.IScreen;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.enums.Layer;

public interface ImageDisplayInfo extends IGuiEventListener, IRenderable, IScreen {

    void updateOffsets();

    ImageAsset getImage();

    int getZIndex();

    int getTrueOffsetX();

    int getTrueOffsetY();

    int getRelativeOffsetX();

    int getRelativeOffsetY();

    ImageDisplayInfo getParentScreen();

    ImageDisplayInfo getAnchor();

    Layer getLayer();

    void init(Minecraft minecraft, int width, int height);

    default void onMouseClicked(double mouseX, double mouseY, int button) {

    }

}
