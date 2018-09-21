package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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

	public static List<MappingEntry> entries = new ArrayList();//modded definitions of mob entries
	public static File baseDir = null;
	
	public static final ResourceLocation dungeon = new ResourceLocation("dungeon");
	public static final ResourceLocation mansion = new ResourceLocation("mansion");
	public static final ResourceLocation mineshaft = new ResourceLocation("mineshaft");
	public static final ResourceLocation netherfortress = new ResourceLocation("netherfortress");
	public static final ResourceLocation stronghold = new ResourceLocation("stronghold");
	
	public static void cacheMobs()
	{
		Class c = DungeonHooks.class;
		EntityUtil.cacheEnts();
		entries.clear();
		Config.definitions.clear();
		baseDir = new File(Config.dir,"entries");
		
		loadDefinitions();
		populateConfigs();
	}
	
	public static void populateConfigs() 
	{
		Set<ResourceLocation> list = EntityUtil.living.keySet();
		HashMap<File,ConfigLine> cfgs = new HashMap();
		
		for(MappingEntry map : entries)
		{
			File dir = map.baseDir;
			for(ResourceLocation loc : list)
			{
				String domain = loc.getResourceDomain();
				File actual = new File(dir,domain + ".txt");
				ConfigLine cfg = cfgs.get(actual);
				if(cfg == null)
				{
					cfg = new ConfigLine(actual);
					cfg.loadConfig();
					cfgs.put(cfg.file, cfg);
				}
				LineArray line = new LineArray(loc.toString() + " = " + getWeightForGen(map.loc,loc) );
				cfg.addLine(line);
			}
		}
		for(ConfigLine cfg : cfgs.values())
		{
			cfg.saveConfig(true, false, true);
		}
	}
	public static List<DungeonMob> forgeHooks = null;
	/**
	 * get a default weight when generating a file
	 */
	public static int getWeightForGen(ResourceLocation dungeonId,ResourceLocation loc)
	{
		if(dungeonId.equals(dungeon))
		{
			if(forgeHooks == null)
				forgeHooks = (List<DungeonMob>) ReflectionUtil.getObject(null, DungeonHooks.class, "dungeonMobs");
			DungeonMob forge = getDungeonMob(forgeHooks,loc);
			if(forge != null)
				return forge.itemWeight;
		}
		return Config.default_weight;
	}

	private static DungeonMob getDungeonMob(List<DungeonMob> li, ResourceLocation loc) {
		for(DungeonMob mob : li)
		{
			if(mob.type.equals(loc))
				return mob;
		}
		return null;
	}
	
	public static void loadDefinitions() 
	{
		registerDungeon(dungeon,baseDir);
		registerDungeon(mansion,baseDir);
		registerDungeon(mineshaft,baseDir);
		registerDungeon(netherfortress,baseDir);
		registerDungeon(stronghold,baseDir);
		
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

	public static void registerDungeon(ResourceLocation loc,File f) 
	{
		if(!Loader.isModLoaded(loc.getResourceDomain()))
			return;
		entries.add(new MappingEntry(loc,new File(f,loc.toString().replaceAll(":", "/") ) ));
	}
	public static void registerDungeon(ResourceLocation loc,File f,int dimension) 
	{
		if(!Loader.isModLoaded(loc.getResourceDomain()))
			return;
		entries.add(new MappingEntry(loc,new File(f,loc.toString().replaceAll(":", "/") + "/DIM/" + "DIM-" + dimension),dimension ));
	}
	public static void registerDungeon(ResourceLocation loc,File f,ResourceLocation biome) 
	{
		if(!Loader.isModLoaded(loc.getResourceDomain()))
			return;
		String biomePath = biome.toString().replaceAll(":", "/");
		entries.add(new MappingEntry(loc,new File(f,loc.toString().replaceAll(":", "/") + "/BIOME/" + biomePath),biome));
	}

}
