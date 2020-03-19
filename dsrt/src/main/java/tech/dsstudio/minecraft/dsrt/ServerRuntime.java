package tech.dsstudio.minecraft.dsrt;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ServerRuntime extends JavaPlugin {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		try {
			hub = new FileHub(config);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable() {
		if (hub != null)
			hub.closeAll();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (args.length < 1) {
			sender.sendMessage("Need 1 arg");
			return true;
		}
		Optional<RuntimeFileHandler> handler = hub.open(sender, args[0]);
		if (!handler.isPresent()) {
			sender.sendMessage("Cannot get handler, check console or the file is occupied");
			return true;
		}
		switch (command.getName()) {
			case "ls":
				handler.ifPresent(it -> {
					try {
						Files.walk(Paths.get(it.getFile().toURI())).forEach(entry -> sender.sendMessage(entry.getFileName().toString()));
					} catch (IOException e) {
						sender.sendMessage("Cannot walk file. You should see the console!");
						e.printStackTrace();
					}
					hub.close(it);
				});
				break;
			case "rm":
				handler.ifPresent(it -> {
					hub.delete(it);
					sender.sendMessage("Deleted");
				});
				break;
			case "touch":
				handler.ifPresent(it -> {
					hub.close(it);
					sender.sendMessage("Touched");
				});
				break;
		}
		return super.onCommand(sender, command, label, args);
	}

	public static FileHub getHub() {
		return hub;
	}

	private static FileHub hub = null;
}
