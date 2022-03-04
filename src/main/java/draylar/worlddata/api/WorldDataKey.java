package draylar.worlddata.api;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public record WorldDataKey<T extends WorldData>(Identifier id, Function<ServerWorld, T> supplier) {
    T create(ServerWorld world) {
        return supplier.apply(world);
    }

    public T get(ServerWorld world) {
        return WorldData.getData(world, this);
    }
}
