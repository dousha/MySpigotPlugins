package tech.dsstudio.minecraft.playerdata.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;

public class StorageReadyEvent extends Event {
	public StorageReadyEvent(PlayerDataStorage storage) {
		this.storage = storage;
	}

	public PlayerDataStorage getStorage() {
		return storage;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	private PlayerDataStorage storage;
	private static final HandlerList HANDLER_LIST = new HandlerList();
}
