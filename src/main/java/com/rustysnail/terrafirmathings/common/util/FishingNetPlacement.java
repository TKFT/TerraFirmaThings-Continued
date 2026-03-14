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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;

public final class FishingNetPlacement
{

    public static @NotNull Result place(@NotNull ServerLevel level, @NotNull BlockPos a, @NotNull BlockPos b)
    {
        Plan plan = plan(level, a, b);
        if (plan.result.hasError()) return plan.result;

        int placed = 0;
        for (Segment segInfo : plan.toPlace)
        {
            BlockPos p = segInfo.pos();

            var fluidProp = FishingNetBlock.FLUID;
            var fluidKey = fluidProp.keyFor(Fluids.WATER);

            BlockState seg = TFCThingsBlocks.FISHING_NET.get().defaultBlockState()
                .setValue(FishingNetBlock.AXIS, plan.axis)
                .setValue(FishingNetBlock.SAG, segInfo.sag())
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
            return new Plan(new Result(Status.NOT_SAME_Y, Direction.Axis.X, 0, 0, 0), Direction.Axis.X, 0, a.getY(), a.getY(), List.of());
        }

        if (a.getX() != b.getX() && a.getZ() != b.getZ())
        {
            return new Plan(new Result(Status.NOT_ALIGNED, Direction.Axis.X, 0, 0, 0), Direction.Axis.X, 0, a.getY(), a.getY(), List.of());
        }

        Direction.Axis axis = (a.getX() != b.getX()) ? Direction.Axis.X : Direction.Axis.Z;
        int dist = (axis == Direction.Axis.X) ? Math.abs(a.getX() - b.getX()) : Math.abs(a.getZ() - b.getZ());

        if (dist < 2 || dist > FishingNetUtil.MAX_SPAN)
        {
            return new Plan(new Result(Status.TOO_FAR, axis, dist, 0, 0), axis, dist, a.getY(), a.getY(), List.of());
        }

        int step = (axis == Direction.Axis.X) ? Integer.signum(b.getX() - a.getX()) : Integer.signum(b.getZ() - a.getZ());
        int yStart = a.getY() - 1;
        int yBottom = yStart;

        List<Segment> place = new ArrayList<>();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos();

        for (int i = 1; i < dist; i++)
        {
            int x = (axis == Direction.Axis.X) ? a.getX() + step * i : a.getX();
            int z = (axis == Direction.Axis.Z) ? a.getZ() + step * i : a.getZ();

            double f = Math.sin(Math.PI * i / (double) dist);
            int sag = (int) Math.round(f * 7.0);

            for (int dy = 0; dy < FishingNetUtil.MAX_DEPTH; dy++)
            {
                int y = yStart - dy;
                if (y < level.getMinBuildHeight()) break;

                p.set(x, y, z);

                FluidState fs = level.getFluidState(p);
                if (!fs.is(TFCThingsTags.Fluids.FISHING_NET_PLACEABLE))
                {
                    break;
                }

                BlockState s = level.getBlockState(p);
                if (!s.isAir() && !s.canBeReplaced())
                {
                    break;
                }

                place.add(new Segment(p.immutable(), sag));
                yBottom = Math.min(yBottom, y);
            }
        }

        if (place.isEmpty())
        {
            return new Plan(new Result(Status.NO_WATER, axis, dist, 0, 0), axis, dist, a.getY(), a.getY(), List.of());
        }

        return new Plan(new Result(Status.OK, axis, dist, place.size(), 0), axis, dist, a.getY(), yBottom, place);
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

    private record Segment(BlockPos pos, int sag) {}

    private record Plan(Result result, Direction.Axis axis, int distance, int yTop, int yBottom, List<Segment> toPlace) {}
}
