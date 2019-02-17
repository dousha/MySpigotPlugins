package tech.dsstudio.minecraft.portapack.buttons;

import org.bukkit.Material;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePack;

import java.util.List;

public class InfoButton extends Button {
	public InfoButton(Material material, String name, List<String> lore) {
		super(material, name, lore);
	}

	@Override
	public void onClick(PortablePack pack) {
		// do absolutely nothing
	}
}
