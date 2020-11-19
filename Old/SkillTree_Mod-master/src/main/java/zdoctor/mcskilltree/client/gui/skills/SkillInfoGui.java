package zdoctor.mcskilltree.client.gui.skills;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.ClientSkillApi;
import zdoctor.mcskilltree.api.ISkillInfoGui;
import zdoctor.mcskilltree.api.SkillApi;
import zdoctor.mcskilltree.client.gui.skilltree.AbstractSkillTreeGui;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skilltree.SkillTreeBackground;
import zdoctor.mcskilltree.util.text.SkillTranslationTextComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

@OnlyIn(Dist.CLIENT)
public class SkillInfoGui extends Screen implements ISkillInfoGui {
    public static final ResourceLocation WINDOW = new ResourceLocation(
            McSkillTree.MODID, "textures/gui/skilltree/skill_info.png");

    protected final SkillEntryGui entryGui;
    protected final Skill skill;
    protected SkillTreeBackground background;

    protected Integer x;
    protected Integer y;
    protected int xOffset = -6;
    protected int yOffset = -13;

    protected final List<BiFunction<Double, Double, Boolean>> hitBoxes = new ArrayList<>();

    protected SkillTranslationTextComponent unowned;
    protected SkillTranslationTextComponent owned;

    protected SkillTranslationTextComponent defaultUnowned;
    protected SkillTranslationTextComponent defaultOwned;

    public SkillInfoGui(SkillEntryGui entryGui) {
        super(entryGui.getDisplayInfo().getTitle());
        this.minecraft = Minecraft.getInstance();
        this.entryGui = entryGui;
        this.skill = entryGui.getSkill();
        this.width = 131;
        this.height = 83;

        this.unowned = new SkillTranslationTextComponent(skill.getUnlocalizedName() + ".entry.unowned",
                ClientSkillApi.getPlayerHandler(), skill).withDefault("default.entry.unowned");
        this.owned = new SkillTranslationTextComponent(skill.getUnlocalizedName() + ".entry.owned",
                ClientSkillApi.getPlayerHandler(), skill).withDefault("default.entry.owned");

        x = getSkillEntry().getX() + MathHelper.floor(getSkillEntry().getTab().getScrollX());
        y = getSkillEntry().getY() + MathHelper.floor(getSkillEntry().getTab().getScrollY());
        // TODO Fix top left corner pixels
        // TODO Add checks to see if we would render off screen and if so render alternatives
        // TODO Add more buttons based on context (buy, activate, deactivate, upgrade, etc) or make button
        //  text change based on context
        // TODO Show preview of next skill based on upgrade and/or render tooltip that describes next tier
        // TODO Make better way to tell if buy button should be added
        // TODO Make cost appear when you can buy
        // TODO Redesign info screen
        this.background = SkillTreeBackground.DEFAULT.with(0, 7, 0, 4);
        if (!ClientSkillApi.hasSkill(entryGui.getSkill()) || getSkillEntry().getSkill().canBuyMultiple())
            addButton(new Button(x + 29, y - 18, 50, 20, "Buy", button -> {
                if (SkillApi.buySkill(minecraft.player, getSkillEntry().getSkill())) {
                    if (!getSkillEntry().getSkill().canBuyMultiple()) {
                        children.remove(button);
                        buttons.remove(button);
                    }

                    McSkillTree.LOGGER.debug("Player bought skill: {}", entryGui.getSkill());
                }
            }));

        hitBoxes.add((mouseX, mouseY) -> mouseX >= x + 15 && mouseX < x + 15 + 131 && mouseY <= y + 15 && mouseY > y - 83 + 15);
        // TODO Make sure this hitbox is right
        hitBoxes.add((mouseX, mouseY) -> mouseX >= x + 10 && mouseX < x + 10 + 23 && mouseY <= y + 15 - 83 - 5 + 23 && mouseY > y + 15 - 83 - 5);
    }

    @Override
    public boolean withinBounds(double mouseX, double mouseY) {
        return hitBoxes.stream().anyMatch(function -> function.apply(mouseX, mouseY));
    }


    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.enableDepthTest();
//        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
//        RenderSystem.colorMask(false, false, false, false);
//        fill(4680, 2260, -4680, -2260, -16777216);
//        RenderSystem.colorMask(true, true, true, true);
//        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
//        RenderSystem.depthFunc(518);
//        fill(234, 113, 0, 0, -16777216);
//        RenderSystem.depthFunc(515);


//        RenderSystem.disableCull();
        RenderSystem.translatef(x + 15, y + 16 - height, -300);
        this.renderInside(mouseX, mouseY, partialTicks);
        this.renderInfo(mouseX, mouseY, partialTicks);
        this.renderWindow(mouseX, mouseY, partialTicks);
        this.renderMisc(mouseX, mouseY);

//        RenderSystem.depthFunc(518);
//        RenderSystem.translatef(0.0F, 0.0F, -950.0F);
//        RenderSystem.colorMask(false, false, false, false);
//        fill(4680, 2260, -4680, -2260, -16777216);
//        RenderSystem.colorMask(true, true, true, true);
//        RenderSystem.translatef(0.0F, 0.0F, 950.0F);
//        RenderSystem.depthFunc(515);
        RenderSystem.popMatrix();

        super.render(mouseX, mouseY, partialTicks);
    }

    protected void renderInside(int mouseX, int mouseY, float partialTicks) {
        // TODO Fix or change how this is rendered to get rid of stray pixels
        background.renderAt(minecraft, 0, 0);

    }

    int tempX = 15;
    int tempY = 16;

    protected void renderInfo(int mouseX, int mouseY, float partialTicks) {
        int offset = minecraft.fontRenderer.FONT_HEIGHT + 2;
        int count = -1;
        // TODO Add more info (circle with question mark) that renders tooltip when hovered
        // TODO Localize render info
        // TODO Adjust font color
        SkillTranslationTextComponent translate = ClientSkillApi.hasSkill(skill) ? owned : unowned;

        for (String s : translate.getFormattedText().split("\n")) {
            minecraft.fontRenderer.drawString(s, tempX, tempY + offset * (count += 1), 0);
        }
    }

    protected void renderWindow(int mouseX, int mouseY, float partialTicks) {
        minecraft.getTextureManager().bindTexture(WINDOW);
        RenderSystem.enableBlend();
        // 11 69
        this.blit(0, 0, 0, 0, width, height);
        // 0, 194; 103, 62
        minecraft.getTextureManager().bindTexture(SkillEntryGui.WIDGETS);
        int barWidth = getSkillEntry().width - getSkillEntry().width / 2;
        int type = ClientSkillApi.hasSkill(getSkillEntry().getSkill()) ? 0 : 1;
        AbstractSkillTreeGui.draw2DTex(xOffset - 3, yOffset, 0, type * 26, barWidth, 26);
        AbstractSkillTreeGui.draw2DTex(xOffset - 3 + barWidth, yOffset, 200 - barWidth, type * 26, barWidth, 26);
        getSkillEntry().drawAt(xOffset, yOffset, type == 0, false);
        minecraft.fontRenderer.drawStringWithShadow(title.getFormattedText(), 23, yOffset + 9, -1);

    }

    protected void renderMisc(int mouseX, int mouseY) {
    }

    @Override
    public SkillEntryGui getSkillEntry() {
        return entryGui;
    }

    @Override
    public void onClose() {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }
}
