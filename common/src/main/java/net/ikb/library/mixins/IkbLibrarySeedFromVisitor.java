package net.ikb.library.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ikb.library.world.gen.densityfunction.SeededVisitor;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RandomState.class)
public abstract class IkbLibrarySeedFromVisitor {

    @WrapOperation(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/NoiseRouter;mapAll(Lnet/minecraft/world/level/levelgen/DensityFunction$Visitor;)Lnet/minecraft/world/level/levelgen/NoiseRouter;"))
    private NoiseRouter ikblib$seedVisitor(NoiseRouter instance, DensityFunction.Visitor visitor, Operation<NoiseRouter> original, @Local(argsOnly = true) NoiseGeneratorSettings settings, @Local(argsOnly = true) final long levelSeed) {
        return original.call(instance, visitor).mapAll(new SeededVisitor(levelSeed));
    }

}
