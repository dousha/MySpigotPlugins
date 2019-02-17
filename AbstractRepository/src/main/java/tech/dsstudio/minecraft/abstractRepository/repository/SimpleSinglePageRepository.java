package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import java.util.UUID;

public abstract class SimpleSinglePageRepository implements AbstractRepository {
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
	public boolean isInventorySpecial(Inventory inventory) {
		return inventory != null && inventory.getHolder() == null && inventory.getViewers().size() > 0;
	}

	@Override
	public boolean isInventoryViewSpecial(InventoryView view) {
		return isInventorySpecial(view.getTopInventory());
	}
}
