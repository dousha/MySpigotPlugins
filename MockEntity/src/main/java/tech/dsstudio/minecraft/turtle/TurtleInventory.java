package tech.dsstudio.minecraft.mockentity;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.inventory.InventoryHelper;

import java.util.ArrayList;

public class MockEntityInventory {
	public MockEntityInventory(MockEntity entity) {
		owner = entity;
		inventory = Bukkit.createInventory(null, InventoryType.CHEST);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean appendItem(ItemStack item) {
		return InventoryHelper.tryAppendItem(inventory, item);
	}

	private MockEntity owner;
	private Inventory inventory;
}
