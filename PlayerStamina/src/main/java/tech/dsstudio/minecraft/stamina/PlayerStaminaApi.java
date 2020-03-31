package tech.dsstudio.minecraft.stamina;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.dingbats.ThreadSafe;
import tech.dsstudio.minecraft.playerdata.driver.PlayerDataStorage;
import tech.dsstudio.minecraft.playerdata.events.RequestForStorageEvent;
import tech.dsstudio.minecraft.playerdata.events.StorageReadyEvent;

public class PlayerStaminaApi extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().callEvent(new RequestForStorageEvent());
	}

	@EventHandler
	public void onStorageReady(StorageReadyEvent e) {
		PlayerDataStorage dataStorage = e.getStorage();
		logic = new CoreLogic(this, dataStorage, getConfig().getStringList("worlds"));
		getServer().getPluginManager().registerEvents(logic, this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "pst":
				if (args.length != 2) {
					return false;
				}
				boolean mode = args[0].endsWith("enable");
				Player player = getServer().getPlayer(args[1]);
				if (player == null) {
					sender.sendMessage("Player is not online");
				} else {
					if (mode) {
						logic.setStaminaMode(player);
					} else {
						logic.clearStaminaMode(player);
					}
				}
				break;
			case "psw":
				if (args.length != 2) {
					return false;
				}
				boolean enter = args[0].equals("enable");
				boolean exit = args[1].equals("enable");
				logic.setAutomaticSetOnEnter(enter);
				logic.setAutomaticResetOnExit(exit);
				sender.sendMessage("Automatic set on enter: " + (enter ? "yes" : "no") + "\nAutomatic clear on exit: " + (exit ? "yes" : "no"));
				break;
			case "psc":
				if (args.length != 1) {
					return false;
				}
				player = getServer().getPlayer(args[0]);
				if (player == null) {
					sender.sendMessage("Player is not online");
				} else {
					if (logic.isPlayerOnStamina(player)) {
						sender.sendMessage("" + player.getSaturation());
					} else {
						sender.sendMessage("Player is not on stamina mode");
					}
				}
				break;
			default:
				return false;
		}
		return true;
	}

	@ThreadSafe
	public boolean isPlayerOnStamina(@NotNull Player player) {
		return logic.isPlayerOnStamina(player);
	}

	public void setPlayerStaminaMode(@NotNull Player player) {
		logic.setStaminaMode(player);
	}

	public void clearPlayerStaminaMode(@NotNull Player player) {
		logic.clearStaminaMode(player);
	}

	@ThreadSafe
	public void setPlayerStaminaTop(@NotNull Player player, float value) {
		logic.setPlayerStaminaTop(player.getUniqueId(), value);
	}

	@ThreadSafe
	public float getPlayerStaminaTop(@NotNull Player player) {
		return logic.getPlayerStaminaTop(player.getUniqueId());
	}

	public static PlayerStaminaApi getInstance() {
		return instance;
	}

	private CoreLogic logic;
	private static PlayerStaminaApi instance;
}
