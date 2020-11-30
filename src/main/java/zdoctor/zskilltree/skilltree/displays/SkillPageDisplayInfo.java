package zdoctor.zskilltree.skilltree.displays;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import zdoctor.zskilltree.api.ImageAsset;
import zdoctor.zskilltree.api.ImageAssets;
import zdoctor.zskilltree.api.enums.SkillPageAlignment;

public class SkillPageDisplayInfo extends DisplayInfo {
    private final SkillPageAlignment alignment;

    public SkillPageDisplayInfo(ItemStack icon, ITextComponent pageName, ITextComponent description) {
        this(icon, pageName, description, ImageAssets.DEFAULT_TILE, SkillPageAlignment.VERTICAL);
    }

    public SkillPageDisplayInfo(ItemStack icon, ITextComponent pageName, ITextComponent description, SkillPageAlignment alignment) {
        this(icon, pageName, description, ImageAssets.DEFAULT_TILE, alignment);
    }

    public SkillPageDisplayInfo(ItemStack icon, ITextComponent pageName, ITextComponent description, ImageAsset background, SkillPageAlignment alignment) {
        super(pageName, description, icon, background);
        this.alignment = alignment;
    }

    protected SkillPageDisplayInfo(SkillPageDisplayInfo displayInfo) {
        super(displayInfo);
        this.alignment = displayInfo.alignment;
    }

    public SkillPageDisplayInfo(JsonObject jsonObject) {
        super(jsonObject);
        if (jsonObject.has("alignment"))
            this.alignment = SkillPageAlignment.valueOf(JSONUtils.getString(jsonObject, "alignment").toUpperCase());
        else
            this.alignment = SkillPageAlignment.VERTICAL;
    }


    public SkillPageDisplayInfo(PacketBuffer buf) {
        super(buf);
        this.alignment = buf.readEnumValue(SkillPageAlignment.class);
    }

    public SkillPageAlignment getAlignment() {
        return alignment;
    }

    @Override
    public void write(PacketBuffer buf) {
        super.write(buf);
        buf.writeEnumValue(this.alignment);
    }

    @Override
    public JsonObject serialize() {
        JsonObject jsonObject = super.serialize();
        jsonObject.addProperty("alignment", this.alignment.name());

        return jsonObject;
    }
}
