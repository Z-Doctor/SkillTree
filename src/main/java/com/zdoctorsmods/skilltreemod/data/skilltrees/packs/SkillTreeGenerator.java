package com.zdoctorsmods.skilltreemod.data.skilltrees.packs;

import java.util.List;

import com.zdoctorsmods.skilltreemod.data.skilltrees.SkillTreeProvider;

import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SkillTreeGenerator {
   // TODO Register a starting skill tab view
   public static SkillTreeProvider create(PackOutput pOutput) {
      return new SkillTreeProvider(pOutput, null, List.of());
   }

   // TODO Test out generation
   @SubscribeEvent
   public static void addSkillGenerator(GatherDataEvent event) {
      event.getGenerator().addProvider(event.includeServer(),
            new SkillTreeProvider(event.getGenerator().getPackOutput(), event.getLookupProvider(), List.of()));
   }
}
