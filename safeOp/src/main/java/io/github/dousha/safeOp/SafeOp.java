package io.github.dousha.safeOp;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SafeOp extends JavaPlugin implements Listener{
	private FileConfiguration yaml = getConfig();
	private List<String> list = new ArrayList<String>();
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		getServer().getLogger().info("[SafeOp] Loading Op list.");
		saveDefaultConfig();
		list = (List<String>) yaml.getList("ops");
		for(OfflinePlayer p : getServer().getOperators()){
			getServer().getLogger().info(p.getName());
			if(!(list.contains(p.getName()))){
				getServer().getLogger().info(
						p.getName()
						+ "is NOT in SafeOp list. Deoping."
						);
				p.setOp(false);
			}
		}
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getLogger().info("[SafeOp] Op list loaded.");
	}
	
	@Override
	public void onDisable(){
		flush();
		getServer().getLogger().info("[SafeOp] SafeOp destructed.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(sender.isOp()){
			if(cmd.getName().equals("/safe_op")){
				op(args[0]);
				return true;
			}
			else if(cmd.getName().equals("/safe_deop")){
				deop(args[0]);
				return true;
			}
			else if(cmd.getName().equals("/safe_op_flush")){
				flush();
				saveDefaultConfig();
				return true;
			}
			else
				return false;
		}
		else{
			sender.sendMessage("You must be an op to perform this command!");
			return false;
		}
	}
	
	private void op(String name){
		list.add(name);
		try{
			getServer().getPlayer(name).setOp(true);
		}catch(NullPointerException ex){
			getServer().getLogger().info("Attempting to op a non-existing player. Ignored.");
		}
	}
	
	private void deop(String name){
		list.remove(name);
		try{	
			getServer().getPlayer(name).setOp(false);
		}catch(NullPointerException ex){
			getServer().getLogger().info("Attempting to deop a non-existing player. Ignored.");
		}
	}
	
	private void flush(){
		getServer().getLogger().info("[SafeOp] Flushing into disk.");
		yaml.set("ops", list);
		saveDefaultConfig();
		getServer().getLogger().info("[SafeOp] Op list written.");
	}
}
