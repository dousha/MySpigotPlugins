package tech.dsstudio.minecraft.dialog;

import org.bukkit.entity.Player;

import java.util.UUID;

public interface SessionContext {
	/**
	 * Called when the context is registered with the player.
	 *
	 * @param player Player
	 */
	void initialize(Player player);

	/**
	 * Called when the player is determined cannot answer to the session (e.g. left server)
	 * or timed out if the session is registered with a timeout.
	 *
	 * @param uuid Player UUID
	 */
	void terminate(UUID uuid);

	/**
	 * Called when the player answer to the session.
	 *
	 * @param player Player
	 * @param msg What player said
	 * @return true if the session needs to be kept, false otherwise
	 */
	boolean advance(Player player, String msg);

	/**
	 * Should the message be disclosed.
	 *
	 * @return true when the message should display, false otherwise
	 */
	boolean disclose();
}
