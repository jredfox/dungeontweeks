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
import com.EvilNotch.dungeontweeks.util.Line.Comment;
import com.EvilNotch.dungeontweeks.util.Line.ConfigBase;
import com.EvilNotch.dungeontweeks.util.Line.ILine;
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

	public static ArrayList<File> dirs = new ArrayList();
	public static ArrayList<MappingEntry> entries = new ArrayList();//modded definitions of mob entries
	
	public static void cacheMobs()
	{
		mob_dungeon.clear();
		mob_mineshaft.clear();
		mob_stronghold.clear();
		mob_netherfortress.clear();
		mob_mansion.clear();
		//clear other stuff that is in use
		dirs.clear();
		Config.cfgdefinitions.clear();
		entries.clear();
		
		File dir = new File(Config.dir,"entries");
		Config.loadDefinitionsDir(Config.dir);
		
		//populate defined dungeon directories
		for(LineItemStack line : Config.cfgdefinitions)
		{
		    if(!Loader.isModLoaded(line.modid))
		        continue;//skip mods that are not loaded
			int dim = line.meta;
			String strdim = "DIM" + dim;
			if(!line.hasMeta)
			    strdim = "ANYDIM";
			File file = new File(dir,"definitions/" + strdim  + "/" + line.modid + "/" + line.name.replaceAll(":", "/"));
			dirs.add(file);
			entries.add(new MappingEntry(new ResourceLocation(line.modid + ":" + line.name),new ArrayList(),dim,file,line.bhead) );
		}
		
		try{
		Set<ResourceLocation> list = EntityList.getEntityNameList();
		HashMap<File,ConfigBase> configs = new HashMap();
		File dir_dungeon = new File(dir,"dungeon");
		File dir_mineshaft = new File(dir,"mineshaft");
		File dir_stronghold = new File(dir,"stronghold");
		File dir_netherfortess = new File(dir,"netherfortress");
		File dir_mansion = new File(dir,"mansion");
		dirs.add(dir_dungeon);
		dirs.add(dir_mineshaft);
		dirs.add(dir_stronghold);
		dirs.add(dir_netherfortess);
		dirs.add(dir_mansion);
		
		//cache and/or create config files to disk and memory
		for(File f : dirs)
		{
			if(!f.exists())
				f.mkdirs();
			ArrayList<File> files = new ArrayList();
			getFilesFromDir(f, files, ".txt");
			for(File file : files)
			{
				String strname = getFileTrueDisplayName(file);
				if(!Loader.isModLoaded(strname))
				{
					System.out.println("Skipping:" + strname + ".txt as mod isn't loaded");
					continue;//do not load configs into memory that don't exist
				}
				
				ConfigBase cfg = new ConfigBase(file, new ArrayList(),"DungeonMobs");
				configs.put(file, cfg);
			}
			if(f.equals(dir_dungeon))
			{
				ArrayList<DungeonMob> forgemobs = (ArrayList<DungeonMob>) ReflectionUtil.getObject(null, DungeonHooks.class, "dungeonMobs");
				if(forgemobs == null)
					for(int i=0;i<20;i++)
						System.out.println("Forge Dungeon Hooks List Empty Wrong Reflection Name");
				else{
					//import modded weights if added before considering default weight to be whatever user configured
					for(DungeonMob d : forgemobs)
					{
						if(d.type.equals(new ResourceLocation("minecraft:blank_dungeon")))
								continue;
						if(!Loader.isModLoaded(d.type.getResourceDomain()))
						{
							System.out.println("Skipping ForgeHooks Entity:" + d.type + " as mod isn't loaded");
							continue;//don't create config into memory for mods that are in the game
						}
						
						File mod = new File(f,JavaUtil.toFileCharacters(d.type.getResourceDomain()) + ".txt");
						ConfigBase cfg = configs.get(mod);
						if(cfg == null)
						{
							configs.put(mod , new ConfigBase(mod,new ArrayList(),"DungeonMobs" ) );
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
				MappingEntry entry = getMappingEntry(f);
				String dungeontype = getFileTrueDisplayName(f);
				if(entry != null)
					dungeontype = entry.loc.toString();
				ConfigBase cfg = configs.get(mod);
				if(cfg == null)
				{
					configs.put(mod, new ConfigBase(mod,new ArrayList(),"DungeonMobs") );
					cfg = configs.get(mod);
				}

				LineBase line = new LineItemStackBase("\"" + loc.toString() + "\"");
				int weight = Config.default_weight;
				if(modid.equals("minecraft"))
				{
				    //vanilla
					if(name.equals("cave_spider") && dungeontype.equals("mineshaft"))
						weight = 300;
					if(name.equals("silverfish") &&  dungeontype.equals("stronghold"))
						weight = 150;
					if(name.equals("blaze") &&  dungeontype.equals("netherfortress"))
						weight = 300;
					if(name.equals("spider") && dungeontype.equals("mansion"))
						weight = 150;
					
					//battletowers support so it's not blank by default however if not vanilla nor battle towers user has to stop being lazy and add it themselves
					if(name.equals("cave_spider") && dungeontype.indexOf("battletowers:") == 0)
					    weight = 100;
					if(name.equals("spider") && dungeontype.indexOf("battletowers:") == 0)
                        weight = 90;
					if(name.equals("skeleton") && dungeontype.indexOf("battletowers:") == 0)
                        weight = 120;
					if(name.equals("zombie") && dungeontype.indexOf("battletowers:") == 0)
                        weight = 120;
				}
				if(!cfg.containsLine(line))
					cfg.appendLine(new LineItemStack("\"" + loc.toString() + "\"" +  " = " + weight) );
			}
		 }
		//validate
		if(Config.validateGeneratedEntries)
		for(ConfigBase cfg : configs.values())
		{
			for(int i=0;i<cfg.lines.size();i++)
			{
				ILine line = cfg.lines.get(i);
				if(!list.contains(line.getModPath()) || !line.getModid().equals(getFileTrueDisplayName(cfg.cfgfile) )) 
				{
					if(i != cfg.lines.size()-1)
						i--;//prevents it from skipping an index because all ones above it go to the end
					cfg.deleteLine(line);
					System.out.println("Not Valid Entry! LineBase Removed:" + line + " " + cfg.cfgfile);
					System.out.println("Domanin Not Valid:" + (!line.getModid().equals(getFileTrueDisplayName(cfg.cfgfile)) ) );
				}
			}
		}
		//Populate arrayLists
		for(ConfigBase cfg : configs.values())
		{
			File f = cfg.cfgfile.getParentFile();
			String name = f.equals(dir_dungeon) || f.equals(dir_mansion) || f.equals(dir_mineshaft) || f.equals(dir_netherfortess) || f.equals(dir_stronghold) ? getFileTrueDisplayName(f) : "";
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
			else
			{
				MappingEntry entry = getMappingEntry(f);
				if(entry != null)
					applyDungeonMobs(cfg,entry.list);
			}
		}
		
		//save configs and parse to lists
		Iterator<Map.Entry<File,ConfigBase> > it = configs.entrySet().iterator();
		while(it.hasNext())
		{
			ConfigBase cfg = it.next().getValue();
			
			ArrayList<Comment> strlist = new ArrayList();
            if(!Config.fancyConfig)
                cfg.header = "";
            
            strlist.add(new Comment("DungeonTweaks For:" + getFileTrueDisplayName(cfg.cfgfile)));
            strlist.add(new Comment("To Add NBT Entry make sure your in the right file then add a line in this format \"modid:mobid\" { } = weight"));
            
            for(Comment c : cfg.getInit())
            {
                if(!strlist.contains(c))
                    strlist.add(c);
            }
            
            cfg.setInit(strlist);
			cfg.updateConfig(true,!Config.optimizedFileWriting,true);
		}
		
		}catch(Exception ex){ex.printStackTrace();}
		
		//Don't empty lists if legacy forge dungeon hooks ovrride isn't enabled
		if(!Config.legacyHooks)
		    return;
		
	        ArrayList default_forge = new ArrayList();
	        for(DungeonMobEntry entry : mob_dungeon)
	        	if(entry.nbt == null)
	        		default_forge.add(new DungeonMob(entry.itemWeight,entry.type));
	        
	        ReflectionUtil.setObject(null, default_forge, DungeonHooks.class, "dungeonMobs");//empties forges list as it's no longer needed
	}
	public static MappingEntry getMappingEntry(File f) {
		if(f == null)
			return null;
		for(MappingEntry entry : entries)
		{
			if(entry.file.equals(f) )
				return entry;
		}
		return null;
	}
	public static MappingEntry getMappingEntry(ResourceLocation loc, int dimension) {
		if(loc == null)
			return null;
		MappingEntry e =  null;
		for(MappingEntry entry : entries)
		{
			if(entry.loc.equals(loc) && dimension == entry.dimension && !entry.anyDim)
				return entry;
			if(entry.loc.equals(loc) && entry.anyDim)
			    e = entry;
		}
		return e;
	}
	/**
	 * populate arraylists
	 */
	public static void applyDungeonMobs(ConfigBase cfg, ArrayList<DungeonMobEntry> list) {
		for(ILine lineobj : cfg.lines)
		{
			LineItemStack line = null;
			if(lineobj instanceof LineItemStack)
				line = (LineItemStack)lineobj;
			if(line == null || line.head <= 0)
				continue;//skip invalid lines
				list.add(new DungeonMobEntry(line.head,line.getModPath(),line.NBT) );	
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
    public static DungeonMobEntry pickMobSpawner(Random rand,EventDungeon.Type type,ResourceLocation loc,int dimension)
    {
    	ArrayList<DungeonMobEntry> list = getList(type);
    	if(type != type.MODED)
    	{
    	    if(list.size() != 0)
    	        return WeightedRandom.getRandomItem(rand, list);
    	}
    	if(type == type.MODED && loc != null)
    	{
    		MappingEntry e = DungeonMobs.getMappingEntry(loc,dimension);
    		if(e != null)
    		{
    			if(e.list.size() > 0)
    				return WeightedRandom.getRandomItem(rand, e.list);
    		}
    	}
    	
    	return Config.blankSpawnerWhenBlank ? new DungeonMobEntry(1,new ResourceLocation("minecraft:dungeon:" + type.toString().toLowerCase() ),null) : new DungeonMobEntry(1,new ResourceLocation("pig"),null);
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
	public static void printDungeonMobs() {
		System.out.println("Dungeon:" + DungeonMobs.mob_dungeon);
		System.out.println("MineShaft:" + DungeonMobs.mob_mineshaft);
		System.out.println("Mansion:" + DungeonMobs.mob_mansion);
		System.out.println("Nether Fortress:" + DungeonMobs.mob_netherfortress);
		System.out.println("Stronghold: " + DungeonMobs.mob_stronghold);
		for(MappingEntry e : entries)
			System.out.println(e);
	}

}
