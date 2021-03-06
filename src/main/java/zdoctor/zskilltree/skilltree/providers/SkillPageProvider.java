package zdoctor.zskilltree.skilltree.providers;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import zdoctor.zskilltree.skilltree.criterion.SkillPage;
import zdoctor.zskilltree.skilltree.generators.SkillPageGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class SkillPageProvider implements IDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
    private final DataGenerator generator;
    private final List<Consumer<Consumer<SkillPage>>> pages = ImmutableList.of(new SkillPageGenerator());

    public SkillPageProvider(DataGenerator generator) {
        this.generator = generator;
    }

    private static Path getPath(Path pathIn, SkillPage pageIn) {
        return pathIn.resolve("data/" + pageIn.getRegistryName().getNamespace() + "/pages/" + pageIn.getRegistryName().getPath() + ".json");
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        Path path = this.generator.getOutputFolder();
        Set<ResourceLocation> set = new HashSet<>();
        Consumer<SkillPage> consumer = page -> {
            if (!set.add(page.getRegistryName())) {
                throw new IllegalStateException("Duplicate skill page " + page.getRegistryName());
            } else {
                LOGGER.info("Added Page: " + page);
                Path path1 = getPath(path, page);

                try {
                    IDataProvider.save(GSON, cache, page.serialize(), path1);
                } catch (IOException ioexception) {
                    LOGGER.error("Couldn't save skill page {}", path1, ioexception);
                }

            }
        };

        pages.forEach(consumerConsumer -> consumerConsumer.accept(consumer));
    }

    @Override
    public String getName() {
        return "Skill Pages";
    }
}
