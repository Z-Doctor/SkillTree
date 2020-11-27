package zdoctor.zskilltree.network;

import net.minecraft.network.PacketBuffer;
import zdoctor.zskilltree.api.annotations.ClassNameMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "unused"})
public class NetworkSerializationRegistry {
    private static final Map<Class<?>, Map<String, Function<PacketBuffer, ?>>> CLASS_MAPPER = new HashMap<>();

    public static <T> Map<String, Function<PacketBuffer, T>> registerMapping(Class<T> key) {
        CLASS_MAPPER.putIfAbsent(key, new HashMap<>());
        return (Map<String, Function<PacketBuffer, T>>) (Object) CLASS_MAPPER.get(key);
    }


    public static <T, R> boolean register(Class<T> key, Function<PacketBuffer, R> reader, Class<R> mapKey) {
        if (!CLASS_MAPPER.containsKey(mapKey))
            return false;
        ClassNameMapper mapping = key.getAnnotation(ClassNameMapper.class);
        if (mapping == null)
            CLASS_MAPPER.get(mapKey).put(key.getSimpleName(), reader);
        else
            CLASS_MAPPER.get(mapKey).putIfAbsent(mapping.key(), reader);
        return true;
    }

    public static <T> boolean register(String key, Function<PacketBuffer, T> reader, Class<T> mapKey) {
        if (!CLASS_MAPPER.containsKey(mapKey))
            return false;
        CLASS_MAPPER.get(mapKey).putIfAbsent(key, reader);
        return true;
    }

    public static <T> Map<String, Function<PacketBuffer, T>> findRegistry(Class<T> key) {
        return (Map<String, Function<PacketBuffer, T>>) (Object) CLASS_MAPPER.get(key);
    }
}
