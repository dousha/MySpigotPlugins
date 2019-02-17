package tech.dsstudio.minecraft.portapack.internal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import tech.dsstudio.minecraft.portapack.internal.journal.Journal;
import tech.dsstudio.minecraft.portapack.internal.journal.JournalType;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePack;
import tech.dsstudio.minecraft.portapack.internal.pack.PortablePackManager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Monitor implements Listener {
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerClickInventoryItem(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Player player = (Player) event.getInventory().getHolder();
		PortablePackManager mgr = Instances.packer;
		if (mgr.isSpecialInventory(player, inventory) && event.getRawSlot() >= 0 && event.getRawSlot() < 54) {
			if (isLatched.get()) {
				event.setResult(Event.Result.DENY);
				event.setCancelled(true);
				return;
			}
			PortablePack pack = mgr.getPack(player);
			int inventoryIndex = pack.getCurrentPage();
			if (event.getSlot() >= 45) {
				// button stripe
				event.setCancelled(true);
				event.setResult(Event.Result.DENY);
				Bukkit.getScheduler().runTaskLater(Instances.main, () -> {
					pack.buttonClick(event.getSlot() - 45); // inventory operations are required to have 1 tick delay
				}, 1);
			} else {
				Journal journal = Instances.journal;
				int itemIndex = event.getSlot();
				// inventory operation
				InventoryAction action = event.getAction();
				switch (action) {
					case PICKUP_ALL:
					case PICKUP_SOME:
					case PICKUP_HALF:
					case PICKUP_ONE:
						ItemStack withdrawItem = event.getCurrentItem();
						journal.logTransaction(player, JournalType.WITHDRAW, inventoryIndex, itemIndex, withdrawItem);
						ItemStack remainder = event.getCursor();
						journal.logTransaction(player, JournalType.DEPOSIT, inventoryIndex, itemIndex, remainder);
						break;
					case PLACE_ALL:
					case PLACE_SOME:
					case PLACE_ONE:
						ItemStack depositItem = event.getCursor();
						journal.logTransaction(player, JournalType.DEPOSIT, inventoryIndex, itemIndex, depositItem);
						break;
					case SWAP_WITH_CURSOR:
						ItemStack inItem = event.getCursor();
						ItemStack outItem = event.getCurrentItem();
						journal.logTransaction(player, JournalType.WITHDRAW, inventoryIndex, itemIndex, outItem);
						journal.logTransaction(player, JournalType.DEPOSIT, inventoryIndex, itemIndex, inItem);
						break;
					case HOTBAR_MOVE_AND_READD:
						ItemStack item = event.getCurrentItem();
						journal.logTransaction(player, JournalType.WITHDRAW, inventoryIndex, itemIndex, item);
						break;
					case HOTBAR_SWAP:
						ItemStack withdrawnItem = inventory.getItem(event.getSlot());
						ItemStack depositedItem = player.getInventory().getItemInMainHand();
						journal.logTransaction(player, JournalType.WITHDRAW, inventoryIndex, itemIndex, withdrawnItem);
						journal.logTransaction(player, JournalType.DEPOSIT, inventoryIndex, itemIndex, depositedItem);
						break;
					case MOVE_TO_OTHER_INVENTORY:
						// wait..
						// we have to check if this move corrupts button stripe
						// TODO: make it smarter
					case COLLECT_TO_CURSOR:
						// wtf?
						event.setCancelled(true);
						event.setResult(Event.Result.DENY);
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
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerDragItem(InventoryDragEvent event) {
		Inventory inventory = event.getInventory();
		if (inventory.getHolder() instanceof Player) {
			Player player = (Player) inventory.getHolder();
			PortablePackManager mgr = Instances.packer;
			if (mgr.isSpecialInventory(player, inventory)) {
				// TODO: make it smarter
				event.setCancelled(true);
				event.setResult(Event.Result.DENY);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Instances.packer.getPack(event.getPlayer()).saveInventory();
	}

	@EventHandler
	public void onPlayerKicked(PlayerKickEvent event) {
		Instances.packer.getPack(event.getPlayer()).saveInventory();
		playerLatches.remove(event.getPlayer().getName());
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		playerLatches.put(event.getPlayer().getName(), new ReentrantReadWriteLock());
		Instances.packer.preparePack(event.getPlayer());
	}

	public void setLatch() {
		isLatched.set(true);
	}

	public void clearLatch() {
		isLatched.set(false);
	}

	private AtomicBoolean isLatched = new AtomicBoolean(false);
	private ConcurrentHashMap<String, ReentrantReadWriteLock> playerLatches = new ConcurrentHashMap<>();
}
