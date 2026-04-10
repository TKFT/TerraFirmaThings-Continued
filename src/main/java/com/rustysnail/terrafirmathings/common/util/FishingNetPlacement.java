package com.rustysnail.terrafirmathings.common.util;

import java.util.ArrayList;
import java.util.List;
import com.rustysnail.terrafirmathings.common.TFCThingsBlocks;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.block.FishingNetAnchorBlock;
import com.rustysnail.terrafirmathings.common.block.FishingNetBlock;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import net.dries007.tfc.common.fluids.TFCFluids;

public final class FishingNetPlacement
{

    private static final double FLOW_THRESHOLD = 0.05;

    public static @NotNull Result place(@NotNull ServerLevel level, @NotNull BlockPos a, @NotNull BlockPos b)
    {
        Plan plan = plan(level, a, b);
        if (plan.result.hasError()) return plan.result;

        int placed = 0;
        for (Segment segInfo : plan.toPlace)
        {
            BlockPos p = segInfo.pos();

            var fluidProp = FishingNetBlock.FLUID;
            FluidState fluidState = level.getFluidState(p);
            Fluid fluidForLogging = fluidState.getType();

            if (fluidForLogging == TFCFluids.RIVER_WATER.get())
            {
                fluidForLogging = Fluids.WATER;
            }

            var fluidKey = fluidProp.keyForOrEmpty(fluidForLogging);

            BlockState seg = TFCThingsBlocks.FISHING_NET.get().defaultBlockState()
                .setValue(FishingNetBlock.AXIS, plan.axis)
                .setValue(FishingNetBlock.PROFILE, segInfo.profile())
                .setValue(FishingNetBlock.BOW_SIDE, plan.bowSide)
                .setValue(fluidProp, fluidKey);

            level.setBlock(p, seg, 3);
            placed++;
        }

        final long linkId = level.getGameTime();
        final int minCoord = plan.axis == Direction.Axis.X ? Math.min(a.getX(), b.getX()) : Math.min(a.getZ(), b.getZ());
        final int maxCoord = plan.axis == Direction.Axis.X ? Math.max(a.getX(), b.getX()) : Math.max(a.getZ(), b.getZ());

        if (level.getBlockEntity(a) instanceof FishingNetAnchorBlockEntity beA)
        {
            beA.setLink(b, plan.axis, minCoord, maxCoord, plan.yTop, plan.yBottom, linkId);
        }
        if (level.getBlockEntity(b) instanceof FishingNetAnchorBlockEntity beB)
        {
            beB.setLink(a, plan.axis, minCoord, maxCoord, plan.yTop, plan.yBottom, linkId);
        }

        level.setBlock(a, level.getBlockState(a)
            .setValue(FishingNetAnchorBlock.AXIS, plan.axis)
            .setValue(FishingNetAnchorBlock.FLUID, FishingNetAnchorBlock.FLUID.keyFor(Fluids.EMPTY)), 3);
        level.setBlock(b, level.getBlockState(b)
            .setValue(FishingNetAnchorBlock.AXIS, plan.axis)
            .setValue(FishingNetAnchorBlock.FLUID, FishingNetAnchorBlock.FLUID.keyFor(Fluids.EMPTY)), 3);

        return new Result(Status.OK, plan.axis, plan.distance, plan.toPlace.size(), placed);
    }

    public static @NotNull Result preview(@NotNull ServerLevel level, @NotNull BlockPos a, @NotNull BlockPos b)
    {
        return plan(level, a, b).result;
    }

