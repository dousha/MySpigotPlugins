package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public abstract class ComplexSinglePageRepository implements AbstractRepository {
	@Override
	public Inventory getInventory(UUID uuid) {
		if (!inventories.containsKey(uuid)) {
			inventories.put(uuid, createInventoryPage(uuid));
		}
		return inventories.get(uuid);
	}

	@Override
	public int getCurrentPage(UUID uuid) {
		return 0;
	}

	@Override
	public int totalPageCount(UUID uuid) {
		return 1;
	}

	@Override
	public void forceSetCurrentPage(UUID uuid, int pageIndex) {
		// do nothing
	}

	@Override
	public boolean nextPage(UUID uuid) {
		return false;
	}

	@Override
	public boolean previousPage(UUID uuid) {
		return false;
	}

	@Override
	public boolean setCurrentPage(UUID uuid, int pageIndex) {
		return pageIndex == 0;
	}

	@Override
	public Optional<ItemStack> getItemAt(UUID uuid, int slotIndex) {
		if (!inventories.containsKey(uuid)) {
			inventories.put(uuid, createInventoryPage(uuid));
		}
		if (slotIndex < 0 || slotIndex >= inventories.get(uuid).getSize()) {
			return Optional.empty();
		}
		ItemStack item = inventories.get(uuid).getItem(slotIndex);
		if (item == null) {
			return Optional.of(new ItemStack(Material.AIR));
		} else {
			return Optional.of(item);
		}
	}

	protected abstract Inventory createInventoryPage(UUID uuid);

	protected HashMap<UUID, Inventory> inventories = new HashMap<>();
}
