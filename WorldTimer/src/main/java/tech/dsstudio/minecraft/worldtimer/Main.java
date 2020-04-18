package tech.dsstudio.minecraft.worldtimer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;
import tech.dsstudio.minecraft.taskhook.PlayerTaskHookApi;
import tech.dsstudio.minecraft.taskhook.TaskDescriptor;
import tech.dsstudio.minecraft.worldtimer.objects.WorldEffectDescriptor;
import tech.dsstudio.minecraft.worldtimer.objects.WorldLimitDescriptor;
import tech.dsstudio.minecraft.worldtimer.runnables.MasterRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().callEvent(new RequestForStorageEvent());
	}

	@EventHandler
	public void onStorageReady(StorageReadyEvent e) {
		this.storage = e.getStorage();
		load();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		updateWorldLimit(e.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		Player player = e.getPlayer();
		PlayerData data = storage.get(player.getUniqueId());
		data.set(WORLD_KEY_NAME, player.getWorld().getName());
	}

	@EventHandler
	public void onPlayerChangingWorld(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();
		String targetWorldName = player.getWorld().getName();
		String fromWorldName = e.getFrom().getName();
		PlayerData data = storage.get(player.getUniqueId());
		if (limitDescriptors.containsKey(fromWorldName)) {
			WorldLimitDescriptor descriptor = limitDescriptors.get(fromWorldName);
			data.set(WORLD_KEY_NAME, fromWorldName);
			removeWorldLimit(player, data, descriptor);
		}
		data.set(WORLD_KEY_NAME, targetWorldName);
		if (limitDescriptors.containsKey(targetWorldName) && !player.hasPermission("wtexempt")) {
			applyWorldLimit(player, data);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("wt")) {
			if (args.length > 2 || args.length < 1) {
				return false;
			}
			String playerName;
			if (args.length == 1) {
				if (!(sender instanceof Player)) {
					sender.sendMessage("You have to specify a player");
					return false;
				} else {
					playerName = sender.getName();
				}
			} else {
				playerName = args[1];
			}
			Player player = getServer().getPlayer(playerName);
			if (player == null) {
				sender.sendMessage("Player is not online");
				return true;
			}
			switch (args[0]) {
				case "pause":
					suspendTimer(player);
					break;
				case "resume":
					resumeTimer(player);
					break;
				default:
					return false;
			}
			return true;
		} else {
			return false;
		}
	}

	public void suspendTimer(@NotNull Player player) {
		PlayerData data = storage.get(player.getUniqueId());
		if (data.hasVolatileKey(MASTER_TASK_KEY_NAME)) {
			TaskDescriptor task = (TaskDescriptor) data.getVolatileOrFail(MASTER_TASK_KEY_NAME);
			task.suspend();
		}
		if (data.hasVolatileKey(BOSS_BAR_TASK_KEY_NAME)) {
			TaskDescriptor task = (TaskDescriptor) data.getVolatileOrFail(BOSS_BAR_TASK_KEY_NAME);
			task.suspend();
		}
	}
	
	public void resumeTimer(@NotNull Player player) {
		PlayerData data = storage.get(player.getUniqueId());
		if (data.hasVolatileKey(MASTER_TASK_KEY_NAME)) {
			TaskDescriptor task = (TaskDescriptor) data.getVolatileOrFail(MASTER_TASK_KEY_NAME);
			task.resume();
		}
		if (data.hasVolatileKey(BOSS_BAR_TASK_KEY_NAME)) {
			TaskDescriptor task = (TaskDescriptor) data.getVolatileOrFail(BOSS_BAR_TASK_KEY_NAME);
			task.resume();
		}
	}

	private void applyWorldLimit(@NotNull Player player, @NotNull PlayerData data) {
		if (data.hasKey(WORLD_KEY_NAME)) {
			String worldName = data.getString(WORLD_KEY_NAME);
			WorldLimitDescriptor limitDescriptor = limitDescriptors.get(worldName);
			if (limitDescriptor == null) {
				getLogger().info("World " + worldName + " is not a limited world, why are you doing this?");
				return;
			}
			setMasterTimer(player, data, limitDescriptor);
			setBossBarUpdater(player, data, limitDescriptor);
			getLogger().info("Player " + player.getDisplayName() + " entered a timed world");
		} else {
			getLogger().warning("Player entered an unknown world");
		}
	}

	private void removeWorldLimit(@NotNull Player player, @NotNull PlayerData data, @NotNull WorldLimitDescriptor descriptor) {
		if (data.hasVolatileKey(MASTER_TASK_KEY_NAME)) {
			TaskDescriptor master = (TaskDescriptor) data.getVolatileOrFail(MASTER_TASK_KEY_NAME);
			master.cancel();
			data.setVolatile(MASTER_TASK_KEY_NAME, null);
			List<TaskDescriptor> children = (List<TaskDescriptor>) data.getVolatile(CHILD_TASK_KEY_NAME);
			if (children != null) {
				children.forEach(TaskDescriptor::cancel);
				data.setVolatile(CHILD_TASK_KEY_NAME, null);
			}
			removeBossBar(player, data);
			if (!descriptor.lingering) {
				getServer().getScheduler().runTaskLater(this, () -> descriptor.debuff.forEach(debuff -> player.removePotionEffect(debuff.effect.getType())), 1L);
			}
			getLogger().info("Timers for player " + player.getDisplayName() + " has lifted");
		} else {
			getLogger().warning("Inconsistent state! Trying to remove not applied world limit!");
		}
	}

	private void removeBossBar(@NotNull Player player, @NotNull PlayerData data) {
		BossBar bar = (BossBar) data.getVolatile(BOSS_BAR_KEY_NAME);
		if (bar != null) {
			bar.setVisible(false);
			bar.removePlayer(player);
			data.setVolatile(BOSS_BAR_KEY_NAME, null);
		} else {
			// wtf?
			getLogger().warning("Inconsistent state! No boss bar found!");
		}
		TaskDescriptor updater = (TaskDescriptor) data.getVolatile(BOSS_BAR_TASK_KEY_NAME);
		if (updater != null) {
			updater.cancel();
			data.setVolatile(BOSS_BAR_TASK_KEY_NAME, null);
		}
	}

	private void updateWorldLimit(@NotNull Player player) {
		PlayerData data = storage.get(player.getUniqueId());
		if (data == null) {
			getLogger().warning("Incosistent state! Player data is not loaded");
			return;
		}
		String currentWorldName = player.getWorld().getName();
		if (data.hasKey(WORLD_KEY_NAME)) {
			String lastWorldName = data.getString(WORLD_KEY_NAME);
			if (!lastWorldName.equals(currentWorldName)) {
				// player spawned in a different world
				if (limitDescriptors.containsKey(lastWorldName)) {
					WorldLimitDescriptor descriptor = limitDescriptors.get(lastWorldName);
					removeWorldLimit(player, data, descriptor);
				}
			} else {
				// player entered the game, and config seems to be updated
				if (limitDescriptors.containsKey(currentWorldName) && !data.hasVolatileKey(MASTER_TASK_KEY_NAME)) {
					applyWorldLimit(player, data);
				} // the task would resume otherwise
			}
		} else {
			// player joined the game for the first time, maybe
			if (limitDescriptors.containsKey(currentWorldName) && !player.hasPermission("wtexempt")) {
				data.set(WORLD_KEY_NAME, currentWorldName);
				applyWorldLimit(player, data);
			}
		}
	}

	private void setMasterTimer(@NotNull Player player, @NotNull PlayerData data, @NotNull WorldLimitDescriptor descriptor) {
		Runnable masterRunnable = new MasterRunnable(this, player, data, descriptor);
		final long limit = descriptor.limit * 20;
		TaskDescriptor masterTask = PlayerTaskHookApi.runTaskLater(this, player, masterRunnable, limit);
		data.setVolatile(MASTER_TASK_KEY_NAME, masterTask);
	}

	private void setBossBarUpdater(@NotNull Player player, @NotNull PlayerData data, @NotNull WorldLimitDescriptor descriptor) {
		BossBar bar = getServer().createBossBar(descriptor.title, descriptor.color, BarStyle.SOLID);
		bar.setProgress(1.00);
		bar.setVisible(true);
		bar.addPlayer(player);
		data.setVolatile(BOSS_BAR_KEY_NAME, bar);
		final double tickFraction = 1.0 / ((double) descriptor.limit);
		data.setVolatile(BOSS_BAR_TASK_KEY_NAME, PlayerTaskHookApi.runTaskTimer(this, player, () -> {
			bar.setProgress(clampProgress(bar.getProgress() - tickFraction));
		}, 20L, 20L));
	}

	private static double clampProgress(double value) {
		return Math.max(0.0, Math.min(1.0, value));
	}

	private void load() {
		getConfig().getRoot().getKeys(false).forEach(world -> {
			ConfigurationSection section = getConfig().getConfigurationSection(world);
			WorldLimitDescriptor descriptor = new WorldLimitDescriptor();
			descriptor.name = world;
			descriptor.title = section.getString("title");
			descriptor.color = BarColor.valueOf(section.getString("color").toUpperCase());
			descriptor.limit = section.getInt("limit", 0);
			descriptor.lingering = section.getBoolean("lingering", false);
			ArrayList<WorldEffectDescriptor> debuffs = new ArrayList<>();
			AtomicReference<ArrayList<WorldEffectDescriptor>> ref = new AtomicReference<>(debuffs);
			section.getList("debuff").stream().map(it -> (LinkedHashMap<String, Object>) it).forEach(map -> {
				WorldEffectDescriptor desc = new WorldEffectDescriptor();
				desc.effect = new PotionEffect(PotionEffectType.getByName((String) map.get("name")),
						(int) map.getOrDefault("duration", 100),
						(int) map.getOrDefault("level", 1));
				desc.interval = (int) map.getOrDefault("interval", map.getOrDefault("duration", 100));
				desc.offset = (int) map.getOrDefault("offset", 0);
				getLogger().info(desc.toString());
				ref.get().add(desc);
			});
			descriptor.debuff = debuffs;
			getLogger().info("" + descriptor.debuff.size() + " debuff(s) configured for world " + world);
			limitDescriptors.put(world, descriptor);
		});
		getLogger().info("Loaded " + limitDescriptors.size() + " world(s)");
	}

	private PlayerDataStorage storage = null;
	private HashMap<String, WorldLimitDescriptor> limitDescriptors = new HashMap<>();
	private static final String MASTER_TASK_KEY_NAME = "wtMasterTask";
	public static final String CHILD_TASK_KEY_NAME = "wtTasks";
	private static final String BOSS_BAR_TASK_KEY_NAME = "wtBossBarTask";
	private static final String BOSS_BAR_KEY_NAME = "wtBossBar";
	private static final String WORLD_KEY_NAME = "wtWorldName";
}
