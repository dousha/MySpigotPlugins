package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.RepositoryUtils;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

/**
 * Before you use this stuff, make sure that you DO want SHARE a SAME inventory among players.
 * All players who can have access to this repository will see and operate on THE SAME INVENTORY!
 */
public abstract class SimpleSinglePageReadWriteRepository extends SimpleSinglePageRepository {
	@Override
	public void initialize(File base, FileConfiguration config) {
		inventory = Bukkit.createInventory(null, config.getInt("row") * 9);
	}

	@Override
	public void loadConfiguration(FileConfiguration config, FileConfiguration data) {
		inventory = RepositoryUtils.loadInventory((YamlConfiguration) data);
	}

	@Override
	public Optional<YamlConfiguration> saveConfiguration() {
		return Optional.of(RepositoryUtils.saveInventory(inventory));
	}

	@Override
	public boolean canTakeItemAt(UUID uuid, int slot, int rawSlot) {
		return true;
	}

	@Override
	public boolean canPutItemAt(UUID uuid, int slot, int rawSlot) {
		return true;
	}

	@Override
	public ItemOperationResult putItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.ACCEPT_CHANGE;
	}

	@Override
	public ItemOperationResult takeItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.ACCEPT_CHANGE;
	}

	@Override
	public Inventory getInventory(UUID uuid) {
		return inventory; // as everyone share the same view
	}

	@Override
	public int getInventorySize(UUID uuid) {
		return inventory.getSize();
	}

	@Override
	public Optional<ItemStack> getItemAt(UUID uuid, int slotIndex) {
		if (slotIndex < 0 || slotIndex >= inventory.getSize()) {
			return Optional.empty();
		} else {
			return RepositoryUtils.getItemAt(inventory, slotIndex);
		}
	}

	private Inventory inventory;
}
