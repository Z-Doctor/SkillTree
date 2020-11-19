package zdoctor.mcskilltree.client.gui.skilltree;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum SkillTreeTabType {
    ABOVE(0, 0, 28, 32, 8),
    BELOW(84, 0, 28, 32, 8),
    LEFT(0, 64, 32, 28, 5),
    RIGHT(96, 64, 32, 28, 5),
    VERTICAL(ABOVE, BELOW),
    HORIZONTAL(LEFT, RIGHT);

    public static final int MAX_TABS = VERTICAL.getMax() + HORIZONTAL.getMax();

    private final int textureX;
    private final int textureY;
    private final int width;
    private final int height;
    private final int max;

    private final boolean combo;
    private SkillTreeTabType type1, type2;

    SkillTreeTabType(SkillTreeTabType type1, SkillTreeTabType type2) {
        this.textureX = this.textureY = this.width = this.height = 0;
        this.max = type1.max + type2.max;
        this.type1 = type1;
        this.type2 = type2;
        combo = true;
    }

    SkillTreeTabType(int textureX, int textureY, int widthIn, int heightIn, int max) {
        this.textureX = textureX;
        this.textureY = textureY;
        this.width = widthIn;
        this.height = heightIn;
        this.max = max;
        this.combo = false;
    }

    public int getTextureX(int index) {
        if (combo) {
            if (index < getMax() / 2)
                return type1.textureX;
            else
                return type2.textureX;
        }
        return textureX;
    }

    public int getTextureY(int index) {
        if (combo) {
            if (index < getMax() / 2)
                return type1.textureY;
            else
                return type2.textureY;
        }
        return textureY;
    }

    public int getWidth(int index) {
        if (combo) {
            if (index % getMax() < getMax() / 2)
                return type1.width;
            else
                return type2.width;
        }
        return width;
    }

    public int getHeight(int index) {
        if (combo) {
            if (index % getMax() < getMax() / 2)
                return type1.height;
            else
                return type2.height;
        }
        return height;
    }


    public int getMax() {
        return this.max;
    }

    @OnlyIn(Dist.CLIENT)
    public void draw(AbstractGui guiIn, int x, int y, boolean isSelected, int index) {
        int i = getTextureX(index);
        if (index > 0) {
            i += getWidth(index);
        }

        if (index == getMax() - 1) {
            i += getWidth(index);
        }

        int j = isSelected ? getTextureY(index) + getHeight(index) : getTextureY(index);
        guiIn.blit(x + this.getX(index), y + this.getY(index), i, j, getWidth(index), getHeight(index));
    }

    @OnlyIn(Dist.CLIENT)
    public void drawIcon(int centerX, int centerY, int index, ItemRenderer renderItemIn, ItemStack stack) {
        int i = centerX + this.getX(index);
        int j = centerY + this.getY(index);
        switch (this) {

            case ABOVE:
                i += 6;
                j += 9;
                break;
            case BELOW:
                i += 6;
                j += 6;
                break;
            case LEFT:
                i += 10;
                j += 5;
                break;
            case RIGHT:
                i += 6;
                j += 5;
                break;
            case VERTICAL:
                if (index < getMax() / 2)
                    j += 9;
                else
                    j += 6;
                i += 6;
                break;
            case HORIZONTAL:
                if (index < getMax() / 2)
                    i += 10;
                else
                    i += 6;
                j += 5;
                break;
        }
        if (stack != null)
            renderItemIn.renderItemAndEffectIntoGUI((LivingEntity) null, stack, i, j);
        else {
            Minecraft.getInstance().getTextureManager().bindTexture(TextureManager.RESOURCE_LOCATION_EMPTY);
            AbstractSkillTreeGui.draw2DTex(i, j, 0, 0, 16, 16, 16, 16);
        }
    }

    public int getX(int index) {
        switch (this) {
            case VERTICAL:
            case ABOVE:
            case BELOW:
                return (getWidth(index) + 4) * (index % getMax());
            case HORIZONTAL:
                if (index < getMax() / 2)
                    return (getWidth(index) + 4) * (index % getMax());
                else
                    return -getWidth(index) + 4 * (index % getMax());
            case LEFT:
                return -getWidth(index) + 4;
            case RIGHT:
                return 248;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public boolean isCombo() {
        return combo;
    }

    public int getY(int index) {
        switch (this) {
            case VERTICAL:
                if (index < getMax() / 2)
                    return -getHeight(index) + 4;
                else
                    return 136;
            case HORIZONTAL:
                return getHeight(index) * index / getMax();
            case ABOVE:
                return -getHeight(index) + 4;
            case BELOW:
                return 136;
            case LEFT:
            case RIGHT:
                return getHeight(index) * index;
            default:
                throw new UnsupportedOperationException("Don't know what this tab type is!" + this);
        }
    }

    public boolean isHovered(int index, double mouseX, double mouseY) {
        int i = this.getX(index);
        int j = this.getY(index);
        return mouseX > (double) i && mouseX < (double) (i + getWidth(index)) && mouseY > (double) j && mouseY < (double) (j + getHeight(index));
    }
}
