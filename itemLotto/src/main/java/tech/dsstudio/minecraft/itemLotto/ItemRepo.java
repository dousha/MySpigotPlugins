package tech.dsstudio.minecraft.itemLotto;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

public class ItemRepo {
	public ItemRepo(File base) {
		this.config = new File(base, "items.yml");
		checkItemFile();
		loadItems();
	}

	public void loadItems() {
		YamlConfiguration config = new YamlConfiguration();
		try {
			config.load(this.config);
			config.getKeys(false).forEach(it -> items.put(it, config.getItemStack(it)));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	public void saveItems() {
		YamlConfiguration config = new YamlConfiguration();
		items.forEach((name, item) -> {
			config.set(name, item);
			try {
				config.save(this.config);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	public void deleteItem(String name) {
		items.remove(name);
	}

	public void addItem(String name, ItemStack item) {
		items.put(name, item);
	}

	public ItemStack getItem(String name) {
		return items.get(name);
	}

	public boolean hasItem(String name) {
		return items.containsKey(name);
	}

	public Set<String> getItems() {
		return items.keySet();
	}

	private void checkItemFile() {
		if (!config.exists()) {
			try {
				if (!config.createNewFile()) {
					throw new RuntimeException();
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}

	private File config;
	private HashMap<String, ItemStack> items = new HashMap<>();
}
