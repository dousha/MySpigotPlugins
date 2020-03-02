package tech.dsstudio.minecraft.taskhook;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class PlayerTaskHookApi extends JavaPlugin implements Listener {
	public PlayerTaskHookApi() {
		instance = this;
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		instance = this;
		masterTask = getServer().getScheduler().runTaskTimer(this, () -> {
			// this might cause a serious performance issue
			long current = currentTick.getAndIncrement();
			activeTasks.forEachValue(8, tasks -> {
				tasks.removeAll(tasks.stream().filter(task -> task.needRemoval).collect(Collectors.toSet()));
				tasks.forEach(task -> {
					task.remainingWaitTime = task.executionTime - current;
					if (task.remainingWaitTime <= 0) {
						task.runnable.run();
						if (task.repeat) {
							task.assignedTime = current;
							task.executionTime = task.assignedTime + task.interval;
						} else {
							task.needRemoval = true;
						}
					}
				});
			});
		}, 1L, 1L);
		getLogger().info("PlayerTaskHook loaded");
	}

	@Override
	public void onDisable() {
		masterTask.cancel();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (suspendedTasks.containsKey(uuid)) {
			// resume all
			getLogger().info("Resuming tasks for " + uuid.toString());
			ConcurrentHashSet<TaskDescriptor> tasks = suspendedTasks.get(uuid);
			suspendedTasks.remove(uuid);
			tasks.forEach(task -> task.assignedTime = currentTick.get() - task.remainingWaitTime);
			activeTasks.put(uuid, tasks);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent event) {
		UUID uuid = event.getPlayer().getUniqueId();
		if (activeTasks.containsKey(uuid)) {
			// suspend all
			getLogger().info("Suspending tasks for " + uuid.toString());
			ConcurrentHashSet<TaskDescriptor> tasks = activeTasks.get(uuid);
			activeTasks.remove(uuid);
			suspendedTasks.put(uuid, tasks);
		}
	}

	public static TaskDescriptor runTaskLater(JavaPlugin owner, Player player, Runnable work, long delay) {
		if (instance == null) {
			throw new IllegalStateException("Task was assigned before plugin was loaded");
		}
		TaskDescriptor descriptor = new TaskDescriptor();
		descriptor.taskId = UUID.randomUUID();
		descriptor.owner = owner;
		descriptor.runnable = work;
		descriptor.delay = delay;
		descriptor.repeat = false;
		descriptor.interval = 0;
		descriptor.remainingWaitTime = delay;
		descriptor.playerUuid = player.getUniqueId();
		descriptor.assignedTime = currentTick.get();
		descriptor.executionTime = descriptor.assignedTime + descriptor.delay;
		instance.activeTasks.computeIfAbsent(descriptor.playerUuid, (k) -> new ConcurrentHashSet<>()).add(descriptor);
		return descriptor;
	}

	public static TaskDescriptor runTaskTimer(JavaPlugin owner, Player player, Runnable work, long delay, long interval) {
		if (instance == null) {
			throw new IllegalStateException("Task was assigned before plugin was loaded");
		}
		TaskDescriptor descriptor = new TaskDescriptor();
		descriptor.taskId = UUID.randomUUID();
		descriptor.owner = owner;
		descriptor.runnable = work;
		descriptor.delay = delay;
		descriptor.repeat = true;
		descriptor.interval = interval;
		descriptor.remainingWaitTime = delay;
		descriptor.playerUuid = player.getUniqueId();
		descriptor.assignedTime = currentTick.get();
		descriptor.executionTime = descriptor.assignedTime + descriptor.delay;
		instance.activeTasks.computeIfAbsent(descriptor.playerUuid, (k) -> new ConcurrentHashSet<>()).add(descriptor);
		return descriptor;
	}

	public static void suspend(TaskDescriptor descriptor) {
		try {
			instance.activeTasks.get(descriptor.playerUuid).remove(descriptor);
			if (!instance.suspendedTasks.containsKey(descriptor.playerUuid)) {
				instance.suspendedTasks.put(descriptor.playerUuid, new ConcurrentHashSet<>());
			}
			instance.suspendedTasks.get(descriptor.playerUuid).add(descriptor);
		} catch (NullPointerException ex) {
			instance.getLogger().warning("Trying to suspend a task that was not active for this player");
			ex.printStackTrace();
		}
	}

	public static void resume(TaskDescriptor descriptor) {
		try {
			instance.suspendedTasks.computeIfAbsent(descriptor.playerUuid, k -> new ConcurrentHashSet<>()).remove(descriptor);
			descriptor.assignedTime = currentTick.get() - descriptor.remainingWaitTime;
			instance.activeTasks.computeIfAbsent(descriptor.playerUuid, k -> new ConcurrentHashSet<>()).add(descriptor);
		} catch (NullPointerException ex) {
			instance.getLogger().warning("Trying to resume a task that was not suspended for this player");
			ex.printStackTrace();
		}
	}

	public static void cancel(TaskDescriptor descriptor) {
		descriptor.needRemoval = true;
		instance.activeTasks.computeIfAbsent(descriptor.playerUuid, k -> new ConcurrentHashSet<>()).remove(descriptor);
	}

	private ConcurrentHashMap<UUID, ConcurrentHashSet<TaskDescriptor>> suspendedTasks = new ConcurrentHashMap<>();
	private ConcurrentHashMap<UUID, ConcurrentHashSet<TaskDescriptor>> activeTasks = new ConcurrentHashMap<>();
	private BukkitTask masterTask = null;
	private static PlayerTaskHookApi instance = null;
	private static AtomicLong currentTick = new AtomicLong(0L);
}
