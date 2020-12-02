package zdoctor.zskilltree.mixin;

import net.minecraft.server.integrated.IntegratedServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import zdoctor.zskilltree.skilltree.events.IntegratedServerTick;

import java.util.function.BooleanSupplier;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject(method = "tick", remap = false, at = @At(value = "HEAD"))
    public void onPreTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new IntegratedServerTick(TickEvent.Phase.START));
    }

    @Inject(method = "tick", remap = false, at = @At(value = "RETURN"))
    public void onPostTick(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new IntegratedServerTick(TickEvent.Phase.END));
    }
}
