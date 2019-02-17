package tech.dsstudio.minecraft.itemLotto;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		RegisteredServiceProvider<Economy> ecoProvider = getServer().getServicesManager().getRegistration(Economy.class);
		if (ecoProvider != null) {
			economy = ecoProvider.getProvider();
		} else {
			System.err.println("[ItemLotto] Vault is not present or not correctly installed!!");
			getServer().getPluginManager().disablePlugin(this);
		}
		Plugin plugin = getServer().getPluginManager().getPlugin("PlayerPoints");
		if (plugin != null) {
			points = ((PlayerPoints) plugin).getAPI();
		} else {
			System.err.println("[ItemLotto] PlayerPoints is not present or not correctly installed!!");
			getServer().getPluginManager().disablePlugin(this);
		}
		saveDefaultConfig();
		repo = new ItemRepo(getDataFolder());
		vaultInventory = new LottoInventory(this, repo, economy, points, getConfig().getConfigurationSection("vault"));
		ppInventory = new LottoInventory(this, repo, economy, points, getConfig().getConfigurationSection("vault"));
		getServer().getPluginManager().registerEvents(vaultInventory, this);
		getServer().getPluginManager().registerEvents(ppInventory, this);
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {
		if (repo != null) {
			repo.saveItems();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "ilvo":
				if (sender instanceof Player)
					vaultInventory.openInventory((Player) sender);
				else
					sender.sendMessage("仅玩家可使用此命令");
				break;
			case "ilpo":
				if (sender instanceof Player)
					ppInventory.openInventory((Player) sender);
				else
					sender.sendMessage("仅玩家可使用此命令");
				break;
			case "ilr":
				repo.loadItems();
				vaultInventory.load(getConfig().getConfigurationSection("vault"));
				ppInventory.load(getConfig().getConfigurationSection("playerpoints"));
				sender.sendMessage(ChatColor.YELLOW + "已重载配置");
				break;
			case "ils":
				if (sender instanceof Player) {
					if (args.length != 1) {
						sender.sendMessage(ChatColor.RED + "名称指定不正确");
						return false;
					} else {
						String input = args[0];
						Player player = (Player) sender;
						ItemStack item = player.getItemInHand();
						if (input.matches("[0-9+]")) {
							int index = Integer.parseInt(input);
							if (index >= 0 && index < 18) {
								PendingTransaction transaction = new PendingTransaction(index, item, player);
								interactive.put(player, transaction);
								sender.sendMessage(ChatColor.YELLOW + "将该物品保存到哪个奖池？ 聊天框输入 v 保存到金币奖池，输入 p 保存到点券奖池，输入其他内容取消");
								// see events below
							}
						} else {
							if (!repo.hasItem(input)) {
								repo.addItem(input, item);
								repo.saveItems();
								sender.sendMessage(ChatColor.GREEN + "已将手中物品添加为 " + args[0]);
							} else {
								sender.sendMessage(ChatColor.RED + "已经存在物品 " + args[0]);
								sender.sendMessage(ChatColor.RED + "必须先删除该物品才能添加");
							}
						}
					}
				} else
					sender.sendMessage("仅玩家可使用此命令");
				break;
			case "ild":
				if (args.length != 1) {
					sender.sendMessage(ChatColor.RED + "名称指定不正确");
					return false;
				} else {
					repo.deleteItem(args[0]);
					repo.saveItems();
					sender.sendMessage(ChatColor.YELLOW + "已删除物品 " + args[0]);
				}
				break;
			case "ilp":
				repo.getItems().forEach(sender::sendMessage);
				break;
			case "ilh":
				if (args.length == 0) {
					help.getHelp(sender, null);
				} else {
					help.getHelp(sender, args[0]);
				}
				break;
		}
		return true;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (interactive.containsKey(event.getPlayer())) {
			event.setCancelled(true);
			interactive.get(event.getPlayer()).action(event.getMessage());
			interactive.remove(event.getPlayer());
		}
	}

	private class PendingTransaction {
		public PendingTransaction(int index, ItemStack item, Player player) {
			this.index = index;
			this.item = item;
			this.player = player;
		}

		public void action(String act) {
			String name;
			do {
				name = UUID.randomUUID().toString();
			} while (repo.hasItem(name));
			repo.addItem(name, item);
			FileConfiguration config = getConfig();
			ConfigurationSection section;
			if (act.startsWith("v")) {
				section = config.getConfigurationSection("vault.slots");
			} else if (act.startsWith("p")) {
				section = config.getConfigurationSection("playerpoints.slots");
			} else {
				return;
			}
			ConfigurationSection slotSection = section.getConfigurationSection(String.valueOf(index));
			slotSection.set("name", name);
			saveConfig();
			player.sendMessage(ChatColor.GREEN + "物品已被保存为 " + name + ", 已添加至第 " + index + " 格");
		}

		private int index;
		private ItemStack item;
		private Player player;
	}

	private Economy economy = null;
	private PlayerPointsAPI points = null;
	private ItemRepo repo = null;
	private LottoInventory vaultInventory = null;
	private LottoInventory ppInventory = null;
	private HashMap<Player, PendingTransaction> interactive = new HashMap<>();
	private Help help = new Help();
}
