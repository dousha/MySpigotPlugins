package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.scheduler.BukkitRunnable;

/// Yes I DO know what "teller" means.
/// I have passed CET4. (CET6 grade pending).
/// Besides, isn't this a good pun? Someone deals directly with customers about their TRANSACTIONS.
/// N.B. Transactions n. An input message to a computer system dealt with as a single unit of work.
public class Teller extends BukkitRunnable {
	public Teller(Lattice lattice) {
		this.lattice = lattice;
	}

	@Override
	public void run() {
		HopperScanner.annoy(String.format("已扫描 %d / %d 个区块", lattice.getScannedChunkCount(), lattice.getEstimatedChunkCount()));
	}

	private Lattice lattice;
}
