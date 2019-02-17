package tech.dsstudio.minecraft.portapack.buttons;

import org.bukkit.Material;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePack;

import java.util.List;

public class PreviousPageButton extends Button {
	public PreviousPageButton(Material material, String name, List<String> lore) {
		super(material, name, lore);
	}

	@Override
	public void onClick(PortablePack pack) {
		visible = pack.prevPage();
		pack.openInventory();
	}
}
