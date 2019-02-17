package tech.dsstudio.minecraft.portapack.internal.pack;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class InventoryUtil {
	public static YamlConfiguration saveInventory(Inventory inventory) {
		YamlConfiguration config = new YamlConfiguration();
		for (int i = 0; i < capacity - 9; i++) {
			ItemStack currentItem = inventory.getItem(i);
			if (currentItem == null || currentItem.getType().equals(Material.AIR)) continue;
			config.set(String.valueOf(i), currentItem);
		}
		return config;
	}

	public static void loadInventory(Inventory inventory, File file) {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(file);
			config.getKeys(false).forEach(it -> inventory.setItem(Integer.parseInt(it), config.getItemStack(it)));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
	}

	public static void loadInventory(Inventory inventory, ConfigurationSection section) {
		section.getKeys(false).forEach(it -> inventory.setItem(Integer.parseInt(it), section.getItemStack(it)));
	}

	private final static int capacity = 54;
}
