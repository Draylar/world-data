package draylar.worlddata.api;

import draylar.worlddata.impl.WorldDataAccessor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

/**
 * Represents arbitrary data attached to a {@link World}.
 */
public interface WorldData {

    /**
     * Serializes this data into a {@link NbtCompound}.
     *
     * @param root a mutable tag to write data into
     */
    void writeNbt(NbtCompound root);

    /**
     * Deserializes data into this instance from the given {@link NbtCompound}.
     *
     * @param root tag to read data from
     */
    void readNbt(NbtCompound root);

    /**
     * @return the {@link World} associated with this {@link WorldData} instance.
     */
    World getWorld();

    default void markDirty() {
        ((WorldDataAccessor) getWorld()).postMateria_getWorldDataState().markDirty();
    }

    static <T extends WorldData> T getGlobalData(MinecraftServer server, WorldDataKey<T> key) {
        return ((WorldDataAccessor) server.getWorld(World.OVERWORLD)).postMateria_getWorldDataState().get(key);
    }

    static <T extends WorldData> T getData(ServerWorld world, WorldDataKey<T> key) {
        return ((WorldDataAccessor) world).postMateria_getWorldDataState().get(key);
    }

//    default Self get(ServerWorld world) {
//        return getData(world, this);
//    }
}
