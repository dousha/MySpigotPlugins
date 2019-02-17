package tech.dsstudio.minecraft.itemLotto;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class LottoInventory implements Listener {
	public LottoInventory(Main main, ItemRepo repo, Economy economy, PlayerPointsAPI pp, ConfigurationSection section) {
		this.main = main;
		this.repo = repo;
		this.eco = economy;
		this.pp = pp;
		ItemMeta placeholderMeta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
		placeholderMeta.setDisplayName("---");
		placeholder.setItemMeta(placeholderMeta);
		ItemMeta cursorMeta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
		cursorMeta.setDisplayName("---");
		cursor.setItemMeta(cursorMeta);
		ItemMeta runButtonMeta = Bukkit.getItemFactory().getItemMeta(Material.STAINED_GLASS_PANE);
		runButtonMeta.setDisplayName(section.getString("button").replace('&', '\u00a7').replace("$cost", String.valueOf(section.getDouble("cost"))));
		runButton.setItemMeta(runButtonMeta);
		load(section);
	}

	public boolean isInventorySpecial(Inventory inventory) {
		return inventory != null && inventory.getTitle().equals(title);
	}

	public void openInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(player, 54, title);
		loadInventory(inventory);
		player.openInventory(inventory);
	}

	public void load(ConfigurationSection section) {
		mode = section.getName();
		cost = section.getDouble("cost");
		title = section.getString("title").replace('&', '\u00a7');
		loadSlots(section.getConfigurationSection("slots"));
	}

	@EventHandler
	public void onPlayerClickInventory(InventoryClickEvent event) {
		Inventory inventory = event.getView().getTopInventory();
		if (isInventorySpecial(inventory)) {
			event.setCancelled(true);
			event.setResult(Event.Result.DENY);
			if (event.getSlot() == 1) {
				Player player = (Player) event.getWhoClicked();
				if (player.hasPermission("itemlotto.use." + mode)) {
					if (items.length > 0) {
						if (pay(player, cost)) {
							fireUpAnimation(inventory, player);
						} else {
							Bukkit.getScheduler().runTaskLater(main, () -> {
								player.closeInventory();
								player.sendMessage(ChatColor.YELLOW + "金额不足");
							}, 1);
						}
					} else {
						Bukkit.getScheduler().runTaskLater(main, player::closeInventory, 1);
						player.sendMessage(ChatColor.YELLOW + "奖池是空的，请检查配置文件并重载");
					}
				} else {
					Bukkit.getScheduler().runTaskLater(main, player::closeInventory, 1);
					player.sendMessage("没有权限！");
				}
			}
		}
	}

	private void fireUpAnimation(Inventory inventory, Player player) {
		int target = decide();
		ItemStack item = items[target];
		final int[] currentCursorPos = {17};
		final boolean[] latched = {false};
		currentCursorPos[0] = moveCursor(inventory, currentCursorPos[0]);
		int animator = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> currentCursorPos[0] = moveCursor(inventory, currentCursorPos[0]), 0, 2);
		Bukkit.getScheduler().runTaskLater(main, () -> {
			Bukkit.getScheduler().cancelTask(animator);
			int gopher = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, () -> {
				if (currentCursorPos[0] == target) {
					if (!latched[0]) {
						Inventory playerPack = player.getInventory();
						int index = playerPack.firstEmpty();
						if (index == -1) {
							player.getWorld().dropItemNaturally(player.getLocation(), item);
						} else {
							playerPack.setItem(index, item);
						}
						latched[0] = true;
					}
				} else {
					currentCursorPos[0] = moveCursor(inventory, currentCursorPos[0]);
				}
			}, 0, 10);
			Bukkit.getScheduler().runTaskLater(main, () -> Bukkit.getScheduler().cancelTask(gopher), 100);
		}, 20);
	}

	private int moveCursor(Inventory inventory, int currentPos) {
		int out = nextNotEmpty(currentPos);
		inventory.setItem(27 + currentPos, blank);
		inventory.setItem(27 + out, cursor);
		return out;
	}

	private void loadSlots(ConfigurationSection section) {
		AtomicReference<ArrayList<LottoEntry>> ref = new AtomicReference<>(entries);
		section.getKeys(false).forEach(it -> {
			int index = Integer.parseInt(it);
			LottoEntry entry = new LottoEntry(index, section.getConfigurationSection(it));
			ref.get().add(entry);
			ItemStack item = repo.getItem(entry.getName());
			items[index] = item;
		});
		Collections.sort(entries);
	}

	private void loadInventory(Inventory inventory) {
		populatedPlaceholders(inventory);
		populateItem(inventory);
		inventory.setItem(27, cursor);
	}

	private void populatedPlaceholders(Inventory inventory) {
		IntStream.range(0, 18).forEach(it -> inventory.setItem(it, placeholder));
		inventory.setItem(1, runButton);
	}

	private void populateItem(Inventory inventory) {
		IntStream.range(18, 27).filter(it -> items[it - 18] != null).forEach(it -> inventory.setItem(it, items[it - 18]));
		IntStream.range(45, 54).filter(it -> items[it - 36] != null).forEach(it -> inventory.setItem(it, items[it - 36]));
	}

	private int nextNotEmpty(int offset) {
		int i = offset + 1;
		for(; i < items.length; i++) {
			if (items[i] != null) return i;
		}
		for(i = 0; i < items.length; i++) {
			if (items[i] != null) return i;
		}
		return -1; // should not be reachable
	}

	private int decide() {
		double rng = Math.random();
		double acc = 0.0;
		for (LottoEntry entry : entries) {
			if (rng <= acc) {
				return entry.getOffset();
			}
			acc += entry.getRate();
		}
		return -1; // should not be reachable
	}

	private boolean pay(Player player, double cost) {
		if (this.mode.equals("vault")) {
			return eco.withdrawPlayer(player, cost).transactionSuccess();
		} else {
			return pp.take(player.getUniqueId(), (int) cost);
		}
	}

	private double cost;
	private String title;
	private String mode;
	private Main main;
	private ItemRepo repo;
	private Economy eco;
	private PlayerPointsAPI pp;
	private ItemStack[] items = new ItemStack[18];
	private ArrayList<LottoEntry> entries = new ArrayList<>();
	private ItemStack placeholder = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.WHITE.getData());
	private ItemStack runButton = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.ORANGE.getData());
	private ItemStack cursor = new ItemStack(Material.STAINED_GLASS_PANE, 1, DyeColor.PURPLE.getData());
	private ItemStack blank = new ItemStack(Material.AIR);
}
