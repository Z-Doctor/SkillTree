# Overview
Creating a skill tree is a farily straight forward process. This explaination assumes you know how to setup your mod and have setup your proxies.

# Creating a skill tab
The skill tab is what the player sees and clicks on to view the page. It's creation was modeled after and simlar to creating a Creative Tab. [You can find the Skill tab class here.](../master/src/main/java/zdoctor/skilltree/tabs/SkillTabs.java) During the preinizilization event, create a new instance of SkillTabs passing a String as the label and class that extends SkillPage. This skill page will be where you add and position your skills. That is all for creating a skill tab. You can save it as a reference, but currently it is not needed. [An example can be seen here.](../Example/src/main/java/zdoctor/mcskilltree/skills/tabs/MCSkillTreeTabs.java#L9)



# Creating a Skill Page
A Skill Page holds all you skills. When the page is created you add all your skills to the page. [The skill page class can be found here.](../master/src/main/java/zdoctor/skilltree/skills/pages/SkillPageBase.java) Depending on the backgound you want, you can replace what getBackgroundType() returns with the [enums found here.](../master/src/main/java/zdoctor/skilltree/api/enums/EnumSkillInteractType.java) Skills should extend [the Skill.class](../master/src/main/java/zdoctor/skilltree/skills/Skill.java) or you can extend the [SkillBase.class if you want more control.](../master/src/main/java/zdoctor/skilltree/skills/SkillBase.java) You can create you skills in-line, but it is recommended to create more complex skills in a seperate class. getLastAddedSkill() is a useful function for adding the same parent, or creating a consecetive set of skills in a chain. The page's name will also abe automatically rendered in the top left corner.

# Creating a Skill
[A skill](../master/src/main/java/zdoctor/skilltree/skills/Skill.java) needs several parameters: unlocalized name, column number and row number, an Item or ItemStack for the icon, and optional requirements. Currently this mod supports up to 72 unique skills (12 columns and 6 rows) per page (bigger pages that can be moved by the mouse coming later). Depending on where you want it placed pass the appropriate column and row and it will automatically render there. [An image guide can be found here.](../master/src/main/resources/assets/skilltree/textures/gui/skilltree/guide_skill_tree.png) The Item or ItemStack will also be automatically sized and rendered on top of the icon and the mod will handle all other visuals be default. If you are creating a new attribute skill, it is recommended to [create a base atrribute class like this.](../Example/src/main/java/zdoctor/mcskilltree/skills/AttackSkill.java) Where you take the name, column, row, icon and a Attribute modifier and hard code a passing of an attribute to be modified.

# Creating a Requirements
Creating a requirement is simple. Create and class [that extends ISkillRequirment and override the methods.](../master/src/main/java/zdoctor/skilltree/api/skills/ISkillRequirment.java) Addtional info is avaible in the class.

# Congratulations
You have successfully created a skill page and some skills. Now we will talk about some useful API and other functionality.

# Advanced Tabs
You can override you Skill Tab if you want greater how the tabs work, or changed the background texture. By defalut, a new tab is placed in the next avaiable slot. You can manually place it however. If you try to place it where another tab would be, all the other tabs will shift over for you. You cannot gurantee your spot, the last man can come first.

# Advanced Pages
If you would like greater control over how your page is rendered [like the Player Info Page.](../master/src/main/java/zdoctor/skilltree/skills/pages/PlayerInfoPage.java) You must register your [custom GuiSkillPage class](../master/src/main/java/zdoctor/skilltree/client/gui/GuiSkillPage.java) in the [GuiPageRegistry during the client side preinit](/master/src/main/java/zdoctor/skilltree/client/GuiPageRegistry.java) with registerGui(...). If you would like to override an exisiting page or someone elses, you must then call overrideGui(...). These pages must have a constructor that takes in [a SkillPageBase](../master/src/main/java/zdoctor/skilltree/skills/pages/SkillPageBase.java).

# Advanced Skills
Skill Trees adds serveral default Interfaces that will increase the customablity of skills. To make your skill a toggle ability, simply implement [the IToggleSkill interface.](../master/src/main/java/zdoctor/skilltree/api/skills/IToggleSkill.java). To do something every tick the skill is active, simply [implement ISkillWatcher and define what happens.](../master/src/main/java/zdoctor/skilltree/api/skills/ISkillWatcher.java).

# Using the API
The class you will use the most is [SkillTreeApi](../master/src/main/java/zdoctor/skilltree/api/SkillTreeApi.java). It makes working with the Skill Capapbility simple. First it is recommended to use or add the SkillTreeApi.DEPENDENCY to you depencies annotation in your mod. That way you are sure to have the correct version of the api for your mod. Most of the methods already have documentation, and the other methods are self expalinatory. Use the getSkillHandler(EntityLivingBase entity) to get the entity's skill capapbility. This provides more methods associated with the ISkillHandler capapbility. If you want to add a skill tree capability to a non player (like for a custom npc with skills), follow the example of [attaching the skill handler here.](../master/src/main/java/zdoctor/skilltree/skills/CapabilitySkillHandler.java)
