package tech.dsstudio.minecraft.portapack.internal;

import org.bukkit.command.CommandSender;

public interface CommandHandler {
	boolean invoke(CommandSender sender, String[] args);
}
