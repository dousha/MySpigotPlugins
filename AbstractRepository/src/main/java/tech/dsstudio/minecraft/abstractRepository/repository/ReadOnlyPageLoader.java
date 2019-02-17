package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.buttons.Stripe;

public class ReadOnlyPageLoader {
	public static Stripe[] createPage(Inventory inventory, ConfigurationSection section) {
		int rowCount = section.getInt("row");
		Stripe[] stripes = new Stripe[rowCount];
		ConfigurationSection stripeSection = section.getConfigurationSection("stripes");
		stripeSection.getKeys(false).forEach(it -> {
			int stripIndex = Integer.parseInt(it);
			stripes[stripIndex] = new Stripe(stripeSection.getConfigurationSection(it));
		});
		ItemStack[] items = new ItemStack[rowCount * 9];
		for (int j = 0; j < rowCount; j++) {
			System.arraycopy(stripes[j].renderItems(), 0, items, 9 * j, 9);
		}
		inventory.setStorageContents(items);
		return stripes;
	}

	public static void updatePage(Inventory inventory, Stripe[] stripes) {
		ItemStack[] storage = new ItemStack[inventory.getSize()];
		for (int i = 0; i < storage.length / 9; i++) {
			System.arraycopy(stripes[i].renderItems(), 0, storage, 9 * i, 9);
		}
		inventory.setStorageContents(storage);
	}
}
