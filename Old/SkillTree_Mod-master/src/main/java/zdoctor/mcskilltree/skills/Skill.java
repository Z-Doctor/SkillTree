package zdoctor.mcskilltree.skills;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.*;
import zdoctor.mcskilltree.registries.SkillTreeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/**
 * Basic skills will act as yes/no where modders can test if a player has a skill
 */
public class Skill extends ForgeRegistryEntry<Skill> implements ISkill {
    @ObjectHolder(McSkillTree.MODID + ":none")
    public static final Skill NONE = null;
    public static final ISkillGetter<Integer> COST_GETTER = Skill::getCost;
    public static final ISkillGetter<Integer> TIER_GETTER = (skill, handler) -> handler.getTier(skill);
//    public static final ISkillProperty<Boolean> ACTIVE = SkillProperties.withDefault("active", false);
//    public static final ISkillProperty<Enum<Type>> TYPE = SkillProperties.withDefault("type", Type.PASSIVE).withOverride(Skill::getType);



    private String translationKey;

    protected SkillDisplayInfo displayInfo;
    protected ITextComponent displayText;

    protected Set<Skill> parents;
    protected Set<Skill> children;
    protected Set<IRequirement> requirements;

    private final Map<ResourceLocation, ISkillGetter<?>> properties = Maps.newHashMap();

    protected int cost = 1;
    protected Type type = Type.PASSIVE;
    protected Predicate<LivingEntity> visibleCondition = Predicates.alwaysTrue();

    // TODO Change positions to be doubles, create builder to make display info and remove x, y from constructor
    public Skill(String name, Item icon) {
        this(name, icon.getDefaultInstance());
    }

    public Skill(String name, ItemStack icon) {
        setRegistryName(name);
        this.displayText = new StringTextComponent(Objects.requireNonNull(getRegistryName()).toString());
        // TODO Add way to automate placing
        displayInfo = new SkillDisplayInfo(this).setIcon(icon);
        initSkill();

    }

    public Skill(String name, SkillDisplayInfo displayInfo) {
        setRegistryName(name);
        this.displayText = new StringTextComponent(Objects.requireNonNull(getRegistryName()).toString());
        this.displayInfo = displayInfo;
        initSkill();
    }

    protected void initSkill() {
        SkillTreeRegistries.SKILLS.register(this);
        addPropertyOverride(new ResourceLocation("cost"), COST_GETTER);
        addPropertyOverride(new ResourceLocation("tier"), TIER_GETTER);
    }

    public final void addPropertyOverride(ResourceLocation key, ISkillGetter<?> getter) {
        this.properties.put(key, getter);
    }

    public Map<ResourceLocation, ISkillGetter<?>> getProperties() {
        return ImmutableMap.copyOf(properties);
    }

    @Nullable
    public ISkillGetter<?> getProperty(ResourceLocation key) {
        return this.properties.get(key);
    }

    public Skill position(int x, int y) {
        displayInfo.setPosition(x, y);
        return this;
    }

    /**
     * Used to get the icon for the local player. Should not be used for anything else
     *
     * @return The icon for the local player
     */
    @OnlyIn(Dist.CLIENT)
    public ItemStack getIcon() {
        return displayInfo.getIcon();
    }

    /**
     * Used to determine if the icon for the local player should be rendered in the gui page
     * and ignore mouse input. Will not effect children
     *
     * @return - True if rendering should be skipped and mouse input ignored
     */
    @OnlyIn(Dist.CLIENT)
    public boolean isHidden() {
        return !visibleCondition.test(ClientSkillApi.getPlayer());
    }

    public Skill setVisibilityCondition(Predicate<LivingEntity> condition) {
        this.visibleCondition = condition;
        return this;
    }

    public Skill setType(Type type) {
        this.type = type;
        return this;
    }

    public Type getType() {
        return type;
    }

    public String getUnlocalizedName() {
        if (translationKey == null)
            translationKey = Util.makeTranslationKey("skill", getRegistryName());
        return translationKey;
    }

    public SkillDisplayInfo getDisplayInfo() {
        return displayInfo;
    }

    public boolean addRequirement(IRequirement requirement) {
        if (requirement == null)
            return false;
        if (requirements == null)
            requirements = Sets.newHashSet();
        return requirements.add(requirement);
    }

    public int getRequirementCount() {
        return requirements == null ? 0 : requirements.size();
    }

    public ITextComponent getDisplayText() {
        return displayText;
    }

    @Override
    public String toString() {
        return getDisplayText().getString();
    }

    public void addChildren(Skill... skills) {
        for (Skill skill : skills) {
            if (skill != null && skill != this) {
                addChild(skill);
            }
        }
    }

    public void addChild(@Nonnull Skill child) {
        if (child == this)
            throw new IllegalArgumentException("Tried to add self as child");
        if (this.children == null)
            this.children = Sets.newHashSet();
        if (children.add(child)) {
            // TODO Add support for multiple parents and drawing connections
            child.addParent(this);
        }
    }

    public List<Skill> getChildren() {
        return new ArrayList<>(hasChildren() ? children : Sets.newHashSet());
    }

    public List<Skill> getAllChildren() {
        return getAllChildren(new ArrayList<>());
    }

    protected List<Skill> getAllChildren(List<Skill> children) {
        if (!hasChildren())
            return children;
        for (Skill child : this.children) {
            children.add(child);
            child.getAllChildren(children);
        }
        return children;
    }

    public boolean hasChildren() {
        return children != null;
    }

    public Set<Skill> getParents() {
        return parents;
    }

    public boolean hasParents() {
        return parents != null;
    }

    public void addParent(Skill parent) {
        if (this.parents == null)
            this.parents = new HashSet<>();
        if (this.parents.add(parent))
            parent.addChild(this);
    }

    public boolean canBuy(ISkillHandler handler) {
        return (!handler.hasSkill(this) || canBuyMultiple()) && hasRequirements(handler) && handler.getSkillPoints() >= getCost(handler);
    }



    private boolean hasRequirements(ISkillHandler handler) {
        return getRequirements().stream().allMatch(requirement -> requirement.test(handler));
    }

    public List<IRequirement> getRequirements() {
        List<IRequirement> requirements = new ArrayList<>();
        if (hasParents())
            for (Skill parent : getParents()) {
                requirements.add(IRequirement.asParent(parent));
            }
        if (this.requirements != null)
            requirements.addAll(this.requirements);
        return requirements;
    }

    public boolean canBuyMultiple() {
        return false;
    }

    public Skill setCost(int cost) {
        this.cost = cost;
        return this;
    }

    /**
     * Get's the price based off the handler.
     *
     * @return The cost or -1 if no cost (i.e. can't be bought)
     */
    public int getCost(ISkillHandler handler) {
        if (handler.hasSkill(this) && !canBuyMultiple())
            return -1;
        return cost;
    }

    public Skill offset(int x, int y) {
        getDisplayInfo().setOffset(x, y);
        return this;
    }

}
