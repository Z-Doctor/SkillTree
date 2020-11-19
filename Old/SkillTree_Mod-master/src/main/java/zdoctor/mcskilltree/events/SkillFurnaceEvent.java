package zdoctor.mcskilltree.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.util.Hand;
import net.minecraft.util.IIntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import zdoctor.mcskilltree.blocks.SkillFurnace;

public abstract class SkillFurnaceEvent extends Event {

    public static class OverrideVanillaEvent extends SkillFurnaceEvent {

    }

    @HasResult
    @Cancelable

    public static class CanInteractCheckEvent extends Event {
        public CanInteractCheckEvent(Result result) {
            setResult(result);
        }
    }

    @HasResult
    @Cancelable
    public static class PlayerInteractEvent extends SkillFurnaceEvent {

        private final BlockState state;
        private final BlockPos pos;
        private final PlayerEntity player;
        private final Hand handIn;
        private final BlockRayTraceResult rayTraceResult;

        public PlayerInteractEvent(BlockState state, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult rayTraceResult) {
            this.state = state;
            this.pos = pos;
            this.player = player;
            this.handIn = handIn;
            this.rayTraceResult = rayTraceResult;
        }

        public BlockState getState() {
            return state;
        }

        public BlockPos getPos() {
            return pos;
        }

        public PlayerEntity getPlayer() {
            return player;
        }

        public Hand getHandIn() {
            return handIn;
        }

        public BlockRayTraceResult getRayTraceResult() {
            return rayTraceResult;
        }
    }

    public static class CreateMenuEvent extends SkillFurnaceEvent {

        private final int id;
        private final PlayerInventory player;
        private final SkillFurnace.SkillFurnaceTileEntity tileEntity;
        private final IIntArray furnaceData;
        private FurnaceContainer container;

        public CreateMenuEvent(int id, PlayerInventory player, SkillFurnace.SkillFurnaceTileEntity tileEntity, IIntArray furnaceData) {
            this.id = id;
            this.player = player;
            this.tileEntity = tileEntity;
            this.furnaceData = furnaceData;
            this.container = new FurnaceContainer(id, player, tileEntity, furnaceData);
        }

        public int getId() {
            return id;
        }

        public PlayerInventory getPlayer() {
            return player;
        }

        public SkillFurnace.SkillFurnaceTileEntity getTileEntity() {
            return tileEntity;
        }

        public IIntArray getFurnaceData() {
            return furnaceData;
        }

        public FurnaceContainer getContainer() {
            return container;
        }

        public void setContainer(FurnaceContainer container) {
            this.container = container;
        }
    }
}
