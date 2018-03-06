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
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon.Type;
import com.EvilNotch.dungeontweeks.util.JavaUtil;
import com.EvilNotch.dungeontweeks.util.Line.ConfigBase;
import com.EvilNotch.dungeontweeks.util.Line.LineBase;
import com.EvilNotch.dungeontweeks.util.Line.LineItemStack;
import com.EvilNotch.dungeontweeks.util.Line.LineItemStackBase;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.DungeonHooks.DungeonMob;
import net.minecraftforge.fml.common.Loader;

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
			if(!files.isEmpty())
			for(File file : files)
			{
				String strname = getFileTrueDisplayName(file);
				if(!Loader.isModLoaded(strname))
				{
					System.out.println("Skipping:" + strname + ".txt as mod isn't loaded");
					continue;//do not load configs into memory that don't exist
				}
				ConfigBase cfg = new ConfigBase(file, new ArrayList());
				configs.put(file, cfg);
			}
			if(getFileTrueDisplayName(f).equals("dungeon"))
			{
				ArrayList<DungeonMob> forgemobs = (ArrayList<DungeonMob>) ReflectionUtil.getObject(null, DungeonHooks.class, "dungeonMobs");
				if(forgemobs == null)
					for(int i=0;i<20;i++)
						System.out.println("Forge Dungeon Hooks List Empty Wrong Reflection Name");
				else{
					//import modded weights if added before considering default weight to be whatever user configured
					for(DungeonMob d : forgemobs)
					{
						if(!Loader.isModLoaded(d.type.getResourceDomain()))
						{
							System.out.println("Skipping ForgeHooks Entity:" + d.type + " as mod isn't loaded");
							continue;//don't create config into memory for mods that are in the game
						}
						
						File mod = new File(f,JavaUtil.toFileCharacters(d.type.getResourceDomain()) + ".txt");
						ConfigBase cfg = configs.get(mod);
						if(cfg == null)
						{
							configs.put(mod , new ConfigBase(mod,new ArrayList() ) );
							cfg = configs.get(mod);
						}
						String str = "\"" + d.type.toString() + "\" + = " + d.itemWeight;
						LineBase line = new LineItemStackBase(str);
					
						if(!cfg.containsLine(line))
							cfg.appendLine(new LineItemStack(str + " = " + d.itemWeight));
					}
				 }
			 }
		}
		
		//add lines to configs if necessary from entity list
		for(ResourceLocation loc : list)
		{
			try{
				Class c = EntityList.getClass(loc);
				if(c == null)
				{
					System.out.println("CLASS Null Ask Mod Author To Have Default World Constructor:" + loc);
					continue;
				}
				if(!EntityLiving.class.isAssignableFrom(c) )
				{
					if(Config.Debug)
						System.out.println("nonliving:" + loc);
					continue;
				}
			}catch(Throwable t){t.printStackTrace(); continue;}
			
			String modid = loc.getResourceDomain();
			String name = loc.getResourcePath();
			for(File f : dirs)
			{
				if(!f.exists())
					f.mkdirs();
				File mod = new File(f,JavaUtil.toFileCharacters(modid) + ".txt");
				String dungeontype = getFileTrueDisplayName(mod.getParentFile() );
				ConfigBase cfg = configs.get(mod);
				if(cfg == null)
				{
					configs.put(mod, new ConfigBase(mod,new ArrayList()) );
					cfg = configs.get(mod);
				}

				LineBase line = new LineItemStackBase("\"" + loc.toString() + "\"");
				int weight = Config.default_weight;
				if(modid.equals("minecraft"))
				{
					if(name.equals("cave_spider") && dungeontype.equals("mineshaft"))
						weight = 300;
					if(name.equals("silverfish") &&  dungeontype.equals("stronghold"))
						weight = 150;
					if(name.equals("blaze") &&  dungeontype.equals("netherfortress"))
						weight = 300;
					if(name.equals("spider") && dungeontype.equals("mansion"))
						weight = 150;
				}
				if(!cfg.containsLine(line))
					cfg.appendLine(new LineItemStack("\"" + loc.toString() + "\"" +  " = " + weight) );
			}
		 }
		//validate
		if(Config.validateGeneratedEntries)
		for(ConfigBase cfg : configs.values())
		{
			for(LineBase line : cfg.lines)
				if(!list.contains(new ResourceLocation(line.modid + ":" + line.name) ) || !line.modid.equals(getFileTrueDisplayName(cfg.cfgfile) ) )
				{
					cfg.deleteLine(line);
					System.out.println("Not Valid Entry! LineBase Removed:" + line + " " + cfg.cfgfile);
					System.out.println("Domanin Not Valid:" + (!line.modid.equals(getFileTrueDisplayName(cfg.cfgfile)) ) );
				}
		}
		//Populate arrayLists
		for(ConfigBase cfg : configs.values())
		{
			String name = getFileTrueDisplayName(cfg.cfgfile.getParentFile());
			if(name.equals("dungeon"))
				applyDungeonMobs(cfg,DungeonMobs.mob_dungeon);
			else if(name.equals("mineshaft"))
				applyDungeonMobs(cfg,DungeonMobs.mob_mineshaft);
			else if(name.equals("mansion"))
				applyDungeonMobs(cfg,DungeonMobs.mob_mansion);
			else if(name.equals("netherfortress"))
				applyDungeonMobs(cfg,DungeonMobs.mob_netherfortress);
			else if(name.equals("stronghold"))
				applyDungeonMobs(cfg,DungeonMobs.mob_stronghold);
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
		
		
		//sets forge to default list for per entity compatibility detection
		ArrayList default_forge = new ArrayList();
		default_forge.add(new DungeonMob(100, new ResourceLocation("minecraft:blank_dungeon")));
		ReflectionUtil.setObject(null, default_forge, DungeonHooks.class, "dungeonMobs");//empties forges list as it's no longer needed

	}
	/**
	 * populate arraylists
	 */
	public static void applyDungeonMobs(ConfigBase cfg, ArrayList<DungeonMobEntry> list) {
		for(LineBase lineobj : cfg.lines)
		{
			LineItemStack line = null;
			if(lineobj instanceof LineItemStack)
				line = (LineItemStack)lineobj;
			if(line == null || line.head <= 0)
				continue;//skip invalid lines
				list.add(new DungeonMobEntry(line.head,new ResourceLocation(line.modid + ":" + line.name),line.NBT) );	
		}
	}

	/**
	 * returns name from first index till it disovers a dot
	 * @param file
	 * @return
	 */
	private static String getFileTrueDisplayName(File file) {
		return file.getName().split("\\.")[0];
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
    public static DungeonMobEntry pickMobSpawner(Random rand,EventDungeon.Type type)
    {
    	ArrayList<DungeonMobEntry> list = getList(type);
    	if(type == Type.DUNGEON)
    		return WeightedRandom.getRandomItem(rand, list);
    	if(type == Type.MINESHAFT)
    		return WeightedRandom.getRandomItem(rand, list);
    	if(type == Type.STRONGHOLD)
    		return WeightedRandom.getRandomItem(rand,list);
    	if(type == Type.NETHERFORTRESS)
    		return WeightedRandom.getRandomItem(rand, list);
    	if(type == type.MANSION)
    		return WeightedRandom.getRandomItem(rand, list);
    	
    	return new DungeonMobEntry(1,new ResourceLocation("blank_" + type.toString().toLowerCase()),null);
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
    	if(t == Type.MANSION)
    		return mob_mansion;
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
