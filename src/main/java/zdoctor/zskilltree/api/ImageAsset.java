package zdoctor.zskilltree.api;

import com.google.gson.JsonObject;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistry;
import zdoctor.zskilltree.ModMain;

import java.util.ArrayList;
import java.util.List;

public class ImageAsset extends ForgeRegistryEntry<ImageAsset> {
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

    public static ImageAsset deserialize(JsonObject object) {
        ResourceLocation texture = object.has("texture") ? new ResourceLocation(JSONUtils.getString(object, "texture")) : null;
        JsonObject data = JSONUtils.getJsonObject(object, "data");
        int xSize = data.has("x") ? JSONUtils.getInt(data, "x") : 0;
        int ySize = data.has("y") ? JSONUtils.getInt(data, "y") : 0;
        int uOffset = data.has("u") ? JSONUtils.getInt(data, "u") : 0;
        int vOffset = data.has("v") ? JSONUtils.getInt(data, "v") : 0;
        return new ImageAsset(texture, xSize, ySize, uOffset, vOffset);
    }

    public static ImageAsset read(PacketBuffer buf) {
        int flag = buf.readInt();
        ResourceLocation texture = (flag & 1) != 0 ? buf.readResourceLocation() : null;
        return new ImageAsset(texture, buf.readInt(), buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static Builder builder(String name) {
        return new Builder(name);
    }

    @Override
    public String toString() {
        return "Image Asset{ " + getRegistryName() + " }";
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

    public static class SubAsset {

    }

    public static class Builder {
        private final String name;
        private String folder;
        private String namespace = ModMain.MODID;
        private ImageAsset build;

        private List<Builder> subdivisions;

        private int u, v, width, height;

        private Builder(String name) {
            this.name = name;
        }

        public String getFolder() {
            if (folder == null || folder.isEmpty())
                return "";
            if (folder.endsWith("/"))
                return folder;
            return folder + "/";
        }

        public Builder setFolder(String folder) {
            this.folder = folder;
            return this;
        }

        public Builder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public Builder setUV(Integer u, Integer v) {
            if (u != null)
                this.u = u;
            if (v != null)
                this.v = v;
            return this;
        }

        public Builder setDimensions(Integer width, Integer height) {
            if (width != null)
                this.width = width;
            if (height != null)
                this.height = height;
            return this;
        }

        public String getPreparedPath() {
            return "textures/" + getFolder() + name + ".png";
        }

        public Builder subAsset() {
            if(subdivisions == null)
                subdivisions = new ArrayList<>();

            Builder subBuilder = new Builder(name);
            subBuilder.setDimensions(width, height);
            subBuilder.setUV(u, v);
            subBuilder.setFolder(folder);
            subBuilder.setNamespace(namespace);
            subdivisions.add(subBuilder);
            return subBuilder;
        }

//        public RegistryObject<ImageAsset> build(String name, DeferredRegister<ImageAsset> registry) {
//            return registry.register(name, () -> build(new ResourceLocation(name)));
//        }

        public ImageAsset build(String name) {
            return build(new ResourceLocation(namespace, name));
        }

        public ImageAsset build(ResourceLocation location) {
            ImageAsset imageAsset = new ImageAsset(new ResourceLocation(location.getNamespace() + ":" + getPreparedPath()),
                    u, v, width, height);
            imageAsset.setRegistryName(location);
            build = imageAsset;
            return imageAsset;
        }

        public ImageAsset getBuild() {
            return build;
        }

        public boolean subdivided() {
            return subdivisions != null && !subdivisions.isEmpty();
        }

        public void register(IForgeRegistry<ImageAsset> registry) {
            if(subdivided()) {
                subdivisions.stream().map(Builder::getBuild).forEach(registry::register);
                subdivisions.clear();
            } else {
                registry.register(build);
            }
        }
    }
}
