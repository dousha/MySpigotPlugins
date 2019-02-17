package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.RepositoryUtils;
import tech.dsstudio.minecraft.abstractRepository.buttons.Stripe;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public abstract class SimpleSinglePageReadOnlyRepository extends SimpleSinglePageRepository {
	@Override
	public void initialize(File base, FileConfiguration config) {
		int rowCount = config.getInt("row");
		inventory = Bukkit.createInventory(null, rowCount * 9);
		stripes = ReadOnlyPageLoader.createPage(inventory, config);
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
		stripes[slot / 9].clickOn(this, uuid, slot);
		return ItemOperationResult.CANCEL_EVENT;
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
		if (slotIndex < 0 || slotIndex >= stripes.length * 9) {
			return Optional.empty();
		} else {
			return RepositoryUtils.getItemAt(inventory, slotIndex);
		}
	}

	private Inventory inventory;
	private Stripe[] stripes;
}
