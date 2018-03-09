package com.EvilNotch.dungeontweeks.util.Line;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class LineItemStackBase extends LineBase
{
	public int meta;
	public NBTTagCompound NBT;

	public LineItemStackBase(String s)
	{
		super(s);//Initiate modid:block and line string
		this.meta = parseMeta(this.strLineBase);
		this.NBT = parseNBT(this.strLineBase);
	}
	/**
	 * Returns nbt via String from line format
	 * @param s
	 * @return
	 * @throws NBTException 
	 * @Format "modid:block" <int> {NBT} = int
	 */
	private NBTTagCompound parseNBT(String s)
	{
		String strnbt = "";
		int first = findFirstNBT(s);
		int last = findLastNBT(s);
		if(first != 0 && last != 0)
			strnbt += s.substring(first, last);
		else
			return null;
		
		NBTTagCompound nbt = null;
		try {
			nbt = (NBTTagCompound)JsonToNBT.getTagFromJson(strnbt);
		} 
		catch (NBTException e) {return null;}
		
		return nbt;
	}
	/**
	 * Finds first instanceof "{"
	 * @param s
	 * @return
	 */
	private int findFirstNBT(String s) 
	{
		for(int i=0;i<s.length();i++)
		{
			if(s.substring(i, i+1).equals("{"))
				return i;
		}
		return 0;
	}
	/**
	 * Finds last instanceof "}"
	 * @param s
	 * @return
	 */
	private int findLastNBT(String s) 
	{
		for(int i=s.length()-1;i>0;i--)
		{
			if(s.substring(i, i+1).equals("}"))
				return i+1;
		}
		return 0;
	}
	

	/**
	 * Gets Meta data from <int>
	 * @param str
	 * @return
	 * @Format "modid:block" <int> {NBT} = int
	 */
	private int parseMeta(String str) 
	{
		boolean meta = false;
		String strmeta = "";
		for(int i=0;i<str.length();i++)
		{
			if(str.substring(i, i+1).equals("<"))
				meta = true;
			if(str.substring(i, i+1).equals(">"))
				meta = false;
			if(meta && !str.substring(i, i+1).equals("<") && LineBase.isCharNum(str.substring(i, i+1)) || meta && !str.substring(i, i+1).equals("<") && str.substring(i, i+1).equals("-"))
				strmeta += str.substring(i, i+1);
		}
		if(!strmeta.equals(""))
			return Integer.parseInt(strmeta);
		return -1;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineItemStackBase))
			return this.NBT == null && this.meta == -1 && super.equals(obj);
		LineItemStackBase line = (LineItemStackBase)obj;
		boolean nbt = false;
		if(this.NBT != null && line.NBT != null)
			nbt = this.NBT.equals(line.NBT);
		if(this.NBT == null)
			nbt = line.NBT == null;
		return super.equals(obj) && this.meta == line.meta && nbt;
	}

	@Override
	public String toString()
	{
		String strid = super.toString();
		return strid + " <" + this.meta + "> " + this.NBT;	
	}
	
	@Override
	public String getString()
	{
		String str = "\"" + this.modid + ":" + this.name + "\"";
		if(this.meta != -1)
			str += " <" + this.meta + ">";
		if(this.NBT != null && meta != -1)
			str += " " + this.NBT.toString();
		if(this.NBT != null && meta == -1)
			str += " " + this.NBT.toString();
		return str;
	}

}
