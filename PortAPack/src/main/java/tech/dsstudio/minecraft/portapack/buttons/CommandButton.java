package tech.dsstudio.minecraft.portapack.buttons;

import org.bukkit.Material;
import tech.dsstudio.minecraft.portapack.internal.Instances;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePack;

import java.util.List;

public class CommandButton extends Button {
	public CommandButton(Material material, String name, List<String> lore, String command) {
		super(material, name, lore);
		this.command = command;
	}

	@Override
	public void onClick(PortablePack pack) {
		Instances.main.getServer().dispatchCommand(pack.getOwner(), command);
	}

	private String command;
}
