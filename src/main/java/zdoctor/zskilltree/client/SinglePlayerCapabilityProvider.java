package zdoctor.zskilltree.client;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import zdoctor.zskilltree.client.multiplayer.ClientSkillTreeTracker;
import zdoctor.zskilltree.skilltree.providers.MultiplayerSkillTreeProvider;

public class SinglePlayerCapabilityProvider extends MultiplayerSkillTreeProvider {
    @Override
    public ICapabilityProvider createProvider(Entity entity) {
        if (entity instanceof ClientPlayerEntity)
            return Provider.of(new ClientSkillTreeTracker((ClientPlayerEntity) entity));
        return super.createProvider(entity);
    }
}
