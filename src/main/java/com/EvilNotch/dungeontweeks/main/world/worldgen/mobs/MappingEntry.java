package com.EvilNotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;

public class MappingEntry {
	public ArrayList<DungeonMobEntry> list;
	public ResourceLocation loc;
	public int dimension;
	public File file;
	
	public MappingEntry(ResourceLocation loc, ArrayList<DungeonMobEntry> list,int dimension,File f)
	{
		this.list = list;
		this.loc = loc;
		this.dimension = dimension;
		this.file = f;
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
	public String toString(){return this.list.toString();}

}
