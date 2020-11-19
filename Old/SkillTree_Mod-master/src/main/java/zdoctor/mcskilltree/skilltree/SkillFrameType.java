package zdoctor.mcskilltree.skilltree;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.client.gui.skilltree.AbstractSkillTreeGui;

public class SkillFrameType {
    // TODO Move and make a separate file
    public static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");

    public static SkillFrameType NORMAL = new SkillFrameType(72, 140, 26, 26, 256, 256, TextFormatting.GREEN);
    public static SkillFrameType SPECIAL = new SkillFrameType(98, 140, 26, 26, 256, 256, TextFormatting.DARK_PURPLE);
    public static SkillFrameType ROUNDED = new SkillFrameType(124, 140, 26, 26, 256, 256, TextFormatting.GREEN);

    protected int u;
    protected int v;
    protected int width;
    protected int height;
    protected int texWidth;
    protected int texHeight;
    protected TextFormatting format;
    protected ResourceLocation background;

    public SkillFrameType(int u, int v, int width, int height, int texWidth, int texHeight, TextFormatting formatIn) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
        this.texWidth = texWidth;
        this.texHeight = texHeight;
        this.format = formatIn;
    }

    @OnlyIn(Dist.CLIENT)
    public void drawFrame(Minecraft minecraft, int left, int top, boolean hasSkill) {
        minecraft.textureManager.bindTexture(getBackground());
        AbstractSkillTreeGui.draw2DTex(left, top, 72, 140 + (hasSkill ? 0 : 26), 26, 26, texWidth, texHeight);
    }

    public SkillFrameType setBackground(ResourceLocation background) {
        this.background = background;
        return this;
    }

    public ResourceLocation getBackground() {
        if (background == null)
            background = SkillTreeBackground.WINDOW;
        return background;
    }


    public TextFormatting getFormat() {
        return this.format;
    }

}