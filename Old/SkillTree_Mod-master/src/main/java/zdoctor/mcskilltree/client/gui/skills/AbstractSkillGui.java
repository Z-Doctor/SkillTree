package zdoctor.mcskilltree.client.gui.skills;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AbstractSkillGui extends AbstractGui {
    public final Minecraft minecraft = Minecraft.getInstance();
}
