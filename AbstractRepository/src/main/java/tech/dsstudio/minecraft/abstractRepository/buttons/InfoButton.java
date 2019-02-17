package tech.dsstudio.minecraft.abstractRepository.buttons;

import org.bukkit.Material;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.util.List;
import java.util.UUID;

public class InfoButton extends Button {
	public InfoButton(Material material, String name, List<String> lore) {
		super(material, name, lore);
	}

	@Override
	public void onClick(AbstractRepository repository, UUID uuid) {
		// do absolutely nothing
	}
}
