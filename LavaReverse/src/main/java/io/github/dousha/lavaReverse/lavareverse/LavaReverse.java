package io.github.dousha.lavaReverse.lavareverse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class LavaReverse extends JavaPlugin implements Listener{
	private String world = null;
	private File f = null;
	@Override
	public void onEnable(){
		getServer().getPluginManager().registerEvents(this, this);
		f = new File("plugins/lava.cfg");
		if(!(f.exists())){
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try{
				BufferedReader br = new BufferedReader(
						new FileReader(f.getAbsolutePath()));
				world = br.readLine();
				br.close();
			}catch(IOException ex){
				ex.printStackTrace();
			}
		}
		getLogger().info(world);
		getLogger().info("Lava Reverse constructed!");
	}
	
	@Override
	public void onDisable(){
		getLogger().info("Lava Reverse destroyed!");
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent e){
		if(e.getPlayer().getWorld().equals(getServer()
				.getWorld(world))){
			if((e.getItemInHand().getType().equals(Material.BUCKET))
					&& (e.getBlock().getType()
							.equals(Material.OBSIDIAN)))
				e.getBlock().setType(Material.LAVA);
		}
	}
}
