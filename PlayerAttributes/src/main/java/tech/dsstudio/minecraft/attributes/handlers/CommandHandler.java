package tech.dsstudio.minecraft.attributes.handlers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import tech.dsstudio.minecraft.attributes.PlayerAttributeApi;

public class CommandHandler implements Listener {
	public CommandHandler(PlayerAttributeApi main) {
		this.main = main;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, @NotNull String[] args) {
		// TODO
		if (args.length < 1) {
			return false;
		}
		String category = args[0];
		switch (category) {
			case "add":
				sender.sendMessage("Not implemented");
				break;
			case "config":
				if (args.length != 2) {
					switch (args[1]) {
						case "reload":
							main.loadConfig();
							sender.sendMessage("Reloaded");
							break;
						case "write":
							sender.sendMessage("Not implemented");
							break;
						default:
							return false;
					}
				} else {
					return false;
				}
				break;
			case "debug":
				sender.sendMessage("Not implemented");
				break;
			case "delete":
				sender.sendMessage("Not implemented");
				break;
			case "eval":
				sender.sendMessage("Not implemented");
				break;
			case "set":
				if (args.length < 2) {
					return false;
				}
				switch (args[1]) {
					case "playerAttribute":

				}
				break;
			default:
				sender.sendMessage("No such category");
		}
		return true;
	}

	private PlayerAttributeApi main;
}
