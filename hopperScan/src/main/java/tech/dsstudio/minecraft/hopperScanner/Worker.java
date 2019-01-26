package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.Material;
import org.bukkit.block.BlockState;

import java.util.Arrays;

public class Worker implements Runnable {
	public Worker(int x, int z, BlockState[] entities, Lattice lattice) {
		this.entities = entities;
		this.lattice = lattice;
		this.x = x;
		this.z = z;
	}

	@Override
	public void run() {
		Arrays.stream(entities).filter(it -> it.getType().equals(Material.HOPPER)).forEach(it -> {
			try {
				lattice.addHopper(it);
			} catch (ClassCastException ignored) {
				HopperScanner.annoy("Beep");
			}
		});
		lattice.increaseScannedChunkCount();
	}

	private BlockState[] entities;
	private Lattice lattice;
	private int x, z;
}
