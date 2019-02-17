package tech.dsstudio.minecraft.abstractRepository;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RepositoryUtils {
	public static boolean tryToPutIntoInventory(Inventory inventory, ItemStack item) {
		int index = inventory.first(item.getType());
		if (index == -1) {
			int targetIndex = inventory.firstEmpty();
			if (targetIndex == -1) {
				return false;
			}
			inventory.setItem(targetIndex, item);
		} else {
			ItemStack oldItem = inventory.getItem(index);
			int count = oldItem.getMaxStackSize() - oldItem.getAmount();
			if (count >= item.getAmount()) {
				oldItem.setAmount(oldItem.getAmount() + item.getAmount());
			} else {
				int remainder = oldItem.getAmount() + item.getAmount() - item.getMaxStackSize();
				oldItem.setAmount(oldItem.getMaxStackSize());
				ItemStack newItem = new ItemStack(item.getType(), remainder);
				return tryToPutIntoInventory(inventory, newItem);
			}
		}
		return true;
	}

	public static boolean populateInventory(Inventory inventory, Set<ItemStack> items) {
		return items.stream().allMatch(it -> tryToPutIntoInventory(inventory, it));
	}

	public static boolean populateInventory(Inventory inventory, Set<ItemStack> items, Comparator<ItemStack> comparator) {
		List<ItemStack> processedItems = new ArrayList<>(items);
		processedItems.sort(comparator);
		return processedItems.stream().allMatch(it -> tryToPutIntoInventory(inventory, it));
	}

	public static Inventory[] generateInventories(Set<ItemStack> items, InventoryType type, Comparator<ItemStack> comparator) {
		List<ItemStack> processedItems = new ArrayList<>(items);
		processedItems.sort(comparator);
		int splitCount = type.getDefaultSize();
		Inventory[] inventories = new Inventory[(int) Math.ceil((double) processedItems.size() / ((double) splitCount))];
		List<List<ItemStack>> partitions = Lists.partition(processedItems, splitCount);
		for (int i = 0; i < inventories.length; i++) {
			inventories[i] = Bukkit.createInventory(null, type);
			inventories[i].setStorageContents(partitions.get(i).toArray(new ItemStack[splitCount]));
		}
		return inventories;
	}

	public static Inventory[] generateInventoriesWithStripe(Set<ItemStack> items, InventoryType type, ItemStack[] stripe, Comparator<ItemStack> comparator) {
		switch (type) {
			case HOPPER:
			case ANVIL:
			case BEACON:
			case WORKBENCH:
			case ENCHANTING:
			case MERCHANT:
			case CRAFTING:
			case CREATIVE:
			case FURNACE:
			case DROPPER:
			case DISPENSER:
			case BREWING:
			case PLAYER:
				return null; // these inventory types are too good to create a stripe
			case CHEST:
			case ENDER_CHEST:
			case SHULKER_BOX:
				List<ItemStack> processedItems = new ArrayList<>(items);
				processedItems.sort(comparator);
				int splitCount = type.getDefaultSize() - 9;
				Inventory[] inventories = new Inventory[(int) Math.ceil((double) processedItems.size() / ((double) splitCount))];
				List<List<ItemStack>> partitions = Lists.partition(processedItems, splitCount);
				for (int i = 0; i < inventories.length; i++) {
					ItemStack[] page = partitions.get(i).toArray(new ItemStack[0]);
					ItemStack[] content = new ItemStack[type.getDefaultSize()];
					System.arraycopy(page, 0, content, 0, type.getDefaultSize() - 9);
					System.arraycopy(stripe, 0, content, type.getDefaultSize() - 9, 9);
					inventories[i] = Bukkit.createInventory(null, type);
					inventories[i].setStorageContents(content);
				}
				return inventories;
		}
		return null; // should not reach here
	}

	/**
	 * This method assumes that index is in bound.
	 *
	 * @param inventory Inventory
	 * @param index Item index
	 * @return The thing
	 */
	public static Optional<ItemStack> getItemAt(Inventory inventory, int index) {
		ItemStack item = inventory.getItem(index);
		if (item != null) {
			return Optional.of(item);
		} else {
			return Optional.of(new ItemStack(Material.AIR));
		}
	}

	/**
	 * This method will save the storage content of inventory.
	 * <p>
	 * The method relies on {@link YamlConfiguration#set(String, Object)} to serialize items,
	 * which may not generate portable configuration since it's implementation varies.
	 * <p>
	 * The file generated will be used for {@link RepositoryUtils#loadInventory(YamlConfiguration)}.
	 *
	 * @param inventory Inventory to get saved
	 * @return YamlConfiguration contains inventory title and content
	 */
	public static YamlConfiguration saveInventory(Inventory inventory) {
		YamlConfiguration configuration = new YamlConfiguration();
		configuration.set("type", inventory.getType());
		configuration.set("title", inventory.getTitle());
		ConfigurationSection section = configuration.createSection("items");
		ItemStack[] items = inventory.getStorageContents();
		for (int i = 0; i < items.length; i++) {
			if (items[i] != null && !items[i].getType().equals(Material.AIR)) {
				section.set(String.valueOf(i), items[i]);
			}
		}
		return configuration;
	}

	/**
	 * This is the counterpart function of {@link RepositoryUtils#saveInventory(Inventory)}.
	 * Do NOT use this function unless it's saved with the aforementioned function.
	 *
	 * @param configuration Configuration
	 * @return Inventory loaded from the specified configuration
	 */
	public static Inventory loadInventory(YamlConfiguration configuration) {
		InventoryType type = InventoryType.valueOf(configuration.getString("type"));
		String title = configuration.getString("title");
		Inventory inventory = Bukkit.createInventory(null, type, title);
		ConfigurationSection section = configuration.getConfigurationSection("items");
		section.getKeys(false).forEach(it -> {
			int index = Integer.parseInt(it);
			inventory.setItem(index, section.getItemStack(it));
		});
		return inventory;
	}
}
