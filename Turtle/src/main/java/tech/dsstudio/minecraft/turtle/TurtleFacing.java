package tech.dsstudio.minecraft.turtle;

import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public enum TurtleFacing {
	NORTH(0, 0, -1),
	SOUTH(0, 0, 1),
	EAST(1, 0, 0),
	WEST(-1, 0, 0);

	TurtleFacing(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public static TurtleFacing fromYaw(float yaw) {
		if (yaw >= -45.0 && yaw < 45.0) {
			return SOUTH;
		} else if (yaw > 45.0 && yaw < 135.0) {
			return WEST;
		} else if (yaw > 135.0 || yaw < -135.0) {
			return NORTH;
		} else {
			return EAST;
		}
	}

	public Vector toVector() {
		return new Vector(x, y, z);
	}

	public EulerAngle toEulerAngle() {
		return new EulerAngle(Math.PI / 2 * x, Math.PI / 2 * y, Math.PI / 2 * z);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	private int x, y, z;
}
