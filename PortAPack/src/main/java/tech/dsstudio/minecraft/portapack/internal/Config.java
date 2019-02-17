package tech.dsstudio.minecraft.portapack.internal;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import tech.dsstudio.minecraft.portapack.buttons.Button;

public class Config {
	public Config(FileConfiguration config) {
		this.config = config;
		doLoad();
	}

	public void reload() {
		doLoad();
	}

	public int getFreePage() {
		return freePage;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public Cascade<String> getPremiums() {
		return premiums;
	}

	public String getTitle() {
		return title;
	}

	public Button[] getFunctionStripe() {
		return functionStripe;
	}

	private void doLoad() {
		title = config.getString("title");
		parseItemStripe(config.getConfigurationSection("buttons"));
		freePage = config.getInt("freePage");
		maxPage = config.getInt("maxPage");
		parsePremiums(config.getConfigurationSection("pagePerm"));
		Instances.feeder.setTolerance(config.getLong("watchdogTolerance"));
	}

	private void parseItemStripe(ConfigurationSection section) {
		functionStripe = new Button[9];
		section.getKeys(false).forEach(it -> {
			int index = Integer.parseInt(it);
			functionStripe[index] = Button.createButton(section.getConfigurationSection(it));
		});
	}

	private void parsePremiums(ConfigurationSection section) {
		premiums = new Cascade<>();
		section.getKeys(false).forEach(it -> {
			int index = Integer.parseInt(it);
			premiums.put(index, section.getString(it));
		});
	}

	private FileConfiguration config;
	private String title;
	private Button[] functionStripe;
	private int freePage, maxPage;
	private Cascade<String> premiums;
}
