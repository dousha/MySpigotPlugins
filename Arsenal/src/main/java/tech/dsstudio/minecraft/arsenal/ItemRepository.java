package tech.dsstudio.minecraft.arsenal;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemRepository {
	public ItemRepository() {
		this.config = new YamlConfiguration();
	}

	public void load(ConfigurationSection section) {
		section.getKeys(false).forEach(it -> items.put(it, config.getItemStack(it)));
	}

	public void reload() {
		load(config);
	}

	public void save() {
		items.forEach((perm, item) -> config.set(perm, item));
	}

	public void addItem(String permission, ItemStack item) {
		items.put(permission, item);
		save();
	}

	public void deleteItem(String permission) {
		items.remove(permission);
	}

	public List<ItemStack> getPlayerGoods(Player player) {
		ArrayList<ItemStack> goods = new ArrayList<>();
		items.forEach((perm, item) -> {
			if (player.hasPermission(perm))
				goods.add(item);
		});
		return goods;
	}

	public YamlConfiguration getConfig() {
		return config;
	}

	private HashMap<String, ItemStack> items = new HashMap<>();
	private YamlConfiguration config;
}
