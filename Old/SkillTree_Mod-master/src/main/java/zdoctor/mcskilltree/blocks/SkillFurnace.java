package zdoctor.mcskilltree.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FurnaceBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ObjectHolder;
import zdoctor.mcskilltree.events.SkillFurnaceEvent;

public class SkillFurnace extends FurnaceBlock {
    public SkillFurnace() {
        super(Properties.from(Blocks.FURNACE));
        setRegistryName(Blocks.FURNACE.getRegistryName());
    }

    @Override
    protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof AbstractFurnaceTileEntity) {
            player.openContainer((INamedContainerProvider) tileentity);
            player.addStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new SkillFurnaceTileEntity();
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResult) {
        SkillFurnaceEvent.PlayerInteractEvent event = new SkillFurnaceEvent.PlayerInteractEvent(state, pos, player, handIn, rayTraceResult);
        if (MinecraftForge.EVENT_BUS.post(event))
            return ActionResultType.FAIL;
        if (event.getResult() == Event.Result.DENY)
            return ActionResultType.CONSUME;

        if (!worldIn.isRemote) {
            this.interactWith(worldIn, pos, player);
        }
        return ActionResultType.SUCCESS;
    }

    public static class SkillFurnaceTileEntity extends FurnaceTileEntity {
        @ObjectHolder("minecraft:furnace")
        public static final TileEntityType<?> FURNACE = null;

        @Override
        public TileEntityType<?> getType() {
            return FURNACE;
        }

        @Override
        protected Container createMenu(int id, PlayerInventory player) {
            SkillFurnaceEvent.CreateMenuEvent event = new SkillFurnaceEvent.CreateMenuEvent(id, player, this, this.furnaceData);
            MinecraftForge.EVENT_BUS.post(event);
            return event.getContainer();
        }

    }

}
