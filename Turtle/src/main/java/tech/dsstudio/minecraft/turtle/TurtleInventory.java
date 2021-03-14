package tech.dsstudio.minecraft.turtle;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.inventory.InventoryHelper;

public class TurtleInventory {
	public TurtleInventory(@NotNull Turtle entity) {
		owner = entity;
		inventory = Bukkit.createInventory(entity, InventoryType.CHEST);
	}

	public TurtleInventory(@NotNull Turtle entity, @NotNull Inventory inventory) {
		owner = entity;
		this.inventory = inventory;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public boolean appendItem(@NotNull ItemStack item) {
		return InventoryHelper.tryAppendItem(inventory, item);
	}

	public boolean removeItem(@NotNull ItemStack item) {
		return InventoryHelper.tryRemoveItems(inventory, item);
	}

	public boolean removeItem(@NotNull Material material, int amount) {
		return InventoryHelper.tryRemoveItems(inventory, material, amount);
	}

	public int refuel() {
		// todo
		return 0;
	}

	public void saveInventory(FileConfiguration configuration) {
		configuration.set("inventoryName", owner.getPuppet().getCustomName());
		InventoryHelper.saveChestInventory(inventory, configuration);
	}

	public static TurtleInventory loadInventory(Turtle owner, FileConfiguration configuration) {
		Inventory inv = InventoryHelper.loadChestInventory(owner, configuration);
		return new TurtleInventory(owner, inv);
	}

	public Turtle getOwner() {
		return owner;
	}

	private Turtle owner;
	private Inventory inventory;
}
