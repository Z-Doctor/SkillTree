package zdoctor.mcskilltree.skills;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import zdoctor.mcskilltree.skilltree.SkillFrameType;
import zdoctor.mcskilltree.skilltree.Vector2;
import zdoctor.mcskilltree.util.text.SkillTranslationTextComponent;

public class SkillDisplayInfo {
    protected final Skill skill;
    protected ITextComponent title;
    protected ITextComponent description;
    protected ItemStack icon;
    protected SkillFrameType frame;
    protected ResourceLocation background;
    protected boolean hidden;
    protected Vector2 position;
    protected Vector2 offset;

    public SkillDisplayInfo(Skill skill) {
        this(skill, 0, 0);
    }

    public SkillDisplayInfo(Skill skill, Item icon) {
        this(skill, 0, 0);
    }

    public SkillDisplayInfo(Skill skill, int x, int y) {
        this.skill = skill;
        // TODO Add SkillTranslation Component that will handle add arguments such as level
        title = new TranslationTextComponent(skill.getUnlocalizedName() + ".title");
        description = new TranslationTextComponent(skill.getUnlocalizedName() + ".desc");

        position = new Vector2(x, y);
        offset = new Vector2();
        setIcon(Items.DIAMOND.getDefaultInstance());
    }

    public SkillFrameType getFrame() {
        if (frame == null)
            frame = SkillFrameType.NORMAL;
        return frame;
    }

    public SkillDisplayInfo setFrame(SkillFrameType frame) {
        this.frame = frame;
        return this;
    }

    public boolean isHidden() {
        return hidden;
    }

    public SkillDisplayInfo setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public ITextComponent getTitle() {
        return title;
    }

    public ITextComponent getDescription() {
        return description;
    }

    public SkillDisplayInfo setTitle(ITextComponent title) {
        this.title = title;
        return this;
    }

    public SkillDisplayInfo setDescription(ITextComponent description) {
        this.description = description;
        return this;
    }

    public SkillDisplayInfo setTitle(String title) {
        this.title = new StringTextComponent(title);
        return this;
    }

    public SkillDisplayInfo setDescription(String description) {
        this.description = new StringTextComponent(description);
        return this;
    }

    public SkillDisplayInfo setPosition(int x, int y) {
        return setPosition(new Vector2(x, y));
    }

    public SkillDisplayInfo setPosition(Vector2 position) {
        this.position = position;
        return this;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getOffset() {
        return offset;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public SkillDisplayInfo setIcon(ItemStack icon) {
        this.icon = icon;
        return this;
    }

    public int getX() {
        return getPosition().getX();
    }

    public int getY() {
        return getPosition().getY();
    }

    public void setOffset(int x, int y) {
        this.offset.setX(x);
        this.offset.setY(y);
    }

    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }
}
