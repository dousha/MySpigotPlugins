package tech.dsstudio.minecraft.worldtimer;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;
import tech.dsstudio.minecraft.worldtimer.objects.WorldEffectDescriptor;
import tech.dsstudio.minecraft.worldtimer.objects.WorldLimitDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onStorageReady(StorageReadyEvent e) {
		this.storage = e.getStorage();
		load();
	}

	@EventHandler
	public void onPlayerChangingWorld(PlayerChangedWorldEvent e) {
		Player player = e.getPlayer();
		String targetWorldName = player.getWorld().getName();
		String fromWorldName = e.getFrom().getName();
		PlayerData data = storage.get(player.getUniqueId());
		if (worlds.containsKey(targetWorldName) && !player.hasPermission("wtexempt")) {
			WorldLimitDescriptor descriptor = worlds.get(targetWorldName);
			getLogger().info("Player " + player.getDisplayName() + " is entering a time limiting world");
			ArrayList<BukkitTask> tasks = new ArrayList<>();
			AtomicReference<ArrayList<BukkitTask>> ref = new AtomicReference<>(tasks);
			descriptor.debuff.forEach(debuff -> {
				long offset = descriptor.limit * 20 + debuff.offset;
				BukkitTask task = getServer().getScheduler().runTaskTimer(this, () -> player.addPotionEffect(debuff.effect), offset, debuff.interval);
				ref.get().add(task);
			});
			BossBar bar = getServer().createBossBar(descriptor.title, descriptor.color, BarStyle.SOLID);
			bar.addPlayer(player);
			bar.setProgress(1.00);
			double secondFraction = 1.0 / ((double) descriptor.limit);
			BukkitTask barUpdater = getServer().getScheduler().runTaskTimer(this, () -> bar.setProgress(bar.getProgress() <= 0.0 ? 0.0 : bar.getProgress() - secondFraction), 20L,20L);
			tasks.add(barUpdater);
			data.setVolatile(TASKS_KEY_NAME, tasks);
		} else if (worlds.containsKey(fromWorldName) && !player.hasPermission("wtexempt")) {
			WorldLimitDescriptor descriptor = worlds.get(targetWorldName);
			ArrayList<BukkitTask> tasks = (ArrayList<BukkitTask>) data.getVolatile(TASKS_KEY_NAME);
			if (tasks != null) {
				tasks.forEach(BukkitTask::cancel);
			}
			if (!descriptor.lingering) {
				// clear all
				descriptor.debuff.forEach(debuff -> {
					getServer().getScheduler().runTaskLater(this, () -> player.removePotionEffect(debuff.effect.getType()), 1L);
				});
			}
		}
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
						(int) map.getOrDefault("level", 1),
						(int) map.getOrDefault("duration", 20));
				desc.interval = (int) map.getOrDefault("interval", 20);
				desc.offset = (int) map.getOrDefault("offset", 0);
				ref.get().add(desc);
			});
			descriptor.debuff = debuffs;
			worlds.put(world, descriptor);
		});
		getLogger().info("Loaded " + worlds.size() + " world(s)");
	}

	private PlayerDataStorage storage = null;
	private HashMap<String, WorldLimitDescriptor> worlds = new HashMap<>();
	private static final String TASKS_KEY_NAME = "wtTasks";
}
