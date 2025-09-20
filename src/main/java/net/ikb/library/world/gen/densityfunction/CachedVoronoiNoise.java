package net.ikb.library.world.gen.densityfunction;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.HashMap;

public class CachedVoronoiNoise {

    public static CachedVoronoiNoise create(long seed) {
        return new CachedVoronoiNoise(seed);
    }

    CachedVoronoiNoise(long seed) {
        this.seed = seed;
    }

    public long seed;

    private final HashMap<Vec3i, VoronoiPlate> MEMOIZED_PLATES = new HashMap<>(); //vector is the plate index

    HashMap<DensityFunction.FunctionContext, double[]> memoDists = new HashMap<>();
    HashMap<DensityFunction.FunctionContext, double[]> memoValues = new HashMap<>();
    HashMap<DensityFunction.FunctionContext, double[]> memoVelocities = new HashMap<>();
    HashMap<DensityFunction.FunctionContext, double[]> memoPassives = new HashMap<>();

    public double[] getDistances(DensityFunction.FunctionContext blockPos, boolean flat, double scale, double jitter, int metric, int maxCheck) {
        if (memoDists.containsKey(blockPos)) {
            double[] val = memoDists.get(blockPos);
            return val != null ? val : getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 0);
        } else return getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 0);
    }

    public double[] getValues(DensityFunction.FunctionContext blockPos, boolean flat, double scale, double jitter, int metric, int maxCheck) {
        if (memoValues.containsKey(blockPos)) {
            double[] val = memoValues.get(blockPos);
            return val != null ? val : getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 1);
        } else return getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 1);
    }

    public double[] getVelocities(DensityFunction.FunctionContext blockPos, boolean flat, double scale, double jitter, int metric, int maxCheck) {
        if (memoVelocities.containsKey(blockPos)) {
            double[] val = memoVelocities.get(blockPos);
            return val != null ? val : getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 2);
        } else return getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 2);
    }

    public double[] getPassives(DensityFunction.FunctionContext blockPos, boolean flat, double scale, double jitter, int metric, int maxCheck) {
        if (memoPassives.containsKey(blockPos)) {
            double[] val = memoPassives.get(blockPos);
            return val != null ? val : getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 3);
        } else return getVoronoi(blockPos, flat, scale, jitter, metric, maxCheck, 3);
    }

    private double[] getVoronoi(DensityFunction.FunctionContext blockPos, boolean flat, double scale, double jitter, int metric, int maxCheck, int mode) {

        double x = ((double) blockPos.blockX()) / scale;
        double y = flat ? 0 : ((double) blockPos.blockY()) / scale;
        double z = ((double) blockPos.blockZ()) / scale;

        Vec3i posIndex = new Vec3i(
                (int) (x >= 0 ? x + 0.5 : x - 0.5),
                (int) (y >= 0 ? y + 0.5 : y - 0.5),
                (int) (z >= 0 ? z + 0.5 : z - 0.5)
        );

        VoronoiPlate[] sortPlates = new VoronoiPlate[maxCheck];

        double[] sortDistances = new double[maxCheck];
        double[] sortValues = new double[maxCheck];
        double[] sortVelocities = new double[maxCheck];
        double[] sortPassives = new double[maxCheck];

        Arrays.fill(sortDistances, Double.MAX_VALUE);

        for (int xi = -1; xi <= 1; xi++) {
            for (int zi = -1; zi <= 1; zi++) {
                Vec3i checkIndex = new Vec3i(posIndex.getX() + xi, posIndex.getY(), posIndex.getZ() + zi);
                if (!this.MEMOIZED_PLATES.containsKey(checkIndex))
                    this.MEMOIZED_PLATES.put(checkIndex, new VoronoiPlate(seed, checkIndex, jitter));
                VoronoiPlate checkPlate = this.MEMOIZED_PLATES.get(checkIndex);
                double checkDistance = checkPlate.getDist(new Vec3(x, y, z), flat, metric);

                for (int i = 0; i < maxCheck; i++) {
                    if (checkDistance < sortDistances[i]) {
                        for (int j = maxCheck - 1; j > i; j--) {
                            if (sortPlates[j - 1] != null) {
                                sortDistances[j] = sortDistances[j - 1];
                                sortPlates[j] = sortPlates[j - 1];
                            }
                        }
                        sortDistances[i] = checkDistance;
                        sortPlates[i] = checkPlate;
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < maxCheck; i++) {
            sortValues[i] = sortPlates[i].getValue();
            sortVelocities[i] = i == 0 ? 0 : sortPlates[0].relativeVelocity(sortPlates[i]);
            sortPassives[i] = i == 0 ? 1 : sortPlates[0].velocity() == sortPlates[i].velocity() ? 1 : 0;
        }

        memoDists.put(blockPos, sortDistances);
        memoValues.put(blockPos, sortValues);
        memoVelocities.put(blockPos, sortVelocities);
        return switch (mode) {
            default -> sortDistances;
            case 1 -> sortValues;
            case 2 -> sortVelocities;
            case 3 -> sortPassives;
        };
    }


}
