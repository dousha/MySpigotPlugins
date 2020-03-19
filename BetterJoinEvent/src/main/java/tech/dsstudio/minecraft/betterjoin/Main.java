package tech.dsstudio.minecraft.betterjoin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import tech.dsstudio.minecraft.betterjoin.events.PlayerRejoinEvent;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onStorageReady(StorageReadyEvent e) {
		this.storage = e.getStorage();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (storage != null) {
			PlayerData data = storage.get(e.getPlayer().getUniqueId());
			Object obj = data.get(LAST_TIME_JOIN_KEY);
			long lastLeave = (long) data.getOrDefault(LAST_TIME_LEAVE_KEY, 0L);
			PlayerRejoinEvent event;
			if (obj != null) {
				long lastJoin = (long) obj;
				event = new PlayerRejoinEvent(e, false, lastJoin, lastLeave);
			} else {
				event = new PlayerRejoinEvent(e, true, 0L, 0L);
			}
			data.set(LAST_TIME_JOIN_KEY, System.currentTimeMillis());
			getServer().getPluginManager().callEvent(event);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (storage != null) {
			PlayerData data = storage.get(e.getPlayer().getUniqueId());
			data.set(LAST_TIME_LEAVE_KEY, System.currentTimeMillis());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerKick(PlayerKickEvent e) {
		if (storage != null) {
			PlayerData data = storage.get(e.getPlayer().getUniqueId());
			data.set(LAST_TIME_LEAVE_KEY, System.currentTimeMillis());
		}
	}

	private PlayerDataStorage storage = null;
	private static final String LAST_TIME_JOIN_KEY = "btjLastJoin";
	private static final String LAST_TIME_LEAVE_KEY = "btjLastLeave";
}
