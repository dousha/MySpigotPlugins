package io.github.dousha.rpgFull.utils;

import org.bukkit.Bukkit;

public class Yell {
	private static final String _myname = "[RPGFull] ";
	
	public static void yell(String msg){
		Bukkit.getServer().getLogger().info(_myname + msg);
	}
}
