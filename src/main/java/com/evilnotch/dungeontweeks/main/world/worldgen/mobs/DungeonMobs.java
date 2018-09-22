package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.annotation.Nullable;

import com.evilnotch.dungeontweeks.main.Config;
import com.evilnotch.dungeontweeks.main.events.EventDungeon;
import com.evilnotch.dungeontweeks.main.events.EventDungeon.Type;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.EntityUtil;
import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.Line;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.comment.Comment;
import com.evilnotch.lib.util.line.comment.IComment;
import com.evilnotch.lib.util.line.config.ConfigBase;
import com.evilnotch.lib.util.line.config.ConfigLine;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
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
	
	public static void cacheMobs()
	{
		entries.clear();
		Config.definitions.clear();
		baseDir = new File(Config.dir,"entries");
		
		EntityUtil.cacheEnts();
		cacheForge();
		cacheVanilla();
		loadDefinitions();
		populateConfigs();
	}

	public static void cacheVanilla() 
	{
		addDungeonMob(mansion, new ResourceLocation("minecraft:spider"), null, 150);
		addDungeonMob(mineshaft, new ResourceLocation("minecraft:cave_spider"), null, 150);
		addDungeonMob(netherfortress, new ResourceLocation("minecraft:blaze"), null, 150);
		addDungeonMob(netherfortress, new ResourceLocation("minecraft:wither_skeleton"), null, 70);
		addDungeonMob(stronghold, new ResourceLocation("minecraft:silverfish"), null, 150);
		addDungeonMob(stronghold, new ResourceLocation("minecraft:silverfish"), null, 150);
	}

	public static void populateConfigs() 
	{
		Set<ResourceLocation> list = EntityUtil.living.keySet();
		HashMap<File,ConfigLine> cfgs = new HashMap();
		
		//bridge forge and custom dungeon tweaks addons using it
		for(DungeonEntry map : codedEntries)
		{
			DungeonEntry e = getDungeonEntry(map.loc, entries);
			File dir = e.baseDir;
			for(DungeonMob mob : map.list)
			{
				File actual = new File(dir,mob.type.getResourceDomain() + ".txt");
				ConfigBase config = getConfig(cfgs,actual);
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
				ConfigLine cfg = getConfig(cfgs,actual);
				LineArray line = new LineArray(loc.toString() + " = " + Config.default_weight);
				cfg.addLine(line);
			}
		}
		for(ConfigLine cfg : cfgs.values())
		{
			cfg.saveConfig(true, false, true);
		}
	}
	public static ConfigLine getConfig(HashMap<File, ConfigLine> cfgs, File actual) {
		ConfigLine cfg = cfgs.get(actual);
		if(cfg == null)
		{
			cfg = new ConfigLine(actual);
			cfg.loadConfig();
			cfgs.put(actual, cfg);
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
				cachedForgeHooks = true;
			}
		}
	}
	/**
	 * use this for coders adding custom weights pre-defined users will still be able to configure them using this mod
	 */
	public static void addDungeonMob(DungeonLocation dungeonId, ResourceLocation entityId, @Nullable NBTTagCompound nbt,int itemWeight) 
	{
		DungeonEntry entry = getDungeonEntry(dungeonId,codedEntries);
		if(entry == null)
		{
			registerDungeon(dungeonId);
			entry = getDungeonEntry(dungeonId, codedEntries);
		}
		
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
		for(LineArray line : Config.definitions)
		{
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

}
