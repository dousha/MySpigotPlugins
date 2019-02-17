package tech.dsstudio.minecraft.abstractRepository.buttons;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.util.List;
import java.util.UUID;

public class CommandButton extends Button {
	public CommandButton(Material material, String name, List<String> lore, String command) {
		super(material, name, lore);
		this.command = command;
	}

	@Override
	public void onClick(AbstractRepository repository, UUID uuid) {
		Bukkit.getServer().dispatchCommand(Bukkit.getPlayer(uuid), command);
	}

	private String command;
}
