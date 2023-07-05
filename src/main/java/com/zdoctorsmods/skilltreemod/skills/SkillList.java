package com.zdoctorsmods.skilltreemod.skills;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceLocation;

public class SkillList {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Map<ResourceLocation, Skill> skillLookup = new HashMap<>();
   private final Set<Skill> trees = new LinkedHashSet<>();
   private final Set<Skill> skills = new LinkedHashSet<>();
   private Listener listener;

   private void remove(Skill skill) {
      for (Skill child : skill.getChildren()) {
         this.remove(child);
      }

      LOGGER.info("Forgot about skill {}", skill.getId());
      skillLookup.remove(skill.getId());
      if (skill.getParent() == null) {
         trees.remove(skill);
         if (listener != null)
            listener.onTreeRemoved(skill);
      } else {
         skills.remove(skill);
         if (listener != null)
            listener.onRemoveSkill(skill);
      }
   }

   public void remove(Set<ResourceLocation> skills) {
      for (ResourceLocation resourcelocation : skills) {
         Skill skill = skillLookup.get(resourcelocation);
         if (skill == null) {
            LOGGER.warn("Told to remove skill {} but I don't know what that is", resourcelocation);
         } else {
            remove(skill);
         }
      }
   }

   /**
    * Doing it this way, we ensure that skills are added in a descending order that
    * builds a hierachy by
    * making sure that a skill preresequite skills are added
    * 
    * @param skills
    */
   public void add(Map<ResourceLocation, Skill.Builder> skills) {
      Map<ResourceLocation, Skill.Builder> skillBuilders = new HashMap<>(skills);

      while (!skillBuilders.isEmpty()) {
         boolean newSkillAdded = false;
         Iterator<Map.Entry<ResourceLocation, Skill.Builder>> iterator = skillBuilders.entrySet().iterator();

         while (iterator.hasNext()) {
            Map.Entry<ResourceLocation, Skill.Builder> entry = iterator.next();
            ResourceLocation skillId = entry.getKey();
            Skill.Builder skillBuilder = entry.getValue();

            if (skillBuilder.canBuild(this.skillLookup::get)) {
               Skill skill = skillBuilder.build(skillId);
               add(skill);
               newSkillAdded = true;
               iterator.remove();
            }
         }

         if (!newSkillAdded) {
            for (Map.Entry<ResourceLocation, Skill.Builder> failed : skillBuilders.entrySet()) {
               LOGGER.error("Couldn't load skill {}: {}", failed.getKey(), failed.getValue());
            }
            break;
         }
      }

      LOGGER.info("Loaded {} skills", this.skillLookup.size());

   }

   public void add(Skill skill) {
      this.skillLookup.put(skill.getId(), skill);
      if (skill.getParent() == null) {
         this.trees.add(skill);
         if (this.listener != null) {
            this.listener.onTreeAdded(skill);
         }
      } else {
         this.skills.add(skill);
         if (this.listener != null) {
            this.listener.onAddSkill(skill);
         }
      }
   }

   public void clear() {
      skillLookup.clear();
      trees.clear();
      skills.clear();
      if (listener != null)
         listener.onSkillsCleared();
   }

   public void setListener(Listener listener) {
      this.listener = listener;

      if (listener != null) {
         for (Skill skill : getTrees()) {
            listener.onTreeAdded(skill);
         }
         for (Skill skill : getSkills()) {
            listener.onAddSkill(skill);
         }
      }
   }

   public Iterable<Skill> getTrees() {
      return trees;
   }

   public Collection<Skill> getAllSkills() {
      return skillLookup.values();
   }

   public Iterable<Skill> getSkills() {
      return skills;
   }

   // public Iterable<Skill> getSkills(Skill skillTree) {
   // if (skillTree == null || !trees.contains(skillTree))
   // return null;

   // Set<Skill> matches = new HashSet<>(skills);
   // matches.removeIf(skill -> skill.getTree() != skillTree);
   // return matches;
   // }

   public Skill get(ResourceLocation skillId) {
      return this.skillLookup.get(skillId);
   }

   public int size() {
      return this.skillLookup.entrySet().size();
   }

   public interface Listener {
      void onSelectedTreeChanged(Skill skill);

      void onTreeAdded(Skill skill);

      void onTreeRemoved(Skill skill);

      void onAddSkill(Skill skill);

      void onRemoveSkill(Skill skill);

      void onSkillsCleared();

      void onUpdateSkillProgress(Skill skill, SkillProgress progress);
   }
}
