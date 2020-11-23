package zdoctor.zskilltree.api;

import net.minecraft.util.ResourceLocation;
import zdoctor.zskilltree.ModMain;

public class ImageAssets {
    public static ImageAsset register(String category, String name, int uOffset, int vOffset, int xSize, int ySize) {
        return new ImageAsset(new ResourceLocation(ModMain.MODID, String.format("textures/%s/%s.png", category, name)),
                uOffset, vOffset, xSize, ySize);
    }

    public static ImageAsset registerGui(String name, int xSize, int ySize) {
        return registerGui(name, 0, 0, xSize, ySize);
    }

    public static ImageAsset registerGui(String name, int uOffset, int vOffset, int xSize, int ySize) {
        return register("gui", name, uOffset, vOffset, xSize, ySize);
    }

    // TODO Make ImageAsset registry to make using them in json easier
    public static final ImageAsset MISSING = registerGui("missing", 16, 16);
    public static final ImageAsset SKILL_TREE_WINDOW = registerGui("skill_tree", 252, 140);
    public static final ImageAsset DEFAULT_TILE = registerGui("skill_tree", 176, 212, 16, 16);
    public static final ImageAsset SANDSTONE_TILE = registerGui("skill_tree", 192, 212, 16, 16);
    public static final ImageAsset ENDSTONE_TILE = registerGui("skill_tree", 208, 212, 16, 16);
    public static final ImageAsset DIRT_TILE = registerGui("skill_tree", 224, 212, 16, 16);
    public static final ImageAsset NETHERRRACK_TILE = registerGui("skill_tree", 176, 228, 16, 16);
    public static final ImageAsset STONE_TILE = registerGui("skill_tree", 192, 228, 16, 16);

    public static final ImageAsset BLACK_WINDOW = registerGui("skill_tree", 176, 140, 51, 72);

    public static final ImageAsset COMMON_FRAME_OWNED = registerGui("skill_tree", 72, 140, 26, 26);
    public static final ImageAsset COMMON_FRAME_UNOWNED = registerGui("skill_tree", 72, 166, 26, 26);

    public static class Tabs {
        public static final ImageAsset TAB_TOP_LEFT = registerGui("tabs", 0, 0, 28, 32);
        public static final ImageAsset TAB_TOP_CENTER = registerGui("tabs", 28, 0, 28, 32);
        public static final ImageAsset TAB_TOP_RIGHT = registerGui("tabs", 56, 0, 28, 32);
        public static final ImageAsset TAB_TOP_LEFT_SELECTED = registerGui("tabs", 0, 32, 28, 32);
        public static final ImageAsset TAB_TOP_CENTER_SELECTED = registerGui("tabs", 28, 32, 28, 32);
        public static final ImageAsset TAB_TOP_RIGHT_SELECTED = registerGui("tabs", 56, 32, 28, 32);

        public static final ImageAsset TAB_BOTTOM_LEFT = registerGui("tabs", 84, 0, 28, 32);
        public static final ImageAsset TAB_BOTTOM_CENTER = registerGui("tabs", 112, 0, 28, 32);
        public static final ImageAsset TAB_BOTTOM_RIGHT = registerGui("tabs", 140, 0, 28, 32);
        public static final ImageAsset TAB_BOTTOM_LEFT_SELECTED = registerGui("tabs", 84, 32, 28, 32);
        public static final ImageAsset TAB_BOTTOM_CENTER_SELECTED = registerGui("tabs", 112, 32, 28, 32);
        public static final ImageAsset TAB_BOTTOM_RIGHT_SELECTED = registerGui("tabs", 140, 32, 28, 32);

        public static final ImageAsset TAB_LEFT_TOP = registerGui("tabs", 0, 64, 32, 28);
        public static final ImageAsset TAB_LEFT_CENTER = registerGui("tabs", 32, 64, 32, 28);
        public static final ImageAsset TAB_LEFT_BOTTOM = registerGui("tabs", 64, 64, 32, 28);
        public static final ImageAsset TAB_LEFT_TOP_SELECTED = registerGui("tabs", 0, 92, 32, 28);
        public static final ImageAsset TAB_LEFT_CENTER_SELECTED = registerGui("tabs", 32, 92, 32, 28);
        public static final ImageAsset TAB_LEFT_BOTTOM_SELECTED = registerGui("tabs", 64, 92, 32, 28);

        public static final ImageAsset TAB_RIGHT_TOP = registerGui("tabs", 96, 64, 32, 28);
        public static final ImageAsset TAB_RIGHT_CENTER = registerGui("tabs", 128, 64, 32, 28);
        public static final ImageAsset TAB_RIGHT_BOTTOM = registerGui("tabs", 160, 64, 32, 28);
        public static final ImageAsset TAB_RIGHT_TOP_SELECTED = registerGui("tabs", 96, 92, 32, 28);
        public static final ImageAsset TAB_RIGHT_CENTER_SELECTED = registerGui("tabs", 128, 92, 32, 28);
        public static final ImageAsset TAB_RIGHT_BOTTOM_SELECTED = registerGui("tabs", 160, 92, 32, 28);
    }

}
