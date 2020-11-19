package zdoctor.zskilltree.extra;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class ImageAsset {
    public final ResourceLocation texture;
    public final int xSize;
    public final int ySize;

    public final int uOffset;
    public final int vOffset;

    public ImageAsset(ResourceLocation texture, int xSize, int ySize) {
        this(texture, 0, 0, xSize, ySize);
    }

    public ImageAsset(ResourceLocation texture, int uOffset, int vOffset, int xSize, int ySize) {
        this.texture = texture;
        this.xSize = xSize;
        this.ySize = ySize;

        this.uOffset = uOffset;
        this.vOffset = vOffset;
    }

    @Override
    public String toString() {
        return "Image Asset{ " + texture + " }";
    }

    public static ImageAsset deserialize(JsonObject object) {
        ResourceLocation texture = object.has("texture") ? new ResourceLocation(JSONUtils.getString(object, "texture")) : null;
        JsonObject data = JSONUtils.getJsonObject(object, "data");
        int xSize = data.has("x") ? JSONUtils.getInt(data, "x") : 0;
        int ySize = data.has("y") ? JSONUtils.getInt(data, "y") : 0;
        int uOffset = data.has("u") ? JSONUtils.getInt(data, "u") : 0;
        int vOffset = data.has("v") ? JSONUtils.getInt(data, "v") : 0;
        return new ImageAsset(texture, xSize, ySize, uOffset, vOffset);
    }

    public void write(PacketBuffer buf) {
        if (this.texture != null) {
            buf.writeInt(1);
            buf.writeResourceLocation(this.texture);
        } else
            buf.writeInt(0);

        buf.writeInt(xSize);
        buf.writeInt(ySize);
        buf.writeInt(uOffset);
        buf.writeInt(vOffset);
    }

    public static ImageAsset read(PacketBuffer buf) {
        int flag = buf.readInt();
        ResourceLocation texture = (flag & 1) != 0 ? buf.readResourceLocation() : null;
        return new ImageAsset(texture, buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public JsonObject serialize() {
        JsonObject jsonobject = new JsonObject();
        if (this.texture != null) {
            jsonobject.addProperty("texture", this.texture.toString());
        }
        JsonObject data = new JsonObject();
        data.addProperty("x", xSize);
        data.addProperty("y", ySize);
        data.addProperty("u", uOffset);
        data.addProperty("v", vOffset);
        jsonobject.add("data", data);

        return jsonobject;
    }
}
