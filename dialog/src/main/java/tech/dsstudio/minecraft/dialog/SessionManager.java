package tech.dsstudio.minecraft.dialog;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SessionManager {
	SessionManager(Main main) {
		this.main = main;
	}

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
		context.initialize(who);
		return true;
	}

	public boolean registerContext(Player who, SessionContext context, long timeout) {
		boolean result = registerContext(who, context);
		Bukkit.getScheduler().runTaskLater(main, () -> cancelSession(who.getUniqueId(), context), timeout);
		return result;
	}

	public void forceRegisterContext(Player who, SessionContext context) {
		synchronized (this) {
			if (!isRunning) {
				return;
			}
		}
		sessions.put(who.getUniqueId(), context);
		context.initialize(who);
	}

	public void forceRegisterContext(Player who, SessionContext context, long timeout) {
		forceRegisterContext(who, context);
		Bukkit.getScheduler().runTaskLater(main, () -> cancelSession(who.getUniqueId(), context), timeout);
	}

	public SessionContext getCurrentContext(Player who) {
		synchronized (this) {
			if (!isRunning) {
				return null;
			}
		}
		return sessions.get(who.getUniqueId());
	}

	public boolean isOccupied(Player player) {
		return sessions.containsKey(player.getUniqueId());
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

	private void cancelSession(UUID uuid, SessionContext context) {
		if (sessions.containsKey(uuid)) {
			SessionContext currentContext = sessions.get(uuid);
			if (currentContext == context) {
				currentContext.terminate(uuid);
			}
		}
	}
	private boolean isRunning = true;
	private final ConcurrentHashMap<UUID, SessionContext> sessions = new ConcurrentHashMap<>();
	private Main main;
}
