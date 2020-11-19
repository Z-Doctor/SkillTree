package zdoctor.mcskilltree.skilltree;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.WorkbenchContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.play.server.SSetSlotPacket;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import zdoctor.mcskilltree.events.SkillWorkBenchEvent;
import zdoctor.mcskilltree.skills.variants.CraftSkill;

import java.util.Optional;

public class SkillWorkbenchContainer extends WorkbenchContainer {
    protected final CraftingInventory craftMatrix;
    protected final CraftResultInventory craftResult;
    protected final IWorldPosCallable worldPos;
    protected final PlayerEntity player;

    public SkillWorkbenchContainer(int id, PlayerInventory inventory) {
        this(id, inventory, IWorldPosCallable.DUMMY);
    }

    public SkillWorkbenchContainer(int id, PlayerInventory inventory, IWorldPosCallable worldPos) {
        super(id, inventory, worldPos);
        this.worldPos = worldPos;
        this.player = inventory.player;
        craftMatrix = ObfuscationReflectionHelper.getPrivateValue(WorkbenchContainer.class, this, "craftMatrix");
        craftResult = ObfuscationReflectionHelper.getPrivateValue(WorkbenchContainer.class, this, "craftResult");
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        Event.Result result = super.canInteractWith(playerIn) ? Event.Result.ALLOW : Event.Result.DENY;
        SkillWorkBenchEvent.CanInteractCheckEvent event = new SkillWorkBenchEvent.CanInteractCheckEvent(result);
        return !MinecraftForge.EVENT_BUS.post(event) && event.getResult() != Event.Result.DENY;
    }

    @Override
    public void onCraftMatrixChanged(IInventory inventoryIn) {
        worldPos.consume((world, blockPos) -> {
            sendUpdate(this.windowId, world, this.player, this.craftMatrix, this.craftResult);
        });
    }

    public static void sendUpdate(int id, World world, PlayerEntity player, CraftingInventory matrix, CraftResultInventory result) {
        if (!world.isRemote) {
            ServerPlayerEntity serverplayerentity = (ServerPlayerEntity) player;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<ICraftingRecipe> optional = world.getServer().getRecipeManager().getRecipe(IRecipeType.CRAFTING, matrix, world);
            if (optional.isPresent()) {
                ICraftingRecipe icraftingrecipe = optional.get();
                if (result.canUseRecipe(world, serverplayerentity, icraftingrecipe) &&
                        CraftSkill.canCraftRecipe(player, result.getRecipeUsed())) {
                    itemstack = icraftingrecipe.getCraftingResult(matrix);
                }
            }

            result.setInventorySlotContents(0, itemstack);
            serverplayerentity.connection.sendPacket(new SSetSlotPacket(id, 0, itemstack));
        }
    }
}
