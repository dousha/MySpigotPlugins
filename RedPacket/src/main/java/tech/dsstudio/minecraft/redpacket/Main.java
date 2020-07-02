package tech.dsstudio.minecraft.redpacket;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/// sorry, but quick and dirty is what the client wanted
/// to the future self: redo anything that's not good enough
/// love, a fucked-up past self
public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		this.entries = new ConcurrentHashMap<>();
		this.keywords = new ConcurrentHashMap<>();
		Plugin plugin = getServer().getPluginManager().getPlugin("PlayerPoints");
		PlayerPoints pp = (PlayerPoints) plugin;
		if (pp == null) {
			getLogger().severe("PlayerPoints not found!");
		} else {
			papi = pp.getAPI();
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			getLogger().severe("Vault not found!");
		} else {
			eco = rsp.getProvider();
		}
		if (papi == null || eco == null) {
			getLogger().severe("Dependency failure");
			getServer().getPluginManager().disablePlugin(this);
		}
		saveDefaultConfig();
		File file = new File(getDataFolder(), "log.log");
		if (!file.exists()) {
			try {
				if (!file.createNewFile()) {
					getLogger().severe("Cannot open log file!");
					getServer().getPluginManager().disablePlugin(this);
				}
			} catch (IOException ex) {
				getLogger().severe("Cannot open log file!");
				ex.printStackTrace();
				getServer().getPluginManager().disablePlugin(this);
			}
		}
		getServer().getPluginManager().registerEvents(this, this);
		try {
			os = new FileOutputStream(file);
			dos = new DataOutputStream(os);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
		}
	}

	@Override
	public void onDisable() {
		try {
			dos.close();
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "hehe":
				if (args.length < 4) {
					return false;
				} else {
					if (args[0].equals("custom")) {
						if (args.length < 5) {
							return false;
						}
						if (sender.isOp() || sender instanceof ConsoleCommandSender) {
							int count;
							String displayName;
							String mode;
							String token = null;
							StringBuilder cmd = new StringBuilder();
							try {
								count = Integer.parseInt(args[1]);
							} catch (NumberFormatException ex) {
								sender.sendMessage("Count must be an integer");
								return false;
							}
							if (count < 1) {
								sender.sendMessage(getInternalMessage("nobodyHomeMessage"));
								return true;
							}
							displayName = args[2].replace('&', '§');
							mode = args[3];
							int i;
							if (mode.equals("m2")) {
								token = args[4];
								i = 5;
							} else {
								i = 4;
							}
							for (; i < args.length; i++) {
								cmd.append(args[i]);
								cmd.append(" ");
							}
							RedPacketEntry entry = new RedPacketEntry("系统", displayName, RedPacketType.CUSTOM, count, count);
							entry.setCommand(cmd.toString().trim());
							UUID uuid = UUID.randomUUID();
							entries.put(uuid, entry);
							if (mode.equals("m2")) {
								keywords.put(token, uuid);
								entry.setToken(token);
							}
							Bukkit.getScheduler().runTaskLater(this, () -> entries.remove(uuid), 20 * getConfig().getInt("controls.expires", 60));
							broadcastDraw(uuid, mode.equals("m1"));
						} else {
							sender.sendMessage("?");
						}
					} else {
						if (sender instanceof ConsoleCommandSender) {
							sender.sendMessage("Only player has currency/points account!");
							return true;
						}
						if (!hasPermission((Player) sender, "issue")) {
							sender.sendMessage(getInternalMessage("issuePermissionMessage"));
							return true;
						}
						String type = args[0];
						int playerCount;
						int value;
						String displayName = args[3].replace('&', '§');
						String mode;
						String token = null;
						try {
							playerCount = Integer.parseInt(args[1]);
							value = Integer.parseInt(args[2]);
						} catch (NumberFormatException ex) {
							sender.sendMessage(getInternalMessage("hasToBeIntegerMessage"));
							return false;
						}
						if (playerCount < 1) {
							sender.sendMessage(getInternalMessage("nobodyHomeMessage"));
						}
						if (args.length < 5) {
							mode = "m1";
						} else {
							mode = args[4];
						}
						if (mode.equals("m2")) {
							if (args.length < 6) {
								sender.sendMessage("What's your token?");
								return false;
							} else {
								token = args[5];
							}
						}
						switch (type) {
							case "money":
								if (!eco.has((Player) sender, value)) {
									sender.sendMessage(getInternalMessage("overdrawMessage"));
									return true;
								} else {
									eco.withdrawPlayer((OfflinePlayer) sender, value);
								}
								break;
							case "points":
								if (!papi.take(((Player) sender).getUniqueId(), value)) {
									sender.sendMessage(getInternalMessage("overdrawMessage"));
									return true;
								}
								break;
							default:
								return false;
						}
						RedPacketEntry entry = new RedPacketEntry(((Player) sender).getDisplayName(), displayName, type.equals("points") ? RedPacketType.POINTS : RedPacketType.CURRENCY, value, playerCount);
						UUID uuid = UUID.randomUUID();
						entries.put(uuid, entry);
						if (mode.equals("m2")) {
							keywords.put(token, uuid);
							entry.setToken(token);
						}
						broadcastDraw(uuid, mode.equals("m1"));
						Bukkit.getScheduler().runTaskLater(this, () -> {
							// remember to return
							UUID puid = ((Player) sender).getUniqueId();
							entries.remove(uuid);
							if (entry.getAmount() > 0 && !entry.isAnnounced()) {
								if (entry.getType() == RedPacketType.CURRENCY) {
									eco.depositPlayer(Bukkit.getOfflinePlayer(puid), entry.getAmount());
								} else {
									papi.give(puid, entry.getAmount());
								}
							}
						}, 20 * getConfig().getInt("controls.expires", 60));
						writeLog(uuid, true, entry, (Player) sender, value);
					}
				}
				break;
			case "rpd":
				if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage("This command is for players only.");
				} else {
					if (args.length != 1) {
						sender.sendMessage(getInternalMessage("strangeErrorMessage"));
					} else {
						Player player = (Player) sender;
						UUID uuid = UUID.fromString(args[0]);
						enterTheDraw(player, uuid);
					}
				}
				break;
			case "hehereload":
				saveDefaultConfig();
				reloadConfig();
				keywords.clear();
				entries.clear();
				sender.sendMessage("已重载");
				break;
			default:
				return false;
		}
		return true;
	}

	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		if (keywords.containsKey(event.getMessage().trim())) {
			String key = event.getMessage().trim();
			Player player = event.getPlayer();
			if (hasPermission(event.getPlayer(), "draw")) {
				UUID uuid = keywords.get(key);
				RedPacketEntry entry = entries.get(uuid);
				if (entry != null) {
					if (entry.getPlayerCount() == 0) {
						keywords.remove(key);
					} else {
						enterTheDraw(player, uuid);
					}
				} else {
					keywords.remove(key);
				}
			} else {
				player.sendMessage(getInternalMessage("drawPermissionMessage"));
			}
		}
	}

	private void broadcastDraw(UUID uuid, boolean isButtonMode) {
		RedPacketEntry entry = entries.get(uuid);
		BaseComponent base = new TextComponent();
		List<String> fmt = null;
		switch (entry.getType()) {
			case CURRENCY:
				fmt = getConfig().getStringList("messages.money");
				break;
			case POINTS:
				fmt = getConfig().getStringList("messages.points");
				break;
			case CUSTOM:
				fmt = getConfig().getStringList("messages.custom");
				break;
		}
		if (fmt == null) {
			getLogger().severe("Bad configuration!");
			getLogger().severe("Will not announce drawing information!");
		} else {
			fmt.forEach(line -> {
				if (line.contains("%c")) {
					TextComponent component;
					if (isButtonMode) {
						component = new TextComponent(line.replace("%c", getInternalMessage("buttonStyle")));
						component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rpd " + uuid.toString()));
					} else {
						component = new TextComponent(line.replace("%c", getInternalMessage("sayToDrawMessage").replace("%s", entry.getToken())));
						if (getConfig().getBoolean("controls.allowQuickTokenInput", false)) {
							component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, entry.getToken()));
						}
					}
					base.addExtra(component);
					base.addExtra("\n");
				} else {
					base.addExtra(line.replace("%s", entry.getName()).replace("%q", String.valueOf(entry.getPlayerCount())).replace("%v", String.valueOf(entry.getAmount())) + '\n');
				}
			});
			getServer().getScheduler().runTask(this, () -> {
				getServer().getOnlinePlayers().forEach(it -> it.spigot().sendMessage(base));
			});
		}
	}

	private boolean hasPermission(Player player, String key) {
		return player.hasPermission(getConfig().getString("permissions." + key, "_"));
	}

	private String getInternalMessage(String key) {
		return getConfig().getString("messages." + key, key);
	}

	private String getWording(String key) {
		return getConfig().getString("wordings." + key, key);
	}

	private void enterTheDraw(Player player, UUID uuid) {
		if (hasPermission(player, "draw")) {
			if (!entries.containsKey(uuid)) {
				player.sendMessage(getInternalMessage("expiredMessage"));
			} else {
				RedPacketEntry entry = entries.get(uuid);
				if (entry != null) {
					if (entry.isDrawn(player)) {
						player.sendMessage(getInternalMessage("redrawMessage"));
					} else {
						int value = entry.draw(player);
						if (value == 0) {
							player.sendMessage(getInternalMessage("emptyDrawMessage"));
						} else {
							switch (entry.getType()) {
								case CURRENCY:
									eco.depositPlayer(player, value);
									if (getConfig().getBoolean("controls.announceEveryone")) {
										getServer().getScheduler().runTask(this, () -> {
											getServer().broadcastMessage(getInternalMessage("drawMessage").replace("%w", player.getDisplayName()).replace("%p", entry.getWho()).replace("%s", entry.getName()).replace("%t", getWording("currency")).replace("%v", String.valueOf(value)).replace("%r", String.valueOf(entry.getPlayerCount())));
										});
									} else {
										player.sendMessage(getInternalMessage("silentDrawMessage").replace("%p", entry.getWho()).replace("%t", getWording("currency")).replace("%v", String.valueOf(value)));
									}
									break;
								case POINTS:
									papi.give(player.getUniqueId(), value);
									if (getConfig().getBoolean("controls.announceEveryone")) {
										getServer().getScheduler().runTask(this, () -> {
											getServer().broadcastMessage(getInternalMessage("drawMessage").replace("%w", player.getDisplayName()).replace("%p", entry.getWho()).replace("%s", entry.getName()).replace("%t", getWording("points")).replace("%v", String.valueOf(value)).replace("%r", String.valueOf(entry.getPlayerCount())));
										});
									} else {
										player.sendMessage(getInternalMessage("silentDrawMessage").replace("%p", entry.getWho()).replace("%t", getWording("points")).replace("%v", String.valueOf(value)));
									}
									break;
								case CUSTOM:
									getServer().dispatchCommand(getServer().getConsoleSender(), entry.getCommand().replace("%p", player.getName()));
									if (getConfig().getBoolean("controls.announceEveryone")) {
										getServer().getScheduler().runTask(this, () -> {
											getServer().broadcastMessage(getInternalMessage("customDrawMessage").replace("%w", player.getDisplayName()).replace("%p", entry.getWho()).replace("%s", entry.getName()).replace("%r", String.valueOf(entry.getPlayerCount())));
										});
									} else {
										player.sendMessage(getInternalMessage("silentCustomDrawMessage").replace("%p", entry.getWho()));
									}
									break;
							}
							writeLog(uuid, false, entry, player, value);
						}
					}
					if (entry.getPlayerCount() == 0 && !entry.isAnnounced()) {
						if (entry.getType() != RedPacketType.CUSTOM && getConfig().getBoolean("controls.announceWinner")) {
							getServer().getScheduler().runTask(this, () -> {
								getServer().broadcastMessage(getInternalMessage("winnerMessage").replace("%p", entry.getWho()).replace("%w", entry.getWinner()).replace("%t", getWording(entry.getType() == RedPacketType.CURRENCY ? "currency" : "points")).replace("%v", String.valueOf(entry.getWinnerValue())));
							});
						}
						entry.setAnnounced();
						entries.remove(uuid);
					}
				}
			}
		} else {
			player.sendMessage(getInternalMessage("drawPermissionMessage"));
		}
	}

	private void writeLog(UUID uuid, boolean isCreation, RedPacketEntry entry, Player participant, int value) {
		try {
			dos.writeUTF(isCreation ? "+" : "-" + "\t" + uuid.toString() + "\t" + entry.getType().toString() + "\t" + participant.getName() + "\t" + value + "\t" + entry.getAmount() + "\t" + entry.getPlayerCount() + "\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ConcurrentHashMap<String, UUID> keywords;
	private ConcurrentHashMap<UUID, RedPacketEntry> entries;
	private PlayerPointsAPI papi;
	private Economy eco;

	private FileOutputStream os;
	private DataOutputStream dos;
}
