package tech.dsstudio.minecraft.dialog;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
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

	public static SessionManager getSessionManager() {
		return manager;
	}

	private static SessionManager manager;
}
