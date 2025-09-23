package net.ikb.library.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.ikb.library.tags.IkbLibTags;
import net.minecraft.client.Camera;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class ikbLibraryStackableFluidFog {

    @Shadow private BlockGetter level;
    @Shadow private Vec3 position = Vec3.ZERO;
    @Shadow private final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();

    @WrapOperation(
            method = "getFluidInCamera",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/BlockGetter;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;", ordinal = 0))
    public FluidState ikbLib$getStackableFluidInCamera(BlockGetter instance, BlockPos pos, Operation<FluidState> original) {
        FluidState fluidState = original.call(instance, pos);
        if (this.position.y >= (double) ((float) this.blockPosition.getY() + fluidState.getHeight(level, this.blockPosition))) {
            FluidState aboveState = level.getFluidState(this.blockPosition.above());
            if (!fluidState.getType().isSame(aboveState.getType()))
                for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                    if (fluidState.is(check) && aboveState.is(check))
                        return aboveState;
        }
        return fluidState;
    }

}
