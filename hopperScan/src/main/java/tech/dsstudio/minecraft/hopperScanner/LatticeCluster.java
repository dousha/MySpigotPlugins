package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class LatticeCluster {
	public void addHopperCluster(HopperCluster cluster) {
		clusters.add(cluster);
	}

	public boolean tryAddHopperCluster(HopperCluster cluster) {
		if (cluster.getClusterCenter().distanceSquared(getClusterCenter()) <= clusterSnapLimit) {
			addHopperCluster(cluster);
			return true;
		} else {
			return false;
		}
	}

	public Vector getClusterCenter() {
		if (clusters.isEmpty())
			return null;
		else {
			AtomicReference<Vector> vector = new AtomicReference<>();
			clusters.forEach(it -> {
				if (vector.get() == null) {
					vector.set(it.getClusterCenter());
				} else {
					Vector v = it.getClusterCenter().subtract(vector.get());
					vector.set(vector.get().add(v));
				}
			});
			return vector.get();
		}
	}

	public long countHoppers() {
		AtomicLong count = new AtomicLong(0);
		clusters.forEach(it -> count.addAndGet(it.countHoppers()));
		return count.get();
	}

	public static void notifyUpdate() {
		clusterSnapLimit = Config.getValue("clusterSnapDistance");
	}

	private HashSet<HopperCluster> clusters = new HashSet<>();
	private static double clusterSnapLimit = 90000.0; // 300.0 ^ 2
}
