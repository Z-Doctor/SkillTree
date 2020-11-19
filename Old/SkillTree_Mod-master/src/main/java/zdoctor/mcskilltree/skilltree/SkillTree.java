package zdoctor.mcskilltree.skilltree;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.antlr.v4.runtime.misc.OrderedHashSet;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.ISkillTreeTabGui;
import zdoctor.mcskilltree.client.gui.skilltree.SkillTreeTabGui;
import zdoctor.mcskilltree.client.gui.skilltree.SkillTreeTabType;
import zdoctor.mcskilltree.skills.Skill;

import java.util.*;


public class SkillTree extends ForgeRegistryEntry<SkillTree> {

    private static final Map<SkillTreeTabType, Integer> nextIndex;
    private static final Map<SkillTreeTabType, Map<Integer, SkillTree>> reservedIndex;

    static {
        nextIndex = new HashMap<>();
        reservedIndex = new HashMap<>();
        for (SkillTreeTabType type : SkillTreeTabType.values()) {
            nextIndex.put(type, 0);
            reservedIndex.put(type, new HashMap<>());
        }
    }

    @ObjectHolder(McSkillTree.MODID + ":" + "player_info")
    public static final SkillTree PLAYER_INFO = null;

    private SkillTreeInfo displayInfo;
    private String translationKey;

    @OnlyIn(Dist.CLIENT)
    protected ISkillTreeTabGui tabGui;

    protected final Set<Skill> skills = new OrderedHashSet<>();

    public static int getNextIndex(SkillTreeTabType type) {
        int index = nextIndex.get(type);
        Map<Integer, SkillTree> lookup = reservedIndex.get(type);
        while (lookup.containsKey(index)) {
            index += 1;
        }
        return index;
    }

    public SkillTree(String name, ItemStack icon) {
        this(-1, SkillTreeTabType.VERTICAL, name, icon);
    }

    public SkillTree(SkillTreeTabType type, String name, ItemStack icon) {
        this(-1, type, name, icon);
    }

    public SkillTree(int index, SkillTreeTabType type, String name, ItemStack icon) {
        setRegistryName(name);
        if (index < 0)
            index = getNextIndex(type);
        else {
            index = getNextIndex(type);
            reservedIndex.get(type).put(index, this);
            McSkillTree.LOGGER.debug("Reserved {} for type {} to index {}", this, type, index);
            // TODO Event to handle conflicts
        }
        McSkillTree.LOGGER.debug("Mapped {} of type {} to index {}", this, type, index);
        setDisplayInfo(new SkillTreeInfo(index, type, icon, getUnlocalizedName(), getDescription()));

    }

    public void reload() {

    }

    public SkillTreeInfo getDisplayInfo() {
        return displayInfo;
    }

    public SkillTree setDisplayInfo(SkillTreeInfo displayInfo) {
        this.displayInfo = displayInfo;
        return this;
    }

    @OnlyIn(Dist.CLIENT)
    public String getTabName() {
        return I18n.format(getUnlocalizedName());
    }

    public String getUnlocalizedName() {
        if (translationKey == null)
            translationKey = Util.makeTranslationKey("tab", getRegistryName());
        return translationKey;
    }

    public SkillTree setUnlocalizedName(String name) {
        translationKey = name;
        return this;
    }

    public String getDescription() {
        return getUnlocalizedName() + ".desc";
    }

    public boolean addSkill(Skill skill) {
        if (!skills.add(skill)) {
            McSkillTree.LOGGER.error("Tried to add duplicate skill: {}", skill);
            return false;
        }
        return true;
    }

    public List<Skill> getSkills() {
        return new ArrayList<>(skills);
    }

    @OnlyIn(Dist.CLIENT)
    public ISkillTreeTabGui getTabGui() {
        if (tabGui == null)
            tabGui = new SkillTreeTabGui(this);
        return tabGui;
    }

    public SkillTree addSkills(Skill... skills) {
        for (Skill skill : skills) {
            if (addSkill(skill) && skill.hasChildren()) {
                addSkills(skill.getAllChildren().toArray(new Skill[0]));
            }
        }
        return this;
    }
}
