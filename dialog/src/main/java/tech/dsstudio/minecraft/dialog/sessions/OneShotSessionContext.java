package tech.dsstudio.minecraft.dialog.sessions;

import org.bukkit.entity.Player;
import tech.dsstudio.minecraft.dialog.SessionContext;

import java.util.UUID;
import java.util.function.Consumer;

public class OneShotSessionContext implements SessionContext {
	public OneShotSessionContext(Consumer<String> callback) {
		this.callback = callback;
	}

	@Override
	public void initialize(Player player) { }

	@Override
	public void terminate(UUID uuid) {
		this.callback.accept(null);
	}

	@Override
	public boolean advance(Player player, String msg) {
		this.callback.accept(msg);
		return false;
	}

	@Override
	public boolean disclose() {
		return false;
	}

	private Consumer<String> callback;
}
