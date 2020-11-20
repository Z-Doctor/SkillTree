package zdoctor.zskilltree.skilltree.skill;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import zdoctor.zskilltree.client.gui.ImageAssets;
import zdoctor.zskilltree.extra.ImageAsset;

public class SkillDisplayInfo {
    private final ITextComponent skillName;
    private final ITextComponent description;
    private final ItemStack icon;
    private ImageAsset frame;
    private int x;
    private int y;

    public SkillDisplayInfo(ItemStack icon, ITextComponent skillName, ITextComponent description) {
        this(icon, skillName, description, ImageAssets.COMMON_FRAME_UNOWNED);
    }

    public SkillDisplayInfo(ItemStack icon, ITextComponent skillName, ITextComponent description, ImageAsset frame) {
        this.skillName = skillName;
        this.description = description;
        this.icon = icon;
        this.frame = frame;
    }

    public ITextComponent getSkillName() {
        return skillName;
    }

    public ITextComponent getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ImageAsset getFrame() {
        return frame;
    }

    public void setFrame(ImageAsset frame) {
        this.frame = frame;
    }

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

    public static SkillDisplayInfo deserialize(JsonObject object) {
        ITextComponent pageName = ITextComponent.Serializer.getComponentFromJson(object.get("title"));
        ITextComponent description = ITextComponent.Serializer.getComponentFromJson(object.get("description"));
        if (pageName != null && description != null) {
            ItemStack icon = deserializeIcon(JSONUtils.getJsonObject(object, "icon"));

            ImageAsset frame = null;
            if (object.has("frame"))
                frame = ImageAsset.deserialize(JSONUtils.getJsonObject(object, "frame"));
            return new SkillDisplayInfo(icon, pageName, description, frame);
        } else {
            throw new JsonSyntaxException("Both title and description must be set");
        }
    }

    private static ItemStack deserializeIcon(JsonObject object) {
        if (!object.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        } else {
            Item item = JSONUtils.getItem(object, "item");
            if (object.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                ItemStack itemstack = new ItemStack(item);
                if (object.has("nbt")) {
                    try {
                        CompoundNBT compoundnbt = JsonToNBT.getTagFromJson(JSONUtils.getString(object.get("nbt"), "nbt"));
                        itemstack.setTag(compoundnbt);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
                    }
                }

                return itemstack;
            }
        }
    }

    public void write(PacketBuffer buf) {
        buf.writeTextComponent(this.skillName);
        buf.writeTextComponent(this.description);
        buf.writeItemStack(this.icon);
        if (this.frame != null) {
            buf.writeBoolean(true);
            this.frame.write(buf);
        } else
            buf.writeBoolean(false);

        buf.writeInt(x);
        buf.writeInt(y);
    }

    public static SkillDisplayInfo read(PacketBuffer buf) {
        ITextComponent pageName = buf.readTextComponent();
        ITextComponent description = buf.readTextComponent();
        ItemStack icon = buf.readItemStack();
        boolean flag = buf.readBoolean();
        ImageAsset frame = flag ? ImageAsset.read(buf) : null;
        SkillDisplayInfo displayInfo = new SkillDisplayInfo(icon, pageName, description, frame);
        displayInfo.setPosition(buf.readInt(), buf.readInt());
        return displayInfo;
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("icon", this.serializeIcon());
        jsonobject.add("title", ITextComponent.Serializer.toJsonTree(this.skillName));
        jsonobject.add("description", ITextComponent.Serializer.toJsonTree(this.description));
        if (this.frame != null) {
            jsonobject.add("frame", this.frame.serialize());
        }

        return jsonobject;
    }

    private JsonObject serializeIcon() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.icon.getItem()).toString());
        if (this.icon.hasTag()) {
            jsonobject.addProperty("nbt", this.icon.getTag().toString());
        }

        return jsonobject;
    }
}
