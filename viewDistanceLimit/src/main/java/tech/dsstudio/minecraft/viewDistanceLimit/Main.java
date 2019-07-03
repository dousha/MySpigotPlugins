package tech.dsstudio.minecraft.viewDistanceLimit;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		this.viewCheck(event.getPlayer());
	}

	@EventHandler
	public void worldToggle(PlayerChangedWorldEvent event) {
		this.viewCheck(event.getPlayer());
	}

	@EventHandler
	public void respawnPlayer(PlayerRespawnEvent e) {
		this.viewCheck(e.getPlayer());
	}

	@EventHandler
	public void tpPlayer(PlayerTeleportEvent e) {
		this.viewCheck(e.getPlayer());
	}

	public void viewCheck(Player player) {
		String wname = player.getWorld().getName();
		if (this.getConfig().getBoolean("world." + wname + ".enabled")) {
			if (player.hasPermission("chunkrange.pass") && this.getConfig().getBoolean("world." + wname + ".passportIngore")) {
				annoyPlayer(player, "§6ViewLimit §a>> §b您拥有此世界视野受限豁免权限，不受视野限制");
			} else {
				player.setViewDistance(this.getConfig().getInt("world." + wname + ".limit"));
				if (this.getConfig().getBoolean("world." + wname + ".showMessage")) {
					player.sendTitle(this.getConfig().getString("world." + wname + ".title"), this.getConfig().getString("world." + wname + ".subtitle"), 0, 120, 0);
					annoyPlayer(player, "§6ViewLimit §a>> §c您当前正在视野受限世界，在此世界内您的视野将会持续受限");
				}
			}
		} else if (player.hasPermission("chunkrange.pass")) {
			player.setViewDistance(this.getConfig().getInt("pass.limit"));
			annoyPlayer(player, "§6ViewLimit §a>> §b您拥有VIP权限，已自动调整视野距离");
		} else if (!player.hasPermission("chunkrange.pass")) {
			player.setViewDistance(this.getConfig().getInt("pass.limit"));
		}

	}

	private void annoyPlayer(Player player, String message) {
		try {
			if (getConfig().getBoolean("silent"))
				return;
		} catch (Exception e) {
			return; // fuck
		}
		player.sendRawMessage(message);
	}
}
