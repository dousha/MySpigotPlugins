package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class LimitEntry {
	public LimitEntry(String msg, List<String> permissions) {
		this.msg = msg;
		this.permissions = permissions;
	}

	public boolean match(Player player) {
		return permissions.size() <= 0 || permissions.stream().allMatch(player::hasPermission);
	}

	public void annoy(Player player, ItemStack item) {
		player.sendMessage(msg.replace('&', '\u00a7').replace("%item%", Util.stripItemName(item)));
	}

	private String msg;
	private List<String> permissions;
}
