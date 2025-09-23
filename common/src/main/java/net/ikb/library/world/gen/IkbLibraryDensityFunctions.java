package net.ikb.library.world.gen;

import com.mojang.serialization.MapCodec;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.DeferredSupplier;
import dev.architectury.registry.registries.RegistrySupplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.ikb.library.IkbLibrary;
import net.ikb.library.world.gen.densityfunction.CachedVoronoiDF;
import net.ikb.library.world.gen.densityfunction.PullFromCachedVoronoiDF;
import net.ikb.library.world.gen.densityfunction.VoronoiDF;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.function.Supplier;

@Environment(EnvType.SERVER)
public class IkbLibraryDensityFunctions {

    public static final DeferredRegister<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPES =
            DeferredRegister.create(IkbLibrary.MODID, Registries.DENSITY_FUNCTION_TYPE);

    public static DeferredSupplier<MapCodec<? extends DensityFunction>> CACHED_VORONOI;
    public static DeferredSupplier<MapCodec<? extends DensityFunction>> VORONOI;
    public static DeferredSupplier<MapCodec<? extends DensityFunction>> PULL_CACHED_VORONOI;

    public static void register() {
        CACHED_VORONOI = registerFunction("cached_voronoi", CachedVoronoiDF.CODEC::codec);
        VORONOI = registerFunction("voronoi", VoronoiDF.CODEC::codec);
        PULL_CACHED_VORONOI = registerFunction("pull_cached_voronoi", PullFromCachedVoronoiDF.CODEC::codec);
    }

    private static RegistrySupplier<MapCodec<? extends DensityFunction>> registerFunction(String name, Supplier<MapCodec<? extends DensityFunction>> function) {
        return DENSITY_FUNCTION_TYPES.register(ResourceLocation.fromNamespaceAndPath(IkbLibrary.MODID, name), function);
    }

}
