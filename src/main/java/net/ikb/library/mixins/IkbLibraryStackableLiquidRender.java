package net.ikb.library.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.ikb.library.tags.IkbLibTags;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FluidRenderer.class)
public abstract class IkbLibraryStackableLiquidRender {

    @Shadow
    public static boolean shouldRenderSide(BlockRenderView world, BlockPos pos, FluidState fluidState, BlockState blockState, Direction direction, FluidState neighborFluidState) {
        return false;
    }

    @Unique
    private static boolean ikbLib$shouldRenderFace(BlockRenderView level, BlockPos pos, FluidState fluidState, BlockState selfState, Direction direction, BlockState otherState, boolean brineLike) {
        FluidState neighborFluid = otherState.getFluidState();
        if (brineLike) {
            if (direction == Direction.DOWN) return false;
            Fluid aboveFluid = level.getFluidState(pos.offset(direction).up()).getFluid();
            if (neighborFluid.getFluid().matchesType(aboveFluid)) return false;
            else if (aboveFluid.matchesType(fluidState.getFluid()))
                return shouldRenderSide(level, pos, fluidState, selfState, direction, ((FlowableFluid) aboveFluid).getFlowing(8, true).getBlockState());
        }

        return shouldRenderSide(level, pos, fluidState, selfState, direction, otherState);
    }

    @Unique
    private static boolean ikbLib$isBrineLike(FluidState here, FluidState there) {
        if (!isSameFluid(here, there)) {
            for (TagKey<Fluid> check : IkbLibTags.Fluids.stackableTags)
                if (here.is(check) && there.is(check))
                    if (here.getFluidType().getDensity() <= there.getFluidType().getDensity()) return true;
        }
        return false;
    }

