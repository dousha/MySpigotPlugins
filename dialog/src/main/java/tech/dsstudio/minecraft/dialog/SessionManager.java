package tech.dsstudio.minecraft.dialog;

import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
	public boolean playerTalked(Player who, String what) {
		UUID id = who.getUniqueId();
		if (sessions.containsKey(id)) {
			SessionContext ctx = sessions.get(id);
			if (ctx == null) {
				sessions.remove(id);
				return false;
			} else {
				if (!ctx.advance(who, what)) {
					sessions.remove(id);
				}
			}
			return !ctx.disclose();
		}
		return false;
	}

	void playerLeft(Player who) {
		UUID id = who.getUniqueId();
		if (sessions.containsKey(id)) {
			SessionContext ctx = sessions.get(id);
			if (ctx != null) {
				ctx.terminate(id);
			}
			sessions.remove(id);
		}
	}

	public boolean registerContext(Player who, SessionContext context) {
		synchronized (this) {
			if (!isRunning)
				return false;
		}
		UUID id = who.getUniqueId();
		if (sessions.containsKey(id)) {
			if (sessions.get(id) != null) {
				return false;
			}
		}
		sessions.put(id, context);
		return true;
	}

	void kill() {
		synchronized (this) {
			isRunning = false;
		}
		sessions.forEach((k, v) -> {
			if (v != null) {
				v.terminate(k);
			}
		});
		sessions.clear();
	}

	boolean isOccupied(Player player) {
		return sessions.containsKey(player.getUniqueId());
	}

	private boolean isRunning = true;
	private final ConcurrentHashMap<UUID, SessionContext> sessions = new ConcurrentHashMap<>();
}
