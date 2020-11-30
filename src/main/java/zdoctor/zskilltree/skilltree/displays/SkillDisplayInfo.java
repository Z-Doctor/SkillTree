package zdoctor.zskilltree.skilltree.displays;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;

public class SkillDisplayInfo extends DisplayInfo {
    //    private final ITextComponent skillName;
//    private final ITextComponent description;
//    private final ItemStack icon;
//    private ImageAsset frame;
    private int x;
    private int y;

    public SkillDisplayInfo(PacketBuffer buf) {
        super(buf);
        setPosition(buf.readInt(), buf.readInt());
    }

    public SkillDisplayInfo(JsonObject jsonObject) {
        super(jsonObject);
    }

    protected SkillDisplayInfo(SkillDisplayInfo other) {
        super(other);
        x = other.getX();
        y = other.getY();
    }

    public SkillDisplayInfo(ItemStack icon, ITextComponent skillName, ITextComponent description) {
        this(icon, skillName, description, ImageAssets.COMMON_FRAME_UNOWNED);
    }

    public SkillDisplayInfo(ItemStack icon, ITextComponent skillName, ITextComponent description, ImageAsset background) {
        super(skillName, description, icon, background);
    }

    // TODO Setup placement system
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);
        buf.writeInt(x);
        buf.writeInt(y);
    }

}
