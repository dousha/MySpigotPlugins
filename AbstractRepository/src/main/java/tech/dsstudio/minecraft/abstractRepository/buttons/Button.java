package tech.dsstudio.minecraft.abstractRepository.buttons;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public abstract class Button {
	public Button(Material material, String name, List<String> lore) {
		String name1 = name.replace('&', '\u00a7');
		List<String> lore1 = lore.stream().map(it -> it.replace('&', '\u00a7')).collect(Collectors.toList());

		ItemMeta meta = Bukkit.getItemFactory().getItemMeta(material);
		meta.setDisplayName(name1);
		meta.setLore(lore1);
		ItemStack icon = new ItemStack(material, 1);
		icon.setItemMeta(meta);
		this.bakedIcon = icon;
	}

	public abstract void onClick(AbstractRepository repository, UUID uuid);

	public ItemStack render() {
		if (visible) {
			return bakedIcon;
		} else {
			return invisibleIcon;
		}
	}

	public static Button createButton(ConfigurationSection config) {
		if (config == null) return null;
		try {
			ButtonType type = ButtonType.valueOf(config.getString("type"));
			Material material = Material.valueOf(config.getString("icon"));
			String name = config.getString("name");
			List<String> lore = null;
			if (config.contains("lore")) {
				lore = config.getStringList("lore");
			}
			switch (type) {
				case PREVIOUS_PAGE:
					return new PreviousPageButton(material, name, lore);
				case NEXT_PAGE:
					return new NextPageButton(material, name, lore);
				case COMMAND:
					return new CommandButton(material, name, lore, config.getString("command"));
				case INFO:
					return new InfoButton(material, name, lore);
				default:
					return null;
			}
		} catch (Exception ignored) {
			return null;
		}
	}

	protected boolean visible = true;
	private ItemStack bakedIcon;
	private static final ItemStack invisibleIcon = new ItemStack(Material.AIR, 1);
}
