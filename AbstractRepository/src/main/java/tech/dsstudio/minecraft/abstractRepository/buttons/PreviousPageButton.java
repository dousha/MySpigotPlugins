package tech.dsstudio.minecraft.abstractRepository.buttons;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import tech.dsstudio.minecraft.abstractRepository.Main;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.util.List;
import java.util.UUID;

public class PreviousPageButton extends Button {
	public PreviousPageButton(Material material, String name, List<String> lore) {
		super(material, name, lore);
	}

	@Override
	public void onClick(AbstractRepository repository, UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		if (repository.previousPage(uuid)) {
			Bukkit.getScheduler().runTaskLater(Main.main, () -> player.openInventory(repository.getInventory(uuid)), 1);
		}
	}
}
