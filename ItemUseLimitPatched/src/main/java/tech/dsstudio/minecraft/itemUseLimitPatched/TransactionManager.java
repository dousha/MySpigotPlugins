package tech.dsstudio.minecraft.itemUseLimitPatched;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionManager implements Runnable {
	public TransactionManager(Main father) {
		TransactionManager.father = father;
	}

	public void run() {
		while (!mainThreadTransactions.isEmpty()) {
			mainThreadTransactions.poll().action();
		}
		if (!hasWorkerSpinning.get()) {
			hasWorkerSpinning.set(true);
			new Thread(new Worker(), "TransactionWorker").start();
		}
	}

	public static void preparePlayerHotBarScan(Player player) {
		transactions.add(new HotBarScanTransaction(player));
	}

	public static void preparePlayerArmorScan(Player player) {
		transactions.add(new ArmorScanTransaction(player));
	}

	public static void prepareItemSwap(Inventory inventory, int left, int right) {
		transactions.add(new InventoryMoveTransaction(inventory, left, right));
	}

	public static void prepareItemReplace(Inventory inventory, int from, int to) {
		transactions.add(new InventoryReplaceTransaction(inventory, from, to));
	}

	public static void prepareItemDrop(Player player, ItemStack item) {
		mainThreadTransactions.add(new ItemDropTransaction(player, item));
	}

	private class Worker implements Runnable {
		@Override
		public void run() {
			while (!transactions.isEmpty()) {
				transactions.poll().action();
			}
			hasWorkerSpinning.set(false);
		}
	}

	private interface Transaction {
		void action();
	}

	private static class HotBarScanTransaction implements Transaction {
		public HotBarScanTransaction(Player player) {
			this.player = player;
		}

		@Override
		public void action() {
			father.getMonitor().tryTranspose(player, player.getInventory().getItemInMainHand(), player.getInventory().getHeldItemSlot());
		}

		private Player player;
	}

	private static class ArmorScanTransaction implements Transaction {
		public ArmorScanTransaction(Player player) {
			this.player = player;
		}

		@Override
		public void action() {
			father.getMonitor().scanArmorAndOffHand(player);
		}
		private Player player;
	}

	private static class InventoryMoveTransaction implements Transaction {
		public InventoryMoveTransaction(Inventory inventory, int left, int right) {
			this.inventory = inventory;
			this.left = left;
			this.right = right;
		}

		@Override
		public void action() {
			ItemStack leftItem = inventory.getItem(left);
			ItemStack rightItem = inventory.getItem(right);
			inventory.clear(left);
			inventory.clear(right);
			inventory.setItem(right, leftItem);
			inventory.setItem(left, rightItem);
		}

		private Inventory inventory;
		private int left, right;
	}

	private static class InventoryReplaceTransaction implements Transaction {
		public InventoryReplaceTransaction(Inventory inventory, int from, int to) {
			this.inventory = inventory;
			this.from = from;
			this.to = to;
		}

		@Override
		public void action() {
			ItemStack leftItem = inventory.getItem(from);
			inventory.clear(from);
			inventory.clear(to);
			inventory.setItem(to, leftItem);
		}

		private Inventory inventory;
		private int from, to;
	}

	private interface MainThreadTransaction {
		void action();
	}

	private static class ItemDropTransaction implements MainThreadTransaction {
		public ItemDropTransaction(Player player, ItemStack item) {
			this.player = player;
			this.item = item;
		}

		@Override
		public void action() {
			player.getWorld().dropItemNaturally(player.getLocation(), item).setPickupDelay(20);
		}

		private Player player;
		private ItemStack item;
	}

	private static ConcurrentLinkedQueue<Transaction> transactions = new ConcurrentLinkedQueue<>();
	private static ConcurrentLinkedQueue<MainThreadTransaction> mainThreadTransactions = new ConcurrentLinkedQueue<>();
	private static AtomicBoolean hasWorkerSpinning = new AtomicBoolean(false);
	private static Main father;
}
