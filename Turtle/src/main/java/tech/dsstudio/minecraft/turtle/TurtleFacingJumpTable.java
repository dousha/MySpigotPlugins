package tech.dsstudio.minecraft.turtle;

import org.jetbrains.annotations.NotNull;

public class TurtleFacingJumpTable {
	public static TurtleFacing turnLeft(@NotNull TurtleFacing current) {
		switch (current) {
			case NORTH:
				return TurtleFacing.WEST;
			case SOUTH:
				return TurtleFacing.EAST;
			case EAST:
				return TurtleFacing.NORTH;
			case WEST:
				return TurtleFacing.SOUTH;
		}
		return TurtleFacing.NORTH;
	}

	public static TurtleFacing turnRight(@NotNull TurtleFacing current) {
		switch (current) {
			case NORTH:
				return TurtleFacing.EAST;
			case SOUTH:
				return TurtleFacing.WEST;
			case EAST:
				return TurtleFacing.SOUTH;
			case WEST:
				return TurtleFacing.NORTH;
		}
		return TurtleFacing.SOUTH;
	}
}
