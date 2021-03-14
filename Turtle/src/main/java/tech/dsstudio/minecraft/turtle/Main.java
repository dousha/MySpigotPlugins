package tech.dsstudio.minecraft.turtle;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.api.ResidenceApi;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().info("[DSStudio|CSDept] libTurtle");
		Plugin plugin = getServer().getPluginManager().getPlugin("Residence");
		if (plugin == null) {
			getLogger().info("Residence not found, turtles will have full permissions!");
			api = null;
		} else {
			api = Residence.getInstance().getAPI();
		}
		saveDefaultConfig();
		registry = new TurtleRegistry();
		turtleInfoBase = new File(getDataFolder(), "turtles");
		if (!turtleInfoBase.exists()) {
			if (!turtleInfoBase.mkdirs()) {
				getLogger().warning("Cannot create config folder!");
			}
		}
		getConfig()
				.getStringList("enabledWorlds")
				.stream()
				.map(name -> getServer().getWorld(name))
				.filter(Objects::nonNull)
				.forEach(world -> registry.loadTurtles(world, turtleInfoBase));
	}

	@Override
	public void onDisable() {
		registry.saveTurtles(turtleInfoBase);
	}

	public static ResidenceApi getResidenceApi() {
		return api;
	}

	public static TurtleRegistry getTurtleRegistry() {
		return registry;
	}

	private static ResidenceApi api;
	private static TurtleRegistry registry;
	private static File turtleInfoBase;
}
