package tech.dsstudio.minecraft.portapack.internal.pack;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.io.File;
import java.util.HashMap;

public class PortablePackManager {
	public PortablePackManager(File dataFolder) {
		base = new File(dataFolder, "data");
		if (!base.exists()) {
			base.mkdirs();
		}
	}

	public File getPlayerPackFolder(OfflinePlayer player) {
		File file = new File(base, player.getName());
		if (!file.exists()) {
			if (!file.mkdirs()) {
				throw new RuntimeException();
			}
		}
		return file;
	}

	public PortablePack getPack(Player player) {
		return packs.computeIfAbsent(player.getName(), (ignored) -> new PortablePack(player));
	}

	public void preparePack(Player player) {
		if (!packs.containsKey(player.getName())) {
			packs.put(player.getName(), new PortablePack(player));
		}
	}

	public boolean isSpecialInventory(Player player, Inventory inventory) {
		return packs.containsKey(player.getName()) && packs.get(player.getName()).isSpecialInventory(inventory);
	}

	public void savePacks() {
		packs.values().forEach(PortablePack::saveInventory);
	}

	private HashMap<String, PortablePack> packs = new HashMap<>();
	private File base;
}