    private static @NotNull Plan plan(@NotNull ServerLevel level, @NotNull BlockPos a, @NotNull BlockPos b)
    {
        if (a.getY() != b.getY())
        {
            return new Plan(new Result(Status.NOT_SAME_Y, Direction.Axis.X, 0, 0, 0), Direction.Axis.X, false, 0, a.getY(), a.getY(), List.of());
        }

        if (a.getX() != b.getX() && a.getZ() != b.getZ())
        {
            return new Plan(new Result(Status.NOT_ALIGNED, Direction.Axis.X, 0, 0, 0), Direction.Axis.X, false, 0, a.getY(), a.getY(), List.of());
        }

        Direction.Axis axis = (a.getX() != b.getX()) ? Direction.Axis.X : Direction.Axis.Z;
        int dist = (axis == Direction.Axis.X) ? Math.abs(a.getX() - b.getX()) : Math.abs(a.getZ() - b.getZ());

        if (dist < 2 || dist > FishingNetUtil.MAX_SPAN)
        {
            return new Plan(new Result(Status.TOO_FAR, axis, dist, 0, 0), axis, false, dist, a.getY(), a.getY(), List.of());
        }

        int step = (axis == Direction.Axis.X) ? Integer.signum(b.getX() - a.getX()) : Integer.signum(b.getZ() - a.getZ());
        int yStart = a.getY() - 1;
        int yBottom = yStart;

        List<List<BlockPos>> columns = new ArrayList<>(dist - 1);
        double totalPerpFlow = 0.0;
        int flowSamples = 0;

        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();

        for (int i = 1; i < dist; i++)
        {
            int x = (axis == Direction.Axis.X) ? a.getX() + step * i : a.getX();
            int z = (axis == Direction.Axis.Z) ? a.getZ() + step * i : a.getZ();

            List<BlockPos> col = new ArrayList<>();

            for (int dy = 0; dy < FishingNetUtil.MAX_DEPTH; dy++)
            {
                int y = yStart - dy;
                if (y < level.getMinBuildHeight()) break;

                p.set(x, y, z);

                FluidState fs = level.getFluidState(p);
                if (!fs.is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE)) break;

                BlockState s = level.getBlockState(p);
                if (!s.isAir() && !s.canBeReplaced()) break;

                Vec3 flow = fs.getFlow(level, p);
                double perp = (axis == Direction.Axis.X) ? flow.z : flow.x;
                totalPerpFlow += perp;
                flowSamples++;

                col.add(p.immutable());
                yBottom = Math.min(yBottom, y);
            }

            columns.add(col);
        }

        boolean anyWater = columns.stream().anyMatch(col -> !col.isEmpty());
        if (!anyWater)
        {
            return new Plan(new Result(Status.NO_WATER, axis, dist, 0, 0), axis, false, dist, a.getY(), a.getY(), List.of());
        }

        double avgPerpFlow = flowSamples > 0 ? totalPerpFlow / flowSamples : 0.0;
        boolean hasMeaningfulFlow = Math.abs(avgPerpFlow) >= FLOW_THRESHOLD;
        boolean bowSide = avgPerpFlow >= 0.0;

        final var place = getSegments(columns, dist, hasMeaningfulFlow);

        return new Plan(new Result(Status.OK, axis, dist, place.size(), 0), axis, bowSide, dist, a.getY(), yBottom, place);
    }

    private static @NotNull List<Segment> getSegments(List<List<BlockPos>> columns, int dist, boolean hasMeaningfulFlow)
    {
        List<Segment> place = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++)
        {
            int colIndex = i + 1;
            int distFromEnd = Math.min(colIndex, dist - colIndex);

            FishingNetBlock.Profile profile = hasMeaningfulFlow
                ? switch (distFromEnd)
            {
                case 1 -> FishingNetBlock.Profile.END;
                case 2 -> FishingNetBlock.Profile.INNER_1;
                case 3 -> FishingNetBlock.Profile.INNER_2;
                case 4 -> FishingNetBlock.Profile.INNER_3;
                default -> FishingNetBlock.Profile.MIDDLE;
            }
                : FishingNetBlock.Profile.END;

            for (BlockPos pos : columns.get(i))
            {
                place.add(new Segment(pos, profile));
            }
        }
        return place;
    }

    private FishingNetPlacement() {}

    public enum Status
    {
        OK,
        NOT_ALIGNED,
        NOT_SAME_Y,
        TOO_FAR,
        NO_WATER,
        BLOCKED
    }

    public record Result(Status status, Direction.Axis axis, int distance, int requiredSegments, int placedSegments)
    {
        public boolean hasError() {return status != Status.OK;}
    }

    private record Segment(BlockPos pos, FishingNetBlock.Profile profile) {}

    private record Plan(Result result, Direction.Axis axis, boolean bowSide, int distance, int yTop, int yBottom, List<Segment> toPlace) {}
}
