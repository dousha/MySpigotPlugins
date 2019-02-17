package tech.dsstudio.minecraft.abstractRepository;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.abstractRepository.repository.AbstractRepository;

import java.util.UUID;

public class RepositoryOperationMonitor implements Listener {
	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player && event.getClickedInventory() != null) {
			Player player = (Player) event.getWhoClicked();
			UUID uuid = player.getUniqueId();
			int clickedSlotIndex = event.getSlot();
			int rawSlotIndex = event.getRawSlot();
			RepositoryManager.getRepositories().stream().filter(it -> it.isInventorySpecial(event.getClickedInventory())).forEach(it -> {
				switch (event.getAction()) {
					case PICKUP_SOME:
					case PICKUP_HALF:
					case PICKUP_ONE:
						if (it.canPutItemAt(uuid, clickedSlotIndex, rawSlotIndex)) {
							ItemStack remainder = event.getCursor();
							if (remainder != null && !remainder.getType().equals(Material.AIR))
								checkPutItem(event, uuid, clickedSlotIndex, rawSlotIndex, it, remainder);
						} else {
							rejectEvent(event);
							return;
						}
					case PICKUP_ALL:
						if (it.canTakeItemAt(uuid, clickedSlotIndex, rawSlotIndex)) {
							checkTakeItem(event, uuid, clickedSlotIndex, rawSlotIndex, it);
						} else {
							rejectEvent(event);
							return;
						}
						break;
					case PLACE_ALL:
					case PLACE_SOME:
					case PLACE_ONE:
						ItemStack depositItem = event.getCursor();
						if (!it.canPutItemAt(uuid, clickedSlotIndex, rawSlotIndex)) {
							rejectEvent(event);
						} else {
							checkPutItem(event, uuid, clickedSlotIndex, rawSlotIndex, it, depositItem);
						}
						break;
					case SWAP_WITH_CURSOR:
						if (it.canPutItemAt(uuid, clickedSlotIndex, rawSlotIndex) && it.canTakeItemAt(uuid, clickedSlotIndex, rawSlotIndex)) {
							ItemStack inItem = event.getCursor();
							ItemStack outItem = event.getCurrentItem();
							checkTakeAndPutItem(event, uuid, clickedSlotIndex, rawSlotIndex, it, outItem, inItem);
						} else {
							rejectEvent(event);
						}
						break;
					case HOTBAR_MOVE_AND_READD:
						if (it.canTakeItemAt(uuid, clickedSlotIndex, rawSlotIndex)) {
							checkTakeItem(event, uuid, clickedSlotIndex, rawSlotIndex, it);
						} else {
							rejectEvent(event);
						}
						break;
					case HOTBAR_SWAP:
						if (it.canTakeItemAt(uuid, clickedSlotIndex, rawSlotIndex) && it.canPutItemAt(uuid, clickedSlotIndex, rawSlotIndex)) {
							ItemStack withdrawnItem = event.getClickedInventory().getItem(event.getSlot());
							ItemStack depositedItem = player.getInventory().getItemInMainHand();
							checkTakeAndPutItem(event, uuid, clickedSlotIndex, rawSlotIndex, it, withdrawnItem, depositedItem);
						} else {
							rejectEvent(event);
						}
						break;
					case MOVE_TO_OTHER_INVENTORY:
						// wait..
						// we have to check if this move corrupts button stripe
						// TODO: make it smarter
					case COLLECT_TO_CURSOR:
						// wtf?
						debugPrint(event);
						rejectEvent(event);
						break;
					case CLONE_STACK:
						// only possible in creative mode, ignore
					case DROP_ALL_CURSOR:
					case DROP_ONE_CURSOR:
					case DROP_ALL_SLOT:
					case DROP_ONE_SLOT:
						// well, as far as I could tell, they have to, at least, withdraw an item before throwing it away
						// and slot drops are only possible in hotbar
						// so ignored
					case NOTHING:
					case UNKNOWN:
						break;

				}
			});
		}
	}

	private void checkTakeAndPutItem(InventoryClickEvent event, UUID uuid, int clickedSlotIndex, int rawSlotIndex, AbstractRepository it, ItemStack withdrawnItem, ItemStack depositedItem) {
		switch (it.takeItemAt(uuid, clickedSlotIndex, rawSlotIndex, withdrawnItem).cascade(it.putItemAt(uuid, clickedSlotIndex, rawSlotIndex, depositedItem))) {
			case CANCEL_EVENT:
				rejectEvent(event);
				break;
			case REVERT_CONTENT:
				recoverInventory(event);
				break;
			case ACCEPT_CHANGE:
				break;
		}
	}

	private void checkTakeItem(InventoryClickEvent event, UUID uuid, int clickedSlotIndex, int rawSlotIndex, AbstractRepository repo) {
		ItemStack withdrawItem = event.getCurrentItem();
		switch (repo.takeItemAt(uuid, clickedSlotIndex, rawSlotIndex, withdrawItem)) {
			case CANCEL_EVENT:
				rejectEvent(event);
				break;
			case REVERT_CONTENT:
				recoverInventory(event);
				break;
			case ACCEPT_CHANGE:
				break;
		}
	}

	private void checkPutItem(InventoryClickEvent event, UUID uuid, int clickedSlotIndex, int rawSlotIndex, AbstractRepository repo, ItemStack item) {
		switch (repo.putItemAt(uuid, clickedSlotIndex, rawSlotIndex, item)) {
			case CANCEL_EVENT:
				rejectEvent(event);
				break;
			case REVERT_CONTENT:
				recoverInventory(event);
				break;
			case ACCEPT_CHANGE:
				break;
		}
	}

	private void rejectEvent(InventoryClickEvent event) {
		event.setCancelled(true);
		event.setResult(Event.Result.DENY);
	}

	private void recoverInventory(InventoryClickEvent event) {
		Inventory inventory = event.getClickedInventory();
		ItemStack slotItem = event.getCurrentItem();
		ItemStack cursorItem = event.getCursor();
		int slotIndex = event.getSlot();
		if (slotItem == null || slotItem.getType().equals(Material.AIR)) {
			// take all
			inventory.setItem(slotIndex, cursorItem);
		} else {
			if (slotItem.isSimilar(cursorItem)) {
				// take some
				inventory.getItem(slotIndex).setAmount(slotItem.getAmount() + cursorItem.getAmount());
			} else {
				// cursor swap
				inventory.setItem(slotIndex, cursorItem);
			}
		}
	}

	private void debugPrint(InventoryClickEvent event) {
		System.out.println(String.format("InventoryClick: Title = %s; Slot = %d; Raw = %d; Action = %s", event.getClickedInventory().getTitle(), event.getSlot(), event.getRawSlot(), event.getAction().toString()));
	}
}
