package tech.dsstudio.minecraft.portapack;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import tech.dsstudio.minecraft.portapack.internal.*;
import tech.dsstudio.minecraft.portapack.internal.journal.Journal;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePackManager;

import java.util.HashMap;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		BukkitScheduler scheduler = Bukkit.getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, dogFeeder, 0, 1);
		// N.B. Do NOT change this sequence!
		Instances.main = this;
		Instances.feeder = dogFeeder;
		try {
			Instances.journal = new Journal(getConfig().getConfigurationSection("database"));
		} catch (RuntimeException e) {
			System.err.println("Cannot connect to database correctly!!");
		}
		Instances.packer = new PortablePackManager(getDataFolder());
		Instances.config = new Config(getConfig());
		Instances.monitor = new Monitor();
		getServer().getPluginManager().registerEvents(Instances.monitor, this);
		watchdog.start();
	}

	@Override
	public void onDisable() {
		Instances.packer.savePacks();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmd = command.getName();
		if (cmd.startsWith("prt")) {
			CommandHandler handler = commands.get(cmd.substring(3));
			if (handler == null) return false;
			else handler.invoke(sender, args);
			return true;
		}
		return false;
	}

	private Integer parseInt(String str) {
		try {
			return new Integer(str);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	private DogFeeder dogFeeder = new DogFeeder();
	private Thread watchdog = new Thread(() -> {
		try {
			Thread.sleep(100);
			if (dogFeeder.check()) {
				System.out.println("[PortAPack:Watchdog] PANIC! SAVING INVENTORIES!");
				Instances.monitor.setLatch();
				Instances.packer.savePacks();
				Instances.monitor.clearLatch();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}, "PortAPackWatchdog");
	private HashMap<String, CommandHandler> commands = new HashMap<String, CommandHandler>(){
		{
			put("jp", (sender, args) -> {
				if (args.length < 1 || args.length > 2) return false;
				Journal journal = Instances.journal;
				Integer limit = args.length > 1 ? parseInt(args[1]) : Integer.valueOf(5);
				if (limit == null) {
					return false;
				}
				journal.printRecentJournals(sender, args[0], limit);
				return true;
			});
			put("jv", (sender, args) -> {
				if (args.length != 1) return false;

				return true;
			});
		}
	};
}
