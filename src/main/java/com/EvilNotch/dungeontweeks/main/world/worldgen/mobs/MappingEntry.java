package com.EvilNotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;

public class MappingEntry {
	public ArrayList<DungeonMobEntry> list = null;
	public ResourceLocation loc = null;
	public int dimension = 0;
	public File file = null;
	public boolean anyDim = false;
	
	public MappingEntry(ResourceLocation loc, ArrayList<DungeonMobEntry> list,int dimension,File f,boolean anyDim)
	{
		this.list = list;
		this.loc = loc;
		this.dimension = dimension;
		this.file = f;
		this.anyDim = anyDim;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof MappingEntry))
			return false;
		MappingEntry e = (MappingEntry)obj;
		return this.list.equals(e.list) && this.loc.equals(e.loc);
	}
	@Override
	public String toString()
	{
	    String str = "";
	    if(!anyDim)
	        str = "<" + this.dimension + ">";
	    else
	        str = "<" + "*" + ">";
	    return this.loc.toString() + str + this.list.toString(); 
	}

}
