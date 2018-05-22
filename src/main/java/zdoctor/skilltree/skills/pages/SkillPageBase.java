package zdoctor.skilltree.skills.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zdoctor.skilltree.events.SkillEvent;
import zdoctor.skilltree.skills.SkillBase;

public abstract class SkillPageBase {
	protected static final HashMap<String, SkillPageBase> SkillPage_Registry = new HashMap();
	public static final SkillPageBase EMPTY = new SkillPageBase("Empty") {
		@Override
		public SkillPageBase addSkill(SkillBase skillIn, int column, int row) {
			return this;
		}

		@Override
		public List<SkillBase> getSkillList() {
			return Collections.emptyList();
		}

		@Override
		public void registerSkills() {

		}

		@Override
		public void loadPage() {

		}
	};

	private String unlocalizedName;
	private String registryName;

	protected final HashMap<SkillBase, Vector<Integer>> SKILL_ENTRIES = new HashMap();

	// private ArrayList<SkillBase> skillList = new ArrayList<>();

	private SkillBase lastAddedSkill;

	public SkillPageBase(String pageName) {
		this.unlocalizedName = pageName;
		this.registryName = Loader.instance().activeModContainer().getModId() + ":" + pageName;
		if (SkillPage_Registry.containsKey(registryName)) {
			throw new IllegalArgumentException("Attempt to register Skill Page '" + registryName + "' twice");
		}
		SkillPage_Registry.put(registryName, this);
		System.out.println("Register Page: " + registryName);

		registerSkills();
		loadPage();

		MinecraftForge.EVENT_BUS.register(this);
	}

	/**
	 * Create your skills here
	 */
	public abstract void registerSkills();

	/**
	 * Add skills during this method. Will be called so skills can be reloaded
	 */
	public abstract void loadPage();

	public SkillPageBase addSkill(SkillBase skillIn, int column, int row) {
		Vector<Integer> pos = new Vector<>(2);
		pos.add(column);
		pos.add(row);
		for (SkillBase skill1 : SKILL_ENTRIES.keySet()) {
			Vector<Integer> pos1 = SKILL_ENTRIES.get(skill1);
			if (pos.equals(pos1))
				throw new IllegalArgumentException("Tried to add skill '" + skillIn.getRegistryName() + "' to page '"
						+ getRegistryName() + "' in the same spot as '" + skill1.getRegistryName() + "'");
		}

		if (SKILL_ENTRIES.get(skillIn) == null) {
			SKILL_ENTRIES.put(skillIn, pos);
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

	public List<SkillBase> getSkillList() {
		return new ArrayList(SKILL_ENTRIES.keySet());
	}

	public static SkillPageBase getpageFromKey(String key) {
		return SkillPage_Registry.get(key);
	}

	public BackgroundType getBackgroundType() {
		return BackgroundType.DEFAULT;
	}

	@SideOnly(Side.CLIENT)
	public int getColumn(SkillBase skill) {
		return SKILL_ENTRIES.get(skill).get(0);
	}

	@SideOnly(Side.CLIENT)
	public int getRow(SkillBase skill) {
		return SKILL_ENTRIES.get(skill).get(1);
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

	@SubscribeEvent
	public void reloadPage(SkillEvent.ReloadPages.Pre e) {
		SKILL_ENTRIES.clear();
		loadPage();
	}

}