    @WrapMethod(method = "render")
    public void ikbLib$render(BlockRenderView level, BlockPos pos, VertexConsumer buffer, BlockState blockState, FluidState fluidState, Operation<Void> original) {

        boolean isVirtual = !level.getFluidState(pos).equals(fluidState);

        TextureAtlasSprite[] atextureatlassprite = FluidSpriteCache.getFluidSprites(level, pos, fluidState);
        int tintColor = IClientFluidTypeExtensions.of(fluidState).getTintColor(fluidState, level, pos);
        float alpha = (float)(tintColor >> 24 & 255) / 255.0F;
        float red = (float)(tintColor >> 16 & 255) / 255.0F;
        float green = (float)(tintColor >> 8 & 255) / 255.0F;
        float blue = (float)(tintColor & 255) / 255.0F;

        BlockState blockU = level.getBlockState(pos.offset(Direction.UP));
        BlockState blockD = level.getBlockState(pos.offset(Direction.DOWN));
        FluidState fluidD = blockD.getFluidState();
        BlockState blockN = level.getBlockState(pos.offset(Direction.NORTH));
        FluidState fluidN = blockN.getFluidState();
        BlockState blockS = level.getBlockState(pos.offset(Direction.SOUTH));
        FluidState fluidS = blockS.getFluidState();
        BlockState blockE = level.getBlockState(pos.offset(Direction.EAST));
        FluidState fluidE = blockE.getFluidState();
        BlockState blockW = level.getBlockState(pos.offset(Direction.WEST));
        FluidState fluidW = blockW.getFluidState();

        BlockState blockHere = isVirtual ? level.getBlockState(pos) : blockState;
        FluidState fluidHere = isVirtual ? level.getFluidState(pos) : fluidState;

        boolean brineD = ikbLib$isBrineLike(fluidState, fluidD);
        boolean brineN = ikbLib$isBrineLike(fluidState, fluidN);
        boolean brineS = ikbLib$isBrineLike(fluidState, fluidS);
        boolean brineE = ikbLib$isBrineLike(fluidState, fluidE);
        boolean brineW = ikbLib$isBrineLike(fluidState, fluidW);

        boolean renderU = !isVirtual
                && !isNeighborStateHidingOverlay(fluidState, blockU, Direction.DOWN);
        boolean renderD = !isVirtual
                && ikbLib$shouldRenderFace(level, pos, fluidState, blockState, Direction.DOWN, blockD, brineD)
                && !isSideCovered(level, pos, Direction.DOWN, 0.8888889F, blockD);
        boolean renderN = ikbLib$shouldRenderFace(level, pos, fluidState, blockState, Direction.NORTH, blockN, brineN);
        boolean renderS = ikbLib$shouldRenderFace(level, pos, fluidState, blockState, Direction.SOUTH, blockS, brineS);
        boolean renderE = ikbLib$shouldRenderFace(level, pos, fluidState, blockState, Direction.EAST, blockE, brineE);
        boolean renderW = ikbLib$shouldRenderFace(level, pos, fluidState, blockState, Direction.WEST, blockW, brineW);

        if (renderU || renderD || renderE || renderW || renderN || renderS) {
            float shadeD = level.getShade(Direction.DOWN, true);
            float shadeU = level.getShade(Direction.UP, true);
            float shadeN = level.getShade(Direction.NORTH, true);
            float shadeW = level.getShade(Direction.WEST, true);
            Fluid fluid = fluidHere.getType();

            float heightHere = this.getFluidHeight(level, fluid, pos, blockHere, fluidHere);
            float heightNE;
            float heightNW;
            float heightSE;
            float heightSW;
            float blockX;
            float blockY;
            float blockZ;
            if (heightHere >= 1.0F && !isVirtual) {
                heightNE = 1.0F;
                heightNW = 1.0F;
                heightSE = 1.0F;
                heightSW = 1.0F;
            } else {
                float heightN = this.getFluidHeight(level, fluid, pos.north(), blockN, fluidN);
                float heightS = this.getFluidHeight(level, fluid, pos.south(), blockS, fluidS);
                float heightE = this.getFluidHeight(level, fluid, pos.east(), blockE, fluidE);
                float heightW = this.getFluidHeight(level, fluid, pos.west(), blockW, fluidW);
                heightNE = this.calculateFluidHeight(level, fluid, heightHere, heightN, heightE, pos.offset(Direction.NORTH).offset(Direction.EAST));
                heightNW = this.calculateFluidHeight(level, fluid, heightHere, heightN, heightW, pos.offset(Direction.NORTH).offset(Direction.WEST));
                heightSE = this.calculateFluidHeight(level, fluid, heightHere, heightS, heightE, pos.offset(Direction.SOUTH).offset(Direction.EAST));
                heightSW = this.calculateFluidHeight(level, fluid, heightHere, heightS, heightW, pos.offset(Direction.SOUTH).offset(Direction.WEST));
            }

            blockX = (float)(pos.getX() & 15);
            blockY = (float)(pos.getY() & 15);
            blockZ = (float)(pos.getZ() & 15);
            float heightD = renderD ? 0.001F : 0.0F;
            float uNW;
            float uSW;
            float uSE;
            float uNE;
            float vNW;
            float vSW;
            float vSE;
            float vNE;
            float renderR;
            float renderG;
            float renderB;
            if (renderU && !isSideCovered(level, pos, Direction.UP, Math.min(Math.min(heightNW, heightSW), Math.min(heightSE, heightNE)), blockU)) {
                heightNW -= 0.001F;
                heightSW -= 0.001F;
                heightSE -= 0.001F;
                heightNE -= 0.001F;
                Vec3 vec3 = fluidState.getFlow(level, pos);
                TextureAtlasSprite textureatlassprite;
                if (vec3.x == 0.0 && vec3.z == 0.0) {
                    textureatlassprite = atextureatlassprite[0];
                    uNW = textureatlassprite.getU(0.0F);
                    vNW = textureatlassprite.getV(0.0F);
                    uSW = uNW;
                    vSW = textureatlassprite.getV(1.0F);
                    uSE = textureatlassprite.getU(1.0F);
                    vSE = vSW;
                    uNE = uSE;
                    vNE = vNW;
                } else {
                    textureatlassprite = atextureatlassprite[1];
                    float flowAngle = (float) Mth.atan2(vec3.z, vec3.x) - 1.5707964F;
                    float flowSin = Mth.sin(flowAngle) * 0.25F;
                    float flowCos = Mth.cos(flowAngle) * 0.25F;
                    uNW = textureatlassprite.getU(0.5F + (-flowCos - flowSin));
                    vNW = textureatlassprite.getV(0.5F + (-flowCos + flowSin));
                    uSW = textureatlassprite.getU(0.5F + (-flowCos + flowSin));
                    vSW = textureatlassprite.getV(0.5F + (flowCos + flowSin));
                    uSE = textureatlassprite.getU(0.5F + (flowCos + flowSin));
                    vSE = textureatlassprite.getV(0.5F + (flowCos - flowSin));
                    uNE = textureatlassprite.getU(0.5F + (flowCos - flowSin));
                    vNE = textureatlassprite.getV(0.5F + (-flowCos - flowSin));
                }

                float uCenter = (uNW + uSW + uSE + uNE) / 4.0F;
                float vCenter = (vNW + vSW + vSE + vNE) / 4.0F;
                float uvShrink = atextureatlassprite[0].uvShrinkRatio();
                uNW = Mth.lerp(uvShrink, uNW, uCenter);
                uSW = Mth.lerp(uvShrink, uSW, uCenter);
                uSE = Mth.lerp(uvShrink, uSE, uCenter);
                uNE = Mth.lerp(uvShrink, uNE, uCenter);
                vNW = Mth.lerp(uvShrink, vNW, vCenter);
                vSW = Mth.lerp(uvShrink, vSW, vCenter);
                vSE = Mth.lerp(uvShrink, vSE, vCenter);
                vNE = Mth.lerp(uvShrink, vNE, vCenter);
                int l = this.getLight(level, pos);
                renderR = shadeU * red;
                renderG = shadeU * green;
                renderB = shadeU * blue;
                this.vertex(buffer, blockX + 0.0F, blockY + heightNW, blockZ + 0.0F, renderR, renderG, renderB, alpha, uNW, vNW, l);
                this.vertex(buffer, blockX + 0.0F, blockY + heightSW, blockZ + 1.0F, renderR, renderG, renderB, alpha, uSW, vSW, l);
                this.vertex(buffer, blockX + 1.0F, blockY + heightSE, blockZ + 1.0F, renderR, renderG, renderB, alpha, uSE, vSE, l);
                this.vertex(buffer, blockX + 1.0F, blockY + heightNE, blockZ + 0.0F, renderR, renderG, renderB, alpha, uNE, vNE, l);
                if (fluidState.shouldRenderBackwardUpFace(level, pos.above())) {
                    this.vertex(buffer, blockX + 0.0F, blockY + heightNW, blockZ + 0.0F, renderR, renderG, renderB, alpha, uNW, vNW, l);
                    this.vertex(buffer, blockX + 1.0F, blockY + heightNE, blockZ + 0.0F, renderR, renderG, renderB, alpha, uNE, vNE, l);
                    this.vertex(buffer, blockX + 1.0F, blockY + heightSE, blockZ + 1.0F, renderR, renderG, renderB, alpha, uSE, vSE, l);
                    this.vertex(buffer, blockX + 0.0F, blockY + heightSW, blockZ + 1.0F, renderR, renderG, renderB, alpha, uSW, vSW, l);
                }
            }

            if (renderD) {
                float u0 = atextureatlassprite[0].getU0();
                float u1 = atextureatlassprite[0].getU1();
                float v0 = atextureatlassprite[0].getV0();
                float v1 = atextureatlassprite[0].getV1();
                int k = this.getLight(level, pos.below());
                renderR = shadeD * red;
                renderG = shadeD * green;
                renderB = shadeD * blue;
                this.vertex(buffer, blockX, blockY + heightD, blockZ + 1.0F, renderR, renderG, renderB, alpha, u0, v1, k);
                this.vertex(buffer, blockX, blockY + heightD, blockZ, renderR, renderG, renderB, alpha, u0, v0, k);
                this.vertex(buffer, blockX + 1.0F, blockY + heightD, blockZ, renderR, renderG, renderB, alpha, u1, v0, k);
                this.vertex(buffer, blockX + 1.0F, blockY + heightD, blockZ + 1.0F, renderR, renderG, renderB, alpha, u1, v1, k);
            }

            int j = this.getLight(level, pos);

            for(Direction direction : Direction.Plane.HORIZONTAL) {
                //facing toward the thing

                float yTL;
                float yTR;
                float yBL;
                float yBR;
                float x0;
                float x1;
                float z0;
                float z1;

                boolean renderFace;
                switch (direction) {
                    case NORTH:
                        x0 = blockX;
                        x1 = blockX + 1.0F;
                        z0 = blockZ + 0.001F;
                        z1 = blockZ + 0.001F;
                        renderFace = renderN;
                        if (isVirtual) {
                            yTR = 1.0F;
                            yTL = 1.0F;
                            yBR = heightNW;
                            yBL = heightNE;
                        } else if (brineN) {
                            yTR = heightNW;
                            yTL = heightNE;
                            fluid = fluidN.getType();
                            float brineHeightA = this.getFluidHeight(level, fluid, pos.north(), blockN, fluidN);
                            float brineHeightL = this.getFluidHeight(level, fluid, pos.north().east());
                            float brineHeightR = this.getFluidHeight(level, fluid, pos.north().west());
                            float brineHeightH = this.getFluidHeight(level, fluid, pos);
                            yBL = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightL, brineHeightH, pos.east());
                            yBR = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightR, brineHeightH, pos.west());
                        } else {
                            yTR = heightNW;
                            yTL = heightNE;
                            yBL = heightD;
                            yBR = heightD;
                        }
                        break;
                    case SOUTH:
                        x0 = blockX + 1.0F;
                        x1 = blockX;
                        z0 = blockZ + 1.0F - 0.001F;
                        z1 = blockZ + 1.0F - 0.001F;
                        renderFace = renderS;
                        if (isVirtual) {
                            yTR = 1.0F;
                            yTL = 1.0F;
                            yBR = heightSE;
                            yBL = heightSW;
                        } else if (brineS) {
                            yTR = heightSE;
                            yTL = heightSW;
                            fluid = fluidS.getType();
                            float brineHeightA = this.getFluidHeight(level, fluid, pos.south(), blockS, fluidS);
                            float brineHeightL = this.getFluidHeight(level, fluid, pos.south().west());
                            float brineHeightR = this.getFluidHeight(level, fluid, pos.south().east());
                            float brineHeightH = this.getFluidHeight(level, fluid, pos);
                            yBL = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightL, brineHeightH, pos.west());
                            yBR = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightR, brineHeightH, pos.east());
                        } else {
                            yTR = heightSE;
                            yTL = heightSW;
                            yBL = heightD;
                            yBR = heightD;
                        }
                        break;
                    case WEST:
                        x0 = blockX + 0.001F;
                        x1 = blockX + 0.001F;
                        z0 = blockZ + 1.0F;
                        z1 = blockZ;
                        renderFace = renderW;
                        if (isVirtual) {
                            yTR = 1.0F;
                            yTL = 1.0F;
                            yBR = heightSW;
                            yBL = heightNW;
                        } else if (brineW) {
                            yTR = heightSW;
                            yTL = heightNW;
                            fluid = fluidW.getType();
                            float brineHeightA = this.getFluidHeight(level, fluid, pos.north(), blockW, fluidW);
                            float brineHeightL = this.getFluidHeight(level, fluid, pos.north().west());
                            float brineHeightR = this.getFluidHeight(level, fluid, pos.south().west());
                            float brineHeightH = this.getFluidHeight(level, fluid, pos);
                            yBL = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightL, brineHeightH, pos.north());
                            yBR = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightR, brineHeightH, pos.south());
                        } else {
                            yTR = heightSW;
                            yTL = heightNW;
                            yBL = heightD;
                            yBR = heightD;
                        }
                        break;
                    default:
                        x0 = blockX + 1.0F - 0.001F;
                        x1 = blockX + 1.0F - 0.001F;
                        z0 = blockZ;
                        z1 = blockZ + 1.0F;
                        renderFace = renderE;
                        if (isVirtual) {
                            yTR = 1.0F;
                            yTL = 1.0F;
                            yBR = heightNE;
                            yBL = heightSE;
                        } else if (brineE) {
                            yTR = heightNE;
                            yTL = heightSE;
                            fluid = fluidE.getType();
                            float brineHeightA = this.getFluidHeight(level, fluid, pos.north(), blockE, fluidE);
                            float brineHeightL = this.getFluidHeight(level, fluid, pos.south().east());
                            float brineHeightR = this.getFluidHeight(level, fluid, pos.north().east());
                            float brineHeightH = this.getFluidHeight(level, fluid, pos);
                            yBL = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightL, brineHeightH, pos.south());
                            yBR = this.calculateFluidHeight(level, fluid, brineHeightA, brineHeightR, brineHeightH, pos.north());
                        } else {
                            yTR = heightNE;
                            yTL = heightSE;
                            yBL = heightD;
                            yBR = heightD;
                        }
                }

                if (yBL >= yTL && yBR >= yTR) renderFace = false;

                boolean flagX = (yBL > yTL || yBR > yTR) && renderFace;

                if (renderFace
                        && (isVirtual ? !ikbLib$isLowFaceOccludedByNeighbor(level, pos, direction, Math.min(yBL, yBR), level.getBlockState(pos.offset(direction))) : !isSideCovered(level, pos, direction, Math.max(yTL, yTR), level.getBlockState(pos.offset(direction))))) {
                    BlockPos blockpos = pos.offset(direction);
                    TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
                    if (atextureatlassprite[2] != null
                            && level.getBlockState(blockpos).shouldDisplayFluidOverlay(level, blockpos, fluidState)) {
                        textureatlassprite2 = atextureatlassprite[2];
                    }

                    float shade = direction.getAxis() == Direction.Axis.Z ? shadeN : shadeW;
                    renderR = shadeU * shade * red;
                    renderG = shadeU * shade * green;
                    renderB = shadeU * shade * blue;

                    float u0 = textureatlassprite2.getU(0.5F);
                    float u1 = textureatlassprite2.getU(0.0F);

                    if (flagX) {
                        Vec2 point;
                        switch (direction) {
                            case NORTH:
                                point = ikbLib$intersect(x0, blockY + yTR, x1, blockY + yTL, x1, blockY + yBL, x0, blockY + yBR);
                                if (yBL >= yTL) {
                                    x1 = point.x;
                                    u1 = textureatlassprite2.getU((1.0F - (x1 - blockX)) * 0.5F);
                                    yBL = point.y - blockY;
                                    yTL = yBL;
                                } else {
                                    x0 = point.x;
                                    u0 = textureatlassprite2.getU((1.0F - (x0 - blockX)) * 0.5F);
                                    yBR = point.y - blockY;
                                    yTR = yBR;
                                }
                                break;
                            case SOUTH:
                                point = ikbLib$intersect(x0, blockY + yTR, x1, blockY + yTL, x1, blockY + yBL, x0, blockY + yBR);
                                if (yBL >= yTL) {
                                    x1 = point.x;
                                    u1 = textureatlassprite2.getU(((x1 - blockX)) * 0.5F);
                                    yBL = point.y - blockY;
                                    yTL = yBL;
                                } else {
                                    x0 = point.x;
                                    u0 = textureatlassprite2.getU(((x0 - blockX)) * 0.5F);
                                    yBR = point.y - blockY;
                                    yTR = yBR;
                                }
                                break;
                            case WEST:
                                point = ikbLib$intersect(z0, blockY + yTR, z1, blockY + yTL, z1, blockY + yBL, z0, blockY + yBR);
                                if (yBL >= yTL) {
                                    z1 = point.x;
                                    u1 = textureatlassprite2.getU(((z1 - blockZ)) * 0.5F);
                                    yBL = point.y - blockY;
                                    yTL = yBL;
                                } else {
                                    z0 = point.x;
                                    u0 = textureatlassprite2.getU(((z0 - blockZ)) * 0.5F);
                                    yBR = point.y - blockY;
                                    yTR = yBR;
                                }
                                break;
                            default:
                                point = ikbLib$intersect(z0, blockY + yTR, z1, blockY + yTL, z1, blockY + yBL, z0, blockY + yBR);
                                if (yBL >= yTL) {
                                    z1 = point.x;
                                    u1 = textureatlassprite2.getU((1.0F - (z1 - blockZ)) * 0.5F);
                                    yBL = point.y - blockY;
                                    yTL = yBL;
                                } else {
                                    z0 = point.x;
                                    u0 = textureatlassprite2.getU((1.0F - (z0 - blockZ)) * 0.5F);
                                    yBR = point.y - blockY;
                                    yTR = yBR;
                                }
                                break;
                        }
                    }


                    float vTL = textureatlassprite2.getV((1.0F - yTL) * 0.5F);
                    float vTR = textureatlassprite2.getV((1.0F - yTR) * 0.5F);
                    float vBL = textureatlassprite2.getV((1.0F - yBL) * 0.5F);
                    float vBR = textureatlassprite2.getV((1.0F - yBR) * 0.5F);
                    this.vertex(buffer, x0, blockY + yTR, z0, renderR, renderG, renderB, alpha, u0, vTR, j);
                    this.vertex(buffer, x1, blockY + yTL, z1, renderR, renderG, renderB, alpha, u1, vTL, j);
                    this.vertex(buffer, x1, blockY + yBL, z1, renderR, renderG, renderB, alpha, u1, vBL, j);
                    this.vertex(buffer, x0, blockY + yBR, z0, renderR, renderG, renderB, alpha, u0, vBR, j);
                    if (textureatlassprite2 != atextureatlassprite[2]) {
                        this.vertex(buffer, x0, blockY + yBR, z0, renderR, renderG, renderB, alpha, u0, vBR, j);
                        this.vertex(buffer, x1, blockY + yBL, z1, renderR, renderG, renderB, alpha, u1, vBL, j);
                        this.vertex(buffer, x1, blockY + yTL, z1, renderR, renderG, renderB, alpha, u1, vTL, j);
                        this.vertex(buffer, x0, blockY + yTR, z0, renderR, renderG, renderB, alpha, u0, vTR, j);
                    }
                }
            }
        }

    }

    @Unique
    private boolean ikbLib$isLowFaceOccludedByNeighbor(BlockRenderView level, BlockPos pos, Direction face, float bottom, BlockState state) {
        if (state.canOcclude()) {
            VoxelShape voxelshape = Shapes.box(0.0, bottom, 0.0, 1.0, 1.0, 1.0);
            VoxelShape voxelshape1 = state.getOcclusionShape(level, pos.offset(face));
            return Shapes.blockOccudes(voxelshape, voxelshape1, face);
        } else return false;
    }

    @Unique
    private Vec2 ikbLib$intersect(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float m1 = (y1 - y2) / (x1 - x2);
        float m3 = (y3 - y4) / (x3 - x4);
        float x = ((x1 * m1) - (x3 * m3) + y3 - y1) / (m1 - m3);
        float y = (x * m1) - (x1 * m1) + y1;

        return new Vec2(x,y);

    }

}
