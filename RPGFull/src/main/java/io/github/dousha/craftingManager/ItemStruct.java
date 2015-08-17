package io.github.dousha.craftingManager;

public class ItemStruct implements Cloneable {
	public boolean type; // false = in, true = out, I would use #define in c/c++!
	
	public String itemname;
	public int leastAmount;
	
	public boolean isMajor;
	public int explictation;
	
	public double base;
	public String formula;
	public double a, b, c;
	public boolean isMagic;
	public String giveMethod;
	
	ItemStruct(){
		type = false;
		itemname = "";
		leastAmount = 0;
		isMajor = false;
		explictation = 0;
		base = 0.0;
		formula = "none";
		a = 0f;
		b = .0;
		c = 0;
		isMagic = false;
		giveMethod = "";
	}
	
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
