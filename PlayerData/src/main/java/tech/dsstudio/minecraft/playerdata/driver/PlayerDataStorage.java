package tech.dsstudio.minecraft.playerdata.driver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tech.dsstudio.minecraft.playerdata.PlayerData;

import java.util.UUID;

public interface PlayerDataStorage {
	/**
	 * Create a PlayerData storage.
	 *
	 * @param uuid Player UUID
	 * @return Whether storage was created successfully
	 */
	boolean create(@NotNull UUID uuid);

	/**
	 * Find a PlayerData storage.
	 *
	 * @param uuid Player UUID
	 * @return PlayerData. `null` if not found.
	 */
	@Nullable PlayerData find(@NotNull UUID uuid);

	/**
	 * Find a PlayerData storage.
	 *
	 * If not found, a new one will be created.
	 *
	 * @param uuid Player UUID
	 * @return PlayerData.
	 */
	PlayerData get(@NotNull UUID uuid);

	/**
	 * Delete a PlayerData storage.
	 *
	 * @param uuid Player UUID
	 * @return `false` shall be returned only if write operation was not
	 * successful. Return `true` even if given UUID was not exist.
	 */
	boolean delete(@NotNull UUID uuid);

	/**
	 * Test if a PlayerData storage exists.
	 *
	 * @param uuid Player UUID
	 * @return Self-explanatory value
	 */
	boolean isPresent(@NotNull UUID uuid);

	/**
	 * Delete ALL storage.
	 */
	void purge();

	/**
	 * Test if storage interface is ready for read/write operation.
	 *
	 * @return Self-explanatory value
	 */
	boolean ready();

	/**
	 * Load all PlayerData into memory.
	 *
	 * The underlying driver can ignore this call (leave an empty stub)
	 * if lazy loading is preferred.
	 */
	void load();

	/**
	 * Save all PlayerData into disk.
	 *
	 * This function is called when server is about to close or
	 * on auto saving.
	 */
	void save();
}
