package zdoctor.mcskilltree.skills.variants;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.SkillApi;
import zdoctor.mcskilltree.registries.SkillTreeRegistries;
import zdoctor.mcskilltree.skills.Skill;
import zdoctor.mcskilltree.skills.SkillDisplayInfo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftSkill extends Skill {
    protected static boolean initialized;
    protected static List<CraftSkill> craftList;

    protected ItemStack craftable;

    public CraftSkill(String name, Item icon) {
        this(name, icon.getDefaultInstance());
    }

    public CraftSkill(String name, ItemStack icon) {
        super(name, icon);
        craftable = icon;
    }

    public CraftSkill(String name, ItemStack craftable, SkillDisplayInfo displayInfo) {
        super(name, displayInfo);
        this.craftable = craftable;
    }

    @Override
    public CraftSkill position(int x, int y) {
        return (CraftSkill) super.position(x, y);
    }

    public CraftSkill setCraftable(ItemStack craftable) {
        this.craftable = craftable;
        return this;
    }

    public ItemStack getCraftable(LivingEntity crafter) {
        return craftable;
    }

    public boolean isMatch(LivingEntity crafter, IRecipe<?> recipe) {
        return getCraftable(crafter).equals(recipe.getRecipeOutput(), false);
    }

    public boolean canCraft(LivingEntity entity, IRecipe<?> result) {
        return SkillApi.hasSkill(entity, this);
    }

    public static boolean canCraftRecipe(LivingEntity entity, IRecipe<?> result) {
        if (craftList.size() <= 0)
            return true;
        CraftSkill[] matches = craftList.stream().filter(skill -> skill.isMatch(entity, result)).toArray(CraftSkill[]::new);

        if(matches.length <= 0)
            return true;
        else if(matches.length > 1) {
            for (CraftSkill skill : matches) {
                // TODO Add event to handle: Pre and the for each one
                if(skill.canCraft(entity, result))
                    return true;
            }
            return false;
        }

        else
            // It's possible to just return true based on the default implementation of isMatch
            // But if someone did a custom version then this is needed
            return matches[0].canCraft(entity, result);
    }



    public static void init() {
        if (initialized)
            return;
        initialized = true;
        Stream<CraftSkill> craftSkills = SkillTreeRegistries.SKILLS.getValues().stream().filter(skill -> skill instanceof CraftSkill).
                map(skill -> (CraftSkill) skill);
        craftList = craftSkills.collect(Collectors.toList());
        McSkillTree.LOGGER.debug("{} Craft Skills found", craftList.size());
    }

}
