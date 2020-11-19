package zdoctor.mcskilltree.skills.variants;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import zdoctor.mcskilltree.McSkillTree;
import zdoctor.mcskilltree.api.ClientSkillApi;
import zdoctor.mcskilltree.api.ISkillHandler;
import zdoctor.mcskilltree.api.ISkillTier;
import zdoctor.mcskilltree.api.SkillApi;

import javax.annotation.Nonnull;
import java.util.*;

public class MultiTierCraftSkill extends CraftSkill implements ISkillTier {

    protected List<Integer> costs;
    protected List<ResourceLocation> tiers;
    protected Map<ResourceLocation, ItemStack> registry;

    public MultiTierCraftSkill(String name, @Nonnull Item... items) {
        this(name, Arrays.stream(items).map(Item::getDefaultInstance).toArray(ItemStack[]::new));
    }

    public MultiTierCraftSkill(String name, @Nonnull ItemStack... items) {
        super(name, items[0]);
        costs = new ArrayList<>();
        tiers = new ArrayList<>();
        registry = new HashMap<>();
        for (ItemStack stack : items) {
            ResourceLocation key = stack.getItem().getRegistryName();
            tiers.add(key);
            costs.add(tiers.size());
            registry.put(key, stack);
        }
    }

    /**
     * Does a batch set of the tier costs.
     *
     * @param costs - The new amount of the tier or -1 if you want to not touch that tier
     * @return - Self for chaining
     */
    public MultiTierCraftSkill setTierCosts(int... costs) {
        for (int i = 0; i < costs.length; i++) {
            int cost = costs[i];
            if (cost < 0)
                continue;
            McSkillTree.LOGGER.debug("Changed tier {} cost from {} to {} in {}",
                    i + 1, this.costs.get(i), cost, this);
            this.costs.set(i, cost);
        }
        return this;
    }

    public MultiTierCraftSkill setTierCost(int tier, int cost) {
        tier -= 1;
        if (tier <= 0) {
            McSkillTree.LOGGER.error("Min tier is 1, given {}", tier + 1);
            return this;
        }
        costs.set(tier, cost);
        McSkillTree.LOGGER.debug("Changed tier {} cost from {} to {} in {}",
                tier + 1, this.costs.get(tier), cost, this);
        return this;
    }

    public boolean addTier(ItemStack item, int cost) {
        ResourceLocation key = item.getItem().getRegistryName();
        if (registry.containsKey(key))
            return false;
        registry.put(key, item);
        tiers.add(key);
        return true;
    }

    public boolean addTier(ItemStack item, int tier, int cost) {
        tier -= 1;
        if (tier <= 0) {
            McSkillTree.LOGGER.error("Min tier is 1, given {}", tier + 1);
            return false;
        }
        ResourceLocation key = item.getItem().getRegistryName();
        if (registry.containsKey(key))
            return false;
        registry.put(key, item);
        tiers.add(tier, key);
        return true;
    }

    @Override
    public boolean isMatch(LivingEntity crafter, IRecipe<?> recipe) {
        ItemStack item = recipe.getRecipeOutput();
        return registry.containsKey(item.getItem().getRegistryName());
    }

    @Override
    public ItemStack getCraftable(LivingEntity crafter) {
        return getItemTier(SkillApi.getTier(crafter, this));
    }

    @Override
    public ItemStack getItemTier(int tier) {
        if (tier <= 1) {
            if (tier > maxTier())
                McSkillTree.LOGGER.error("Tried to access tier {} from {} when {} is maxTier",
                        tier, this, maxTier());
            return registry.get(tiers.get(0));
        } else
            return registry.get(tiers.get(tier - 1));

    }

    @Override
    public boolean canCraft(LivingEntity entity, IRecipe<?> result) {
        int tier = tiers.indexOf(result.getRecipeOutput().getItem().getRegistryName()) + 1;
        if (tier < 0)
            // Not found, we don't mess with it
            return true;

        return super.canCraft(entity, result) && SkillApi.getTier(entity, this) >= tier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public ItemStack getIcon() {
        return getItemTier(ClientSkillApi.getTier(this));
    }

    @Override
    public int maxTier() {
        return tiers.size();
    }

    @Override
    public MultiTierCraftSkill position(int x, int y) {
        return (MultiTierCraftSkill) super.position(x, y);
    }

    @Override
    public boolean canBuy(ISkillHandler handler) {
        return super.canBuy(handler) && handler.getTier(this) < maxTier();
    }

    @Override
    public void onBuy(ISkillHandler handler, boolean firstBuy) {
        if (firstBuy)
            return;
        int tier = Math.min(maxTier(), handler.getTier(this) + 1);
        handler.setTier(this, tier);
    }

    @Override
    public int getCost(ISkillHandler handler) {
        int tier = handler.getTier(this);
        if (tier > maxTier()) {
//            McSkillTree.LOGGER.debug("Tried to get cost of tier {} in {}. Tier does not exist.", tier, this);
            return -1;
        } else if (tier >= costs.size()) {
            return tier + 1;
        }
        return costs.get(tier);
    }

    @Override
    public boolean canBuyMultiple() {
        return true;
    }
}
