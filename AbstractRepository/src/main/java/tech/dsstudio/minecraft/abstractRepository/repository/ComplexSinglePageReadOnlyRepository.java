package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public abstract class ComplexSinglePageReadOnlyRepository extends ComplexSinglePageRepository {
	@Override
	public ItemOperationResult takeItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.CANCEL_EVENT;
	}

	@Override
	public boolean canTakeItemAt(UUID uuid, int slot, int rawSlot) {
		return true;
	}

	@Override
	public ItemOperationResult putItemAt(UUID uuid, int slot, int rawSlot, ItemStack item) {
		return ItemOperationResult.CANCEL_EVENT;
	}

	@Override
	public boolean canPutItemAt(UUID uuid, int slot, int rawSlot) {
		return false;
	}

	@Override
	public Optional<YamlConfiguration> saveConfiguration() {
		return Optional.empty();
	}

	/**
	 * Implementation should come up with a way to create an POPULATED inventory.
	 * <p>
	 * The configuration could be obtained in
	 * {@link AbstractRepository#loadConfiguration(FileConfiguration, FileConfiguration)}
	 * and {@link AbstractRepository#initialize(File, FileConfiguration)}.
	 * The load methods are guaranteed to be called before this method.
	 * <p>
	 * And if you just want to isolate inventories,
	 * you can use {@link ReadOnlyPage} with {@link ReadOnlyPageLoader}.
	 * <p>
	 * Remember to override {@link SimpleSinglePageRepository#isInventorySpecial(Inventory)}
	 * in order to make this function normally.
	 *
	 * @param uuid Player UUID
	 * @return Inventory
	 */
	protected abstract Inventory createInventoryPage(UUID uuid);
}
