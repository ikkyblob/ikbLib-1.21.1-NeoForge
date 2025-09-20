package net.ikb.library.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.ikb.library.tags.IkbLibTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FlowingFluid.class)
public abstract class IkbLibraryStackableFlowingFluid extends Fluid {

    @WrapMethod(method = "canPassThrough")
    public boolean ikbLib$passThroughStackable(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, Direction direction, BlockPos spreadPos, BlockState spreadState, FluidState fluidState, Operation<Boolean> original) {
        if (!this.isSame(fluidState.getType()))
            for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                if (fluidState.is(check) && fluid.defaultFluidState().is(check))
                    return (direction == Direction.UP ? fluid.getFluidType().getDensity() <= fluidState.getFluidType().getDensity() : fluid.getFluidType().getDensity() >= fluidState.getFluidType().getDensity())
                        && original.call(level, fluid, pos, state, direction, spreadPos, spreadState, fluidState);

        return original.call(level, fluid, pos, state, direction, spreadPos, spreadState, fluidState);
    }

    @WrapMethod(method = "isWaterHole")
    public boolean ikbLib$waterHoleStackable(BlockGetter level, Fluid fluid, BlockPos pos, BlockState state, BlockPos spreadPos, BlockState spreadState, Operation<Boolean> original) {
        if (!this.isSame(spreadState.getFluidState().getType()) && fluid.getFluidType().getDensity() <= spreadState.getFluidState().getFluidType().getDensity())
            for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                if (spreadState.getFluidState().is(check) && fluid.defaultFluidState().is(check))
                    return !(spreadState.getFluidState().isSource() || spreadState.getFluidState().getAmount() >= 7)
                        && original.call(level, fluid, pos, state, spreadPos, spreadState);

        return original.call(level, fluid, pos, state, spreadPos, spreadState);
    }

}
