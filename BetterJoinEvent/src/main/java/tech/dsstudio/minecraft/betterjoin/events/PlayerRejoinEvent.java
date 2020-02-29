package tech.dsstudio.minecraft.betterjoin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerRejoinEvent extends Event {
	public PlayerRejoinEvent(PlayerJoinEvent e, boolean isFirstJoin, long lastJoinTimestamp) {
		this.event = e;
		this.isFirstJoin = isFirstJoin;
		this.lastJoinTimeMills = lastJoinTimestamp;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	public PlayerJoinEvent getEvent() {
		return event;
	}

	public long getLastJoinTimeMills() {
		return lastJoinTimeMills;
	}

	public boolean isFirstJoin() {
		return isFirstJoin;
	}

	private static HandlerList HANDLER_LIST = new HandlerList();
	private PlayerJoinEvent event;
	private boolean isFirstJoin;
	private long lastJoinTimeMills;
}
