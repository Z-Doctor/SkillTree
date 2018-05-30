package zdoctor.skilltree.api.skills.interfaces;

import java.util.List;

import zdoctor.skilltree.api.enums.BackgroundType;
import zdoctor.skilltree.api.events.SkillEvent;

public interface ISkillPage {
	/**
	 * Create your skills here
	 */
	void registerSkills();

	/**
	 * Add skills during this method. Will be called so skills can be reloaded
	 */
	void loadPage();

	ISkillPage addSkill(ISkill skillIn, int column, int row);

	ISkill getLastAddedSkill();

	String getUnlocalizedName();

	String getDisplayName();

	List<ISkill> getSkillList();

	BackgroundType getBackgroundType();

	boolean hasSkillInPage(ISkill skill);

	int getColumn(ISkill skill);

	int getRow(ISkill skill);

	void reloadPage(SkillEvent.ReloadPages.Pre e);

}
