# Overview
Creating a skill tree is a fairly straightforward process. This explanation assumes you know how to setup your mod and have set up your proxies.

# Creating a skill tab
The skill tab is what the player sees and clicks on to view the page. Its creation was modeled after and similar to creating a Creative Tab. [You can find the Skill tab class here.](../master/src/main/java/zdoctor/skill tree/tabs/SkillTabs.java) During the FMLPreInitializationEvent, create a new instance of SkillTabs passing a String as the label and class that extends SkillPage. This skill page will be where you add and position your skills. That's all you need to create a skill tab. You can save it as a reference, but currently, it is not needed. [An example can be seen here.](../Example/src/main/java/zdoctor/mcskilltree/skills/tabs/MCSkillTreeTabs.java#L9) You also need to define the icon (item stack like creative tabs).

# Creating a Skill Page
A Skill Page holds all your skills. When the page is created you add all your skills to the page. [The skill page class can be found here.](../master/src/main/java/zdoctor/skilltree/skills/pages/SkillPageBase.java) Depending on the backgound you want, you can replace what getBackgroundType() returns with the [enums found here.](../master/src/main/java/zdoctor/skilltree/api/enums/EnumSkillInteractType.java) Skills should extend [the Skill.class](../master/src/main/java/zdoctor/skilltree/skills/Skill.java) or you can extend the [SkillBase.class if you want more control.](../master/src/main/java/zdoctor/skilltree/skills/SkillBase.java) You can create you skills in-line, but it is recommended to create more complex skills in a separate class. There are two methods used in page creation: registerSkills and loadPage. In registerSkills you can create your skills for the first time, saving them to a variable. LoadPage is where you add your skills to the page. This gets called when the page is reloaded, making it easier to position skills for devs.

# Creating a Skill
[A skill](../master/src/main/java/zdoctor/skilltree/skills/Skill.java) needs several parameters: unlocalized name, an Item or ItemStack for the icon, and optional requirements. This mod supports and unlimited number of skills per page and will resize depending on need. Depending on where you want it placed, pass the appropriate column and row during page creation and it will automatically render there. [A basic image guide can be found here.](../master/src/main/resources/assets/skilltree/textures/gui/skilltree/guide_skill_tree.png) The Item or ItemStack will also be automatically sized and rendered on top of the icon and the mod will handle all other visuals by default. If you are creating a new attribute skill, it is recommended to [create a base attribute class like this.](../Example/src/main/java/zdoctor/mcskilltree/skills/AttackSkill.java) Where you take the name, icon and an Attribute modifier and hard code a passing of an attribute to be modified. There are multiple interfaces that can increase the functionality of skills easily.

# Creating a Requirements
Creating a requirement is simple. Create and class [that extends ISkillRequirment and override the methods.](../master/src/main/java/zdoctor/skilltree/api/skills/ISkillRequirment.java) Additional info is available in the class.

# Congratulations
You have successfully created a skill page and some skills. Now we will talk about some useful API and other functionality.

# Skill Points
This mod does not award the player skill points, but offers a basic skill point system and methods to add points to the entity. Additional custom skill points are planned later on.

# Advanced Tabs
You can override your Skill Tab if you want greater over how the tab works, or change the background texture. By default, a new tab is placed in the next available slot. You can manually place it, however. If you try to place it where another tab would be, all the other tabs will shift over for you. You cannot guarantee your spot, the last man can come first. 

# Advanced Pages
If you would like greater control over how your page is rendered [like the Player Info Page.](../master/src/main/java/zdoctor/skilltree/skills/pages/PlayerInfoPage.java) You must register your [custom GuiSkillPage class](../master/src/main/java/zdoctor/skilltree/client/gui/GuiSkillPage.java) in the [GuiPageRegistry during the client side preinit](/master/src/main/java/zdoctor/skilltree/client/GuiPageRegistry.java) with registerGui(...). If you would like to override an existing page or someone else's, you must then call overrideGui(...). These pages must have a constructor that takes in [a SkillPageBase](../master/src/main/java/zdoctor/skilltree/skills/pages/SkillPageBase.java).

# Advanced Skills
Skill Trees adds several default Interfaces that will increase the customizability of skills. To make your skill a toggle ability, simply implement [the IToggleSkill interface.](../master/src/main/java/zdoctor/skilltree/api/skills/IToggleSkill.java). To do something every tick the skill is active, simply [implement ISkillWatcher and define what happens.](../master/src/main/java/zdoctor/skilltree/api/skills/ISkillWatcher.java).

# Using the API
The class you will use the most is [SkillTreeApi](../master/src/main/java/zdoctor/skilltree/api/SkillTreeApi.java). It makes working with the Skill Capability simple. First, it is recommended to use or add the SkillTreeApi.DEPENDENCY to you dependencies annotation in your mod. That way you are sure to have the correct version of the API for your mod. Most of the methods already have documentation, and the other methods are self-explanatory. Use the getSkillHandler(EntityLivingBase entity) to get the entity's skill capability. This provides more methods associated with the ISkillHandler capability. If you want to add a skill tree capability to a non-player (like for a custom NPC with skills), follow the example of [attaching the skill handler here.](../master/src/main/java/zdoctor/skilltree/skills/CapabilitySkillHandler.java)
