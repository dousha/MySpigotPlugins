package tech.dsstudio.minecraft.crystal.events;

import org.bukkit.entity.EnderCrystal;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EnderCrystalSpawnEvent extends EntityEvent {
	public EnderCrystalSpawnEvent(@NotNull EnderCrystal what) {
		super(what);
	}

	@Override
	public @NotNull HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private static HandlerList HANDLER_LIST = new HandlerList();
}
