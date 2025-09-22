package net.ikb.library.world.gen.densityfunction;

import net.minecraft.world.level.levelgen.DensityFunction;

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
