package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.evilnotch.dungeontweeks.main.Config;
import com.evilnotch.dungeontweeks.main.events.EventDungeon;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.util.EntityUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigLine;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;

public class DungeonMobs {

	public static List<DungeonEntry> entries = new ArrayList();//modded definitions of mob entries
	/**
	 * list of entries that are coded into the game via my mod and or forge's for default weights and new entries
	 */
	public static List<DungeonEntry> codedEntries = new ArrayList();//allow mods to have custom default weights
	public static File baseDir = null;
	
	public static final DungeonLocation dungeon = new DungeonLocation(new ResourceLocation("dungeon"));
	public static final DungeonLocation mansion = new DungeonLocation(new ResourceLocation("mansion"));
	public static final DungeonLocation mineshaft = new DungeonLocation(new ResourceLocation("mineshaft"));
	public static final DungeonLocation netherfortress = new DungeonLocation (new ResourceLocation("netherfortress"));
	public static final DungeonLocation stronghold = new DungeonLocation(new ResourceLocation("stronghold"));
	
	public static final ResourceLocation blank = new ResourceLocation("blank");
	public static final DungeonMobNBT blank_mob = new DungeonMobNBT(blank,-1);
	public static final DungeonMobNBT pig = new DungeonMobNBT(new ResourceLocation("pig"),-1);
	
	public static void cacheMobs()
	{
		entries.clear();
//		Config.definitions.clear();
		baseDir = new File(Config.dir,"entries");
		
		EntityUtil.cacheEnts();
		cacheForge();
		loadDefinitions();
		populateConfigs();
	}

	public static void populateConfigs() 
	{
		Set<ResourceLocation> list = EntityUtil.living.keySet();
		HashMap<File,ConfigLine> cfgs = new HashMap();
		
		//bridge forge and custom dungeon tweaks addons using it
		for(DungeonEntry map : codedEntries)
		{
			DungeonEntry e = getDungeonEntry(map.loc, entries);
			if(e == null)
				continue;
			File dir = e.baseDir;
			for(DungeonMob mob : map.list)
			{
				File actual = new File(dir,mob.type.getResourceDomain() + ".txt");
				ConfigBase config = getConfig(e,cfgs,actual);
				LineArray line = null;
				if(mob instanceof DungeonMobNBT)
				{
					DungeonMobNBT d = (DungeonMobNBT)mob;
					String str = mob.type.toString();
					if(d.hasNBT())
						str += " " + d.nbt.toString();
					line = new LineArray(str + " = " + mob.itemWeight);
				}
				else
					new LineArray(mob.type.toString() + " = " + mob.itemWeight);
				config.addLine(line);
			}
		}
		
		//populate blanks for configuration purposes
		for(DungeonEntry map : entries)
		{
			File dir = map.baseDir;
			for(ResourceLocation loc : list)
			{
				String domain = loc.getResourceDomain();
				File actual = new File(dir,domain + ".txt");
				ConfigLine cfg = getConfig(map,cfgs,actual);
				LineArray line = new LineArray(loc.toString() + " = " + Config.default_weight);
				cfg.addLine(line);
			}
			//custom entries
			getConfig(map,cfgs, new File(dir,"custom/custom.txt"));
			
			//parse & save
			map.parseConfigs(list);
			map.saveConfigs();
			map.clearConfigs();
		}
	}
	
	public static ConfigLine getConfig(DungeonEntry map,HashMap<File, ConfigLine> cfgs, File actual) 
	{
		ConfigLine cfg = cfgs.get(actual);
		if(cfg == null)
		{
			cfg = new ConfigLine(actual,JavaUtil.<String>asArray("Dungeon Tweaks for:" + JavaUtil.getFileTrueDisplayName(actual),"Format mobid:mobname { } = weight"));
			if(Config.fancyConfig)
				cfg.header = "DungeonMobs";
			cfg.loadConfig();
			cfgs.put(actual, cfg);
			map.addConfig(cfg);
		}
		return cfg;
	}
	
