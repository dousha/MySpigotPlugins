package io.github.dousha.craftingManager;

import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.dousha.rpgFull.utils.Pair;

public class CraftingManager {
	private RecipeSink _recipeSink;
	
	public CraftingManager(YamlConfiguration yaml){
		_recipeSink = new RecipeSink(yaml);
	}
	
	public void craft(Player p, Block b){
		try{
			Pair<Pair<Double, List<ItemStruct>>, ItemStack> i = getRate(((Dropper) b.getState()).getInventory());
			giveInRandom(p, i.getSecond(), i.getFirst());
		}catch(NullPointerException e){
			p.sendMessage(ChatColor.RED 
					+ " [!] \u7136\u800c\u5e76\u6ca1\u6709\u8fd9\u4e2a\u914d\u65b9\n" 
					+ ChatColor.RESET);
		}
	}
	
	// XXX: please rewrite this crap
	public Pair<Pair<Double, List<ItemStruct>>, ItemStack> getRate(Inventory inv){
		List<String> recipeNames = _recipeSink.lookupRecipeByMajorItem(inv);
		List<ItemStruct> in = null;
		List<ItemStruct> out = null;
		if(recipeNames != null){
			ItemStruct protectedStruct = null;
			ItemStack protectedItem = null;
			boolean isSatisfied = true;
			for(ItemStruct item : _recipeSink.lookupByRecipeName(recipeNames.get(0)).getFirst()){
				// XXX: may need optimizations
				if(item.isMajor)
					protectedStruct = item;
			} // find major item
			switch(protectedStruct.explictation){
			case 0:
				// TODO: complete it
				break;
			case 1:
				try{
				protectedItem = 
					inv.getItem(inv.first(Material.valueOf(protectedStruct.itemname)));
				}catch(NullPointerException e){
					// :(
					e.printStackTrace();
				}
				break;
			case 2:
				ItemStack ti = new ItemStack(Material.valueOf(protectedStruct.itemname));
				ti.getItemMeta().setDisplayName(protectedStruct.displayName);
				try{
				protectedItem =
					inv.getItem(inv.first(ti));
				}catch(NullPointerException e){
					// :(
					e.printStackTrace();
				}
				break;
			} // find player's item 
			for(String recipeName : recipeNames){
				Pair<List<ItemStruct>, List<ItemStruct>> curPair
					= _recipeSink.lookupByRecipeName(recipeName);
				in = curPair.getFirst();
				out = curPair.getSecond();
				for(ItemStruct item : in){
					if(isSatisfied){
						switch(item.explictation){
						case 0:
							// determine by name
							// TODO: complete this function
						case 1:
							// determine by material
							if(inv.contains(Material.valueOf(item.itemname)))
								continue;
							else{
								isSatisfied = false;
							}
							break;
						case 2:
							// determine by ItemStack
							// construct an ItemStack
							ItemStack ti = new ItemStack(Material.valueOf(item.itemname));
							ti.getItemMeta().setDisplayName(item.displayName);
							if(inv.containsAtLeast(ti, item.leastAmount))
								continue;
							else{
								isSatisfied = false;
							}
							break;
						}
					}else{
						break;
					} // once it doesn't satisfy, exit looking up loop
				} // check if satisfied
				
				if(isSatisfied)
					break;
				else
					continue;
			} // lookup every available recipes
			
			if(isSatisfied){
				Pair<Pair<Double, List<ItemStruct>>, ItemStack> s =
						new Pair<Pair<Double, List<ItemStruct>>, ItemStack>();
					s.setFirst(new Pair<Double, List<ItemStruct>>(calculateRate(in), out));
					s.setSecond(protectedItem);
				return s;
			}else{
				return null;
			}
			
		} // recipe not found
		else
			return null;
	}
	
	private double calculateRate(List<ItemStruct> recipe){
		
		return 0.0;
	}
	
	private void giveInRandom(Player p, ItemStack majorItem, Pair<Double, List<ItemStruct>> param){
		p.sendMessage("\u5408\u6210...");
		if(new Random().nextDouble() < param.getFirst()){
			p.sendMessage(ChatColor.GREEN + "===== \u5408\u6210\u6210\u529f =====" + ChatColor.RESET);
			for(ItemStruct item : param.getSecond()){
				if(item.giveMethod.equals("command")){
					String command = item.itemname;
					command = command.replaceAll("@p", p.getName());
					Bukkit.getServer().dispatchCommand(
							Bukkit.getServer().getConsoleSender(), command);
					command = null;
				}else{
					ItemStack ti = new ItemStack(Material.valueOf(item.itemname));
					if(item.displayName != null)
						ti.getItemMeta().setDisplayName(item.displayName);
					p.getInventory().addItem(ti); // XXX: may contain nuts
				}
			}
		}else{
			p.sendMessage(ChatColor.RED + "xxxxx \u5408\u6210\u5931\u8d25 xxxxx" + ChatColor.RESET);
			p.getInventory().addItem(majorItem);
		}
	}
}
