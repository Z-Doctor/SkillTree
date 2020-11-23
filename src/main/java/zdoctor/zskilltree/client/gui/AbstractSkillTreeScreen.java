package zdoctor.zskilltree.client.gui;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skillpages.SkillPage;

import java.util.List;

public abstract class AbstractSkillTreeScreen extends Screen {
    protected AbstractSkillTreeScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    public List<? extends ITextProperties> getTooltipFromSkillPage(SkillPage page) {
        return page.getTooltip(getMinecraft().player, getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    public List<? extends ITextProperties> getTooltipFromSkill(Skill skill) {
        return skill.getTooltip(getMinecraft().player, getMinecraft().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }
}
