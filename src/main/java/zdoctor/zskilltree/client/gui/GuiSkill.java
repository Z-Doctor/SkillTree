package zdoctor.zskilltree.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextProperties;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.AnchorPoint;
import zdoctor.zskilltree.api.interfaces.ISkillTreeScreen;
import zdoctor.zskilltree.client.gui.ImageScreen;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.skill.SkillDisplayInfo;

import java.util.List;

public class GuiSkill extends ImageScreen {
    protected static final float xSpacing = 28f;
    protected static final float ySpacing = 27f;

    private final Skill skill;
    private final ISkillTreeScreen skillScreen;
    private final SkillDisplayInfo displayInfo;


    public GuiSkill(Skill skill, ISkillTreeScreen skillScreen) {
        super(skill.getDisplayInfo().getSkillName());
        this.skill = skill;
        this.skillScreen = skillScreen;
        this.displayInfo = skill.getDisplayInfo();
//        setAnchorPoint(AnchorPoint.ABSOLUTE);
//        setPivotPoint(AnchorPoint.ABSOLUTE);
//        setOffsets((int)xSpacing * xCount, displayInfo.getY());
        // TODO Setup child skills
        // TODO Get skill progress, prob through method from listener
    }

    public Skill getSkill() {
        return skill;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        // TODO Check if obtained or not
        int x = getTrueOffsetX();
        int y = getTrueOffsetY();
        renderAt(matrixStack, x, y, partialTicks);

        RenderSystem.color3f(1F, 1F, 1F); //Forge: Reset color in case Items change it.
        RenderSystem.enableBlend(); //Forge: Make sure blend is enabled else tabs show a white border.
        this.itemRenderer.zLevel = 50f;

        x += 5;
        y += 5;
        RenderSystem.enableRescaleNormal();
        ItemStack itemstack = displayInfo.getIcon();
        this.itemRenderer.renderItemAndEffectIntoGuiWithoutEntity(itemstack, x, y);
        this.itemRenderer.renderItemOverlays(this.font, itemstack, x, y);
        this.itemRenderer.zLevel = 0.0F;

        if (isMouseOver(mouseX, mouseY)) {
            // TODO Draw hover text
            renderWrappedToolTip(matrixStack, getTooltipFromSkill(getSkill()), mouseX, mouseY, font);
        }
    }



    @Override
    public ImageAsset getImage() {
        return ImageAssets.COMMON_FRAME_UNOWNED;
    }
}
