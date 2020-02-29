package tech.dsstudio.minecraft.playerdata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RequestForStorageEvent extends Event {
	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private static HandlerList HANDLER_LIST = new HandlerList();
}
