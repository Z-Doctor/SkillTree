package zdoctor.zskilltree.api;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import zdoctor.zskilltree.ModMain;

@Mod.EventBusSubscriber
public class ImageAssets {
    // TODO Add some kind of reader to find an ImageAsset from a resource location
    public static final ImageAsset MISSING = ImageAsset.builder("missing").setDimensions(16, 16).build("missing");

    private static ImageAsset.Builder builder = ImageAsset.builder("skill_tree").setFolder("gui");
    public static final ImageAsset SKILL_TREE_WINDOW = builder.subAsset().setDimensions(252, 140).build("window");
    public static final ImageAsset DEFAULT_TILE = builder.setDimensions(16, 16).subAsset().setUV(176, 212)
            .build("tile.default");
    public static final ImageAsset SANDSTONE_TILE = builder.subAsset().setUV(192, 212).build("tile.sandstone");
    public static final ImageAsset ENDSTONE_TILE = builder.subAsset().setUV(208, 212).build("tile.endstone");

    public static final ImageAsset DIRT_TILE = builder.subAsset().setUV(224, 212).build("tile.dirt");
    public static final ImageAsset NETHERRACK_TILE = builder.subAsset().setUV(176, 228).build("tile.netherrack");
    public static final ImageAsset STONE_TILE = builder.subAsset().setUV(192, 228).build("tile.stone");

    public static final ImageAsset BLACK_WINDOW = builder.subAsset().setUV(176, 140)
            .setDimensions(51, 72).build("black_window");

    public static final ImageAsset COMMON_FRAME_OWNED = builder.setDimensions(26, 26).subAsset()
            .setUV(72, 140).build("frame.common.owned");
    public static final ImageAsset COMMON_FRAME_UNOWNED = builder.setUV(72, 166).build("frame.common.unowned");


    @SubscribeEvent
    public static void register(RegistryEvent.Register<ImageAsset> event) {
        builder.register(event.getRegistry());
        builder = null;
        Tabs.tab = null;
    }

    public static class Tabs {
        private static ResourceLocation tab = new ResourceLocation(ModMain.MODID, "textures/gui/tabs.png");
        public static final ImageAsset TAB_TOP_LEFT = registerTab(0, 0, 28, 32);
        public static final ImageAsset TAB_TOP_CENTER = registerTab(28, 0, 28, 32);
        public static final ImageAsset TAB_TOP_RIGHT = registerTab(56, 0, 28, 32);
        public static final ImageAsset TAB_TOP_LEFT_SELECTED = registerTab(0, 32, 28, 32);
        public static final ImageAsset TAB_TOP_CENTER_SELECTED = registerTab(28, 32, 28, 32);
        public static final ImageAsset TAB_TOP_RIGHT_SELECTED = registerTab(56, 32, 28, 32);
        public static final ImageAsset TAB_BOTTOM_LEFT = registerTab(84, 0, 28, 32);
        public static final ImageAsset TAB_BOTTOM_CENTER = registerTab(112, 0, 28, 32);
        public static final ImageAsset TAB_BOTTOM_RIGHT = registerTab(140, 0, 28, 32);
        public static final ImageAsset TAB_BOTTOM_LEFT_SELECTED = registerTab(84, 32, 28, 32);
        public static final ImageAsset TAB_BOTTOM_CENTER_SELECTED = registerTab(112, 32, 28, 32);
        public static final ImageAsset TAB_BOTTOM_RIGHT_SELECTED = registerTab(140, 32, 28, 32);
        public static final ImageAsset TAB_LEFT_TOP = registerTab(0, 64, 32, 28);
        public static final ImageAsset TAB_LEFT_CENTER = registerTab(32, 64, 32, 28);
        public static final ImageAsset TAB_LEFT_BOTTOM = registerTab(64, 64, 32, 28);
        public static final ImageAsset TAB_LEFT_TOP_SELECTED = registerTab(0, 92, 32, 28);
        public static final ImageAsset TAB_LEFT_CENTER_SELECTED = registerTab(32, 92, 32, 28);
        public static final ImageAsset TAB_LEFT_BOTTOM_SELECTED = registerTab(64, 92, 32, 28);
        public static final ImageAsset TAB_RIGHT_TOP = registerTab(96, 64, 32, 28);
        public static final ImageAsset TAB_RIGHT_CENTER = registerTab(128, 64, 32, 28);
        public static final ImageAsset TAB_RIGHT_BOTTOM = registerTab(160, 64, 32, 28);
        public static final ImageAsset TAB_RIGHT_TOP_SELECTED = registerTab(96, 92, 32, 28);
        public static final ImageAsset TAB_RIGHT_CENTER_SELECTED = registerTab(128, 92, 32, 28);
        public static final ImageAsset TAB_RIGHT_BOTTOM_SELECTED = registerTab(160, 92, 32, 28);

        private static ImageAsset registerTab(int uOffset, int vOffset, int xSize, int ySize) {
            return new ImageAsset(tab, uOffset, vOffset, xSize, ySize);
        }
    }

}
