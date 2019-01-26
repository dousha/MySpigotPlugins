package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.block.BlockState;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class Lattice {
	public Lattice(HopperScanner plugin) {
		father = plugin;
	}

	public void addHopper(BlockState hopper) {
		hopperCount.incrementAndGet();
		if (clusters.stream().noneMatch(it -> it.tryAddHopper(hopper))) {
			HopperCluster hopperCluster = new HopperCluster();
			hopperCluster.addHopper(hopper);
			clusters.add(hopperCluster);
		}
	}

	public void setEstimatedChunkCount(long count) {
		estimatedChunkCount = count;
	}

	public void increaseScannedChunkCount() {
		scannedCount.incrementAndGet();
	}

	public Long getScannedChunkCount() {
		return scannedCount.get();
	}

	public Long getEstimatedChunkCount() {
		return estimatedChunkCount;
	}

	public void clearAll() {
		clusters.clear();
		latticeClusters.clear();
		result.clear();
		estimatedChunkCount = 0;
		scannedCount.set(0);
		hopperCount.set(0);
	}

	public void analyze() {
		HopperScanner.annoy(String.format("正在对地图内的 %d 个漏斗进行分析", hopperCount.get()));
		Thread analyzer = new Thread(() -> {
			HopperScanner.annoy("分析中... (1/3)");
			clusters.forEach(cluster -> {
				if (latticeClusters.stream().noneMatch(it -> it.tryAddHopperCluster(cluster))) {
					LatticeCluster latticeCluster = new LatticeCluster();
					latticeCluster.addHopperCluster(cluster);
					latticeClusters.add(latticeCluster);
				}
			});
			HopperScanner.annoy("分析中... (2/3)");
			latticeClusters.stream().filter(it -> it.countHoppers() >= threshold).forEach(it -> {
				Vector v = it.getClusterCenter();
				result.add(String.format("%d %d %d", v.getBlockX(), v.getBlockY(), v.getBlockZ()));
			});
			HopperScanner.annoy("分析中... (3/3)");
			if (result.size() < 1) {
				HopperScanner.annoy("分析完成，没有发现漏斗滥用");
			} else {
				HopperScanner.annoy("下列位置附近有大量漏斗");
				result.forEach(HopperScanner::annoy);
				HopperScanner.annoy("可用 /hlast 重新查看");
			}
			father.dumpLog(result);
			HopperScanner.done();
		});
		analyzer.start();
	}

	public ArrayList<String> getLastResult() {
		return result;
	}

	public static void notifyUpdate() {
		threshold = (int) Config.getValue("maxHopperCount");
	}

	private HashSet<HopperCluster> clusters = new HashSet<>();
	private HashSet<LatticeCluster> latticeClusters = new HashSet<>();
	private ArrayList<String> result = new ArrayList<>();
	private long estimatedChunkCount = 0;
	private AtomicLong scannedCount = new AtomicLong(0);
	private static int threshold = 100;
	private AtomicInteger hopperCount = new AtomicInteger(0);
	private HopperScanner father;
}
