package tech.dsstudio.minecraft.betterjoin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerRejoinEvent extends Event {
	public PlayerRejoinEvent(PlayerJoinEvent e, boolean isFirstJoin, long lastJoinTimestamp, long lastLeaveTimestamp) {
		this.event = e;
		this.isFirstJoin = isFirstJoin;
		this.lastJoinTimeMills = lastJoinTimestamp;
		this.lastLeaveTimeMills = lastLeaveTimestamp;
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

	public long getLastLeaveTimeMills() {
		return lastLeaveTimeMills;
	}

	public boolean isFirstJoin() {
		return isFirstJoin;
	}

	private static HandlerList HANDLER_LIST = new HandlerList();
	private PlayerJoinEvent event;
	private boolean isFirstJoin;
	private long lastJoinTimeMills;
	private long lastLeaveTimeMills;
}
