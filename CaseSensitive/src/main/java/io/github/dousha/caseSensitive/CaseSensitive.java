package io.github.dousha.caseSensitive;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CaseSensitive extends JavaPlugin implements Listener {
	private final String _myname = "[CaseSensitive] ";
	private String _kickReason;
	private List<String> _loggedIn;
	
	@Override
	public void onEnable(){
		yell("Enabling CaseSensitive");
		saveDefaultConfig();
		_loggedIn = new ArrayList<String>();
		_kickReason = getConfig().getString("kickReason");
		getServer().getPluginManager().registerEvents(this, this);
		yell("Done");
	}
	
	@Override
	public void onDisable(){
		yell("Disabling CaseSensitive");
	}
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent e){
		String curPlayerName = e.getPlayer().getName(); 
		if(_loggedIn.contains(curPlayerName.toLowerCase())){
			e.getPlayer().kickPlayer(_kickReason);
			yell(curPlayerName + "is kicked.");
		}
		else
			_loggedIn.add(curPlayerName.toLowerCase());
	}
	
	@EventHandler
	public void onPlayerLeaveEvent(PlayerQuitEvent e){
		_loggedIn.remove(e.getPlayer().getName().toLowerCase());
	}
	
	private void yell(String msg){
		getServer().getLogger().info(_myname + msg);
	}
}
