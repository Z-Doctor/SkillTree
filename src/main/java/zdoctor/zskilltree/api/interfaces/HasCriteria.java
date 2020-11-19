package zdoctor.zskilltree.api.interfaces;

import net.minecraft.advancements.Criterion;

import java.util.Map;

public interface HasCriteria {
    Map<String, Criterion> getCriteria();

    String[][] getRequirements();
}
