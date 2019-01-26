package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class HopperCluster {
	public void addHopper(BlockState hopper) {
		hoppers.add(hopper);
	}

	public boolean tryAddHopper(BlockState hopper) {
		if (hopper.getBlock().getLocation().distanceSquared(getClusterCenter().toLocation(hopper.getWorld())) <= hopperSnapLimit) {
			addHopper(hopper);
			return true;
		} else {
			return false;
		}
	}

	public Vector getClusterCenter() {
		if (hoppers.isEmpty())
			return null;
		else {
			AtomicReference<Vector> vector = new AtomicReference<>();
			hoppers.forEach(it -> {
				if (vector.get() == null) {
					vector.set(it.getLocation().toVector());
				} else {
					Vector v = it.getLocation().toVector().subtract(vector.get());
					vector.set(vector.get().add(v));
				}
			});
			return vector.get();
		}
	}

	public long countHoppers() {
		return hoppers.size();
	}

	public static void notifyUpdate() {
		hopperSnapLimit = Config.getValue("hopperSnapDistance");
	}

	private HashSet<BlockState> hoppers = new HashSet<>();
	private static double hopperSnapLimit = 100.0; // 10.0 ^ 2
}
