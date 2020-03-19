package tech.dsstudio.minecraft.attributes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;

public class CommandHandler implements Listener {
	public CommandHandler(PlayerAttributeApi main) {
		this.main = main;
	}

	public void onCommand(CommandSender sender, Command command, String label, String[] args) {
		// TODO
	}

	private PlayerAttributeApi main;
}
