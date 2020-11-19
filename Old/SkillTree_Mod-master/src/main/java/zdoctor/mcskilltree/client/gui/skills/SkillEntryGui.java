package zdoctor.mcskilltree.client.gui.skills;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.advancements.AdvancementState;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ISelectiveResourceReloadListener;
import net.minecraftforge.resource.VanillaResourceType;
import zdoctor.mcskilltree.api.*;
import zdoctor.mcskilltree.client.gui.skilltree.AbstractSkillTreeGui;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillDisplayInfo;
import zdoctor.mcskilltree.skilltree.SkillTreeBackground;
import zdoctor.mcskilltree.skilltree.Vector2;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@OnlyIn(Dist.CLIENT)
public class SkillEntryGui extends AbstractSkillTreeGui implements ISelectiveResourceReloadListener {
    public static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/advancements/widgets.png");
    public static final Pattern SENTENCE_PATTERN = Pattern.compile("(.+) \\S+");

    private final ISkillTreeTabGui tab;
    private final Skill skill;
    protected SkillDisplayInfo displayInfo;

    protected String title;
    protected int width;
    protected List<String> description;

    protected ISkillHandler playerHandler;

    private Integer x;
    private Integer y;

    protected Set<SkillEntryGui> parents;

    public SkillEntryGui(ISkillTreeTabGui tab, Skill skill) {
        this.tab = tab;
        this.skill = skill;
        this.displayInfo = skill.getDisplayInfo();
        playerHandler = ClientSkillApi.getPlayerHandler();

        localize();
        ((IReloadableResourceManager) minecraft.getResourceManager()).addReloadListener(this);
    }

    public void localize() {
        this.title = minecraft.fontRenderer.trimStringToWidth(displayInfo.getTitle().getFormattedText(), 163);
        this.width = calculateWidth() + 3 + 5;
    }

    public ISkillTreeTabGui getTab() {
        return tab;
    }

    @Nullable
    @Override
    public SkillEntryGui getFocused() {
        return (SkillEntryGui) super.getFocused();
    }

    public boolean addParent(SkillEntryGui parent) {
        if (parents == null)
            parents = new HashSet<>();
        return parents.add(parent);
    }

    public void clearCache() {
        x = y = null;
    }

    protected int calculateWidth() {
        int i = skill.getRequirementCount();
        int j = String.valueOf(i).length();
        int k = i > 1 ? minecraft.fontRenderer.getStringWidth("  ") + minecraft.fontRenderer.getStringWidth("0") * j * 2 + minecraft.fontRenderer.getStringWidth("/") : 0;
        int width = 29 + minecraft.fontRenderer.getStringWidth(this.title) + k;

        String s = displayInfo.getDescription().getFormattedText();
        this.description = findOptimalLines(s, width);

        for (String s1 : this.description) {
            width = Math.max(width, minecraft.fontRenderer.getStringWidth(s1));
        }

        return width;
    }

    protected List<String> findOptimalLines(String description, int width) {
        if (description.isEmpty()) {
            return Collections.emptyList();
        } else {
            List<String> list = minecraft.fontRenderer.listFormattedStringToWidth(description, width);
            if (list.size() < 2) {
                return list;
            } else {
                String s = list.get(0);
                String s1 = list.get(1);
                int i = minecraft.fontRenderer.getStringWidth(s + ' ' + s1.split(" ")[0]);
                if (i - width <= 10) {
                    return minecraft.fontRenderer.listFormattedStringToWidth(description, i);
                } else {
                    Matcher matcher = SENTENCE_PATTERN.matcher(s);
                    if (matcher.matches()) {
                        int j = minecraft.fontRenderer.getStringWidth(matcher.group(1));
                        if (width - j <= 10) {
                            return minecraft.fontRenderer.listFormattedStringToWidth(description, j);
                        }
                    }

                    return list;
                }
            }
        }
    }

    public SkillDisplayInfo getDisplayInfo() {
        return displayInfo;
    }

    public Skill getSkill() {
        return skill;
    }

    public Skill getFocusedSkill() {
        return getFocused() == null ? null : getFocused().getSkill();
    }

