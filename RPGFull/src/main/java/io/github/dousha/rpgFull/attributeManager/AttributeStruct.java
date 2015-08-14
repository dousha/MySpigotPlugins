package io.github.dousha.rpgFull.attributeManager;

public class AttributeStruct implements Cloneable {
	public double hp; // it's a kind of waste, though
	public double atk;
	public double def;
	public double hitRate;
	public double ultraHitRate;
	public double ultraHitDamageRate;
	
	@Override
	public Object clone(){
		AttributeStruct o = null;
		try{
			o = (AttributeStruct) super.clone();
		}
		catch(CloneNotSupportedException ex){
			ex.printStackTrace();
		}
		return o;
	}
}
