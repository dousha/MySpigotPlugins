package io.github.dousha.suppressCommand;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuppressCommand extends JavaPlugin implements Listener{
	private String _myname = "[SuppressCommand] ";
	private List<String> _suppresseds;
	private int _runLevel = 0;
	
	@Override
	public void onEnable(){
		saveDefaultConfig();
		_suppresseds = getConfig().getStringList("suppresseds");
		_runLevel = getConfig().getInt("runLevel");
		if(_runLevel > 3 || _runLevel < 0){
			yell("Run level incorrect!");
			yell("Readjusting to 3");
			_runLevel = 3;
		}
		getServer().getPluginManager().registerEvents(this, this);
		yell("Enabled SuppressCommand");
	}
	
	@Override
	public void onDisable(){
		saveConfig();
		yell("Disabled SuppressCommand");
	}
	
	@EventHandler
	public void onPlayerCommand(PlayerCommandPreprocessEvent e){
		String curCmd = e.getMessage();
		yell(curCmd);
		if (!e.getPlayer().hasPermission("suppress.bypass")) {
			for (String suppressed : _suppresseds) {
				if (curCmd.matches(suppressed)) {
					e.setCancelled(true);
					switch (_runLevel) {
						case 0: // silent
							;
							break;
						case 1: // log
							yell("Suppressed command " + e.getMessage() + " fired by " + e.getPlayer().getName());
							break;
						case 2: // tell
							e.getPlayer().sendMessage("This command is suppressed by admins.");
							break;
						case 3: // both log and tell
							yell("Suppressed command " + e.getMessage() + " fired by " + e.getPlayer().getName());
							e.getPlayer().sendMessage("This command is suppressed by admins.");
							break;
						default: // behave same as level 0
							;
							break;
					}
					break; // if matched, there's no need to continue.
				}
			}
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		switch (cmd.getName()) {
			case "suppress": {
				StringBuilder curCmd = new StringBuilder();
				for (String curPiece : args) {
					curCmd.append(curPiece).append(" ");
				}
				curCmd = new StringBuilder(curCmd.toString().trim());
				_suppresseds.add(curCmd.toString());
				getConfig().set("suppresseds", _suppresseds);
				sender.sendMessage("Suppressed command `" + curCmd + "`");
				break;
			}
			case "nosuppress": {
				StringBuilder curCmd = new StringBuilder();
				for (String curPiece : args) {
					curCmd.append(curPiece).append(" ");
				}
				curCmd = new StringBuilder(curCmd.toString().trim());
				_suppresseds.remove(curCmd.toString());
				getConfig().set("suppresseds", _suppresseds);
				sender.sendMessage("Unsuppressed command `" + curCmd + "`");
				break;
			}
			case "suppressflush":
				saveConfig();
				sender.sendMessage(_myname + "List saved.");
				break;
			case "suppresslist":
				for (String curSuppress : _suppresseds)
					sender.sendMessage(curSuppress);
				break;
			default:
				return false;
		}
		return true;
	}
	
	private void yell(String msg){
		getServer().getLogger().info(_myname + msg);
	}
}
