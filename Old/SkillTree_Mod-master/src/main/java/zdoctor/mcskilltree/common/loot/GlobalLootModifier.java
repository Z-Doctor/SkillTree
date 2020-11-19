package zdoctor.mcskilltree.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Function;

public class GlobalLootModifier extends GlobalLootModifierSerializer<IGlobalLootModifier> {

    private final IGlobalLootModifier modifier;

    private final Function<ILootCondition[], IGlobalLootModifier> modifierFunction;

    public GlobalLootModifier(LootType type, String name, IGlobalLootModifier modifier) {
        // Add '/' to support folders (workaround)
        setRegistryName(type.toString().toLowerCase()  + "/" + name.toLowerCase());
        this.modifier = modifier;
        this.modifierFunction = null;
    }

    public GlobalLootModifier(LootType type, String name, Function<ILootCondition[], IGlobalLootModifier> modifier) {
        // Add '/' to support folders (workaround)
        setRegistryName(type.toString().toLowerCase()  + "/" + name.toLowerCase());
        this.modifier = null;
        this.modifierFunction = modifier;
    }

    @Override
    public IGlobalLootModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
        if(modifierFunction != null)
            return modifierFunction.apply(ailootcondition);
        else if(modifier != null)
            return new LootModifier(ailootcondition) {
                @Nonnull
                @Override
                protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
                    return modifier.apply(generatedLoot, context);
                }
            };
        return null;
    }


}
