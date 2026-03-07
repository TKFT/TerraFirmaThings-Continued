package com.rustysnail.terrafirmathings.common.util;

import com.rustysnail.terrafirmathings.common.block.FishingNetBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class FishingNetUtil
{

    public static final int MAX_SPAN = 32;
    private static final int MAX_DEPTH = 20;

    public static int removeConnectedNets(Level level, BlockPos anchorPos, Direction.Axis axis)
    {
        int count = 0;

        Direction dir1 = axis == Direction.Axis.X ? Direction.EAST : Direction.SOUTH;
        Direction dir2 = dir1.getOpposite();

        count += removeNetsInDirection(level, anchorPos, axis, dir1);
        count += removeNetsInDirection(level, anchorPos, axis, dir2);

        return count;
    }

    private static int removeNetsInDirection(Level level, BlockPos anchorPos, Direction.Axis axis, Direction direction)
    {
        int count = 0;
        int yStart = anchorPos.getY() - 1;
        BlockPos.MutableBlockPos pos = anchorPos.mutable();

        for (int i = 0; i < MAX_SPAN; i++)
        {
            pos.move(direction);
            boolean foundInColumn = false;

            for (int dy = 0; dy < MAX_DEPTH; dy++)
            {
                BlockPos checkPos = new BlockPos(pos.getX(), yStart - dy, pos.getZ());
                BlockState state = level.getBlockState(checkPos);

                if (state.getBlock() instanceof FishingNetBlock && state.getValue(FishingNetBlock.AXIS) == axis)
                {
                    level.removeBlock(checkPos, false);
                    count++;
                    foundInColumn = true;
                }
                else
                {
                    break;
                }
            }

            if (!foundInColumn)
            {
                break;
            }
        }

        return count;
    }

    private FishingNetUtil() {}
}
