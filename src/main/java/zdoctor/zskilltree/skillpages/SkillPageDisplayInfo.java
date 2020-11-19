package zdoctor.zskilltree.skillpages;

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
import zdoctor.zskilltree.api.enums.SkillPageAlignment;
import zdoctor.zskilltree.client.gui.ImageAssets;
import zdoctor.zskilltree.extra.ImageAsset;

public class SkillPageDisplayInfo {
    private final ITextComponent pageName;
    private final ITextComponent description;
    private final ItemStack icon;
    private final SkillPageAlignment alignment;
    private ImageAsset background;
    private boolean drawTitle = true;
    private boolean isHidden = false;


    public SkillPageDisplayInfo(ItemStack icon, ITextComponent pageName, ITextComponent description) {
        this(icon, pageName, description, ImageAssets.DEFAULT_TILE, SkillPageAlignment.VERTICAL);
    }

    public SkillPageDisplayInfo(ItemStack icon, ITextComponent pageName, ITextComponent description, SkillPageAlignment alignment) {
        this(icon, pageName, description, ImageAssets.DEFAULT_TILE, alignment);
    }

    public SkillPageDisplayInfo(ItemStack icon, ITextComponent pageName, ITextComponent description, ImageAsset background, SkillPageAlignment alignment) {
        this.pageName = pageName;
        this.description = description;
        this.icon = icon;
        this.alignment = alignment;
        this.background = background;
    }

    public ITextComponent getPageName() {
        return pageName;
    }

    public ITextComponent getDescription() {
        return description;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public SkillPageAlignment getAlignment() {
        return alignment;
    }

    public ImageAsset getBackground() {
        return background;
    }

    public void setBackground(ImageAsset background) {
        this.background = background;
    }

    public SkillPageDisplayInfo setNoTitle() {
        drawTitle = false;
        return this;
    }

    public SkillPageDisplayInfo setHidden() {
        isHidden = true;
        return this;
    }

    public boolean drawTitle() {
        return drawTitle;
    }

    public boolean isHidden() {
        return isHidden;
    }

    public static SkillPageDisplayInfo deserialize(JsonObject object) {
        ITextComponent pageName = ITextComponent.Serializer.getComponentFromJson(object.get("title"));
        ITextComponent description = ITextComponent.Serializer.getComponentFromJson(object.get("description"));
        if (pageName != null && description != null) {
            ItemStack icon = deserializeIcon(JSONUtils.getJsonObject(object, "icon"));
            SkillPageAlignment alignment;
            try {
                alignment = object.has("alignment") ?
                        SkillPageAlignment.valueOf(JSONUtils.getString(object, "alignment").toUpperCase()) : SkillPageAlignment.VERTICAL;
            } catch (IllegalArgumentException e) {
                alignment = SkillPageAlignment.VERTICAL;
            }
            ImageAsset background = null;
            if (object.has("background"))
                background = ImageAsset.deserialize(JSONUtils.getJsonObject(object, "background"));
            return new SkillPageDisplayInfo(icon, pageName, description, background, alignment);
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
        buf.writeTextComponent(this.pageName);
        buf.writeTextComponent(this.description);
        buf.writeItemStack(this.icon);
        buf.writeEnumValue(this.alignment);
        if (this.background != null) {
            buf.writeBoolean(true);
            this.background.write(buf);
        } else
            buf.writeBoolean(false);
    }

    public static SkillPageDisplayInfo read(PacketBuffer buf) {
        ITextComponent pageName = buf.readTextComponent();
        ITextComponent description = buf.readTextComponent();
        ItemStack icon = buf.readItemStack();
        SkillPageAlignment alignment = buf.readEnumValue(SkillPageAlignment.class);
        boolean flag = buf.readBoolean();
        ImageAsset background = flag ? ImageAsset.read(buf) : null;
        return new SkillPageDisplayInfo(icon, pageName, description, background, alignment);
    }

    public JsonElement serialize() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("icon", this.serializeIcon());
        jsonobject.add("title", ITextComponent.Serializer.toJsonTree(this.pageName));
        jsonobject.add("description", ITextComponent.Serializer.toJsonTree(this.description));
        jsonobject.addProperty("alignment", this.alignment.name());
        if (this.background != null) {
            jsonobject.add("background", this.background.serialize());
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
