package com.EvilNotch.dungeontweeks.main.world.worldgen.mobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.EvilNotch.dungeontweeks.Api.ReflectionUtil;
import com.EvilNotch.dungeontweeks.main.Config;
import com.EvilNotch.dungeontweeks.main.MainJava;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon.Type;
import com.EvilNotch.dungeontweeks.util.JavaUtil;
import com.EvilNotch.dungeontweeks.util.Line.ConfigBase;
import com.EvilNotch.dungeontweeks.util.Line.LineBase;
import com.EvilNotch.dungeontweeks.util.Line.LineItemStack;
import com.EvilNotch.dungeontweeks.util.Line.LineItemStackBase;

import net.minecraft.entity.EntityList;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.DungeonHooks.DungeonMob;

public class DungeonMobs {
	public static ArrayList<DungeonMobEntry> mob_dungeon = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_mineshaft = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_stronghold = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_netherfortress = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_mansion = new ArrayList();
	
	public static void cacheMobs()
	{
		mob_dungeon.clear();
		mob_mineshaft.clear();
		mob_stronghold.clear();
		mob_netherfortress.clear();
		
		try{
		Set<ResourceLocation> list = EntityList.getEntityNameList();
		HashMap<File,ConfigBase> configs = new HashMap();
		File dir = new File(Config.dir,"entries");
		File dir_dungeon = new File(dir,"dungeon");
		File dir_mineshaft = new File(dir,"mineshaft");
		File dir_stronghold = new File(dir,"stronghold");
		File dir_netherfortess = new File(dir,"netherfortress");
		File dir_mansion = new File(dir,"mansion");
		File[] dirs = {dir_dungeon,dir_mineshaft,dir_stronghold,dir_netherfortess,dir_mansion};
		
		//cache and/or create config files to disk and memory
		for(File f : dirs)
		{
			if(!f.exists())
				f.mkdirs();
			ArrayList<File> files = new ArrayList();
			getFilesFromDir(f, files, ".txt");
			for(File file : files)
			{
				ConfigBase cfg = new ConfigBase(file, new ArrayList());
				configs.put(file, cfg);
			}
		}
		//add lines to configs if nessary
		for(ResourceLocation loc : list)
		{
			String modid = loc.getResourceDomain();
			String name = loc.getResourcePath();
			//grab intial configed entities
			
			for(File f : dirs)
			{
				if(!f.exists())
					f.mkdirs();
				File mod = new File(f,JavaUtil.toFileCharacters(modid) + ".txt");
				ConfigBase cfg = configs.get(mod);
				if(cfg == null)
					cfg = new ConfigBase(mod,new ArrayList());
				LineBase line = new LineItemStackBase("\"" + loc.toString() + "\"");
				if(!cfg.containsLine(line))
					cfg.appendLine(new LineItemStack("\"" + loc.toString() + "\"" +  " = " + Config.default_weight) );
			}
		 }
		//save configs and parse to lists
		Iterator<Map.Entry<File,ConfigBase> > it = configs.entrySet().iterator();
		while(it.hasNext())
		{
			ConfigBase cfg = it.next().getValue();
			cfg.alphabetize();
			cfg.updateConfig();
		}
		
		}catch(Exception ex){ex.printStackTrace();}
		
		//vanilla and mod support
		ArrayList<DungeonMob> forgemobs = (ArrayList<DungeonMob>) ReflectionUtil.getObject(null, DungeonHooks.class, "dungeonMobs");
		if(forgemobs == null)
			for(int i=0;i<20;i++)
				System.out.println("Forge Dungeon Hooks List Empty Wrong Reflection Name");
		else
			for(DungeonMob d : forgemobs)
				mob_dungeon.add(new DungeonMobEntry(d.itemWeight,d.type,null) );//forge hooks compatibility
		
		//sets forge to default list for per entity compatibility detection
		ArrayList default_forge = new ArrayList();
		default_forge.add(new DungeonMob(100, new ResourceLocation("minecraft:blank_dungeon")));
		ReflectionUtil.setObject(null, default_forge, DungeonHooks.class, "dungeonMobs");//empties forges list as it's no longer needed
		
		//vanilla support
		mob_mineshaft.add(new DungeonMobEntry(470,new ResourceLocation("minecraft:cave_spider"), null ) );
		mob_stronghold.add(new DungeonMobEntry(370,new ResourceLocation("minecraft:silverfish"), null ) );
		mob_netherfortress.add(new DungeonMobEntry(470, new ResourceLocation("minecraft:blaze"),null ) );
	}
	
	public static void getFilesFromDir(File directory, ArrayList<File> files,String extension) 
	{
	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) 
	    {
	        if (file.isFile() && !files.contains(file) && file.getName().endsWith(extension)) 
	        {
	            files.add(file);
	        }
	    }
	}
	
	/**
     * Randomly decides which spawner to use in a dungeon
     */
    public static ResourceLocation pickMobSpawner(Random rand,EventDungeon.Type type)
    {
    	ArrayList<DungeonMobEntry> list = getList(type);
    	if(type == Type.DUNGEON)
    		return WeightedRandom.getRandomItem(rand, list).type;
    	if(type == Type.MINESHAFT)
    		return WeightedRandom.getRandomItem(rand, list).type;
    	if(type == Type.STRONGHOLD)
    		return WeightedRandom.getRandomItem(rand,list).type;
    	if(type == Type.NETHERFORTRESS)
    		return WeightedRandom.getRandomItem(rand, list).type;
    	
    	return null;
    }
    
    public static ArrayList<DungeonMobEntry> getList(Type t){
    	if(t == Type.DUNGEON)
    		return mob_dungeon;
    	if(t == Type.MINESHAFT)
    		return mob_mineshaft;
    	if(t == Type.STRONGHOLD)
    		return mob_stronghold;
    	if(t == Type.NETHERFORTRESS)
    		return mob_netherfortress;
    	return null;
    }
    
    /**
     * Adds a mob to the possible list of creatures the spawner will create.
     * If the mob is already in the spawn list, the rarity will be added to the existing one,
     * causing the mob to be more common.
     *
     * @param name The name of the monster, use the same name used when registering the entity.
     * @param rarity The rarity of selecting this mob over others. Must be greater then 0.
     *        Vanilla Minecraft has the following mobs:
     *        Spider   100
     *        Skeleton 100
     *        Zombie   200
     *        Meaning, Zombies are twice as common as spiders or skeletons.
     * @return The new rarity of the monster,
     */
    public static float addDungeonMob(ResourceLocation name, int rarity,NBTTagCompound nbt,Type type)
    {
        if (rarity <= 0)
            throw new IllegalArgumentException("Rarity must be greater then zero");
        DungeonMobEntry entry = new DungeonMobEntry(rarity,name,nbt);
        
        ArrayList<DungeonMobEntry> list = getList(type);
        for (DungeonMobEntry mob :  list)
        {
            if (entry.equals(mob))
                return mob.itemWeight += rarity;
        }
        list.add(entry);
        return rarity;
    }
    
    public static int removeDungeonMob(ResourceLocation name,NBTTagCompound nbt,Type type)
    {
    	ArrayList<DungeonMobEntry> list = getList(type);
    	DungeonMobEntry e = new DungeonMobEntry(0,name,nbt);
        for (DungeonMobEntry mob : list)
        {
            if (mob.equals(e))
            {
            	list.remove(mob);
            	return mob.itemWeight;
            }
        }
        return 0;
    }

}
