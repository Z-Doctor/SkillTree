package zdoctor.zskilltree.skilltree.displays;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import zdoctor.zskilltree.api.ImageAsset;

import java.lang.reflect.InvocationTargetException;

public class DisplayInfo {
    private final ITextComponent title;
    private final ITextComponent description;
    private final ItemStack icon;
    private ImageAsset background;

//    private boolean drawTitle = true;
//    private boolean isHidden = false;


    public DisplayInfo(ITextComponent title, ITextComponent description, ItemStack icon) {
        this(title, description, icon, null);
    }

    public DisplayInfo(ITextComponent title, ITextComponent description, ItemStack icon, ImageAsset background) {
        this.title = title;
        this.description = description;
        this.icon = icon;
        this.background = background;
    }

    public DisplayInfo(DisplayInfo other) {
        this.title = other.title;
        this.description = other.description;
        this.icon = other.icon;
        this.background = other.background != null ? other.background : null;
    }

    public DisplayInfo(PacketBuffer buf) {
        this.title = buf.readTextComponent();
        this.description = buf.readTextComponent();
        this.icon = buf.readItemStack();
        this.background = buf.readBoolean() ? ImageAsset.read(buf) : null;
    }

    public DisplayInfo(JsonObject jsonObject) {
        this.title = ITextComponent.Serializer.getComponentFromJson(jsonObject.get("title"));
        this.description = ITextComponent.Serializer.getComponentFromJson(jsonObject.get("description"));
        if (this.title != null && this.description != null) {
            this.icon = deserializeIcon(JSONUtils.getJsonObject(jsonObject, "icon"));
            if (jsonObject.has("background"))
                this.background = ImageAsset.deserialize(JSONUtils.getJsonObject(jsonObject, "background"));
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

    public ITextComponent getTitle() {
        return title;
    }

    public ITextComponent getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public ImageAsset getBackground() {
        return background;
    }

    @OnlyIn(Dist.CLIENT)
    public void setBackground(ImageAsset background) {
        this.background = background;
    }

    public <T extends DisplayInfo> T copy() {
        try {
            return (T) getClass().getConstructor(getClass()).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void write(PacketBuffer buf) {
        buf.writeTextComponent(this.title);
        buf.writeTextComponent(this.description);
        buf.writeItemStack(this.icon);
        if (this.background != null) {
            buf.writeBoolean(true);
            this.background.write(buf);
        } else
            buf.writeBoolean(false);
    }

    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("title", ITextComponent.Serializer.toJsonTree(this.title));
        jsonobject.add("description", ITextComponent.Serializer.toJsonTree(this.description));
        jsonobject.add("icon", this.serializeIcon());
        if (this.background != null) {
            jsonobject.add("frame", this.background.serialize());
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
