package zdoctor.zskilltree.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.zskilltree.skillpages.SkillPage;

import java.util.List;

public abstract class AbstractGuiSkillTreeScreen extends Screen {
    protected AbstractGuiSkillTreeScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    @OnlyIn(Dist.CLIENT)
    public List<ITextComponent> getTooltipFromSkillPage(SkillPage page) {
        return page.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }
}
