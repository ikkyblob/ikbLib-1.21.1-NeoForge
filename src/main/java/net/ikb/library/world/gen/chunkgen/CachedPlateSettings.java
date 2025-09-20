package net.ikb.library.world.gen.chunkgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record CachedPlateSettings(long seed, boolean flat, double scale, double jitter, int metric, int maxCheck) {
    public static final Codec<CachedPlateSettings> CODEC = RecordCodecBuilder.create((instance) ->
                instance.group(
                        Codec.LONG.fieldOf("salt").forGetter(CachedPlateSettings::seed),
                        Codec.BOOL.optionalFieldOf("flat", true).forGetter(CachedPlateSettings::flat),
                        Codec.DOUBLE.fieldOf("scale").forGetter(CachedPlateSettings::scale),
                        Codec.doubleRange(0.0, 0.5).optionalFieldOf("jitter", 0.4).forGetter(CachedPlateSettings::jitter),
                        Codec.intRange(0, 5).optionalFieldOf("metric", 1).forGetter(CachedPlateSettings::metric),
                        Codec.intRange(1, 9).optionalFieldOf("maxCheck", 3).forGetter(CachedPlateSettings::maxCheck)
                ).apply(instance, CachedPlateSettings::new)
    );

}