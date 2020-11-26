package zdoctor.zskilltree.api.interfaces;

import com.google.gson.JsonElement;

public interface Deserializable {
    default void deserialize(JsonElement element) {

    }

    default void deserialize(JsonElement element, Object data) {
    }
}
