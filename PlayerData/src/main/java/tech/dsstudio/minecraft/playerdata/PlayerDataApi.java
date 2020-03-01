package tech.dsstudio.minecraft.playerdata;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.driver.SimpleFileStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForDriverEvent;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

import java.util.logging.Level;

public class PlayerDataApi extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);

		String driver = getConfig().getString("driver");
		getLogger().info("Using driver `" + driver + "`");
		getServer().getScheduler().runTaskLater(this, () -> getServer().getPluginManager().callEvent(new RequestForDriverEvent(driver, this)), 1L);
		long autoSaveInterval = getConfig().getLong("autosave") * 60 * 20;
		if (autoSaveInterval > 0) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, () -> {
				if (storage != null) {
					storage.save();
				}
			}, autoSaveInterval, autoSaveInterval);
		}
		instance = this;
	}

	@Override
	public void onDisable() {
		if (storage != null) {
			storage.save();
		}
	}

	public void registerDriver(PlayerDataStorage storage) {
		this.storage = storage;
	}

	@EventHandler
	public void onDriverRequest(RequestForDriverEvent event) {
		if (event.getDriverName().equals("file")) {
			registerDriver(new SimpleFileStorage(getDataFolder()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDriverRequestFinished(RequestForDriverEvent event) {
		isPluginLoaded = true;
		broadcastStorage();
	}

	@EventHandler
	public void onStorageRequest(RequestForStorageEvent e) {
		broadcastStorage();
	}

	private void broadcastStorage() {
		if (isPluginLoaded) {
			if (storage != null) {
				while (!storage.ready()) {
					nop();
				}
				storage.load();
				getServer().getPluginManager().callEvent(new StorageReadyEvent(storage));
				getLogger().info("Storage engine ready");
			} else {
				// wtf?
				getLogger().log(Level.SEVERE, "No storage driver was registered!");
			}
		} else {
			getLogger().info("Waiting for storage engine");
		}
	}

	private void nop() {
		if (nop_time == 0) {
			nop_time = System.currentTimeMillis();
		}
		if (System.currentTimeMillis() - nop_time > 30_000) {
			getLogger().warning("It seems that storage engine is stuck");
			nop_time = System.currentTimeMillis();
		}
	}

	@Nullable
	public static PlayerDataStorage getStorage() {
		return instance.storage;
	}

	private static long nop_time = 0;
	private static PlayerDataApi instance = null;
	private PlayerDataStorage storage = null;
	private boolean isPluginLoaded = false;
}
