package zdoctor.skilltree.skills.pages;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.skills.SkillBase;

public abstract class SkillPageBase {
	protected static HashMap<String, SkillPageBase> SkillPage_Registry = new HashMap();

	private String unlocalizedName;
	private String registryName;

	private ArrayList<SkillBase> skillList = new ArrayList<>();

	private SkillBase lastAddedSkill;

	public SkillPageBase(String pageName) {
		this.unlocalizedName = pageName;
		this.registryName = Loader.instance().activeModContainer().getModId() + ":" + pageName;
		if (SkillPage_Registry.containsKey(registryName)) {
			throw new IllegalArgumentException("Attempt to register Skill Page '" + registryName + "' twice");
		}
		SkillPage_Registry.put(registryName, this);
		System.out.println("Register Page: " + registryName);
	}

	public SkillPageBase addSkill(SkillBase skillIn) {
		if (!skillList.contains(skillIn)) {
			skillIn.setPage(this);
			skillList.add(skillIn);
			lastAddedSkill = skillIn;
		} else
			throw new IllegalArgumentException(
					"Tried to add skill '" + skillIn.getRegistryName() + "' to page '" + getRegistryName() + "' twice");
		return this;
	}

	public SkillBase getLastAddedSkill() {
		return lastAddedSkill;
	}

	public String getUnlocalizedName() {
		return "page." + unlocalizedName;
	}

	public String getDisplayName() {
		return I18n.format(unlocalizedName);
	}

	public String getRegistryName() {
		return registryName;
	}

	public ArrayList<SkillBase> getSkillList() {
		return new ArrayList(skillList);
	}

	public static SkillPageBase getpageFromKey(String key) {
		return SkillPage_Registry.get(key);
	}

	public BackgroundType getBackgroundType() {
		return BackgroundType.DEFAULT;
	}

	public static enum BackgroundType {
		DEFAULT,
		SANDSTONE,
		ENDSTONE,
		DIRT,
		NETHERRACK,
		STONE,
		CUSTOM;

		private static int count = 0;
		private int column;

		private BackgroundType() {
			column = getCount();
		}

		private int getCount() {
			return count++;
		}

		@SideOnly(Side.CLIENT)
		public int getColumn() {
			return column > 3 ? column - 4 : column;
		}

		@SideOnly(Side.CLIENT)
		public int getRow() {
			return column > 3 ? 1 : 0;
		}
	}

}
