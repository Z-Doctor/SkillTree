package zdoctor.zskilltree.skilltree.data.providers;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.skilltree.skill.Skill;
import zdoctor.zskilltree.skilltree.data.generators.SkillGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SkillProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<Skill>>> skills = ImmutableList.of(new SkillGenerator());

    public SkillProvider(DataGenerator generator) {
        this.generator = generator;
    }


    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        Consumer<Skill> consumer = skill -> {
            if (!set.add(skill.getRegistryName())) {
                throw new IllegalStateException("Duplicate skill " + skill.getRegistryName());
            } else {
                LOGGER.info("Added skill: " + skill);
                Path path1 = getPath(path, skill);
// TODO Fix
//                try {
//                    IDataProvider.save(GSON, cache, skill.copy(), path1);
//                } catch (IOException ioexception) {
//                    LOGGER.error("Couldn't save skill page {}", path1, ioexception);
//                }

            }
        };

        skills.forEach(skillConsumer -> skillConsumer.accept(consumer));
    }

    private static Path getPath(Path pathIn, Skill skillIn) {
        return pathIn.resolve("data/" + skillIn.getRegistryName().getNamespace() + "/skills/" +
                skillIn.getRegistryName().getPath() + "/" + skillIn.getRegistryName().getPath() + ".json");
    }

    @Override
    public String getName() {
        return "Skill";
    }
}
