package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Util {
	public static boolean dontHavePermission(Player player, ItemStack item) {
		return item != null && (item.hasItemMeta() && (item.getItemMeta().getLore() != null && (item.getItemMeta().getLore().size() != 0 && !Config.hasPermission(player, item))));
	}

	public static int firstEmpty(Player player) {
		PlayerInventory inventory = player.getInventory();
		int forbiddenSlot = inventory.getHeldItemSlot();
		// prefer to use backpack slots first
		for (int i = 9; i < 36; i++) {
			ItemStack current = inventory.getItem(i);
			if (current == null || current.getType().equals(Material.AIR)) {
				return i;
			}
		}
		for (int i = 0; i < 9; i++) {
			if (i == forbiddenSlot) continue;
			ItemStack current = inventory.getItem(i);
			if (current == null || current.getType().equals(Material.AIR)) {
				return i;
			}
		}
		return -1;
	}

	public static boolean isItemSpecial(ItemStack item) {
		return item != null && item.hasItemMeta() && item.getItemMeta().getLore() != null && item.getItemMeta().getLore().size() > 0;
	}

	public static String stripItemName(ItemStack item) {
		if (item.hasItemMeta()) {
			if (item.getItemMeta().hasDisplayName()) {
				return item.getItemMeta().getDisplayName();
			} else {
				return item.getType().name();
			}
		} else {
			return item.getType().name();
		}
	}

	public static String stripColorMarks(String str) {
		return str.replaceAll("([&\u00a7].)", "");
	}
}
