package com.zdoctorsmods.skilltreemod.data.skilltrees;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

// TODO Implement provide for generation
public class SkillTreeProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;
    private final List<SkillTreeSubProvider> subProviders;
    // private final CompletableFuture<HolderLookup.Provider> registries;

    public SkillTreeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries,
            List<SkillTreeSubProvider> pSubProviders) {
        this.pathProvider = pOutput.createPathProvider(PackOutput.Target.DATA_PACK, "skilltrees");
        this.subProviders = pSubProviders;
        // this.registries = pRegistries;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return new CompletableFuture<>();
        // return this.registries.thenCompose((provider) -> {
        // Set<ResourceLocation> set = new HashSet<>();
        // List<CompletableFuture<?>> list = new ArrayList<>();
        // Consumer<SkillTree> consumer = (skillTree) -> {
        // if (!set.add(skillTree.getId())) {
        // throw new IllegalStateException("Duplicate skill tree " + skillTree.getId());
        // } else {
        // Path path = this.pathProvider.json(skillTree.getId());
        // list.add(DataProvider.saveStable(pOutput,
        // skillTree.deconstruct().serializeToJson(), path));
        // }
        // };

        // for (SkillTreeSubProvider skilltreesubprovider : this.subProviders) {
        // skilltreesubprovider.generate(provider, consumer);
        // }

        // return CompletableFuture.allOf(list.toArray((p_253393_) -> {
        // return new CompletableFuture[p_253393_];
        // }));
        // });
    }

    @Override
    public String getName() {
        return "Skill Trees";
    }

}
