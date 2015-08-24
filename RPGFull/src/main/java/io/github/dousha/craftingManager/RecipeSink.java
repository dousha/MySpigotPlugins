package io.github.dousha.craftingManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import io.github.dousha.rpgFull.utils.Pair;
import io.github.dousha.rpgFull.utils.Yell;

public class RecipeSink {
	// List<Pair<$recipeName, Pair<List<$in>, List<$out>>>>
	private List<Pair<String, Pair<List<ItemStruct>, List<ItemStruct>>>> _sink;
	// List<Pair<$majorItem, List<$recipeNames>>>
	private Map<ItemStruct, List<String>> _majorSink;
	
	RecipeSink(YamlConfiguration yaml){
		_sink = new ArrayList<Pair<String, Pair<List<ItemStruct>, List<ItemStruct>>>>();
		_majorSink = new HashMap<ItemStruct, List<String>>();
		loadSink(yaml);
	}
	
	@SuppressWarnings("unchecked")
	public void loadSink(YamlConfiguration yaml){
		List<Map<?, ?>> listin = new ArrayList<Map<?, ?>>();
		List<Map<?, ?>> listout = new ArrayList<Map<?, ?>>();
		int i = 1, j = 0;
		String curName;
		for(i = 1;; i++){
			String id = String.valueOf(i);
			curName = yaml.getString(id + ".name");
			if(curName == null) break;
			else{
				List<ItemStruct> til = new ArrayList<ItemStruct>(), tol = new ArrayList<ItemStruct>();
				Pair<List<ItemStruct>, List<ItemStruct>> curPair = new Pair<List<ItemStruct>, List<ItemStruct>>();
				listin = yaml.getMapList(id + ".in");
				listout = yaml.getMapList(id + ".out");
				for(j = 0; j < listin.size(); j++){
					Map<String, Object> tm = (Map<String, Object>) listin.get(j);
					ItemStruct curStruct = new ItemStruct();
					try{
						curStruct.explictation = (int) tm.get("explictation");
					}catch(NullPointerException e){
						curStruct.explictation = 0;
					}
					curStruct.type = false;
					curStruct.itemname = (String) tm.get("itemname");
					curStruct.isMajor = (boolean) tm.get("isMajor");
					curStruct.leastAmount = (int) tm.get("leastAmount");
					try{
						curStruct.base = (double) tm.get("base");
					}catch(NullPointerException e){
						curStruct.base = 1.0;
					}
					if(curStruct.base != 1.0){
						if(curStruct.base < 1.0){
							curStruct.formula = (String) tm.get("formula");
							// check if formula is legal
							// if `a' is 0, the formula is meaningless
							curStruct.a = (double) tm.get("a");
							if(curStruct.a == 0.0) throw new IllegalArgumentException();
							// when using log, if `b' is less than 0 or is 1, the formula is meaning less
							curStruct.b = (double) tm.get("b");
							if(curStruct.formula.equals("log") && (curStruct.b < 0 || curStruct.b == 1 ))
								throw new IllegalArgumentException();
							curStruct.c = (double) tm.get("c");
							curStruct.isMagic = false;
						}
						else{
							curStruct.isMagic = true;
						}
					}
					til.add((ItemStruct) curStruct.clone());
					curStruct = null;
					tm = null;
				}
				for(j = 0; j < listout.size(); j++){
					ItemStruct curStruct = new ItemStruct();
					Map<String, Object> tm = (Map<String, Object>) listout.get(j);
					curStruct.type = true;
					curStruct.itemname = (String) tm.get("itemname");
					if(tm.get("method") != null)
						curStruct.giveMethod = (String) tm.get("method");
					else
						curStruct.giveMethod = null;
					tol.add((ItemStruct) curStruct.clone());
					curStruct = null;
				}
				curPair.put(til, tol);
				_sink.add(new Pair<String, Pair<List<ItemStruct>, List<ItemStruct>>>(curName, curPair));
				til = null;
				tol = null;
				curPair = null;
			}
		}
		loadMajorSink();
		Yell.yell(String.valueOf(i - 1) + " recipe(s) loaded.");
	}
	
	private void loadMajorSink(){
		for(int i = 0; i < _sink.size(); i++){
			String curRecipeName = _sink.get(i).getFirst();
			List<ItemStruct> curList = _sink.get(i).getSecond().getFirst();
			for(ItemStruct item : curList){
				if(item.isMajor){
					if(_majorSink.containsKey(item)){
						_majorSink.get(item).add(curRecipeName);
					}
					else{
						// XXX: may contain nuts
						List<String> tList = new ArrayList<String>();
						tList.add(curRecipeName);
						_majorSink.put(item, tList);
					}
				}
			}
		}
	}
	
	public Pair<List<ItemStruct>, List<ItemStruct>> lookupByRecipeName(String name){
		for(int i = 0; i < _sink.size(); i++){
			if(_sink.get(i).getFirst().equals(name))
				return _sink.get(i).getSecond();
		}
		return null;
	}
	
	public List<String> lookupRecipeByMajorItem(Inventory inv){
		Set<ItemStruct> set = _majorSink.keySet();
		for(ItemStack item : inv.getContents()){
			for(ItemStruct i : set){
				switch(i.explictation){
				case 0:
					// determine by name
					if(item.hasItemMeta() 
							&& item.getItemMeta().hasDisplayName() 
							&& item.getItemMeta().getDisplayName().equals(i.itemname))
						return _majorSink.get(i);
					break;
				case 1:
					// determine by material
					if(item.getType().equals(Material.valueOf(i.itemname)))
						return _majorSink.get(i);
					break;
				case 2:
					// determine by name AND material
					if(item.hasItemMeta() 
							&& item.getItemMeta().hasDisplayName() 
							&& item.getItemMeta().getDisplayName().equals(i.itemname)
							&& item.getType().equals(Material.valueOf(i.itemname)))
						return _majorSink.get(i);
					break;
				default:
					return null;	
				}
			}
		}
		return null;
	}

}
