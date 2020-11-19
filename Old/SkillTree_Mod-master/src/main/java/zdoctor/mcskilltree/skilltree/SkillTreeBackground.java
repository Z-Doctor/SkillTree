package zdoctor.mcskilltree.skilltree;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.client.gui.skilltree.AbstractSkillTreeGui;

public class SkillTreeBackground {
    // TODO Move to more intuitive spot
    public static final ResourceLocation WINDOW = new ResourceLocation(
            McSkillTree.MODID, "textures/gui/skilltree/skill_tree.png");
    public static final ResourceLocation TABS = new ResourceLocation(
            McSkillTree.MODID, "textures/gui/skilltree/tabs.png");

    public static SkillTreeBackground DEFAULT = new SkillTreeBackground(176f, 212f);
    public static SkillTreeBackground SANDSTONE = new SkillTreeBackground(192f, 212f);
    public static SkillTreeBackground ENDSTONE = new SkillTreeBackground(208f, 212f);
    public static SkillTreeBackground DIRT = new SkillTreeBackground(224f, 212f);
    public static SkillTreeBackground NETHERRACK = new SkillTreeBackground(176f, 228f);
    public static SkillTreeBackground STONE = new SkillTreeBackground(192f, 228f);

    private final float u;
    private final float v;
    private final int width;
    private final int height;
    private final int texWidth;
    private final int texHeight;

    protected int startRow = -1;
    protected int endRow = 15;
    protected int startCol = -1;
    protected int endCol = 8;

    protected ResourceLocation background = WINDOW;

    public SkillTreeBackground(float u, float v) {
        this(16, 16, u, v);
    }

    public SkillTreeBackground(int width, int height, float u, float v) {
        this(width, height, u, v, 256, 256);
    }

    public SkillTreeBackground(int width, int height, float u, float v, int texWidth, int texHeight) {
        this.width = width;
        this.height = height;
        this.u = u;
        this.v = v;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
    }

    public float getU() {
        return u;
    }

    public float getV() {
        return v;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getTexWidth() {
        return texWidth;
    }

    public int getTexHeight() {
        return texHeight;
    }

    public ResourceLocation getResourceLocation() {
        return WINDOW;
    }

    public SkillTreeBackground with(int startRow, int endRow, int startCol, int endCol) {
        SkillTreeBackground copy = new SkillTreeBackground(getWidth(), getHeight(), getU(), getV(), getTexWidth(), getTexHeight());
        copy.startRow = startRow;
        copy.endRow = endRow;
        copy.startCol = startCol;
        copy.endCol = endCol;
        copy.background = background;
        return copy;
    }

    public void setDim(int startRow, int endRow, int startCol, int endCol) {
        this.startRow = startRow;
        this.endRow = endRow;
        this.startCol = startCol;
        this.endCol = endCol;
    }

    @OnlyIn(Dist.CLIENT)
    public void render(Minecraft minecraft, int xOffset, int yOffset) {
        int x = xOffset % 16;
        int y = yOffset % 16;
        minecraft.getTextureManager().bindTexture(getResourceLocation());
        for (int row = startRow; row <= endRow; ++row) {
            for (int col = startCol; col <= endCol; ++col) {
                AbstractSkillTreeGui.draw2DTex(x + getWidth() * row, y + getHeight() * col,
                        getU(), getV(), getWidth(), getHeight(),
                        getTexWidth(), getTexHeight());
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public void renderAt(Minecraft minecraft, int x, int y) {
        minecraft.getTextureManager().bindTexture(getResourceLocation());
        for (int row = startRow; row <= endRow; ++row) {
            for (int col = startCol; col <= endCol; ++col) {
                AbstractSkillTreeGui.draw2DTex(x + getWidth() * row, y + getHeight() * col,
                        getU(), getV(), getWidth(), getHeight(),
                        getTexWidth(), getTexHeight());
            }
        }
    }
}
