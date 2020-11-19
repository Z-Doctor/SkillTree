package zdoctor.mcskilltree.api;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.INestedGuiEventHandler;
import net.minecraft.client.renderer.ItemRenderer;
import zdoctor.mcskilltree.skilltree.SkillTreeInfo;

import javax.annotation.Nullable;

public interface ISkillTreeTabGui extends INestedGuiEventHandler {
    int getPage();

    SkillTreeInfo getDisplayInfo();

    boolean isActive();

    void setActive(boolean active);

    void reload();

    void buildTree();

    void drawTab(int guiLeft, int guiTop, boolean isSelected);


//    void preDrawContents(int guiLeft, int guiTop, int mouseX, int mouseY);
//    void drawContents(int guiLeft, int guiTop, int mouseX, int mouseY);
//    void postDrawContents(int guiLeft, int guiTop, int mouseX, int mouseY);


    void onOpen();

    void onClose();

    ISkillInfoGui getFocused();

    double getScrollX();

    double getScrollY();

    // Cleaned

    /**
     * Called to render contents before being translated to the top left corner of bounds, with true mouse position
     * @param mouseX True mouse x position
     * @param mouseY True mouse y position
     * @param partialTicks The partial ticks
     */
    default void preRender(int mouseX, int mouseY, float partialTicks) {

    }

    /**
     * Called to render contents after being translated to the top left corner of bounds, with true mouse position
     * and partial ticks
     * @param mouseX True mouse x position
     * @param mouseY True mouse y position
     * @param partialTicks The partial ticks
     */
    void render(int mouseX, int mouseY, float partialTicks);

    /**
     * Called to render contents after translated matrix is popped, with true mouse position
     * and partial ticks
     * @param mouseX True mouse x position
     * @param mouseY True mouse y position
     * @param partialTicks The partial ticks
     */
    default void postRender(int mouseX, int mouseY, float partialTicks) {

    }

    /**
     * Called to render contents before being translated to the top left corner of bounds with, an adjusted mouse position
     * and partial ticks
     * @param mouseX Adjusted mouse x position; 0 is top left corner of bounds
     * @param mouseY Adjusted mouse y position; 0 is top left corner of bounds
     * @param partialTicks The partial ticks
     */
    default void preRenderAdjusted(int mouseX, int mouseY, float partialTicks) {
    }

    /**
     * Called to render contents after being translated to the top left corner of bounds, with an adjusted mouse position
     * and partial ticks
     * @param mouseX Adjusted mouse x position; 0 is top left corner of bounds
     * @param mouseY Adjusted mouse y position; 0 is top left corner of bounds
     * @param partialTicks The partial ticks
     */
    void renderAdjusted(int mouseX, int mouseY, float partialTicks);

    /**
     * Called to render contents after translated matrix is popped, with an adjusted mouse position
     * and partial ticks
     * @param mouseX Adjusted mouse x position; 0 is top left corner of bounds
     * @param mouseY Adjusted mouse y position; 0 is top left corner of bounds
     * @param partialTicks The partial ticks
     */
    default void postRenderAdjusted(int mouseX, int mouseY, float partialTicks) {

    }

    /**
     * Called to render icon of Tab at the given offset
     * @param offsetX The x position
     * @param offsetY The y position
     * @param renderItemIn The Item Renderer
     */
    void drawIcon(int offsetX, int offsetY, ItemRenderer renderItemIn);

}
