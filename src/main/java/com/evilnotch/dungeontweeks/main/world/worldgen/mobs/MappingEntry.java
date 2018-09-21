package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;

public class MappingEntry {
	public List<DungeonMobEntry> list = null;
	public ResourceLocation loc = null;
	public ResourceLocation biomeLoc = null;
	public int dimension = 0;
	public File baseDir = null;
	public boolean anyDim = false;
	
	/**
	 * dimension specific
	 */
	public MappingEntry(ResourceLocation loc,File f,int dimension)
	{
		this.loc = loc;
		this.baseDir = f;
		this.list = new ArrayList();
		this.dimension = dimension;
	}
	/**
	 * constructor for any dim and any biome
	 */
	public MappingEntry(ResourceLocation loc,File f)
	{
		this.loc = loc;
		this.baseDir = f;
		this.list = new ArrayList();
		this.anyDim = true;
	}
	public MappingEntry(ResourceLocation dungeonId,File f,ResourceLocation biomeId)
	{
		this.loc = dungeonId;
		this.baseDir = f;
		this.list = new ArrayList();
		this.biomeLoc = biomeId;
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
