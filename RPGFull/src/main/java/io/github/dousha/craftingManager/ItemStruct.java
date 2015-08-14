package io.github.dousha.craftingManager;

public class ItemStruct implements Cloneable {
	public boolean type; // false = in, true = out, I would use #define in c/c++!
	public String itemname;
	public boolean isMajor;
	public int leastAmount;
	public double base;
	public String formula;
	public double a, b, c;
	public boolean isMagic;
	public String giveMethod;
	
	@Override
	public Object clone(){
		ItemStruct o = null;
		try{
			o = (ItemStruct) super.clone();
		}
		catch(CloneNotSupportedException ex){
			ex.printStackTrace();
		}
		return o;
	}
}
