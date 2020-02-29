package tech.dsstudio.minecraft.playerdata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tech.dsstudio.minecraft.playerdata.Main;

public class RequestForDriverEvent extends Event {
	public RequestForDriverEvent(String name, Main plugin) {
		this.driverName = name;
		this.plugin = plugin;
	}

	public String getDriverName() {
		return driverName;
	}

	public Main getPlugin() {
		return plugin;
	}

	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private String driverName;
	private Main plugin;
	private static final HandlerList HANDLER_LIST = new HandlerList();
}