	/**
	 * set to stop the cache from re-instantiating itself over and over again when adding compat to forge
	 */
	public static boolean cachedForgeHooks = false;
	/**
	 * get a default weight when generating a file
	 */
	public static void cacheForge()
	{
		if(!cachedForgeHooks)
		{
			List<DungeonMob> forgeHooks = (List<DungeonMob>) ReflectionUtil.getObject(null, DungeonHooks.class, "dungeonMobs");
			for(DungeonMob mob : forgeHooks)
			{
				NBTTagCompound nbt = null;
				if(mob instanceof DungeonMobNBT)
					nbt = ((DungeonMobNBT)mob).nbt;
				addDungeonMob(dungeon,mob.type,nbt,mob.itemWeight);
			}
		}
		cachedForgeHooks = true;
	}
	/**
	 * use this for any dim and or reflection
	 */
	public static void addDungeonMob(ResourceLocation dungeonId, ResourceLocation entityId,int itemWeight) 
	{
		addDungeonMob(dungeonId,entityId,null,itemWeight);
	}
	public static void addDungeonMob(ResourceLocation dungeonId, ResourceLocation entityId, @Nullable NBTTagCompound nbt,int itemWeight) 
	{
		addDungeonMob(new DungeonLocation(dungeonId),entityId, nbt, itemWeight);
	}
	public static void addDungeonMob(ResourceLocation dungeonId,DungeonMob mob)
	{
		addDungeonMob(new DungeonLocation(dungeonId),mob);
	}
	
	public static void addDungeonMob(DungeonLocation dungeonId, ResourceLocation entityId,int itemWeight) 
	{
		addDungeonMob(dungeonId,entityId,null,itemWeight);
	}
	public static void addDungeonMob(DungeonLocation dungeonId,DungeonMob mob)
	{
		addDungeonMob(dungeonId,mob.type,mob instanceof DungeonMobNBT ? ((DungeonMobNBT)mob).nbt : null, mob.itemWeight);
	}

	/**
	 * use this for coders adding custom weights pre-defined users will still be able to configure them using this mod
	 */
	public static void addDungeonMob(DungeonLocation dungeonId, ResourceLocation entityId, @Nullable NBTTagCompound nbt,int itemWeight) 
	{
		DungeonEntry entry = getOrRegDungeonEntry(dungeonId,codedEntries);
		
		DungeonMobNBT mob = new DungeonMobNBT(entityId,nbt,itemWeight);
		for(DungeonMobNBT rndMob : entry.list)
		{
			if(rndMob.equals(mob))
			{
				rndMob.itemWeight += itemWeight;
				return;
			}
		}
		entry.list.add(mob);
	}
	
	public static List<? extends DungeonMob> getDungeonList(ResourceLocation loc, boolean coded)
	{
		return getDungeonList(new DungeonLocation(loc),coded);
	}
	
	public static List<? extends DungeonMob> getDungeonList(DungeonLocation loc, boolean coded)
	{
		DungeonEntry e = coded ? getOrRegDungeonEntry(loc, codedEntries) : getDungeonEntry(loc, entries);
		if(e == null)
		{
			return null;
		}
		return e.list;
	}
	
	/**
	 * remove entry in code. All mobs are still configurable in dungeon tweaks but, the initial code weight will be removed
	 * or if it's NBT/Sub mob the extra meta line won't exist
	 */
	public static void removeDungeonMob(DungeonLocation dungeonId, ResourceLocation entityId, @Nullable NBTTagCompound nbt)
	{
		DungeonEntry entry = getOrRegDungeonEntry(dungeonId,codedEntries);
		entry.list.remove(new DungeonMobNBT(entityId,nbt,-1));
	}

