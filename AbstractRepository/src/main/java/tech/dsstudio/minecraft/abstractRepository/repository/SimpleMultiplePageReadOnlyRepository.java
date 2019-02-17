package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.buttons.Stripe;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public abstract class SimpleMultiplePageReadOnlyRepository implements AbstractRepository {
	@Override
	public void initialize(File base, FileConfiguration config) {
		int pageCount = config.getInt("page");
		inventories = new Inventory[pageCount];
		stripes = new Stripe[pageCount][];
		for (int i = 0; i < pageCount; i++) {
			int rowCount = config.getInt("row");
			inventories[i] = Bukkit.createInventory(null, rowCount * 9);
			stripes[i] = ReadOnlyPageLoader.createPage(inventories[i], config.getConfigurationSection(String.valueOf(i)));
		}
	}

	@Override
	public Optional<YamlConfiguration> saveConfiguration() {
		return Optional.empty();
	}

	@Override
	public boolean canTakeItemAt(UUID uuid, int slot, int rawSlot) {
		return true;
	}

	@Override
	public boolean canPutItemAt(UUID uuid, int slot, int rawSlot) {
		return false;
	}

	@Override
	public ItemOperationResult putItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.CANCEL_EVENT;
	}

	@Override
	public ItemOperationResult takeItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		int page = pageTracker.get(uuid);
		stripes[page][slot / 9].clickOn(this, uuid, slot);
		return ItemOperationResult.CANCEL_EVENT;
	}

	@Override
	public Inventory getInventory(UUID uuid) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		return inventories[pageTracker.get(uuid)]; // as everyone share the same view
	}

	@Override
	public int getCurrentPage(UUID uuid) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		return pageTracker.get(uuid);
	}

	@Override
	public int totalPageCount(UUID uuid) {
		return inventories.length;
	}

	@Override
	public void forceSetCurrentPage(UUID uuid, int pageIndex) {
		if (pageIndex < 0) pageIndex = 0;
		if (pageIndex >= inventories.length) pageIndex = inventories.length - 1;
		pageTracker.put(uuid, pageIndex);
	}

	@Override
	public boolean nextPage(UUID uuid) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		if (pageTracker.get(uuid) == inventories.length - 1)
			return false;
		forceSetCurrentPage(uuid, pageTracker.get(uuid) + 1);
		return true;
	}

	@Override
	public boolean previousPage(UUID uuid) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		if (pageTracker.get(uuid) == 0)
			return false;
		forceSetCurrentPage(uuid, pageTracker.get(uuid) - 1);
		return true;
	}

	@Override
	public boolean setCurrentPage(UUID uuid, int pageIndex) {
		forceSetCurrentPage(uuid, pageIndex);
		return pageIndex >= 0 && pageIndex < inventories.length;
	}

	@Override
	public int getInventorySize(UUID uuid) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		return inventories[pageTracker.get(uuid)].getSize();
	}

	@Override
	public Optional<ItemStack> getItemAt(UUID uuid, int slotIndex) {
		if (!pageTracker.containsKey(uuid)) {
			pageTracker.put(uuid, 0);
		}
		if (slotIndex < 0 || slotIndex >= inventories[pageTracker.get(uuid)].getSize()) {
			return Optional.empty();
		}
		ItemStack item = inventories[pageTracker.get(uuid)].getItem(slotIndex);
		if (item == null) {
			return Optional.of(new ItemStack(Material.AIR));
		} else {
			return Optional.of(item);
		}
	}

	protected HashMap<UUID, Integer> pageTracker;
	protected Inventory[] inventories;
	protected Stripe[][] stripes;
}
