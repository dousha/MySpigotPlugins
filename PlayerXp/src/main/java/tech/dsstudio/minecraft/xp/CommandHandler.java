package tech.dsstudio.minecraft.xp;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.playerdata.PlayerData;

public class CommandHandler {
	public CommandHandler(@NotNull PlayerXpApi api) {
		this.api = api;
	}

	public boolean handleGet(@NotNull CommandSender who, @NotNull String[] args) {
		if (args.length < 1) {
			return false;
		}
		String playerName;
		if (args.length < 2) {
			if (who instanceof Player) {
				playerName = who.getName();
			} else {
				who.sendMessage("Specify a player name");
				return false;
			}
		} else {
			playerName = args[1];
		}
		OfflinePlayer player = api.getServer().getOfflinePlayer(playerName);
		PlayerData data = api.getStorage().get(player.getUniqueId());
		switch (args[0]) {
			case "bar":
				if (!data.hasKey(CoreLogic.PLAYER_XP_KEY)) {
					who.sendMessage("No experience set");
				} else {
					who.sendMessage(String.valueOf(data.getFloat(CoreLogic.PLAYER_XP_KEY)));
				}
				break;
			case "level":
				if (!data.hasKey(CoreLogic.PLAYER_LEVEL_KEY)) {
					who.sendMessage("No level set");
				} else {
					who.sendMessage(String.valueOf(data.getFloat(CoreLogic.PLAYER_LEVEL_KEY)));
				}
				break;
			default:
				return false;
		}
		return true;
	}

	public boolean handleSet(@NotNull CommandSender who, @NotNull String[] args) {
		if (args.length < 2) {
			return false;
		}
		String playerName;
		if (args.length < 3) {
			if (who instanceof Player) {
				playerName = who.getName();
			} else {
				who.sendMessage("Specify a player name");
				return false;
			}
		} else {
			playerName = args[2];
		}
		Player player = api.getServer().getPlayer(playerName);
		try {
			switch (args[0]) {
				case "bar":
					float percentage = Float.parseFloat(args[1]);
					api.getLogic().setPlayerXpPercentage(player, percentage);
					break;
				case "level":
					int level = Integer.parseInt(args[1]);
					api.getLogic().setPlayerLevel(player, level);
					break;
				default:
					return false;
			}
		} catch (NumberFormatException e) {
			who.sendMessage("Must be a number");
			return false;
		}
		who.sendMessage("Value set");
		return true;
	}

	public boolean handleToggle(@NotNull CommandSender who, @NotNull String[] args) {
		if (args.length < 1) {
			return false;
		}
		String playerName;
		if (args.length < 2) {
			if (who instanceof Player) {
				playerName = who.getName();
			} else {
				who.sendMessage("Specify a player");
				return false;
			}
		} else {
			playerName = args[1];
		}
		Player player = api.getServer().getPlayer(playerName);
		switch (args[0]) {
			case "on":
				api.getLogic().addPlayer(player);
				break;
			case "off":
				api.getLogic().removePlayer(player);
				break;
			default:
				return false;
		}
		return true;
	}

	private PlayerXpApi api;
}
