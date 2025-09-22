package net.ikb.library.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.CodecHolder;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import org.jetbrains.annotations.Nullable;

public class CachedVoronoiDF implements SeededDensityFunction {

    private static final MapCodec<CachedVoronoiDF> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    Codec.LONG.optionalFieldOf("salt", 0L).forGetter((input) -> input.salt),
                    Codec.BOOL.optionalFieldOf("flat", true).forGetter((input) -> input.flat),
                    Codec.DOUBLE.fieldOf("scale").forGetter((input) -> input.scale),
                    Codec.doubleRange(0.0,0.5).optionalFieldOf("jitter", 0.4).forGetter((input) -> input.jitter),
                    Codec.intRange(0,5).optionalFieldOf("metric", 1).forGetter((input) -> input.metric),
                    Codec.intRange(1,9).optionalFieldOf("maxCheck", 3).forGetter((input) -> input.maxCheck)
            ).apply(instance, (CachedVoronoiDF::new))
    );

    public static final CodecHolder<CachedVoronoiDF> CODEC = CodecHolder.of(MAP_CODEC);

    @Nullable
    public CachedVoronoiNoise noise = null;
    private final long salt;
    private final boolean flat;
    private final double scale;
    private final double jitter;
    private final int metric;
    private final int maxCheck;

    public CachedVoronoiDF(long salt, boolean flat, double scale, double jitter, int metric, int maxCheck) {
        this.salt = salt;
        this.flat = flat;
        this.scale = scale;
        this.jitter = jitter;
        this.metric = metric;
        this.maxCheck = maxCheck;
    }

    @Override
    public double sample(NoisePos pos) {
        return 0;
    }

    public double getDistance(NoisePos pos, int index) {
        return this.maxCheck > index && this.noise != null ? this.noise.getDistances(pos, flat, scale, jitter, metric, maxCheck)[index] : 0;
    }

    public double getValue(NoisePos pos, int index) {
        return this.maxCheck > index && this.noise != null ? this.noise.getValues(pos, flat, scale, jitter, metric, maxCheck)[index] : 0;
    }

    public double getVelocity(NoisePos pos, int index) {
        return this.maxCheck > index && this.noise != null ? this.noise.getVelocities(pos, flat, scale, jitter, metric, maxCheck)[index] : 0;
    }

    public double getPassive(NoisePos pos, int index) {
        return this.maxCheck > index && this.noise != null ? this.noise.getPassives(pos, flat, scale, jitter, metric, maxCheck)[index] : 0;
    }

    @Override
    public void fill(double[] doubles, EachApplier eachApplier) {
        eachApplier.fill(doubles, this);
    }

    @Override
    public DensityFunction apply(DensityFunctionVisitor visitor) {
        return visitor.apply(this);
    }

    @Override
    public double minValue() {
        return 0;
    }

    @Override
    public double maxValue() {
        return 0;
    }

    @Override
    public CodecHolder<? extends DensityFunction> getCodecHolder() {
        return CODEC;
    }

    @Override
    public CachedVoronoiDF initialize(long levelSeed) {
        this.noise = CachedVoronoiNoise.create(levelSeed + this.salt);
        return this;
    }

    public boolean initialized() {return this.noise != null;}

}
