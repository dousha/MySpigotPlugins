package tech.dsstudio.minecraft.tracker;

import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!tracking.contains(uuid)) {
			BossBar bar = getServer().createBossBar("Test", BarColor.BLUE, BarStyle.SOLID);
			bar.setProgress(1.00);
			double fraction = 1.0 / 60000.0;
			tech.dsstudio.minecraft.taskhook.Main.runTaskTimer(this, player, () -> {
				double newProgress = bar.getProgress() - fraction;
				if (newProgress <= 0.0) {
					newProgress = 0.0;
				}
				bar.setProgress(newProgress);
				getLogger().info(String.valueOf(newProgress));
			}, 0L, 20L);
			tracking.add(uuid);
		}
	}

	private ConcurrentSkipListSet<UUID> tracking = new ConcurrentSkipListSet<>();
}
