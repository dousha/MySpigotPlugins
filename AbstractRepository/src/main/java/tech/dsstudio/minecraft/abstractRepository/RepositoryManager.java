package tech.dsstudio.minecraft.abstractRepository;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;

public class RepositoryManager {
	public static boolean registerRepository(AbstractRepository repository, JavaPlugin plugin) {
		File base = new File(Main.main.getDataFolder(), repository.getName());
		if (!base.exists()) {
			if (!base.mkdirs()) {
				// GG, dude
				System.err.println("[RepositoryManager] Cannot create data folder for " + plugin.getName() + " with name " + repository.getName());
				return false;
			}
		} else {
			File in = new File(base, repository.getName());
			if (in.exists()) {
				YamlConfiguration data = new YamlConfiguration();
				try {
					data.load(in);
				} catch (IOException | InvalidConfigurationException e) {
					// how come
					e.printStackTrace();
					return false;
				}
				repository.loadConfiguration(plugin.getConfig(), data);
			} else {
				repository.initialize(base, plugin.getConfig());
			}
		}
		repositories.add(repository);
		return true;
	}

	public static HashSet<AbstractRepository> getRepositories() {
		return repositories;
	}

	private static HashSet<AbstractRepository> repositories = new HashSet<>();
}
