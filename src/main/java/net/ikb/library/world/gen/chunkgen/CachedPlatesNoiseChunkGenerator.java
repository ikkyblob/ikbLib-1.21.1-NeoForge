package net.ikb.library.world.gen.chunkgen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;

public class CachedPlatesNoiseChunkGenerator extends NoiseBasedChunkGenerator {
    public static final MapCodec<CachedPlatesNoiseChunkGenerator> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    BiomeSource.CODEC.fieldOf("biome_source").forGetter(getBiome -> getBiome.biomeSource),
                    NoiseGeneratorSettings.CODEC.fieldOf("noise_settings").forGetter(getNoise -> getNoise.noiseSettings),
                    CachedPlateSettings.CODEC.fieldOf("plate_settings").forGetter(getPlates -> getPlates.plateSettings)
            )
            .apply(instance, (CachedPlatesNoiseChunkGenerator::new))
    );
    private final Holder<NoiseGeneratorSettings> noiseSettings;
    private final CachedPlateSettings plateSettings;


    public CachedPlatesNoiseChunkGenerator(BiomeSource biomeSource, Holder<NoiseGeneratorSettings> noiseSettings, CachedPlateSettings plateSettings) {
        super(biomeSource, noiseSettings);
        this.noiseSettings = noiseSettings;
        this.plateSettings = plateSettings;
    }

}
