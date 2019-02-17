package tech.dsstudio.minecraft.portapack.internal.pack;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.portapack.buttons.Button;
import tech.dsstudio.minecraft.portapack.internal.Instances;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.IntStream;

public class PortablePack {
	public PortablePack(Player owner) {
		this.owner = owner;
		this.buttons = Instances.config.getFunctionStripe();
		loadChestDescriptor();
	}

	public void openInventory() {
		owner.openInventory(inventories[currentPage]);
	}

	public void buttonClick(int relativeIndex) {
		buttons[relativeIndex].onClick(this);
	}

	public void clearInventory() {
		clearInventory(currentPage);
	}

	public void clearInventory(int i) {
		inventories[i].clear();
		populateStripe(inventories[i]);
	}

	public void dropAllInventories() {
		IntStream.range(0, totalPage).forEach(this::clearInventory);
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int page) {
		currentPage = Math.max(page, 0);
	}

	public Player getOwner() {
		return owner;
	}

	public boolean nextPage() {
		if (++currentPage == totalPage) {
			currentPage = totalPage - 1;
			return false;
		}
		return true;
	}

	public boolean prevPage() {
		if (--currentPage < 0) {
			currentPage = 0;
			return false;
		}
		return true;
	}

	public boolean isSpecialInventory(Inventory inventory) {
		return Arrays.stream(inventories).anyMatch(it -> it.getName().contains(((Player) inventory.getHolder()).getName()));
	}

	public Inventory getInventory() {
		return inventories[currentPage];
	}

	public Inventory getInventory(int index) {
		if (index >= inventories.length || index < 0) {
			return null;
		}
		return inventories[index];
	}

	public Inventory[] getInventories() {
		return inventories;
	}

	public void saveInventory() {
		IntStream.range(0, totalPage).forEach(it -> {
			File out = checkInventoryFile(it);
			YamlConfiguration config = InventoryUtil.saveInventory(inventories[it]);
			try {
				config.save(out);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		});
	}

	public void restoreToSnapshot(PortablePackSnapshot snapshot) {
		System.arraycopy(inventories, 0, snapshot.getInventories(), 0, inventories.length);
		saveInventory();
	}

	private File checkInventoryFile(int i) {
		File file = new File(Instances.packer.getPlayerPackFolder(owner), i + ".yml");
		if (!file.exists()) {
			try {
				if (!file.createNewFile())
					throw new RuntimeException();
			} catch (IOException e) {
				throw new RuntimeException();
			}
		}
		return file;
	}

	private void loadChestDescriptor() {
		File folder = Instances.packer.getPlayerPackFolder(owner);
		HashMap<Integer, String> map = Instances.config.getPremiums().getContent();
		totalPage = map.keySet().stream().sorted().filter(it -> owner.hasPermission(map.get(it))).max(Comparator.comparingInt(i -> i)).orElse(Instances.config.getFreePage());
		deployChestDescriptor(folder);
		inventories = new Inventory[totalPage];
		IntStream.range(0, totalPage).forEach(it -> {
			String title = Instances.config.getTitle().replace("$player", owner.getDisplayName()).replace("$page", String.valueOf(it + 1)).replace("$total", String.valueOf(totalPage));
			inventories[it] = populateInventory(Bukkit.createInventory(owner, capacity, title), it);
		});
	}

	private void deployChestDescriptor(File folder) {
		if (!IntStream.range(0, totalPage).mapToObj(it -> new File(folder, it + ".yml")).filter(it -> !it.exists()).allMatch(it -> {
			try {
				return it.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		})) {
			throw new RuntimeException();
		}
	}

	private Inventory populateInventory(Inventory inventory, int page) {
		File file = checkInventoryFile(page);
		InventoryUtil.loadInventory(inventory, file);
		populateStripe(inventory);
		return inventory;
	}

	private void populateStripe(Inventory inventory) {
		int offset = 45;
		for (int i = 0; i < buttons.length; i++) {
			if (buttons[i] == null) continue;
			inventory.setItem(offset + i, buttons[i].render());
		}
	}

	private Player owner;
	private Inventory[] inventories;
	private int currentPage = 0;
	private int totalPage = 0;
	private Button[] buttons;
	private final int capacity = 54;
}
