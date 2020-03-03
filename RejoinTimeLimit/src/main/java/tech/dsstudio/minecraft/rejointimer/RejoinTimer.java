package tech.dsstudio.minecraft.rejointimer;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.betterjoin.events.PlayerRejoinEvent;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;
import tech.dsstudio.minecraft.rejointimer.events.PlayerRejoinTimedOutEvent;
import tech.dsstudio.minecraft.taskhook.PlayerTaskHookApi;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class RejoinTimer extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		this.enabledWorlds = getConfig().getStringList("worlds");
		this.lobby = getServer().getWorld(getConfig().getString("lobby")).getSpawnLocation();
		this.kickAlsoCounts = getConfig().getBoolean("including-kicks");
		this.message = getConfig().getString("message");
		this.limit = getConfig().getLong("limit") * 1000;
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().callEvent(new RequestForStorageEvent());
	}

	@EventHandler
	public void onPlayerRejoin(@NotNull PlayerRejoinEvent event) {
		Player player = event.getEvent().getPlayer();
		if (monitoredPlayers.contains(player.getUniqueId())) {
			if (System.currentTimeMillis() - event.getLastLeaveTimeMills() > limit && !player.hasPermission("rjexempt")) {
				// fail
				PlayerRejoinTimedOutEvent timedOutEvent = new PlayerRejoinTimedOutEvent(player, event.getLastLeaveTimeMills());
				getServer().getPluginManager().callEvent(timedOutEvent);
				player.sendMessage(message);
				player.teleport(lobby);
			} else {
				// pass
				monitoredPlayers.remove(player.getUniqueId());
			}
		}
	}

	@EventHandler
	public void onPlayerRejoinTimedOut(@NotNull PlayerRejoinTimedOutEvent event) {
		if (storage != null) {
			Player player = event.getPlayer();
			PlayerData data = storage.get(player.getUniqueId());
			int penaltyCount = (int) data.getOrDefault(PENALTY_MARK_KEY, 0);
			penaltyCount++;
			data.set(PENALTY_MARK_KEY, penaltyCount);
			if (getConfig().getBoolean("penalty.ground.enable")) {
				data.setVolatile(GROUND_MARK_KEY, true);
				long groundTime = getConfig().getLong("penalty.ground.duration") * 20;
				PlayerTaskHookApi.runTaskLater(this, player, () -> {
					data.setVolatile(GROUND_MARK_KEY, null);
				}, groundTime);
			}
			if (getConfig().getBoolean("penalty.clearInventory.enable")) {
				getServer().getScheduler().runTaskLater(this, () -> {
					player.getInventory().clear();
				}, 1L);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerTeleport(@NotNull PlayerTeleportEvent event) {
		if (storage != null) {
			Player player = event.getPlayer();
			PlayerData data = storage.get(player.getUniqueId());
			if (data.hasVolatileKey(GROUND_MARK_KEY)) {
				if (!event.getTo().getWorld().getName().equals(event.getFrom().getWorld().getName())) {
					event.setCancelled(true);
					player.sendMessage(getConfig().getString("penalty.ground.message"));
				}
			}
		}
	}

	@EventHandler
	public void onPlayerLeave(@NotNull PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (enabledWorlds.contains(player.getWorld().getName())) {
			monitoredPlayers.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onPlayerKicked(@NotNull PlayerKickEvent event) {
		Player player = event.getPlayer();
		if (enabledWorlds.contains(player.getWorld().getName()) && kickAlsoCounts) {
			monitoredPlayers.add(player.getUniqueId());
		}
	}

	@EventHandler
	public void onStorageReady(@NotNull StorageReadyEvent event) {
		this.storage = event.getStorage();
	}

	private PlayerDataStorage storage = null;
	private Set<UUID> monitoredPlayers = ConcurrentHashMap.newKeySet();
	private List<String> enabledWorlds = null;
	private Location lobby = null;
	private boolean kickAlsoCounts = false;
	private String message = null;
	private long limit = 0;
	private static final String GROUND_MARK_KEY = "rjtIsGrounded";
	private static final String PENALTY_MARK_KEY = "rjtPenaltyCount";
}
