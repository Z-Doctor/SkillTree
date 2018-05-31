package zdoctor.skilltree.api.skills.page;

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
import zdoctor.skilltree.api.enums.BackgroundType;
import zdoctor.skilltree.api.events.SkillEvent;
import zdoctor.skilltree.api.skills.interfaces.ISkill;
import zdoctor.skilltree.api.skills.interfaces.ISkillPage;

public abstract class SkillPageBase implements ISkillPage {
	protected static final HashMap<String, ISkillPage> SkillPage_Registry = new HashMap();

	public static ISkillPage getpageFromKey(String key) {
		return SkillPage_Registry.get(key);
	}

	public static final SkillPageBase EMPTY = new SkillPageBase("Empty") {
		@Override
		public SkillPageBase addSkill(ISkill skillIn, int column, int row) {
			return this;
		}

		@Override
		public List<ISkill> getSkillList() {
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

	protected final HashMap<ISkill, Vector<Integer>> SKILL_ENTRIES = new HashMap();

	private ISkill lastAddedSkill;

	public SkillPageBase(String pageName) {
		this.unlocalizedName = pageName;
		this.registryName = Loader.instance().activeModContainer().getModId() + ":" + pageName;
		if (SkillPage_Registry.containsKey(registryName)) {
			throw new IllegalArgumentException("Attempt to register Skill Page '" + registryName + "' twice");
		}
		SkillPage_Registry.put(registryName, this);
		// ModMain.proxy.log.debug("Register Page: {}", registryName);

		registerSkills();
		loadPage();

		MinecraftForge.EVENT_BUS.register(this);
	}

	@Override
	public ISkillPage addSkill(ISkill skillIn, int column, int row) {
		if (skillIn == null)
			throw new IllegalArgumentException("Tried to add null skill");
		Vector<Integer> pos = new Vector<>(2);
		pos.add(column);
		pos.add(row);
		for (ISkill skill1 : SKILL_ENTRIES.keySet()) {
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

	@Override
	public ISkill getLastAddedSkill() {
		return lastAddedSkill;
	}

	@Override
	public String getUnlocalizedName() {
		return "page." + unlocalizedName;
	}

	@Override
	public String getDisplayName() {
		return I18n.format(unlocalizedName);
	}

	public String getRegistryName() {
		return registryName;
	}

	@Override
	public List<ISkill> getSkillList() {
		return new ArrayList(SKILL_ENTRIES.keySet());
	}

	@Override
	public BackgroundType getBackgroundType() {
		return BackgroundType.DEFAULT;
	}

	@Override
	public boolean hasSkillInPage(ISkill skill) {
		return SKILL_ENTRIES.get(skill) != null;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColumn(ISkill skill) {
		if (!hasSkillInPage(skill)) {
			// ModMain.proxy.log.catching(new NullPointerException("Tried to get column of
			// skill '"
			// + skill.getRegistryName() + "' that is not on page '" + getRegistryName() +
			// "'"));
			return 0;
		}

		return SKILL_ENTRIES.get(skill).get(0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRow(ISkill skill) {
		if (!hasSkillInPage(skill)) {
			// ModMain.proxy.log.catching(new NullPointerException("Tried to get row of
			// skill '" + skill.getRegistryName()
			// + "' that is not on page '" + getRegistryName() + "'"));
			return 0;
		}
		return SKILL_ENTRIES.get(skill).get(1);
	}

	@Override
	@SubscribeEvent
	public void reloadPage(SkillEvent.ReloadPages.Pre e) {
		SKILL_ENTRIES.clear();
		loadPage();
	}

}
