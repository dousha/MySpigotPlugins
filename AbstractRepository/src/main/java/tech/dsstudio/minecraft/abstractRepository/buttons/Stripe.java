package tech.dsstudio.minecraft.abstractRepository.buttons;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.util.Arrays;
import java.util.UUID;

public class Stripe {
	public Stripe(ConfigurationSection section) {
		buttons = new Button[9]; // only large chests are meant to have stripes
		section.getKeys(false).forEach(it -> {
			int index = Integer.parseInt(it);
			buttons[index] = Button.createButton(section.getConfigurationSection(it));
		});
	}

	public ItemStack[] renderItems() {
		return (ItemStack[]) Arrays.stream(buttons).map(Button::render).toArray();
	}

	public void clickOn(AbstractRepository repository, UUID uuid, int relativeIndex) {
		Button button = buttons[relativeIndex];
		if (button != null) {
			button.onClick(repository, uuid);
		}
	}

	private Button[] buttons;
}
