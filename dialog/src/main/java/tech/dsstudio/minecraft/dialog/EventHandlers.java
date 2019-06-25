package tech.dsstudio.minecraft.dialog;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EventHandlers implements Listener {
	EventHandlers(SessionManager manager) {
		this.manager = manager;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTalk(AsyncPlayerChatEvent e) {
		e.setCancelled(manager.playerTalked(e.getPlayer(), e.getMessage()));
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		manager.playerLeft(e.getPlayer());
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent e) {
		manager.playerLeft(e.getPlayer());
	}

	private SessionManager manager;
}
