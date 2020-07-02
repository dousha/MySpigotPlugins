package tech.dsstudio.minecraft.useblock;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UseBlockApi extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(new MasterListener(), this);
		getServer().getPluginManager().registerEvents(this, this);
		loadConfig();
	}

	@EventHandler
	public void onBlockUsed(PlayerUsedBlockEvent event) {
		Material m = event.getBlock().getType();
		Set<String> commands = config.get(m);
		if (commands != null) {
			commands.forEach(command -> {
				event.getPlayer().performCommand(command);
			});
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("ubtr")) {
			reloadConfig();
			sender.sendMessage("[UseBlockThen] Config reloaded.");
			return true;
		} else {
			return false;
		}
	}

	private void loadConfig() {
		config = new HashMap<>();
		ConfigurationSection section = getConfig().getRoot();
		if (section != null) {
			section.getKeys(false).forEach(key -> {
				ConfigurationSection subsection = section.getConfigurationSection(key);
				if (subsection != null) {
					String material = subsection.getString("block");
					String command = subsection.getString("command");
					if (material != null && command != null) {
						try {
							Material m = Material.valueOf(material.toUpperCase().trim());
							config.computeIfAbsent(m, k -> new HashSet<>()).add(command);
						} catch (Exception ex) {
							getLogger().warning(material + " is not a material name!");
							ex.printStackTrace();
						}
					} else {
						getLogger().warning(key + " is skipped, because material/command key is missing");
					}
				}
			});
		}
	}

	private HashMap<Material, Set<String>> config;
}
