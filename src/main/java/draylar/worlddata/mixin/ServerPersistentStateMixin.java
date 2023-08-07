package draylar.worlddata.mixin;

import draylar.worlddata.api.WorldDataKey;
import draylar.worlddata.api.WorldDataRegistry;
import draylar.worlddata.api.WorldDataState;
import draylar.worlddata.impl.WorldDataAccessor;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.MutableWorldProperties;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Mixin(ServerWorld.class)
public abstract class ServerPersistentStateMixin extends World implements WorldDataAccessor {

    @Shadow public abstract PersistentStateManager getPersistentStateManager();
    @Unique private WorldDataState state;

    private ServerPersistentStateMixin(MutableWorldProperties properties, RegistryKey<World> registryRef, DynamicRegistryManager registryManager, RegistryEntry<DimensionType> dimensionEntry, Supplier<Profiler> profiler, boolean isClient, boolean debugWorld, long biomeAccess, int maxChainedNeighborUpdates) {
        super(properties, registryRef, registryManager, dimensionEntry, profiler, isClient, debugWorld, biomeAccess, maxChainedNeighborUpdates);
    }

    @Inject(
            method = "<init>",
            at = @At("RETURN"))
    private void initializeWorldDataProviders(
            MinecraftServer server,
            Executor workerExecutor,
            LevelStorage.Session session,
            ServerWorldProperties properties,
            RegistryKey<?> worldKey,
            DimensionOptions dimensionOptions,
            WorldGenerationProgressListener worldGenerationProgressListener,
            boolean debugWorld,
            long seed,
            List<?> spawners,
            boolean shouldTickTime,
            RandomSequencesState randomSequencesState,
            CallbackInfo ci
    ) {
        state = getPersistentStateManager().getOrCreate(
                compound -> WorldDataState.readNbt((ServerWorld) (Object) this, compound),
                () -> new WorldDataState((ServerWorld) (Object) this),
                WorldDataState.nameFor(getDimensionEntry()));

        // Add State trackers based on "this" world's type.
        // The Overlord should always exist, so we use it to store 'global' (server-wide) data.
        // Register global data suppliers now.
        if(worldKey == World.OVERWORLD) {
            WorldDataRegistry.getGlobalSuppliers().forEach((key, supplier) -> {

                // Only register the given key if it does not exist yet.
                if(!state.getData().containsKey(key)) {
                    state.add((WorldDataKey) key, supplier.apply((ServerWorld) (Object) this));
                }
            });
        }

        // Register per-world data suppliers.
        WorldDataRegistry.getWorldSuppliers().forEach((key, supplier) -> {

            // Only register the given key if it does not exist yet.
            if(!state.getData().containsKey(key)) {
                state.add((WorldDataKey) key, supplier.apply((ServerWorld) (Object) this));
            }
        });

    }

    @Override
    public WorldDataState worldData$getState() {
        return state;
    }
}
