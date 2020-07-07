package tech.dsstudio.minecraft.crystal;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.PluginManager;
import tech.dsstudio.minecraft.crystal.events.EnderCrystalBrokenEvent;
import tech.dsstudio.minecraft.crystal.events.EnderCrystalPunchedEvent;
import tech.dsstudio.minecraft.playerdata.PlayerData;

public class MasterListener implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onCrystalPunched(EntityDamageEvent event) {
		Entity entity = event.getEntity();
		if (entity.getType().equals(EntityType.ENDER_CRYSTAL)) {
			if (CrystalApi.storage.isPresent(entity.getUniqueId())) {
				PluginManager pm = CrystalApi.getInstance().getServer().getPluginManager();
				PlayerData data = CrystalApi.storage.get(entity.getUniqueId());
				double health = data.getDouble(CrystalApi.CRYSTAL_HEALTH_KEY);
				double damage = event.getDamage();
				double remainingHealth = health - damage;
				event.setCancelled(true);
				if (remainingHealth <= 0.0) {
					// punched!
					pm.callEvent(new EnderCrystalBrokenEvent((EnderCrystal) entity));
				} else {
					pm.callEvent(new EnderCrystalPunchedEvent((EnderCrystal) entity, health, damage));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnderCrystalBroken(EnderCrystalBrokenEvent event) {
		EnderCrystal crystal = (EnderCrystal) event.getEntity();
		crystal.remove();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnderCrystalPunched(EnderCrystalPunchedEvent event) {
		PlayerData data = CrystalApi.storage.get(event.getEntity().getUniqueId());
		data.set(CrystalApi.CRYSTAL_HEALTH_KEY, event.getHealth() - event.getDamage());
	}
}
