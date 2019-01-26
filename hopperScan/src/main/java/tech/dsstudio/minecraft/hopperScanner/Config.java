package tech.dsstudio.minecraft.hopperScanner;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class Config {
	public static void loadConfig(FileConfiguration configuration) {
		map.clear();
		map.put("hopperSnapDistance", configuration.getDouble("hopperSnapDistance"));
		map.put("clusterSnapDistance", configuration.getDouble("clusterSnapDistance"));
		map.put("maxHopperCount", configuration.getDouble("maxHopperCount"));
	}

	public static double getValue(String key) {
		return map.getOrDefault(key, 0.0);
	}

	private static HashMap<String, Double> map = new HashMap<>();
}
