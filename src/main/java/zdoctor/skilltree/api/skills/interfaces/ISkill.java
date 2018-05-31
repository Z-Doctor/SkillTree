package zdoctor.skilltree.api.skills.interfaces;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import zdoctor.skilltree.api.enums.SkillFrameType;
import zdoctor.skilltree.api.skills.SkillToolTip;
import zdoctor.skilltree.api.skills.requirements.DescriptionRequirment;
import zdoctor.skilltree.api.skills.requirements.NameRequirment;
import zdoctor.skilltree.api.skills.requirements.PreviousSkillRequirement;

public interface ISkill {

	ISkill setParent(ISkill parent);

	ISkill getParent();

	NameRequirment getNameRequirement();

	DescriptionRequirment getDescriptionRequirement();

	PreviousSkillRequirement getParentRequirement();

	ISkill setFrameType(SkillFrameType frameType);

	ISkill addChildren(ISkill childIn);

	ISkill setNameColor(int colorObtained, int colorNotObtained);

	ISkill setDescriptionColor(int color);

	ISkill addRequirement(ISkillRequirment requirment);

	ArrayList<ISkill> getChildren();

	String getUnlocaizedName();

	ResourceLocation getRegistryName();

	List<ISkillRequirment> getRequirments(EntityLivingBase entity, boolean hasSkill);

	boolean hasParent();

	int getId();

	SkillFrameType getFrameType(EntityLivingBase entity);

	ItemStack getIcon(EntityLivingBase entity);

	String getDisplayName();

	List<SkillToolTip> getToolTip(EntityLivingBase entity);

	boolean shouldDrawSkill(EntityLivingBase entity);

	ResourceLocation getBackroundLocation(EntityLivingBase entity);

	boolean shouldRenderItem(EntityLivingBase entity);

	void onSkillActivated(EntityLivingBase entity);

	void onSkillDeactivated(EntityLivingBase entity);

	boolean hasRequirments(EntityLivingBase entity);

	void onSkillPurchase(EntityLivingBase entity);

	boolean shouldDrawLineToChildren();

	ISkill setDrawLineToChildren(boolean doDraw);

	boolean removeChild(ISkill skill);

}
