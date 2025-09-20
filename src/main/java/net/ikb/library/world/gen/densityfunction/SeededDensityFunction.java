package net.ikb.library.world.gen.densityfunction;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.util.KeyDispatchDataCodec;
import net.minecraft.world.level.levelgen.DensityFunction;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.UnaryOperator;

public interface SeededDensityFunction extends DensityFunction {

    boolean initialized();

    DensityFunction initialize(long levelSeed);

}
