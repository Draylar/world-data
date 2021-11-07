package draylar.worlddata.api;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class WorldDataRegistry {

    private static final Map<WorldDataKey<?>, Function<ServerWorld, WorldData>> WORLD_SUPPLIERS = new HashMap<>();
    private static final Map<WorldDataKey<?>, Function<ServerWorld, WorldData>> GLOBAL_SUPPLIERS = new HashMap<>();

    public static <T extends WorldData> WorldDataKey<T> register(Identifier id, Function<ServerWorld, T> supplier) {
        WorldDataKey<T> key = new WorldDataKey<>(id, supplier);
        WORLD_SUPPLIERS.put(key, (Function<ServerWorld, WorldData>) supplier);
        return key;
    }

    public static <T extends WorldData> WorldDataKey<T> registerGlobal(Identifier id, Function<ServerWorld, T> supplier) {
        WorldDataKey<T> key = new WorldDataKey<>(id, supplier);
        GLOBAL_SUPPLIERS.put(key, (Function<ServerWorld, WorldData>) supplier);
        return key;
    }

    public static Map<WorldDataKey<?>, Function<ServerWorld, WorldData>> getWorldSuppliers() {
        return WORLD_SUPPLIERS;
    }

    public static Map<WorldDataKey<?>, Function<ServerWorld, WorldData>> getGlobalSuppliers() {
        return GLOBAL_SUPPLIERS;
    }

    @Nullable
    public static WorldDataKey<?> findById(Identifier id) {
        for (Map.Entry<WorldDataKey<?>, Function<ServerWorld, WorldData>> entry : WORLD_SUPPLIERS.entrySet()) {
            if (entry.getKey().id().equals(id)) {
                return entry.getKey();
            }
        }

        for (Map.Entry<WorldDataKey<?>, Function<ServerWorld, WorldData>> entry : GLOBAL_SUPPLIERS.entrySet()) {
            if (entry.getKey().id().equals(id)) {
                return entry.getKey();
            }
        }

        return null;
    }
}
