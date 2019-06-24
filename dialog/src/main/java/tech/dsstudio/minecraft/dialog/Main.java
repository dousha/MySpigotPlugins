package tech.dsstudio.minecraft.dialog;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;

public class Main extends JavaPlugin {
	public Main() {
		super();
	}

	protected Main(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
		super(loader, description, dataFolder, file);
	}

	@Override
	public void onEnable() {
		manager = new SessionManager();
		EventHandlers handlers = new EventHandlers(manager);
		this.getServer().getPluginManager().registerEvents(handlers, this);
	}

	@Override
	public void onDisable() {
		manager.kill();
	}

	public SessionManager getSessionManager() {
		return manager;
	}

	private static SessionManager manager;
}
