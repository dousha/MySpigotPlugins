package tech.dsstudio.minecraft.attributes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.RpnCompiler;
import tech.dsstudio.minecraft.attributes.evaluator.*;
import tech.dsstudio.minecraft.attributes.events.ArmorListener;
import tech.dsstudio.minecraft.attributes.handlers.CommandHandler;
import tech.dsstudio.minecraft.attributes.handlers.MasterEventHandler;
import tech.dsstudio.minecraft.attributes.types.PlayerAttributeSet;
import tech.dsstudio.minecraft.attributes.types.PlayerAttributes;
import tech.dsstudio.minecraft.attributes.utils.Log;
import tech.dsstudio.minecraft.attributes.utils.StorageProxy;
import tech.dsstudio.minecraft.playerdata.PlayerData;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerAttributeApi extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		loadConfig();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this, this);
		pm.registerEvents(new ArmorListener(new ArrayList<>()), this);
		pm.callEvent(new RequestForStorageEvent());
	}

	@Override
	public void onDisable() {
	}

	public void loadConfig() {
		// general
		FileConfiguration config = getConfig();
		ConfigurationSection generalConfigurations = config.getConfigurationSection("general");
		Log.setDebug(generalConfigurations.getBoolean("debug"));
		// attribute sets
		ConfigurationSection attributeConfigurations = config.getConfigurationSection("attributeSets");
		attributeConfigurations.getKeys(false).forEach(key -> {
			ConfigurationSection attributeSection = attributeConfigurations.getConfigurationSection(key);
			attributeSets.put(key, PlayerAttributeSet.fromConfigurationSection(attributeSection));
		});
		// evaluators
		reloadConfiguredEvaluators();
	}

	public void reloadConfiguredEvaluators() {
		ConfigurationSection evaluationConfigurations = getConfig().getConfigurationSection("evaluators");
		List<String> keys = new ArrayList<>(evaluationConfigurations.getKeys(false));
		keys.forEach(key -> {
			ConfigurationSection evaluatorSection = evaluationConfigurations.getConfigurationSection(key);
			if (proxyEvaluators.containsKey(key)) {
				// update
				ConfigurationSection evaluatorConfiguration = evaluationConfigurations.getConfigurationSection(key);
				ProxyEvaluator proxy = proxyEvaluators.get(key);
				ConfigurationBasedEvaluator evaluator = new ConfigurationBasedEvaluator(EvaluationType.valueOf(evaluatorConfiguration.getString("type").toUpperCase()), evaluatorConfiguration);
				proxy.attachEvaluator(evaluator);
			} else {
				// create
				ProxyEvaluator evaluator = new ProxyEvaluator(evaluatorSection);
				proxyEvaluators.put(key, evaluator);
				addEvaluator(evaluator);
			}
		});
		// delete old ones
		proxyEvaluators.keySet().stream().filter(it -> !keys.contains(it)).forEach(it -> {
			ProxyEvaluator evaluator = proxyEvaluators.get(it);
			evaluator.detachEvaluator();
			proxyEvaluators.remove(it);
			removeEvaluator(evaluator);
		});
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
		if (command.getName().equals("pat")) {
			return commandHandler.onCommand(sender, command, label, args);
		} else {
			return false;
		}
	}

	public void addEvaluator(Evaluator evaluator) {
		masterEvaluator.addEvaluator(evaluator);
	}

	public void removeEvaluator(Evaluator evaluator) {
		masterEvaluator.removeEvaluator(evaluator);
	}

	public MasterEvaluator getMasterEvaluator() {
		return masterEvaluator;
	}

	public MasterEventHandler getMasterEventHandler() {
		return masterEventHandler;
	}

	public PlayerDataStorage getStorage() {
		return storage;
	}

	@NotNull
	@Contract("null -> fail; _ -> !null")
	public PlayerAttributes getPlayerAttributes(@NotNull UUID uuid) {
		if (storage == null) {
			throw new IllegalStateException("Player attribute requested before storage is ready");
		}
		return (PlayerAttributes) storage.get(uuid).computeIfAbsent(ATTRIBUTE_KEY, (k) -> new PlayerAttributes());
	}

	@Nullable
	@Contract("null -> fail")
	public String getPlayerAttributeSet(@NotNull UUID id) {
		if (storage == null) {
			throw new IllegalStateException("Player attribute requested before storage is ready");
		}
		return (String) storage.get(id).get(ATTRIBUTE_SET_KEY);
	}

	@Contract("null, _ -> fail")
	public void setPlayerAttributeSet(@NotNull UUID id, @Nullable String set) {
		if (storage == null) {
			throw new IllegalStateException("Player attribute requested before storage is ready");
		}
		PlayerData data = storage.get(id);
		if (set == null) {
			data.set(ATTRIBUTE_SET_KEY, null);
		} else {
			data.set(ATTRIBUTE_SET_KEY, attributeSets.get(set));
		}
	}

	@EventHandler
	public void onStorageReady(@NotNull StorageReadyEvent event) {
		this.storage = event.getStorage();
		StorageProxy.setStorage(this.storage);
		this.masterEventHandler = new MasterEventHandler(this);
		getServer().getPluginManager().registerEvents(this.masterEventHandler, this);
	}

	public static final String ATTRIBUTE_KEY = "paAttributes";
	public static final String ATTRIBUTE_SET_KEY = "paAttrSet";
	public static RpnCompiler rpnCompiler = new RpnCompiler();
	private PlayerDataStorage storage = null;
	private MasterEventHandler masterEventHandler;
	private CommandHandler commandHandler = new CommandHandler(this);
	private MasterEvaluator masterEvaluator = new MasterEvaluator();
	private HashMap<String, ProxyEvaluator> proxyEvaluators = new HashMap<>();
	private HashMap<String, PlayerAttributeSet> attributeSets = new HashMap<>();
}
