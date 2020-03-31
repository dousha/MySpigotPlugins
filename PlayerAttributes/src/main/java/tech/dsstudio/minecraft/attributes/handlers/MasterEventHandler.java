package tech.dsstudio.minecraft.attributes.handlers;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.attributes.PlayerAttributeApi;
import tech.dsstudio.minecraft.attributes.evaluator.EvaluationType;
import tech.dsstudio.minecraft.attributes.events.ArmorEquipEvent;
import tech.dsstudio.minecraft.attributes.utils.RpnStackPrepare;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;

public class MasterEventHandler implements Listener {
	public MasterEventHandler(@NotNull PlayerAttributeApi main) {
		this.main = main;
		this.storage = main.getStorage();
	}

	@EventHandler
	public void onPlayerAttack(@NotNull EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Player) {
			Player player = (Player) damager;
			double damage = main.getMasterEvaluator().evaluate(EvaluationType.ATTACK_DAMAGE, event.getDamage(), player);
			event.setDamage(damage);
		}
	}

	@EventHandler
	public void onPlayerHurt(@NotNull EntityDamageByEntityEvent event) {
		Entity entity = event.getEntity();
		if (entity instanceof Player) {
			Player player = (Player) entity;
			double damage = main.getMasterEvaluator().evaluate(EvaluationType.DEFENSE_DAMAGE, event.getDamage(), player);
			event.setDamage(damage);
		}
	}

	@EventHandler
	public void onPlayerChangeArmor(@NotNull ArmorEquipEvent event) {
		// TODO: set other strange properties
	}

	private PlayerAttributeApi main;
	private PlayerDataStorage storage;
}
