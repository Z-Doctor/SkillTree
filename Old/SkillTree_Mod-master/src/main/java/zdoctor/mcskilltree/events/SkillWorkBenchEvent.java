package zdoctor.mcskilltree.events;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public abstract class SkillWorkBenchEvent extends Event {

    public static class OverrideVanillaEvent extends SkillWorkBenchEvent {

    }

    @HasResult
    public static class CanInteractCheckEvent extends Event {
        public CanInteractCheckEvent(Result result) {
            setResult(result);
        }
    }

    @HasResult
    public static class PlayerInteractEvent extends SkillWorkBenchEvent {

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
}
