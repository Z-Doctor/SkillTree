package zdoctor.skilltree.skills;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class SkillDisplayInfo {
	// private final ITextComponent title;
	// private final ITextComponent description;
	// private final ItemStack icon;
	// private final ResourceLocation background;
	// private final FrameType frame;
	// private final boolean showToast;
	// private final boolean announceToChat;
	// private final boolean hidden;
	// private float x;
	// private float y;

	public SkillDisplayInfo(SkillBase skill, int column, int row, ItemStack icon, ResourceLocation background) {
		// this.title = title;
		// this.description = description;
		// this.icon = icon;
		// this.background = background;
		// this.frame = frame;
		// this.showToast = showToast;
		// this.announceToChat = announceToChat;
		// this.hidden = hidden;
	}

	// public static SkillDisplayInfo deserialize(JsonObject object,
	// JsonDeserializationContext context) {
	// ITextComponent itextcomponent = (ITextComponent)
	// JsonUtils.deserializeClass(object, "title", context,
	// ITextComponent.class);
	// ITextComponent itextcomponent1 = (ITextComponent)
	// JsonUtils.deserializeClass(object, "description", context,
	// ITextComponent.class);
	//
	// if (itextcomponent != null && itextcomponent1 != null) {
	// ItemStack itemstack = deserializeIcon(JsonUtils.getJsonObject(object,
	// "icon"));
	// ResourceLocation resourcelocation = object.has("background")
	// ? new ResourceLocation(JsonUtils.getString(object, "background"))
	// : null;
	// FrameType frametype = object.has("frame") ?
	// FrameType.byName(JsonUtils.getString(object, "frame"))
	// : FrameType.TASK;
	// boolean flag = JsonUtils.getBoolean(object, "show_toast", true);
	// boolean flag1 = JsonUtils.getBoolean(object, "announce_to_chat", true);
	// boolean flag2 = JsonUtils.getBoolean(object, "hidden", false);
	// return new DisplayInfo(itemstack, itextcomponent, itextcomponent1,
	// resourcelocation, frametype, flag, flag1,
	// flag2);
	// } else {
	// throw new JsonSyntaxException("Both title and description must be set");
	// }
	// }
}
