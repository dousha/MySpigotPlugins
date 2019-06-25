package tech.dsstudio.minecraft.dialog.sessions;

import org.bukkit.entity.Player;
import tech.dsstudio.minecraft.dialog.SessionContext;

import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class OneShotSessionContext implements SessionContext {
	public OneShotSessionContext(Consumer<String> callback, Predicate<String> formatter) {
		this.callback = callback;
		this.formatter = formatter;
	}

	public OneShotSessionContext(Consumer<String> callback) {
		this.callback = callback;
		this.formatter = (msg) -> true;
	}

	@Override
	public void initialize(Player player) { }

	@Override
	public void terminate(UUID uuid) {
		this.callback.accept(null);
	}

	@Override
	public boolean advance(Player player, String msg) {
		if (formatter.test(msg)) {
			this.callback.accept(msg);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean disclose() {
		return false;
	}

	private Consumer<String> callback;
	private Predicate<String> formatter;
}
