package tech.dsstudio.minecraft.abstractRepository.repository;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.Optional;
import java.util.UUID;

public interface AbstractRepository {
	/**
	 * Implementation should return an inventory respect to the UUID given.
	 *
	 * @param uuid Player UUID
	 * @return Inventory
	 */
	Inventory getInventory(UUID uuid);

	/**
	 * Implementation should return if given InventoryView is special (i.e. a custom inventory
	 * created by the plugin). Given view is guaranteed not null.
	 *
	 * @param view InventoryView
	 * @return true if given view is special. false otherwise.
	 */
	boolean isInventoryViewSpecial(InventoryView view);

	/**
	 * Implementation should return if given Inventory is special (i.e. a custom inventory
	 * created by the plugin). Given inventory is guaranteed not null.
	 *
	 * @param inventory Inventory
	 * @return true if given view is special. false otherwise.
	 */
	boolean isInventorySpecial(Inventory inventory);

	/**
	 * Implementation should return a view of item in give slot.
	 *
	 * @param uuid      Player UUID
	 * @param slotIndex Slot index, starts from 0 as top-left.
	 * @return A view of given item. If index is out of bound, return `Optional.empty()`. If item is null,
	 * return `AIR`.
	 */
	Optional<ItemStack> getItemAt(UUID uuid, int slotIndex);

	/**
	 * Implementation should return current size of current inventory page.
	 *
	 * @param uuid Player UUID
	 * @return Size of inventory
	 */
	int getInventorySize(UUID uuid);

	/**
	 * Implementation should switch to the previous page.
	 *
	 * @param uuid Player UUID
	 * @return true if page is switched. false otherwise
	 */
	boolean previousPage(UUID uuid);

	/**
	 * Implementation should switch to the next page.
	 *
	 * @param uuid Player UUID
	 * @return true if page is switched. false otherwise
	 */
	boolean nextPage(UUID uuid);

	/**
	 * Implementation should return current page.
	 *
	 * @param uuid Player UUID
	 * @return Current page. Index starts from 0.
	 */
	int getCurrentPage(UUID uuid);

	/**
	 * Implementation should try to switch to given page.
	 *
	 * @param uuid      Player UUID
	 * @param pageIndex Page index, starts from 0
	 * @return true if given page is switched to. false if given page is too large or too small
	 */
	boolean setCurrentPage(UUID uuid, int pageIndex);

	/**
	 * Implementation should switch to given page,
	 * or the first page if given number is below 0,
	 * or the last page if given number is greater than maximum page available to the player.
	 *
	 * @param uuid      Player UUID
	 * @param pageIndex Page index, starts from 0
	 */
	void forceSetCurrentPage(UUID uuid, int pageIndex);

	/**
	 * Implementation should return the total pages of given player.
	 *
	 * @param uuid Player UUID
	 * @return Page count
	 */
	int totalPageCount(UUID uuid);

	/**
	 * Implementation should return if given player has permission to use this repository
	 *
	 * @param uuid Player UUID
	 * @return true if player has permission. false otherwise
	 */
	boolean hasPermission(UUID uuid);

	/**
	 * Implementation should return if given slot could be taken by player.
	 * <p>
	 * Note that if implementation needs to receive events when item is taken, return true in this
	 * method and return false in {@link AbstractRepository#takeItemAt(UUID, int, int, ItemStack)}.
	 * This will ensure that the content of repository wouldn't change but event could be still passed.
	 *
	 * @param uuid    Player UUID
	 * @param slot    Slot index, the one starts at 0 in CLICKED inventory
	 * @param rawSlot Slot index, the one start at 0 in THE TOP LEFT inventory
	 * @return true if player could take the item in given slot
	 */
	boolean canTakeItemAt(UUID uuid, int slot, int rawSlot);

	/**
	 * Implementation should return if given slot could store items put by player.
	 * <p>
	 * Note that if implementation needs to receive events when item is put, return true in this
	 * method and return false in {@link AbstractRepository#putItemAt(UUID, int, int, ItemStack)}.
	 * This will ensure that the content of repository wouldn't change but event could be still passed.
	 *
	 * @param uuid    Player UUID
	 * @param slot    Slot index, the one starts at 0 in CLICKED inventory
	 * @param rawSlot Slot index, the one start at 0 in THE TOP LEFT inventory
	 * @return true if player could put an item in given slot
	 */
	boolean canPutItemAt(UUID uuid, int slot, int rawSlot);

	/**
	 * Implementation should report if given operation should alter inventory storage.
	 * <p>
	 * Note that the {@link AbstractRepository#canTakeItemAt(UUID, int, int)} is called first to check
	 * permission. This method should only return false if the structure should be retailed. The event
	 * will be cancelled if this method returns false.
	 *
	 * @param uuid    Player UUID
	 * @param slot    Slot index, the one starts at 0 in CLICKED inventory
	 * @param rawSlot Slot index, the one start at 0 in THE TOP LEFT inventory
	 * @param item    The item taken by player
	 * @return true if inventory storage is altered. false otherwise.
	 */
	ItemOperationResult takeItemAt(UUID uuid, int slot, int rawSlot, ItemStack item);

	/**
	 * Implementation should report if given operation should alter inventory storage.
	 * <p>
	 * Note that the {@link AbstractRepository#canPutItemAt(UUID, int, int)} is called first to check
	 * permission. This method should only return false if the structure should be retailed. The event
	 * will be cancelled if this method returns false.
	 *
	 * @param uuid    Player UUID
	 * @param slot    Slot index, the one starts at 0 in CLICKED inventory
	 * @param rawSlot Slot index, the one start at 0 in THE TOP LEFT inventory
	 * @param item    The item taken by player
	 * @return true if inventory storage is altered. false otherwise.
	 */
	ItemOperationResult putItemAt(UUID uuid, int slot, int rawSlot, ItemStack item);

	/**
	 * Implementation should load the configuration from previously saved data.
	 * <p>
	 * This function is called if the data file is found.
	 *
	 * @param config Plugin configuration file
	 * @param data Data configuration file
	 */
	void loadConfiguration(FileConfiguration config, FileConfiguration data);

	/**
	 * This function is called if the data file is not found. In this situation, please create your
	 * own stuff manually. Usually it's handled by the abstract classes that you extend. But in
	 * case that it didn't or requires you to do further preparation, please remember to do this or
	 * something may trip and fail.
	 *
	 * @param base The data folder of your repository
	 * @param config The configuration of your plugin
	 */
	void initialize(File base, FileConfiguration config);

	/**
	 * Implementation should return a YAMLConfiguration if the repository should be saved.
	 *
	 * @return YAMLConfiguration to get persisted. If repository do not need to get saved, return Optional.empty()
	 */
	Optional<YamlConfiguration> saveConfiguration();

	/**
	 * Implementation should return plugin name, or any name that you think will not conflict with
	 * other plugins. The name is used when loading storage content and saving storage content.
	 * <p>
	 * Please, for compatibility sake, use a name that matches this regex: <pre>/^[A-Za-z][A-Za-z0-9_]{31}$/</pre> and
	 * does not contain any offensive word.
	 *
	 * @return Plugin name, or your favorable name.
	 */
	String getName();
}
