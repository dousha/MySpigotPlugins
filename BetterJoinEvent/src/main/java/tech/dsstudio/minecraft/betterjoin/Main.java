package tech.dsstudio.minecraft.betterjoin;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
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
			PlayerRejoinEvent event;
			if (obj != null) {
				getLogger().info("Hello again!");
				long lastTime = (long) obj;
				event = new PlayerRejoinEvent(e, false, lastTime);
			} else {
				getLogger().info("Hello!");
				event = new PlayerRejoinEvent(e, true, 0L);
			}
			data.set(LAST_TIME_JOIN_KEY, System.currentTimeMillis());
			getServer().getPluginManager().callEvent(event);
		}
	}

	private PlayerDataStorage storage = null;
	private static final String LAST_TIME_JOIN_KEY = "btjLastJoin";
}
