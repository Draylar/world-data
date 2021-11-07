package draylar.worlddata.api;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.PersistentState;
import net.minecraft.world.dimension.DimensionType;

import java.util.HashMap;
import java.util.Map;

public class WorldDataState extends PersistentState {

    private final ServerWorld world;
    private final Map<WorldDataKey<?>, WorldData> data = new HashMap<>();

    public WorldDataState(ServerWorld world) {
        this.world = world;
    }

    public WorldDataState(ServerWorld world, Map<WorldDataKey<?>, WorldData> data) {
        this(world);
        this.data.putAll(data);
    }

    public <T extends WorldData> void add(WorldDataKey<T> key, T data) {
        this.data.put(key, data);
    }

    public static WorldDataState readNbt(ServerWorld world, NbtCompound nbt) {
        NbtList allData = nbt.getList("AllData", NbtType.COMPOUND);
        Map<WorldDataKey<?>, WorldData> read = new HashMap<>();

        allData.forEach(element -> {
            NbtCompound compoundElement = (NbtCompound) element;

            // find the first matching key for the ID
            Identifier id = new Identifier(compoundElement.getString("Type"));
            WorldDataKey<?> key = WorldDataRegistry.findById(id);
            if(key != null) {
                WorldData data = key.create(world);
                data.readNbt((NbtCompound) compoundElement.get("Data"));
                read.put(key, data);
            }
        });

        return new WorldDataState(world, read);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList allData = new NbtList();

        // Write each individual data tag!
        data.forEach((key, data) -> {
            NbtCompound inner = new NbtCompound();
            inner.putString("Type", key.id().toString());

            // Write data to a new tag.
            NbtCompound dataTag = new NbtCompound();
            data.writeNbt(dataTag);

            // Write under the Data key.
            inner.put("Data", dataTag);
            allData.add(inner);
        });

        nbt.put("AllData", allData);
        return nbt;
    }

    public static String nameFor(DimensionType dimension) {
        return "worlddata" + dimension.getSuffix();
    }

    /**
     *
     * @param key
     * @param <T>
     * @return
     * @throws IllegalArgumentException if this world state does not supply data with the specified key
     */
    public <T extends WorldData> T get(WorldDataKey<T> key) {
        return (T) data.get(key);
    }

    public Map<WorldDataKey<?>, WorldData> getData() {
        return data;
    }
}
