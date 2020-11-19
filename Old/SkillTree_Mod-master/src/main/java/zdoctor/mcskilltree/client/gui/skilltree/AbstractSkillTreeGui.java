package zdoctor.mcskilltree.client.gui.skilltree;

import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FocusableGui;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.client.gui.skills.SkillEntryGui;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractSkillTreeGui extends FocusableGui {
    public Minecraft minecraft = Minecraft.getInstance();
    protected final List<SkillEntryGui> children = Lists.newArrayList();

    @Override
    public List<? extends SkillEntryGui> children() {
        return children;
    }

    public static void draw2DWithOffset(int drawX, int drawY, int offset, float u, float v, int width, int height, int texWidth, int texHeight) {
        blit(drawX, drawY, offset, u, v, width, height, texWidth, texHeight);
    }

    public static void draw2DWithOffset(int drawX, int drawY, int offset, float u, float v, int width, int height) {
        blit(drawX, drawY, offset, u, v, width, height, 256, 256);
    }

    public static void draw2DTex(int drawX, int drawY, float u, float v, int width, int height) {
        blit(drawX, drawY, u, v, width, height, 256, 256);
    }

    public static void draw2DTex(int drawX, int drawY, float u, float v, int width, int height, int textWidth, int texHeight) {
        blit(drawX, drawY, u, v, width, height, textWidth, texHeight);
    }

    public static void drawEntityOnScreen(int guiLeft, int guiTop, int scale, float mouseX, float mouseY, LivingEntity entity) {
        InventoryScreen.drawEntityOnScreen(guiLeft, guiTop, scale, mouseX, mouseY, entity);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // To prevent recursion when focused is itself and a key is released
        return this.getFocused() != this && super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // To prevent recursion when focused is itself and a key is pressed
        return this.getFocused() != this && super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charTyped, int modifiers) {
        // To prevent recursion when focused is itself and a char is typed
        return this.getFocused() != this && super.charTyped(charTyped, modifiers);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double scaledX, double scaledY) {
        // To prevent recursion when focused is itself and a mouse is dragged
        return this.getFocused() != this && super.mouseDragged(mouseX, mouseY, button, scaledX, scaledY);
    }

}
