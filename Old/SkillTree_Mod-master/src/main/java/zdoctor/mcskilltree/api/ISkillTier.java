package zdoctor.mcskilltree.api;

import net.minecraft.item.ItemStack;

public interface ISkillTier {
    ItemStack getItemTier(int tier);
    default int maxTier() {
        return 1;
    }
}
