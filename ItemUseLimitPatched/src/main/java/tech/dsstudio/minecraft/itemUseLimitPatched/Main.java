package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		new Config(getConfig());
		Config.reload();
		sweeper.notifyUpdate();
		sweeperHandle = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, sweeper, 0, 10);
		transactionHandle = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, transactionManager, 0, 2);
		getServer().getPluginManager().registerEvents(monitor, this);
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTask(sweeperHandle);
		Bukkit.getScheduler().cancelTask(transactionHandle);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("iul")) {
			if (sender.hasPermission("itemuselimit.reload")) {
				Config.reload();
				sweeper.notifyUpdate();
				sender.sendMessage("[ItemUseLimitPatched] 已重载");
			}
			return true;
		}
		return false;
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public PermissionSweeper getSweeper() {
		return sweeper;
	}

	private Monitor monitor = new Monitor(this);
	private PermissionSweeper sweeper = new PermissionSweeper(this);
	private TransactionManager transactionManager = new TransactionManager(this);
	private int sweeperHandle = 0, transactionHandle = 0;
}
