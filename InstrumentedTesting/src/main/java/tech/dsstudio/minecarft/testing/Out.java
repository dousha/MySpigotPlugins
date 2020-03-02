package tech.dsstudio.minecarft.testing;

import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Map;

public class IOUtils {
	public static void println(CommandSender sender) {
		sender.sendMessage("");
	}

	public static void println(CommandSender sender, int i) {
		sender.sendMessage(String.valueOf(i));
	}

	public static void println(CommandSender sender, long l) {
		sender.sendMessage(String.valueOf(l));
	}

	public static void println(CommandSender sender, String s) {
		sender.sendMessage(s);
	}

	public static void println(CommandSender sender, Object o) {
		sender.sendMessage(o.toString());
	}

	public static void println(CommandSender sender, Map<Object, Object> map) {
		map.forEach((k, v) -> sender.sendMessage(k.toString() + " = " + v.toString()));
	}

	public static void println(CommandSender sender, Collection<Object> collection) {
		collection.forEach(it -> sender.sendMessage(it.toString()));
	}
}
