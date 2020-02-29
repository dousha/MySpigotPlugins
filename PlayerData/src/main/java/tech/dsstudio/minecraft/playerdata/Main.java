package tech.dsstudio.minecraft.playerdata;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.driver.SimpleFileStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForDriverEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

import java.util.logging.Level;

public class Main extends JavaPlugin implements Listener {
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

	private PlayerDataStorage storage = null;
	private static long nop_time = 0;
}
