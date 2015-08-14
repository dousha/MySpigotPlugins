package io.github.dousha.randomCraft.randomcraft;

import java.util.HashMap;

// WHY DOESN'T JAVA HAVE A STRUCT

public class ItemDescription implements Cloneable{
	public boolean type; // false = in, true = out, I would use #define in c/c++!
	public String itemname;
	@Deprecated
	public int itemid;
	public boolean isMajor;
	public int leastAmount;
	public double base;
	public String formula;
	public double arg1, arg2, arg3;
	public boolean isMagic;
	// TODO: make it works
	public HashMap<String, String> enchantments; // <name, level>
	// ----^-----------------------^-----------
	public String giveMethod;
	
	@Override
	public Object clone(){
		ItemDescription o = null;
		try{
			o = (ItemDescription) super.clone();
		}
		catch(CloneNotSupportedException ex){
			ex.printStackTrace();
		}
		return o;
	}
}