	public static DungeonEntry getOrRegDungeonEntry(DungeonLocation dungeonId, List<DungeonEntry> list) 
	{
		DungeonEntry entry = getDungeonEntry(dungeonId,list);
		if(entry == null)
		{
			registerDungeon(dungeonId);
			entry = getDungeonEntry(dungeonId, codedEntries);
		}
		return entry;
	}
	public static boolean containsDungeonEntry(ResourceLocation dungeonId,int dimension, ResourceLocation biome)
	{
		return getDungeonEntry(dungeonId,dimension,biome) != null;
	}
	public static DungeonEntry getDungeonEntry(ResourceLocation dungeonId,int dim,ResourceLocation biome)
	{
		DungeonEntry e = getDungeonEntry(new DungeonLocation(dungeonId,biome),entries);
		if(e == null)
			e = getDungeonEntry(new DungeonLocation(dungeonId,dim),entries);
		if(e == null)
			e = getDungeonEntry(new DungeonLocation(dungeonId),entries);
		return e;
	}

	public static DungeonEntry getDungeonEntry(DungeonLocation dungeonId,List<DungeonEntry> entries) 
	{
		for(DungeonEntry e : entries)
		{
			if(e.loc.equals(dungeonId))
			{
				return e;
			}
		}
		return null;
	}
	
	public static void loadDefinitions() 
	{	
		//input user definitions
		Config.loadDefinitionsDir(Config.dir);
		for(ILine l : Config.def.lines)
		{
			LineArray line = (LineArray)l;
			if(!line.getBoolean())
			{
				System.out.println("Skipping:" + line);
				continue;
			}
			ResourceLocation loc = line.getResourceLocation();
			if(line.hasMetaDataNum())
			{
				registerDungeon(loc,baseDir,line.getMetaInt());
			}
			else if(line.hasStringMeta())
			{
				registerDungeon(loc,baseDir,new ResourceLocation(line.getMetaString()) );
			}
			else
			{
				registerDungeon(loc,baseDir);
			}
		}
	}

	/**
	 * for coders weights only
	 */
	public static void registerDungeon(DungeonLocation loc) 
	{
		codedEntries.add(new DungeonEntry(loc));
	}
	public static void registerDungeon(ResourceLocation loc,File f) 
	{
		if(!Loader.isModLoaded(loc.getResourceDomain()))
			return;
		entries.add(new DungeonEntry(loc,new File(f,loc.toString().replaceAll(":", "/") ) ));
	}
	public static void registerDungeon(ResourceLocation loc,File f,int dimension) 
	{
		if(!Loader.isModLoaded(loc.getResourceDomain()))
			return;
		entries.add(new DungeonEntry(loc,new File(f,loc.toString().replaceAll(":", "/") + "/DIM/" + "DIM-" + dimension),dimension ));
	}
	public static void registerDungeon(ResourceLocation loc,File f,ResourceLocation biome) 
	{
		if(!Loader.isModLoaded(loc.getResourceDomain()))
			return;
		String biomePath = biome.toString().replaceAll(":", "/");
		entries.add(new DungeonEntry(loc,new File(f,loc.toString().replaceAll(":", "/") + "/BIOME/" + biomePath),biome));
	}
	/**
	 * get a random spawner mob from a dungeon
	 */
	public static DungeonMobNBT pickMobSpawner(Random rnd, ResourceLocation dungeonId, int dim,ResourceLocation biome) 
	{
		DungeonEntry e = getDungeonEntry(dungeonId, dim, biome);
		if(e == null || e.list.isEmpty())
			return Config.blankSpawnerWhenBlank ? blank_mob : pig;
		DungeonMobNBT mob = WeightedRandom.getRandomItem(rnd, e.list);
		return mob;
	}
	/**
	 * other mods call this to fire via reflection/dependancy
	 */
	public static void fireDungeonTweaks(ResourceLocation dungeonId,TileEntity tile,Random rnd,World w)
	{
		EventDungeon event = new EventDungeon(tile, rnd, dungeonId, w);
		MinecraftForge.EVENT_BUS.post(event);
	}
	/**
	 * add your dungeon location to dungeon tweaks programmatically without user input. 
	 * and enabled param being do you want this enabled by default
	 */
	public static void addDefinition(ResourceLocation dungeonId, boolean enabled)
	{
		LineArray l = new LineArray(dungeonId.toString() + " = " + enabled);
		Config.codedDef.add(l);
	}

}
