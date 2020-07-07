package tech.dsstudio.minecraft.crystal.events;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EnderCrystalPunchedEvent extends EntityEvent implements Cancellable {
	public EnderCrystalPunchedEvent(@NotNull EnderCrystal which, double health, double damage) {
		super(which);
		this.health = health;
		this.damage = damage;
	}

	public double getHealth() {
		return health;
	}

	public double getDamage() {
		return damage;
	}

	public void setDamage(double d) {
		this.damage = d;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean b) {
		this.isCancelled = b;
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private final double health;
	private double damage;
	private boolean isCancelled = false;
	private final static HandlerList HANDLER_LIST = new HandlerList();
}
