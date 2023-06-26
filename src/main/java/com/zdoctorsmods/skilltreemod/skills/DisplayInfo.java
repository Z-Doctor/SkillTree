package com.zdoctorsmods.skilltreemod.skills;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.advancements.FrameType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

// TODO Support for drawing connections from 2 skills to 1 skill as preresequites
public class DisplayInfo {
    private final Component title;
    private final Component description;
    private final ItemStack icon;
    private final ResourceLocation background;
    private final FrameType frame;
    private final boolean hidden;
    private Position position;

    public DisplayInfo(ItemStack pIcon, Component pTitle, Component pDescription, ResourceLocation pBackground,
            FrameType pFrame, boolean pHidden) {
        this.title = pTitle;
        this.description = pDescription;
        this.icon = pIcon;
        this.background = pBackground;
        if (pFrame == null)
            this.frame = FrameType.TASK;
        else
            this.frame = pFrame;
        this.hidden = pHidden;
    }

    public DisplayInfo setLocation(Position position) {
        this.position = position;
        return this;
    }

    public DisplayInfo setLocation(byte x, byte y) {
        if (position == null)
            position = new Position();
        this.position.x = x;
        this.position.y = y;
        return this;
    }

    public Component getTitle() {
        return this.title;
    }

    public Component getDescription() {
        return this.description;
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public ResourceLocation getBackground() {
        return this.background;
    }

    public FrameType getFrame() {
        return this.frame;
    }

    public Position getPosition() {
        return position;
    }

    public boolean isHidden() {
        return this.hidden;
    }

    public static DisplayInfo fromJson(JsonObject pJson) {
        Component component = Component.Serializer.fromJson(pJson.get("title"));
        Component component1 = Component.Serializer.fromJson(pJson.get("description"));
        if (component != null && component1 != null) {
            ItemStack itemstack = getIcon(GsonHelper.getAsJsonObject(pJson, "icon"));
            ResourceLocation resourcelocation = pJson.has("background")
                    ? new ResourceLocation(GsonHelper.getAsString(pJson, "background"))
                    : null;
            FrameType frametype = pJson.has("frame") ? FrameType.byName(GsonHelper.getAsString(pJson, "frame"))
                    : FrameType.TASK;
            boolean isHidden = GsonHelper.getAsBoolean(pJson, "hidden", false);

            DisplayInfo displayInfo = new DisplayInfo(itemstack, component, component1, resourcelocation, frametype,
                    isHidden);

            if (pJson.has("position")) {
                displayInfo.setLocation(Position.fromJson(pJson));
            }

            return displayInfo;
        } else {
            throw new JsonSyntaxException("Both title and description must be set");
        }
    }

    private static ItemStack getIcon(JsonObject pJson) {
        if (!pJson.has("item")) {
            throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
        } else {
            Item item = GsonHelper.getAsItem(pJson, "item");
            if (pJson.has("data")) {
                throw new JsonParseException("Disallowed data tag found");
            } else {
                ItemStack itemstack = new ItemStack(item);
                if (pJson.has("nbt")) {
                    try {
                        CompoundTag compoundtag = TagParser
                                .parseTag(GsonHelper.convertToString(pJson.get("nbt"), "nbt"));
                        itemstack.setTag(compoundtag);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
                    }
                }

                return itemstack;
            }
        }
    }

    public void serializeToNetwork(FriendlyByteBuf pBuffer) {
        pBuffer.writeComponent(this.title);
        pBuffer.writeComponent(this.description);
        pBuffer.writeItem(this.icon);
        pBuffer.writeEnum(this.frame);
        int i = 0;
        if (this.background != null) {
            i |= 1;
        }

        if (this.hidden) {
            i |= 2;
        }

        pBuffer.writeInt(i);
        if (this.background != null) {
            pBuffer.writeResourceLocation(this.background);
        }

        boolean hasDisplay = position != null;
        pBuffer.writeBoolean(hasDisplay);

        if (hasDisplay) {
            position.writePosition(pBuffer);
        }

    }

    public static DisplayInfo fromNetwork(FriendlyByteBuf pBuffer) {
        Component component = pBuffer.readComponent();
        Component component1 = pBuffer.readComponent();
        ItemStack itemstack = pBuffer.readItem();
        FrameType frametype = pBuffer.readEnum(FrameType.class);
        int i = pBuffer.readInt();
        ResourceLocation resourcelocation = (i & 1) != 0 ? pBuffer.readResourceLocation() : null;
        boolean hidden = (i & 2) != 0;
        DisplayInfo displayinfo = new DisplayInfo(itemstack, component, component1, resourcelocation, frametype,
                hidden);
        Position position = pBuffer.readNullable(Position::fromNetwork);
        if (position != null)
            displayinfo.setLocation(position);
        return displayinfo;
    }

    public JsonElement serializeToJson() {
        JsonObject jsonobject = new JsonObject();
        jsonobject.add("icon", this.serializeIcon());
        jsonobject.add("title", Component.Serializer.toJsonTree(this.title));
        jsonobject.add("description", Component.Serializer.toJsonTree(this.description));
        jsonobject.addProperty("frame", this.frame.getName());
        jsonobject.addProperty("hidden", this.hidden);
        if (this.background != null) {
            jsonobject.addProperty("background", this.background.toString());
        }
        if (this.position != null) {
            jsonobject.add("position", this.position.serializePosition());
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

    public static class Position {
        public byte x;
        public byte y;

        public Position() {
        }

        public Position(byte x, byte y) {
            this.x = x;
            this.y = y;
        }

        public static Position fromJson(JsonObject pJson) {
            JsonObject jsonPosition = pJson.getAsJsonObject("position");
            if (jsonPosition == null)
                return null;
            Position position = new Position();
            if (jsonPosition.has("x"))
                position.x = jsonPosition.get("x").getAsByte();
            if (jsonPosition.has("y"))
                position.y = jsonPosition.get("y").getAsByte();
            return position;
        }

        public static Position fromNetwork(FriendlyByteBuf pBuffer) {
            Position position = new Position();
            position.x = pBuffer.readByte();
            position.y = pBuffer.readByte();
            return position;
        }

        public JsonObject serializePosition() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("x", x);
            jsonObject.addProperty("y", y);
            return jsonObject;
        }

        public void writePosition(FriendlyByteBuf pBuffer) {
            pBuffer.writeByte(x);
            pBuffer.writeByte(y);
        }
    }
}
