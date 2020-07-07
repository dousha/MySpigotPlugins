package tech.dsstudio.minecraft.crystal.events;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EnderCrystalBrokenEvent extends EntityEvent implements Cancellable {
	public EnderCrystalBrokenEvent(@NotNull EnderCrystal which) {
		super(which);
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

	private boolean isCancelled = false;
	private static final HandlerList HANDLER_LIST = new HandlerList();
}
