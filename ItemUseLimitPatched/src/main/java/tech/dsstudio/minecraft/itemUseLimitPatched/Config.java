package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Config {
	public Config(FileConfiguration config) {
		Config.config = config;
	}

	public static void reload() {
		ConfigurationSection section = config.getConfigurationSection("useLimit");
		permSet.get().clear();
		section.getKeys(false).forEach(it -> {
			ConfigurationSection activeBranch = section.getConfigurationSection(it);
			String msg = activeBranch.getString("cantUseMessage");
			List<String> perm = activeBranch.getStringList("permission");
			permSet.get().addAll(perm);
			LimitEntry entry = new LimitEntry(msg, perm);
			permissions.put(Util.stripColorMarks(it), entry);
		});
	}

	public static boolean hasPermission(Player player, ItemStack item) {
		LimitEntry entry = getLimitEntry(item);
		return entry == null || entry.match(player);
	}

	public static LimitEntry getLimitEntry(ItemStack item) {
		if (item.hasItemMeta() && item.getItemMeta().hasLore() && item.getItemMeta().getLore().size() > 0) {
			AtomicReference<LimitEntry> entry = new AtomicReference<>();
			item.getItemMeta().getLore().stream().filter(it -> permissions.keySet().stream().anyMatch(it::contains)).forEach(it -> permissions.keySet().stream().filter(it::contains).forEach(key -> entry.set(permissions.get(key))));
			return entry.get();
		} else {
			return null;
		}
	}

	public static HashSet<String> getPermSet() {
		return permSet.get();
	}

	private static FileConfiguration config;
	private static HashMap<String, LimitEntry> permissions = new HashMap<>(); // lore -> perm
	private static AtomicReference<HashSet<String>> permSet = new AtomicReference<>(new HashSet<>());
}
