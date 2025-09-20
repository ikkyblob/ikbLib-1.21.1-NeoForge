package net.ikb.library.mixins;

import com.google.gson.internal.LinkedTreeMap;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import net.ikb.library.tags.IkbLibTags;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.Set;

@Mixin(Entity.class)
public abstract class IkbLibraryStackableFluidOnEyes {

    @Shadow private FluidType forgeFluidTypeOnEyes;
    @Shadow public abstract Level level();

    @Inject(method = "updateFluidOnEyes", at = @At(value = "TAIL"))
    private void ikbLib$updateFluidOnEyes(CallbackInfo ci, @Local(ordinal = 0) double d0, @Local(ordinal = 1) double d1, @Local BlockPos blockpos, @Local FluidState fluidstate) {
        if (d1 <= d0) {
            FluidState aboveFluid = level().getFluidState(blockpos.above());
            if (!fluidstate.getType().isSame(aboveFluid.getType()))
                for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                    if (fluidstate.is(check) && aboveFluid.is(check)) this.forgeFluidTypeOnEyes = aboveFluid.getFluidType();
        }
    }

}
