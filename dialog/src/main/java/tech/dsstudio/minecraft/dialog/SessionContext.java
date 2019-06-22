package tech.dsstudio.minecraft.dialog;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface SessionContext {
	void initialize(Player player);
	void terminate(UUID uuid);
	boolean advance(Player player, String msg);
	boolean disclose();
}
