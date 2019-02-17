package tech.dsstudio.minecraft.portapack.internal.pack;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import tech.dsstudio.minecraft.portapack.internal.Instances;
import tech.dsstudio.minecraft.portapack.internal.journal.JournalEntry;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.IntStream;

public class PortablePackSnapshot {
	private PortablePackSnapshot() {
		for (int i = 0; i < inventories.length; i++) {
			inventories[i] = Bukkit.createInventory(null, capacity);
		}
	}

	public static PortablePackSnapshot fromPortablePack(PortablePack pack) {
		PortablePackSnapshot snapshot = new PortablePackSnapshot();
		snapshot.owner = pack.getOwner();
		Inventory[] inventories = pack.getInventories();
		System.arraycopy(inventories, 0, snapshot.inventories, 0, inventories.length);
		return snapshot;
	}

	public static PortablePackSnapshot fromJournal(String playerName, Timestamp time) {
		PortablePackSnapshot snapshot = new PortablePackSnapshot();
		snapshot.owner = Bukkit.getServer().getOfflinePlayer(playerName);
		ArrayList<JournalEntry> entries = Instances.journal.getJournalsWithin(playerName, time);
		entries.forEach(it -> it.applyToPack(snapshot));
		return snapshot;
	}

	public static PortablePackSnapshot fromPlayerAndName(String player, String name) {
		return fromSnapshotFile(checkSnapshotFile(player, name));
	}

	public static PortablePackSnapshot fromPlayer(String player) {
		return fromPlayerAndName(player, "latest");
	}

	private static PortablePackSnapshot fromSnapshotFile(File file) {
		PortablePackSnapshot snapshot = new PortablePackSnapshot();
		snapshot.outName = file.getName();
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
			for (int i = 0; config.contains(String.valueOf(i)); i++) {
				InventoryUtil.loadInventory(snapshot.inventories[i], config.getConfigurationSection(String.valueOf(i)));
			}
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		return null;
	}

	public Inventory getInventory(int index) {
		if (index < 0 || index >= inventories.length) return null;
		return inventories[index];
	}

	public Inventory[] getInventories() {
		return inventories;
	}

	public void save() {
		File out = new File(checkPlayerFolder(owner.getName()), outName);
		YamlConfiguration config = new YamlConfiguration();
		IntStream.range(0, inventories.length).forEach(it -> {
			YamlConfiguration piece = InventoryUtil.saveInventory(inventories[it]);
			config.set(String.valueOf(it), piece);
		});
		try {
			config.save(out);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	private static File checkPlayerFolder(String player) {
		File folder = new File(base, player);
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				throw new RuntimeException();
			}
		}
		return folder;
	}

	private static File checkSnapshotFile(String player, String name) {
		File out = new File(checkPlayerFolder(player), name);
		if (!out.exists()) {
			try {
				if (!out.createNewFile())
					throw new RuntimeException();
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
		return out;
	}

	private OfflinePlayer owner;
	private static File base = new File(Instances.main.getDataFolder(), "snapshots");
	private Inventory[] inventories = new Inventory[Instances.config.getMaxPage()];
	private String outName = "latest.yml";
	private final int capacity = 54;
}
