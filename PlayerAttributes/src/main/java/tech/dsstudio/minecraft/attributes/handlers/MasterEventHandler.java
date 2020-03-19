package tech.dsstudio.minecraft.attributes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class MasterEventHandler implements Listener {
	public MasterEventHandler(PlayerAttributeApi main) {
		this.main = main;
	}

	@EventHandler
	public void onPlayerAttack(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Player) {
			Player player = (Player) damager;
			// TODO
		}
	}

	@EventHandler
	public void onPlayerHurt(EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			// TODO
		}
	}

	private PlayerAttributeApi main;
}
