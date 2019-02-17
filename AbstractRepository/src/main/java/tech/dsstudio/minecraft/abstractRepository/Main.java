package tech.dsstudio.minecraft.abstractRepository;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		main = this;
		monitor = new RepositoryOperationMonitor();
		if (!base.exists()) {
			if (!base.mkdirs()) {
				throw new RuntimeException();
			}
		}
		getServer().getPluginManager().registerEvents(monitor, this);
	}

	@Override
	public void onDisable() {
		RepositoryManager.getRepositories().forEach(it -> it.saveConfiguration().ifPresent(config -> {
			String outName = it.getName();
			File out = new File(base, outName + ".yml");
			try {
				config.save(out);
			} catch (IOException e) {
				e.printStackTrace();
				// well, GG
			}
		}));
	}

	public static Main main;
	public static RepositoryOperationMonitor monitor;
	private File base = new File(getDataFolder(), "data");
}
