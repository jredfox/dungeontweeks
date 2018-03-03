package com.EvilNotch.dungeontweeks.util.Line;

public class LineDWNF extends LineBase{

	public LineBase[] modid_block;
	
	public LineDWNF(String s) 
	{
		super(s);
		this.modid_block = getInts(s); //Gets line breakup
	}
	/**
	 * Breaks up the entire line but, doesn't return any ints
	 * @param s
	 * @return
	 */
	public LineBase[] getInts(String s) 
	{	
		String[] parts = this.strLineBase.split(",");//Splits initial lines but, doesn't remove quote values
		LineBase[] ints = new LineBase[parts.length];
		for(int i=0;i<parts.length;i++)
			ints[i] = LineDynamicLogic.getLineFromString(parts[i]);
		return ints;
	}
	public LineBase getLine(int index)
	{
		if(index < this.modid_block.length)
			return this.modid_block[index];
		else
			return null;
	}
	public int getEnchantId(int index)
	{
		return Integer.parseInt(this.modid_block[index].modid);
	}
	public int getEnchantLevel(int index)
	{
		return Integer.parseInt(this.modid_block[index].name);
	}
	/**
	 * Returnes modid[0] name[1]
	 * @param s
	 * @return
	 */
	@Override
	protected String[] getStrId(String s){return null;}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineDWNF))
			return false;
		LineDWNF dynamic = (LineDWNF)obj;
		if(dynamic.modid_block.length != this.modid_block.length)
			return false;
		for(int i=0;i<this.modid_block.length;i++)
		{
			LineBase lineobj = getLine(i);
			if(!lineobj.equals(dynamic.getLine(i)))
				return false;//If lines don't equal each other this entire object is not equal
			
		}
		return true;
	}
	
	@Override
	public String toString()
	{
		String str = "";
		for(int i=0;i<this.modid_block.length;i++)
			str += this.getLine(i).toString() + ",";
		return str.substring(0, str.length()-1);
	}

}
