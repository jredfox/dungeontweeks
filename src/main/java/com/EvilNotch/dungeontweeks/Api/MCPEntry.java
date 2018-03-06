package com.EvilNotch.dungeontweeks.Api;

import java.util.ArrayList;

import com.EvilNotch.dungeontweeks.main.Config;

public class MCPEntry {
	public String srg = null;
	public String name = null;
	public ArrayList<Class> classes = new ArrayList();
	
	public MCPEntry(String strsrg, String strname,ArrayList<String> clazz)
	{
		try{
		this.srg = strsrg;
		this.name = strname;
		for(String s : clazz)
		{
			try{
			Class c = Class.forName(s.replace("/", "."));
			classes.add(c);
			}catch(Throwable t){
				if(Config.Debug)
					System.out.println("MCP Class Not Found:" + s);
			}
		}
		}catch(Exception e){e.printStackTrace();}
	}
	@Override
	public String toString()
	{
		String s = "";
		for(Class c : classes)
			s += c.getName() + ",";
		return this.srg + "," + this.name + "," + s;
	}
	/**
	 * hotfix
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof MCPEntry) )
			return false;
		MCPEntry e = (MCPEntry) obj;
		return this.srg.equals(e.srg) && this.name.equals(e.name) && this.classes.equals(e.classes);
	}

}
