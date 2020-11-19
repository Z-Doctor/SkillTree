package zdoctor.zskilltree.network;

import net.minecraft.network.PacketBuffer;
import zdoctor.zskilltree.api.annotations.ClassNameMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NetworkSerializationRegistry {
    private static final Map<Class<?>, Map<String, Function<PacketBuffer, ?>>> CLASS_MAPPER = new HashMap<>();

    public static <T> Map<String, Function<PacketBuffer, T>> registerMapping(Class<T> key) {
        CLASS_MAPPER.putIfAbsent(key, new HashMap<>());
        return (Map<String, Function<PacketBuffer, T>>) (Object) CLASS_MAPPER.get(key);
    }


    public static <T, R> boolean register(Class<T> key, Function<PacketBuffer, R> reader, Class<R> returnType) {
        if (!CLASS_MAPPER.containsKey(returnType))
            return false;
        ClassNameMapper mapping = key.getAnnotation(ClassNameMapper.class);
        if(mapping == null)
            CLASS_MAPPER.get(returnType).put(key.getSimpleName(), reader);
        else
            CLASS_MAPPER.get(returnType).putIfAbsent(mapping.mapping(), reader);
        return true;
    }

    public static <T> Map<String, Function<PacketBuffer, T>> findRegistry(Class<T> key) {
        return (Map<String, Function<PacketBuffer, T>>) (Object) CLASS_MAPPER.get(key);
    }
}
