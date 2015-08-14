package io.github.dousha.rpgFull.rpgfull;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class RPGFull extends JavaPlugin implements Listener{
	// the usage of this class is to init everything
	private final String _myname = "[RPGFull] ";
	
	@Override
	public void onEnable(){
		yell("Hello!");
		yell("RPGFull.init()");
	}
	
	@Override
	public void onDisable(){
		
	}
	
	public void yell(String msg){
		getLogger().info(_myname + msg);
	}
}
