package tech.dsstudio.minecarft.testing;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TestingApi extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().warning("For testing purpose only, do NOT use on production server");
		getLogger().warning("Performance overhead might occur, and may cause information leak");
		instance = this;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}

	public static TestingApi getInstance() {
		return instance;
	}

	private static TestingApi instance = null;
}
