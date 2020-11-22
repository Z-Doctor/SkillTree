package zdoctor.zskilltree.client.gui.old;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractGuiSkillTreeScreen extends Screen {
    protected AbstractGuiSkillTreeScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    public List<ITextComponent> getTooltipFromSkillPage(SkillPage page) {
        return page.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    public void bindTexture(ImageAsset imageAsset) {
        getMinecraft().getTextureManager().bindTexture(imageAsset.texture);
    }

    public void renderAt(MatrixStack matrixStack, int x, int y, float partialTicks, ImageAsset imageAsset) {
        bindTexture(imageAsset);
        this.blit(matrixStack, x, y, imageAsset.uOffset, imageAsset.vOffset, imageAsset.xSize, imageAsset.ySize);
    }

}
