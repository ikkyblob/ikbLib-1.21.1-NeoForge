package net.ikb.library.mixins;


import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.ikb.library.tags.IkbLibTags;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.SectionBufferBuilderPack;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(SectionCompiler.class)
public abstract class IkbLibraryStackableSectionCompiler {

    @Shadow protected abstract BufferBuilder getOrBeginLayer(Map<RenderType, BufferBuilder> bufferLayers, SectionBufferBuilderPack sectionBufferBuilderPack, RenderType renderType);

    @WrapOperation(
            method = "Lnet/minecraft/client/renderer/chunk/SectionCompiler;compile(Lnet/minecraft/core/SectionPos;Lnet/minecraft/client/renderer/chunk/RenderChunkRegion;Lcom/mojang/blaze3d/vertex/VertexSorting;Lnet/minecraft/client/renderer/SectionBufferBuilderPack;Ljava/util/List;)Lnet/minecraft/client/renderer/chunk/SectionCompiler$Results;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderLiquid(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/material/FluidState;)V"
            )
    )
    private void ikbLib$triggerVirtualLayer(
            BlockRenderDispatcher instance,
            BlockPos blockpos2,
            BlockAndTintGetter region,
            VertexConsumer bufferbuilder,
            BlockState blockstate,
            FluidState fluidstate,
            Operation<Void> original,
            @Local(argsOnly = true) SectionBufferBuilderPack sectionBufferBuilderPack,
            @Local Map<RenderType, BufferBuilder> map
    ) {
        FluidState aboveState = region.getFluidState(blockpos2.above());
        if (!fluidstate.getType().isSame(aboveState.getType()))
            for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                if (fluidstate.is(check) && aboveState.is(check)
                        && fluidstate.getFluidType().getDensity() >= aboveState.getFluidType().getDensity()) {
                    aboveState = ((FlowingFluid) aboveState.getType()).getFlowing(8, true);
                    RenderType rendertype2 = ItemBlockRenderTypes.getRenderLayer(aboveState);
                    BufferBuilder bufferbuilder2 = this.getOrBeginLayer(map, sectionBufferBuilderPack, rendertype2);
                    original.call(instance, blockpos2, region, bufferbuilder2, aboveState.createLegacyBlock(), aboveState);
                }
        original.call(instance, blockpos2, region, bufferbuilder, blockstate, fluidstate);
    }

}
