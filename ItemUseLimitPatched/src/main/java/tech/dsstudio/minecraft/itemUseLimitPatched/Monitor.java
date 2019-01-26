package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class Monitor implements Listener {
	public Monitor(Main father) {
		this.father = father;
	}

	@EventHandler
	public void onPlayerHandsOn(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		ItemStack item = player.getInventory().getItem(event.getNewSlot());
		if (Util.dontHavePermission(player, item)) {
			LimitEntry entry = Config.getLimitEntry(item);
			if (entry != null) entry.annoy(player, item);
			TransactionManager.prepareItemSwap(player.getInventory(), event.getPreviousSlot(), event.getNewSlot());
		}
	}

	@EventHandler
	public void onPlayerSwapHand(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		ItemStack leftItem = event.getOffHandItem();
		ItemStack rightItem = event.getMainHandItem();
		if (Util.dontHavePermission(player, leftItem)) {
			tryTranspose(player, leftItem, 40);
		}
		if (Util.dontHavePermission(player, rightItem)) {
			tryTranspose(player, rightItem, player.getInventory().getHeldItemSlot());
		}
	}

	@EventHandler
	public void onPlayerInventoryOperation(InventoryClickEvent event) {
		if (event.getInventory().getHolder() instanceof Player) {
			InventoryAction action = event.getAction();
			Player player = (Player) event.getInventory().getHolder();
			if (action.equals(InventoryAction.PLACE_ALL) || action.equals(InventoryAction.PLACE_SOME) || action.equals(InventoryAction.PLACE_ONE)) {
				if (event.getSlot() > 35 || event.getSlot() < 9) {
					TransactionManager.preparePlayerHotBarScan(player);
					TransactionManager.preparePlayerArmorScan(player);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerPickSomething(PlayerPickupItemEvent event) {
		if (Util.isItemSpecial(event.getItem().getItemStack())) {
			Player player = event.getPlayer();
			ItemStack item = event.getItem().getItemStack();
			if (Util.dontHavePermission(player, item)) {
				TransactionManager.preparePlayerHotBarScan(player);
			}
		}
	}

	@EventHandler
	public void onDispenserShoot(BlockDispenseEvent event) {
		if (event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasLore() && event.getItem().getItemMeta().getLore().size() > 0) {
			// man, this might be an expensive operation
			double radius = 4.0; // 2^2
			Location blockLocation = event.getBlock().getLocation();
			event.getBlock().getWorld().getPlayers().stream().filter(it -> it.getLocation().distanceSquared(blockLocation) < radius).forEach(it -> {
				TransactionManager.preparePlayerArmorScan(it);
				TransactionManager.preparePlayerHotBarScan(it);
			});
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasItem()) {
			if (Util.dontHavePermission(event.getPlayer(), event.getItem())) {
				event.setCancelled(true);
				TransactionManager.preparePlayerArmorScan(event.getPlayer());
				TransactionManager.preparePlayerHotBarScan(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		father.getSweeper().addPlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent event) {
		father.getSweeper().deletePlayer(event.getPlayer());
	}

	@EventHandler
	public void onPlayerGetKicked(PlayerKickEvent event) {
		father.getSweeper().deletePlayer(event.getPlayer());
	}

	public void updatePlayerPermission(Player player) {
		PlayerInventory inventory = player.getInventory();
		tryTranspose(player, inventory.getItemInMainHand(), inventory.getHeldItemSlot());
		scanArmorAndOffHand(player);
	}

	public void tryTranspose(Player player, ItemStack item, int itemIndex) {
		if (Util.dontHavePermission(player, item)) {
			LimitEntry entry = Config.getLimitEntry(item);
			if (entry != null) entry.annoy(player, item);
			PlayerInventory inventory = player.getInventory();
			int backpackIndex = Util.firstEmpty(player);
			if (backpackIndex == -1) {
				// inventory full, swap
				for (int swapIndex = 0; swapIndex < 36; swapIndex++) {
					if (!Util.isItemSpecial(inventory.getItem(swapIndex)) && inventory.getHeldItemSlot() != swapIndex) {
						// found
						TransactionManager.prepareItemSwap(inventory, itemIndex, swapIndex);
						return;
					}
				}
				// not found! this is (somehow) impossible, though
				if (itemIndex == 8 || inventory.getHeldItemSlot() == 8) {
					ItemStack appender = inventory.getItem(9);
					TransactionManager.prepareItemReplace(inventory, itemIndex, 9);
					TransactionManager.prepareItemDrop(player, appender);
				} else {
					ItemStack appender = inventory.getItem(8);
					TransactionManager.prepareItemReplace(inventory, itemIndex, 8);
					TransactionManager.prepareItemDrop(player, appender);
				}
			} else {
				TransactionManager.prepareItemReplace(inventory, itemIndex, backpackIndex);
			}
			// FIXME: Item duplication may occur if the inventory is not closed right after these stuff
			player.closeInventory();
		}
	}

	public void scanArmorAndOffHand(Player player) {
		PlayerInventory inventory = player.getInventory();
		tryTranspose(player, inventory.getHelmet(), 39);
		tryTranspose(player, inventory.getChestplate(), 38);
		tryTranspose(player, inventory.getLeggings(), 37);
		tryTranspose(player, inventory.getBoots(), 36);
		tryTranspose(player, inventory.getItemInOffHand(), 40);
	}

	private Main father;
}
