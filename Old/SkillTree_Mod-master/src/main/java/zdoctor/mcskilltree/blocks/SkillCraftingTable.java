package zdoctor.mcskilltree.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.Mod;
import zdoctor.mcskilltree.events.SkillWorkBenchEvent;
import zdoctor.mcskilltree.skilltree.SkillWorkbenchContainer;

@Mod.EventBusSubscriber
public class SkillCraftingTable extends CraftingTableBlock {
    private static final ITextComponent TITLE = new TranslationTextComponent("container.crafting");

    public SkillCraftingTable() {
        super(Properties.from(Blocks.CRAFTING_TABLE));
        setRegistryName(Blocks.CRAFTING_TABLE.getRegistryName());
    }


    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResult) {
        SkillWorkBenchEvent.PlayerInteractEvent event = new SkillWorkBenchEvent.PlayerInteractEvent(state, pos, player, handIn, rayTraceResult);
        if (MinecraftForge.EVENT_BUS.post(event))
            return ActionResultType.FAIL;
        if (event.getResult() == Event.Result.DENY)
            return ActionResultType.CONSUME;

        if (!worldIn.isRemote) {
            player.openContainer(state.getContainer(worldIn, pos));
            player.addStat(Stats.INTERACT_WITH_CRAFTING_TABLE);
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public INamedContainerProvider getContainer(BlockState state, World worldIn, BlockPos pos) {
        return new SimpleNamedContainerProvider((id, inventory, playerEntity) ->
                new SkillWorkbenchContainer(id, inventory, IWorldPosCallable.of(worldIn, pos)), TITLE);
    }

}
