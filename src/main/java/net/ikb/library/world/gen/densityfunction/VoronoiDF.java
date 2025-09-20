package net.ikb.library.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class VoronoiDF implements SeededDensityFunction {

    private static final MapCodec<VoronoiDF> MAP_CODEC = RecordCodecBuilder.mapCodec((instance) ->
            instance.group(
                    Codec.LONG.optionalFieldOf("salt", 0L).forGetter((input) -> input.salt),
                    Codec.BOOL.optionalFieldOf("flat", true).forGetter((input) -> input.flat),
                    Codec.DOUBLE.fieldOf("scale").forGetter((input) -> input.scale),
                    Codec.doubleRange(0.0,0.5).optionalFieldOf("jitter", 0.4).forGetter((input) -> input.jitter),
                    Codec.intRange(0,5).optionalFieldOf("metric", 1).forGetter((input) -> input.metric),
                    Codec.intRange(0,2).optionalFieldOf("mode", 0).forGetter((input) -> input.mode),
                    Codec.intRange(1,9).optionalFieldOf("ordinal", 1).forGetter((input) -> input.ordinal)
            ).apply(instance, VoronoiDF::new)
    );

    public static final KeyDispatchDataCodec<VoronoiDF> CODEC = KeyDispatchDataCodec.of(MAP_CODEC);

    @Nullable
    public VoronoiNoise noise = null;
    private final long salt;
    private final boolean flat;
    private final double scale;
    private final double jitter;
    private final int metric;
    private final int mode;
    private final int ordinal;

    public VoronoiDF(long salt, boolean flat, double scale, double jitter, int metric, int mode, int ordinal) {
        this.salt = salt;
        this.flat = flat;
        this.scale = scale;
        this.jitter = jitter;
        this.metric = metric;
        this.mode = mode;
        this.ordinal = ordinal;
    }

    @Override
    public double compute(FunctionContext pos) {
        if (this.noise == null) {
            throw new NullPointerException("VoronoiDF not initialized");
        } else return this.noise.getVoronoi(pos, this.flat, this.scale, this.jitter, this.metric, this.mode, this.ordinal);
    }

    @Override
    public void fillArray(double[] doubles, ContextProvider contextProvider) {
        contextProvider.fillAllDirectly(doubles, this);
    }

    @Override
    public DensityFunction mapAll(Visitor visitor) {
        return visitor.apply(this);
    }

    @Override
    public double minValue() {
        return Double.MIN_VALUE;
    }

    @Override
    public double maxValue() {
        return Double.MAX_VALUE;
    }

    @Override
    public KeyDispatchDataCodec<? extends DensityFunction> codec() {
        return CODEC;
    }

    @Override
    public VoronoiDF initialize(long levelSeed) {
        this.noise = VoronoiNoise.create(levelSeed + this.salt);
        return this;
    }

    public boolean initialized() {
        return this.noise != null;
    }


}
