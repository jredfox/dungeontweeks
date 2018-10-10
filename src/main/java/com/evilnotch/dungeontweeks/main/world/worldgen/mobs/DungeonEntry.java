package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.evilnotch.dungeontweeks.main.Config;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigLine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;

public class DungeonEntry {
	public List<DungeonMobNBT> list = new ArrayList();
	public DungeonLocation loc = null;
	public List<ConfigLine> cfgs = new ArrayList();
	
	/**
	 * will be null if used for coding list rather then actual configurable lists
	 */
	@Nullable
	public File baseDir = null;
	
	public DungeonEntry(DungeonLocation loc)
	{
		this(loc,null);
	}
	
	public DungeonEntry(DungeonLocation loc,File base)
	{
		this.loc = loc;
		this.baseDir = base;
	}
	
	/**
	 * dimension specific
	 */
	public DungeonEntry(ResourceLocation loc,File f,int dimension)
	{
		this.loc = new DungeonLocation(loc,dimension);
		this.baseDir = f;
	}
	/**
	 * constructor for any dim and any biome
	 */
	public DungeonEntry(ResourceLocation loc,File f)
	{
		this.loc = new DungeonLocation(loc);
		this.baseDir = f;
	}
	public DungeonEntry(ResourceLocation dungeonId,File f,ResourceLocation biomeId)
	{
		this.loc = new DungeonLocation(dungeonId,biomeId);
		this.baseDir = f;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DungeonEntry))
			return false;
		DungeonEntry e = (DungeonEntry)obj;
		return this.loc.equals(e.loc);
	}
	@Override
	public String toString()
	{
	    return this.loc.toString() + this.list.toString(); 
	}

	public void addConfig(ConfigLine config) {
		this.cfgs.add(config);
	}

	public void parseConfigs(Set<ResourceLocation> locs) 
	{
		for(ConfigLine cfg : this.cfgs)
		{
			for(ILine l : cfg.lines)
			{
				ResourceLocation loc = l.getResourceLocation();
				if(Config.validateGeneratedEntries && !Loader.isModLoaded(loc.getResourceDomain()) || Config.validateGeneratedEntries && !locs.contains(loc) && !loc.equals(DungeonMobs.blank))
				{
					System.out.println("skipping:" + loc + " in " + cfg.file.getName());
					continue;
				}
				LineArray line = (LineArray)l;
				int head = line.getInt();
				if(head == 0)
					continue;
				this.addDungeonMob(loc,line.nbt,head);
			}
		}
	}
	
	/**
	 * add a new dungeon mob to the entry
	 */
	public void addDungeonMob(ResourceLocation loc, NBTTagCompound nbt, int head) 
	{
		this.list.add(new DungeonMobNBT(loc,nbt,head));
	}

	public void saveConfigs() 
	{
		for(ConfigBase base : this.cfgs)
			base.saveConfig(true, false, true);
	}
	/**
	 * to preserve memory as they are no longer needed
	 */
	public void clearConfigs()
	{
		this.cfgs.clear();
	}

}
