package io.github.dousha.randomCraft.randomcraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class RandomCraft extends JavaPlugin implements Listener {
	private final String _myname = "[RandomCraft] ";
	private HashMap<String, Pair<List<ItemDescription>, List<ItemDescription>>> _lists = 
			new HashMap<String, Pair<List<ItemDescription>, List<ItemDescription>>>();
	// HashMap<listName, recipe<listIn<elementName>, listOut<elementName>>
	private List<Pair<String, String>> _majorList = new ArrayList<Pair<String, String>>();
	// List<Pair<ItemName, RecipeName>>
	private String _handler = "STICK";
	
	@Override
	public void onEnable(){
		loadRecipes();
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getLogger().info(_myname + "Random Craft constructed.");
	}
	
	@Override
	public void onDisable(){
		getLogger().info(_myname + "Random Craft destroyed.");
	}
	
	@EventHandler
	public void onBlockDamage(BlockDamageEvent e){
		if((e.getItemInHand().getType().equals(Material.valueOf(_handler))) 
				&& (e.getBlock().getType().equals(Material.DROPPER))){
			try {
				craft(e.getPlayer(), e.getBlock());
			} catch (MajorItemNotEnoughException ex) {
				e.getPlayer().sendMessage(ChatColor.RED + "[!] \u4e3b\u8981\u7269\u54c1\u4e0d\u8db3" + ChatColor.RESET);
			} catch (ItemNotEnoughException ex) {
				e.getPlayer().sendMessage(ChatColor.RED + "[!] \u6b21\u8981\u7269\u54c1\u4e0d\u8db3" + ChatColor.RESET);
			}
		}
		else if(e.getBlock().getType().equals(Material.DROPPER)){
			try {
				showRate(e.getPlayer(), ((Dropper) e.getBlock().getState()).getInventory());
			} catch (MajorItemNotEnoughException ex) {
				e.getPlayer().sendMessage(ChatColor.RED + "[!] \u4e3b\u8981\u7269\u54c1\u4e0d\u8db3" + ChatColor.RESET);
			} catch (ItemNotEnoughException ex) {
				e.getPlayer().sendMessage(ChatColor.RED + "[!] \u6b21\u8981\u7269\u54c1\u4e0d\u8db3" + ChatColor.RESET);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private void loadRecipes(){
		YamlConfiguration yaml = new YamlConfiguration();
		yaml = (YamlConfiguration) this.getConfig();
		_handler = yaml.getString("handler");
		List<Map<?, ?>> listin = new ArrayList<Map<?, ?>>();
		List<Map<?, ?>> listout = new ArrayList<Map<?, ?>>();
		int i = 1, j = 0;
		String curName;
		for(i = 1;; i++){
			String id = String.valueOf(i);
			curName = yaml.getString(id + ".name");
			if(curName == null) break;
			else{
				List<ItemDescription> til = new ArrayList<ItemDescription>(), tol = new ArrayList<ItemDescription>();
				Pair<List<ItemDescription>, List<ItemDescription>> curPair = new Pair<List<ItemDescription>, List<ItemDescription>>();
				listin = yaml.getMapList(id + ".in");
				listout = yaml.getMapList(id + ".out");
				for(j = 0; j < listin.size(); j++){
					Map<String, Object> tm = (Map<String, Object>) listin.get(j);
					ItemDescription desc = new ItemDescription();
					desc.type = false;
					desc.itemname = (String) tm.get("itemname");
					desc.isMajor = (boolean) tm.get("isMajor");
					desc.leastAmount = (int) tm.get("leastAmount");
					desc.isMagic = false;
					if(!desc.isMajor){
						desc.base = (double) tm.get("base");
						if(desc.base != 1.0){
							if(desc.base < 1.0){
								desc.formula = (String) tm.get("formula");
								desc.arg1 = (double) tm.get("a");
								desc.arg2 = (double) tm.get("b");
								desc.arg3 = (double) tm.get("c");
								desc.isMagic = false;
							}
							else{
								desc.isMagic = true;
							}
						}else{
							desc.formula = "none";
							desc.arg1 = 0.0;
							desc.arg2 = 0.0;
							desc.arg3 = 0.0;
						}
					}
					else{
						_majorList.add(new Pair<String, String>(desc.itemname, curName));
					}
					til.add((ItemDescription) desc.clone());
					desc = null;
					tm = null;
				}
				for(j = 0; j < listout.size(); j++){
					ItemDescription desc = new ItemDescription();
					Map<String, Object> tm = (Map<String, Object>) listout.get(j);
					desc.type = true;
					desc.itemname = (String) tm.get("itemname");
					if(tm.get("method") != null)
						desc.giveMethod = (String) tm.get("method");
					else
						desc.giveMethod = null;
					tol.add((ItemDescription) desc.clone());
					desc = null;
				}
				curPair.put(til, tol);
				_lists.put(curName, curPair);
				yell(curName);
				til = null;
				tol = null;
				curPair = null;
			}
		}
		yell(_myname + String.valueOf(i - 1) + " recipe(s) loaded.");
	}
	
	private void showRate(Player p, Inventory i) throws MajorItemNotEnoughException, ItemNotEnoughException{
		// rate is determined by the formula type and the things that player
		// put in the dropper
		// but we have to get the things right first
		Pair<Double, String> rate = getRate(i);
		if(rate != null)
			p.sendMessage(
					"+-(\u968f\u673a\u5408\u6210)-------------------\n" +
					"+ \u914d\u65b9\u540d\u79f0:" + ChatColor.GOLD + rate.getSecond() + ChatColor.RESET + "\n" +
					"+-------------------------------------\n" +
					"+ \u5408\u6210\u6982\u7387:" + ChatColor.BLUE + ChatColor.BOLD 
						+ String.valueOf(100 * rate.getFirst()) + "%" + ChatColor.RESET + "\n" +
					"+-------------------------------------\n"
					);
		else
			p.sendMessage(
					"+-(\u968f\u673a\u5408\u6210)-------------------\n" +
					"+" + ChatColor.RED + " [!] \u7136\u800c\u5e76\u6ca1\u6709\u8fd9\u4e2a\u914d\u65b9\n" + ChatColor.RESET +
					"+-------------------------------------\n"
					);
	}
	
	private Pair<Double, String> getRate(Inventory i) throws MajorItemNotEnoughException, ItemNotEnoughException{
		String curname = "";
		List<String> avaliableTargets = new ArrayList<String>();
		boolean lackOfMajor = false, lackOfItem = false;
		// matching major item
		for(int j = 0; j < i.getSize(); j++){
			if(i.getItem(j) != null){
				curname = i.getItem(j).getType().name();
				for(int k = 0; k < _majorList.size(); k++){
					if(curname.equals(_majorList.get(k).getFirst())){
						avaliableTargets.add(_majorList.get(k).getSecond());
					}
				}
			}
		}
		for(int k = 0; k < avaliableTargets.size(); k++){
			lackOfMajor = false;
			lackOfItem = false;
			try {
				Pair<Double, String> result = new Pair<Double, String>(calculateRate(avaliableTargets.get(k), i), avaliableTargets.get(k));
				return result;
			} catch (IllegalFormulaException e) {
				e.printStackTrace();
			} catch (MajorItemNotEnoughException ex){
				lackOfMajor = true;
				continue;
			} catch (ItemNotEnoughException ex){
				lackOfItem = true;
				continue;
			}
		}
		if(lackOfMajor) throw new MajorItemNotEnoughException();
		if(lackOfItem) throw new ItemNotEnoughException();
		avaliableTargets = null;
		// if not found, exit
		return null;
	}
	
	private double calculateRate(String targetName, Inventory i) 
			throws IllegalFormulaException, MajorItemNotEnoughException, ItemNotEnoughException{
		Pair<List<ItemDescription>, List<ItemDescription>> recipe = new Pair<List<ItemDescription>, List<ItemDescription>>();
		double rate = 0.0, temp = 0.0;
		boolean noMoreCalculation = false;
		recipe = _lists.get(targetName);
		// DBG
		yell("Searching: " + targetName + "#" + recipe.toString());
		for(int j = 0; j < recipe.getFirst().size(); j++){
			ItemDescription curItem = recipe.getFirst().get(j);
			// DBG
			yell(curItem.itemname + ":" + curItem.leastAmount);
			// magic, don't touch!
			assert(!curItem.type);
			// test if major item is satisfied
			if(curItem.isMajor){
				if(i.containsAtLeast(
						new ItemStack(
								Material.valueOf(
										curItem.itemname)), 
										curItem.leastAmount)){
					continue;
				}
				else throw new MajorItemNotEnoughException(); // major item not satisfied, exit
			}
			// skip any item which base is 1.0 and satisfied least amount
			if(curItem.base == 1.0){
				int location = i.first(Material.valueOf(curItem.itemname));
				ItemStack is = null;
				if(location >= 0)
					is = i.getItem(location);
				if(is != null && curItem.leastAmount <= is.getAmount())
					rate *= 1.0;
				else throw new ItemNotEnoughException();
			}
			// if got magic item, set rate to 1.0 then exit
			if(curItem.isMagic){
				int seekItemLocation = i.first(Material.valueOf(curItem.itemname));
				if(seekItemLocation >= 0){
					rate = 1.0;
					noMoreCalculation = true;
					continue;
				}
			}
			// then it's gonna rocks
			// accumulate rate for each item 
			if(!curItem.formula.equals("none")){
				int seekItemLocation = 255;
				seekItemLocation = i.first(Material.valueOf(curItem.itemname));
				if(seekItemLocation >= 0 && seekItemLocation < 10){
					if(noMoreCalculation) continue;
					if(curItem.formula.equals("linear")){
						// linear calculation
						temp = (curItem.arg1 
								* i.getItem(seekItemLocation).getAmount() 
								+ curItem.arg2);
					}
					else if(curItem.formula.equals("log")){
						// log_a b / log_a c = log_c b
						temp = (curItem.arg1
								* (Math.log(i.getItem(seekItemLocation).getAmount()) 
								/ Math.log(curItem.arg2)) 
								+ curItem.arg3);
					}
					else if(curItem.formula.equals("expr")){
						temp = (curItem.arg1 
								* (Math.pow(i.getItem(seekItemLocation).getAmount(), 
										curItem.arg2))
								+ curItem.arg3);
					}
					else{
						throw new IllegalFormulaException();
					}
					if(temp <= 0.0) throw new ItemNotEnoughException(); // other items are not satisfied, exit
					rate += temp;
					temp = 0.0;
				}
			}
			curItem = null;
		}
		return rate;
	}
	
	private void craft(Player p, Block d) throws MajorItemNotEnoughException, ItemNotEnoughException{
		craft(p, ((Dropper) d.getState()).getInventory());
		((Dropper) d.getState()).getInventory().clear(); // clear that dropper ASAP
	}
	
	private void craft(Player p, Inventory i) throws MajorItemNotEnoughException, ItemNotEnoughException{
		p.sendMessage("\u5408\u6210...");
		Pair<Double, String> rate = getRate(i);
		if(rate != null){
			if(new Random().nextDouble() < rate.getFirst()){
				for(int j = 0; j < _lists.get(rate.getSecond()).getSecond().size(); j++){
					ItemDescription desc = _lists.get(rate.getSecond()).getSecond().get(j);
					if(desc.giveMethod == null){
						ItemStack item = new ItemStack(Material.valueOf(desc.itemname));
						// TODO: add lore
						p.getInventory().addItem(item);
					}else{
						if(desc.giveMethod.equals("command")){
							// in this time, execute desc.itemname by default
							String command = desc.itemname.replace("/", "");
							command = command.replace("@p", p.getName());
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
							command = null;
						}
					}
					desc = null;
				}
				p.sendMessage(ChatColor.GREEN + "===== \u5408\u6210\u6210\u529f =====" + ChatColor.RESET);
			}
			else{
				p.sendMessage(ChatColor.RED + "xxxxx \u5408\u6210\u5931\u8d25 xxxxx" + ChatColor.RESET);
			}
		}
	}
	
	private void yell(String msg){
		getServer().getLogger().info(msg);
	}
}
