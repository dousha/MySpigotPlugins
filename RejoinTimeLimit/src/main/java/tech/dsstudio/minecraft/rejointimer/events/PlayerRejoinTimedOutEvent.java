package tech.dsstudio.minecraft.rejointimer.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerRejoinTimedOutEvent extends Event {
	public PlayerRejoinTimedOutEvent(Player p, long disconnectTime) {
		this.player = p;
		this.disconnectedTimeMills = disconnectTime;
		this.disconnectedDurationMills = System.currentTimeMillis() - disconnectTime;
	}

	@Override
	public HandlerList getHandlers() {
		return HANDLER_LIST;
	}

	public static HandlerList getHandlerList() {
		return HANDLER_LIST;
	}

	public long getDisconnectedTimeMills() {
		return disconnectedTimeMills;
	}

	public Player getPlayer() {
		return player;
	}

	public long getDisconnectedDurationMills() {
		return disconnectedDurationMills;
	}

	private static HandlerList HANDLER_LIST = new HandlerList();
	private Player player;
	private long disconnectedTimeMills;
	private long disconnectedDurationMills;
}
