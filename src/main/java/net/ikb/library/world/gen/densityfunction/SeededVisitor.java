package net.ikb.library.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public record SeededVisitor(long levelSeed) implements DensityFunction.Visitor {
    @Override
    public DensityFunction apply(DensityFunction function) {
        return function instanceof SeededDensityFunction seededDF ?
                seededDF.initialized() ?
                        seededDF
                        : seededDF.initialize(levelSeed)
                : function;
    }

    public long getSeed() {
        return levelSeed;
    }

}
