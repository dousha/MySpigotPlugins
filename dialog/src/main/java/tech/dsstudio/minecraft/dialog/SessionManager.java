package tech.dsstudio.minecraft.dialog;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

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

	public void playerLeft(Player who) {
		UUID id = who.getUniqueId();
		if (sessions.containsKey(id)) {
			SessionContext ctx = sessions.get(id);
			if (ctx != null) {
				ctx.terminate(id);
			}
			sessions.remove(id);
		}
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

	private boolean isRunning = true;
	private HashMap<UUID, SessionContext> sessions = new HashMap<>();
}
