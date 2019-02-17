package tech.dsstudio.minecraft.rightClickThrottler;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		reloadConfig();
		if (getConfig().getBoolean("interact.enable")) {
			final int maxInterval = getConfig().getInt("interact.maxInterval", 110);
			Bukkit.getPluginManager().registerEvents(new Listener() {
				private final HashMap<Integer, Record> interact = new HashMap<>(64, 0.8f);

				@EventHandler(priority = EventPriority.LOWEST)
				public void onInteract(PlayerInteractEvent event) {
					Action action = event.getAction();
					if ((action == Action.RIGHT_CLICK_BLOCK && !event.isCancelled() &&
							event.getMaterial() != Material.MINECART && event.getMaterial() != Material.BOAT) ||
							action == Action.RIGHT_CLICK_AIR)
						if (computeInterval(interact, event.getPlayer().getUniqueId())
								< maxInterval)
							event.setCancelled(true);
						else
							event.setCancelled(false);
				}
			}, this);
		}

		if (getConfig().getBoolean("hit.enable")) {
			final int maxInterval = getConfig().getInt("hit.maxInterval", 90);
			Bukkit.getPluginManager().registerEvents(new Listener() {
				private final HashMap<Integer, Record> hit = new HashMap<>(64, 0.8f);

				@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
				public void onHit(EntityDamageByEntityEvent event) {
					if (event.getDamager() instanceof HumanEntity)
						if (computeInterval(hit, event.getDamager().getUniqueId())
								< maxInterval)
							event.setCancelled(true);
				}
			}, this);
		}
		if (getConfig().getBoolean("shootArrow.enable")) {
			final int maxInterval = getConfig().getInt("shootArrow.maxInterval", 190);
			Bukkit.getPluginManager().registerEvents(new Listener() {
				private final HashMap<Integer, Record> shoot = new HashMap<>(64, 0.8f);

				@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
				public void onShoot(ProjectileLaunchEvent event) {
					Projectile projectile;
					if ((projectile = event.getEntity()) instanceof Arrow &&
							projectile.getShooter() instanceof HumanEntity)
						if (computeInterval(shoot, ((Entity) projectile.getShooter()).getUniqueId())
								< maxInterval)
							event.setCancelled(true);
				}
			}, this);
		}
		if (getConfig().getBoolean("fishing.enable")) {
			final int maxInterval = getConfig().getInt("fishing.maxInterval", 100);
			Bukkit.getPluginManager().registerEvents(new Listener() {
				private final HashMap<Integer, Record> fishing = new HashMap<>(64, 0.8f);

				@EventHandler(priority = EventPriority.LOWEST)
				public void onFishing(PlayerFishEvent event) {
					if (event.getState().equals(PlayerFishEvent.State.FISHING)) {
						if (computeInterval(fishing, event.getPlayer().getUniqueId()) < maxInterval)
							event.setCancelled(true);
					}
				}
			}, this);
		}
	}

	private static long computeInterval(Map<Integer, Record> map, Object obj) {
		int key = obj.hashCode();

		long curTime = System.currentTimeMillis();
		Record last = map.get(key);
		if (last == null)
			map.put(key, last = new Record(curTime));

		try {
			return curTime - last.time;
		} finally {
			last.time = curTime;
		}
	}

	public static class Record {
		private long time;

		public Record(long time) {
			this.time = time;
		}

		@Override
		public boolean equals(Object obj) {
			return this == obj ||
					(obj != null && getClass() != obj.getClass() && this.time == ((Record) obj).time);
		}

		@Override
		public int hashCode() {
			return 31 * 7 + (int) (this.time ^ (this.time >>> 32));
		}

		@Override
		public String toString() {
			return Long.toString(time);
		}
	}
}