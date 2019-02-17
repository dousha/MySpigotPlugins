package tech.dsstudio.minecraft.arsenal;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.repository.ComplexSinglePageItemVendorRepository;

import java.io.File;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class ArsenalRepository extends ComplexSinglePageItemVendorRepository {
	public ArsenalRepository() {

	}

	@Override
	protected Inventory createInventoryPage(UUID uuid) {
		AtomicReference<Inventory> inventory = new AtomicReference<>(null);
		Optional.of(Bukkit.getPlayer(uuid)).ifPresent(player -> {
			inventory.set(Bukkit.createInventory(null, InventoryType.CHEST, repoTitle));
			inventory.get().setStorageContents(repository.getPlayerGoods(player).toArray(new ItemStack[0]));
		});
		return inventory.get();
	}

	@Override
	public boolean isInventoryViewSpecial(InventoryView inventoryView) {
		return isInventorySpecial(inventoryView.getTopInventory());
	}

	@Override
	public boolean isInventorySpecial(Inventory inventory) {
		return inventory.getHolder() == null && inventory.getTitle().equals(repoTitle);
	}

	@Override
	public int getInventorySize(UUID uuid) {
		return InventoryType.CHEST.getDefaultSize();
	}

	@Override
	public boolean hasPermission(UUID uuid) {
		return true;
	}

	@Override
	public void loadConfiguration(FileConfiguration config, FileConfiguration data) {
		repoTitle = config.getString("title");
		repository = new ItemRepository();
		repository.load(data);
	}

	@Override
	public Optional<YamlConfiguration> saveConfiguration() {
		return Optional.of(repository.getConfig());
	}

	@Override
	public void initialize(File base, FileConfiguration config) {
		repoTitle = config.getString("title");
		repository = new ItemRepository();
	}

	@Override
	public String getName() {
		return "ArsenalRepo";
	}

	public ItemRepository getRepository() {
		return repository;
	}

	public void notifyUpdate(ConfigurationSection config) {
		repoTitle = config.getString("title");
	}

	private ItemRepository repository;
	private String repoTitle;
}