    public void draw(int left, int top, boolean hasSkill) {
        // TODO Create TierSkillEntryGui to display numbers over tiered skills
        //  and automate them for skills that implement @ISkillTier
        if (getSkill().isHidden())
            return;
        int x = getX() + 3 + left;
        int y = getY() + 3 + top;
        preDraw(left, top, x, y, hasSkill);
        getDisplayInfo().getFrame().drawFrame(minecraft, x, y, hasSkill);
        minecraft.getItemRenderer().renderItemAndEffectIntoGUI(null, skill.getIcon(), x + 5, y + 5);
        postDraw(left, top, x, y, hasSkill);
    }

    public void drawAt(int left, int top, boolean hasSkill, boolean withLock) {
        getDisplayInfo().getFrame().drawFrame(minecraft, left, top, hasSkill);
        minecraft.getItemRenderer().renderItemAndEffectIntoGUI(null, skill.getIcon(), left + 5, top + 5);

        if(withLock)
            drawLock(left, top);
    }

    protected void preDraw(int left, int top, int x, int y, boolean hasSkill) {

    }

    protected void postDraw(int left, int top, int x, int y, boolean hasSkill) {
        if (!hasSkill)
            drawLock(x, y);
    }

    public void drawLock(int left, int top) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(0, 0, 400);
        fill(left, top, left + 26, top + 26, 0xCC505050);
        minecraft.getTextureManager().bindTexture(SkillTreeBackground.WINDOW);
        draw2DTex(left + 4, top + 4, ClientSkillApi.hasRequirements(getSkill()) ? 18 : 0, 176,
                18, 18);
        RenderSystem.popMatrix();
    }

    public void drawChildren(int left, int top) {
        for (SkillEntryGui child : children()) {
            child.draw(left, top, SkillApi.hasSkill(minecraft.player, child.getSkill()));
        }
    }

    public void drawConnectivity(int left, int top, boolean outerLine) {
        // TODO Improve/Import my own version
        if (parents != null) {
            for (SkillEntryGui parent : parents) {
                if (parent.getSkill().isHidden())
                    continue;

                int i = left + parent.getX() + 13;
                int j = left + parent.getX() + 26 + 4;
                int k = top + parent.getY() + 13;
                int l = left + getX() + 13;
                int i1 = top + getY() + 13;
                int color = outerLine ? -16777216 : -1;

                if (outerLine) {
                    this.hLine(j, i, k - 1, color);
                    this.hLine(j + 1, i, k, color);
                    this.hLine(j, i, k + 1, color);
                    this.hLine(l, j - 1, i1 - 1, color);
                    this.hLine(l, j - 1, i1, color);
                    this.hLine(l, j - 1, i1 + 1, color);
                    this.vLine(j - 1, i1, k, color);
                    this.vLine(j + 1, i1, k, color);
                } else {
                    this.hLine(j, i, k, color);
                    this.hLine(l, j, i1, color);
                    this.vLine(j, i1, k, color);
                }
            }
        }


        for (SkillEntryGui child : children()) {
            child.drawConnectivity(left + 3, top + 3, outerLine);
        }
    }

    public Vector2 getPosition() {
        return displayInfo.getPosition();
    }

    public Vector2 getOffset() {
        return displayInfo.getOffset();
    }

    public int getX() {
        if (x == null)
            x = getPosition().getX() * 28 + getOffset().getX();
        return x;
    }

    public int getY() {
        if (y == null)
            y = getPosition().getY() * 27 + getOffset().getY();
        return y;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        if (mouseOver(mouseX, mouseY)) {
            setFocused(this);
            return true;
        }
        for (SkillEntryGui child : children()) {
            if (child.mouseOver(mouseX, mouseY)) {
                setFocused(child);
                return true;
            }
        }
        setFocused(null);
        return false;
    }

    public boolean mouseOver(double mouseX, double mouseY) {
        if (getSkill().isHidden())
            return false;

        int left = getX();
        int right = left + 26;
        int top = getY();
        int down = top + 26;
        return mouseX >= left && mouseX <= right && mouseY >= top && mouseY <= down;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0)
            return false;

            // TODO Instead of buying, instead open up new window that displays information about
            //  the skill, description and cost
        return isMouseOver(mouseX, mouseY);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof SkillEntryGui) return ((SkillEntryGui) o).getSkill() == getSkill();
        if (o instanceof Vector2) return o == getPosition();
        if (o instanceof Skill) return (o == getSkill());
        return false;
    }

    @Override
    public int hashCode() {
        return getSkill().hashCode();
    }

    public void drawHovered(int scrollX, int scrollY, float fade, int mouseX, int mouseY) {
        if (getFocused() != null)
            getFocused().drawHover(scrollX, scrollY, fade, mouseX, mouseY);
    }

    public void drawHover(int scrollX, int scrollY, float fade, int mouseX, int mouseY) {
        boolean leftOverflow = mouseX + scrollX + getX() + this.width + 26 >= Objects.requireNonNull(minecraft.currentScreen).width;
//        String s = skill == null ? null : this.advancementProgress.getProgressText();
        String s = null; //"Almost there";
        int progressWidth = s == null ? 0 : minecraft.fontRenderer.getStringWidth(s);

        boolean bottomOverflow = 113 - scrollY - getY() - 26 <= 6 + description.size() * 9;

        // TODO Add support for passive acquisitions of skills
        // float passiveSkillProgress = this.advancementProgress == null ? 0.0F : this.advancementProgress.getPercent();
        // TODO Make Skill progress, probably replace ISkillRequirement
        float passiveSkillProgress = 0f;
        int skillProgress = MathHelper.floor(passiveSkillProgress * (float) width);

        // TODO Maybe make my own way of doing it
        // TODO Make sure Localized text is supported
        AdvancementState advancementstate;
        AdvancementState advancementstate1;
        advancementstate = advancementstate1 = SkillApi.hasSkill(minecraft.player, skill) ? AdvancementState.OBTAINED : AdvancementState.UNOBTAINED;

        if (passiveSkillProgress >= 1.0f)
            skillProgress = this.width / 2;
        else if (skillProgress < 2)
            skillProgress = this.width / 2;

        int barWidth = width - skillProgress;
        minecraft.getTextureManager().bindTexture(WIDGETS);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();
        // TODO Look into this
        int l = scrollY + getY() + 3;
        int i1;
        if (leftOverflow) {
            i1 = scrollX + getX() - width + 26 + 6;
        } else {
            i1 = scrollX + getX();
        }


        int j1 = 32 + description.size() * 9;
        if (!description.isEmpty()) {
            if (bottomOverflow) {
                this.render9Sprite(i1, l + 26 - j1, this.width, j1, 10, 200, 26, 0, 52);
            } else {
                this.render9Sprite(i1, l, this.width, j1, 10, 200, 26, 0, 52);
            }
        }

        draw2DTex(i1, l, 0, advancementstate.getId() * 26, skillProgress, 26);
        draw2DTex(i1 + skillProgress, l, 200 - barWidth, advancementstate1.getId() * 26, barWidth, 26);

        this.draw(scrollX, scrollY, SkillApi.hasSkill(minecraft.player, getSkill()));

        if (leftOverflow) {
            minecraft.fontRenderer.drawStringWithShadow(this.title, (float) (i1 + 5), (float) (scrollY + getY() + 9), -1);
            if (s != null) {
                minecraft.fontRenderer.drawStringWithShadow(s, (float) (scrollX + getX() - progressWidth), (float) (scrollY + getY() + 9), -1);
            }
        } else {
            // TODO Figure out why 12 instead of 9 (like in advancements) is needed. Where is the 3 difference?
            minecraft.fontRenderer.drawStringWithShadow(title, (float) (scrollX + getX() + 32), (float) (scrollY + getY() + 12), -1);
            if (s != null) {
                minecraft.fontRenderer.drawStringWithShadow(s, (float) (scrollX + getX() + this.width - progressWidth - 5), (float) (scrollY + getY() + 9), -1);
            }
        }

        if (bottomOverflow) {
            for (int k1 = 0; k1 < description.size(); ++k1) {
                minecraft.fontRenderer.drawString(this.description.get(k1), (float) (i1 + 5), (float) (l + 26 - j1 + 7 + k1 * 9), -5592406);
            }
        } else {
            for (int l1 = 0; l1 < description.size(); ++l1) {
                // TODO Figure out why 12 instead of 9 (like in advancements) is needed. Where is the 3 difference?
                minecraft.fontRenderer.drawString(this.description.get(l1), (float) (i1 + 5), (float) (scrollY + getY() + 12 + 17 + l1 * 9), -5592406);
            }
        }


    }

    protected void render9Sprite(int left, int top, int horizontalWidth, int height, int size, int uOffset, int vOffset, int u, int v) {
        // Renders top left corner
        AbstractSkillTreeGui.draw2DWithOffset(left, top, 0, u, v, size, size);
        this.renderRepeating(left + size, top, horizontalWidth - size - size, size, u + size, v, uOffset - size - size, vOffset);
        //Renders top right corner
        AbstractSkillTreeGui.draw2DTex(left + horizontalWidth - size, top, u + uOffset - size, v, size, size);
        // Renders bottom left corner
        AbstractSkillTreeGui.draw2DTex(left, top + height - size, u, v + vOffset - size, size, size);
        this.renderRepeating(left + size, top + height - size, horizontalWidth - size - size, size, u + size, v + vOffset - size, uOffset - size - size, vOffset);
        // Renders bottom right corner
        AbstractSkillTreeGui.draw2DTex(left + horizontalWidth - size, top + height - size, u + uOffset - size, v + vOffset - size, size, size);
        this.renderRepeating(left, top + size, size, height - size - size, u, v + size, uOffset, vOffset - size - size);
        this.renderRepeating(left + size, top + size, horizontalWidth - size - size, height - size - size, u + size, v + size, uOffset - size - size, vOffset - size - size);
        this.renderRepeating(left + horizontalWidth - size, top + size, size, height - size - size, u + uOffset - size, v + size, uOffset, vOffset - size - size);
    }

    protected void renderRepeating(int p_192993_1_, int p_192993_2_, int p_192993_3_, int p_192993_4_, int p_192993_5_, int p_192993_6_, int p_192993_7_, int p_192993_8_) {
        for (int i = 0; i < p_192993_3_; i += p_192993_7_) {
            int j = p_192993_1_ + i;
            int k = Math.min(p_192993_7_, p_192993_3_ - i);

            for (int l = 0; l < p_192993_4_; l += p_192993_8_) {
                int i1 = p_192993_2_ + l;
                int j1 = Math.min(p_192993_8_, p_192993_4_ - l);
                AbstractSkillTreeGui.draw2DTex(j, i1, p_192993_5_, p_192993_6_, k, j1);
            }
        }

    }

    public void findChildren() {
        for (Skill child : skill.getChildren()) {
            SkillEntryGui entry = new SkillEntryGui(getTab(), child);
            children.add(entry);
            entry.addParent(this);
            entry.findChildren();
        }
    }

    public int getLeftScroll() {
        int scroll = getX();
        for (SkillEntryGui child : children()) {
            scroll = Math.min(scroll, child.getLeftScroll());
        }
        return scroll;
    }

    public int getRightScroll() {
        int scroll = getX();
        for (SkillEntryGui child : children()) {
            scroll = Math.max(scroll, child.getRightScroll());
        }
        return scroll;
    }

    public int getTopScroll() {
        int scroll = getY();
        for (SkillEntryGui child : children()) {
            scroll = Math.min(scroll, child.getTopScroll());
        }
        return scroll;
    }

    public int getBotScroll() {
        int scroll = getY();
        for (SkillEntryGui child : children()) {
            scroll = Math.max(scroll, child.getBotScroll());
        }
        return scroll;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager, Predicate<IResourceType> resourcePredicate) {
        if(resourcePredicate.test(VanillaResourceType.LANGUAGES)) {
            localize();
        }
    }

    public ISkillInfoGui getSkillInfoGui() {
        // Should this be saved to a variable?
        return new SkillInfoGui(this);
    }
}
