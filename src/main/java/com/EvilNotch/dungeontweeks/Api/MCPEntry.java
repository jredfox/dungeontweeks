package com.EvilNotch.dungeontweeks.Api;

import java.util.ArrayList;

public class MCPEntry {
	public String srg = null;
	public String name = null;
	public ArrayList<String> classes = new ArrayList();
	
	public MCPEntry(String strsrg, String strname,ArrayList<String> clazz)
	{
		try{
		this.srg = strsrg;
		this.name = strname;
		for(String s : clazz)
			classes.add(s);
		}catch(Exception e){e.printStackTrace();}
	}
	@Override
	public String toString()
	{
		String s = "";
		for(String str : classes)
			s += str + ",";
		return this.srg + "," + this.name + "," + s;
	}

}
