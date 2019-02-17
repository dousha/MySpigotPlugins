package tech.dsstudio.minecraft.portapack.buttons;

import org.bukkit.Material;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePack;

import java.util.List;

public class NextPageButton extends Button {
	public NextPageButton(Material material, String name, List<String> lore) {
		super(material, name, lore);
	}

	@Override
	public void onClick(PortablePack pack) {
		visible = pack.nextPage();
		pack.openInventory();
	}
}
