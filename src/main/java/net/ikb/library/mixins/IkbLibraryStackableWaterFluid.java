package net.ikb.library.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ikb.library.tags.IkbLibTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.WaterFluid;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WaterFluid.class)
public abstract class IkbLibraryStackableWaterFluid extends FlowingFluid {

    @WrapMethod(method = "canBeReplacedWith")
    public boolean ikbLib$replaceFFWithStackable(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluidIn, Direction direction, Operation<Boolean> original) {
        if (!this.isSame(fluidIn))
            for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                if (fluidState.is(check) && fluidIn.defaultFluidState().is(check))
                    return (direction == Direction.UP ? fluidIn.getFluidType().getDensity() <= fluidState.getFluidType().getDensity() : fluidIn.getFluidType().getDensity() >= fluidState.getFluidType().getDensity())
                            && original.call(fluidState, blockReader, pos, fluidIn, direction);

        return original.call(fluidState, blockReader, pos, fluidIn, direction);
    }

}
