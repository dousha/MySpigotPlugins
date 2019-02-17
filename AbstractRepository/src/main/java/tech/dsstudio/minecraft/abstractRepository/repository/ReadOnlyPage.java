package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import tech.dsstudio.minecraft.abstractRepository.buttons.Stripe;

/**
 * ReadOnlyPage contains a repository page. Inventory could be obtained with {@link ReadOnlyPage#renderPage()}.
 */
public class ReadOnlyPage {
	public ReadOnlyPage(ConfigurationSection section) {
		this.section = section;
		inventory = Bukkit.createInventory(null, section.getInt("row") * 9);
		stripes = ReadOnlyPageLoader.createPage(inventory, section);
	}

	public Inventory renderPage() {
		ReadOnlyPageLoader.updatePage(inventory, stripes);
		return inventory;
	}

	public void setConfiguration(ConfigurationSection section) {
		this.section = section;
		stripes = ReadOnlyPageLoader.createPage(inventory, section);
	}

	private ConfigurationSection section;
	private Inventory inventory;
	private Stripe[] stripes;
}
