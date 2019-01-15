package com.evilnotch.dungeontweeks.main;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.ILine;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.config.ConfigLine;

import net.minecraftforge.common.config.Configuration;


public class Config {
	public static int radius = 0;
	public static int radius_overWorld = 0;
	public static int radius_nether = 1;
	public static int radius_end = 0;
	public static File dir = null;
	public static int default_weight = 0;
	public static boolean validateGeneratedEntries = true;
	public static ConfigLine def = new ConfigLine();
	public static Set<LineArray> codedDef = new LinkedHashSet<LineArray>();
	public static boolean blankSpawnerWhenBlank = true;
    public static boolean fancyConfig = true;
	
	public static void loadConfig(File moddir)
	{
		if(dir == null)
		{
			dir = new File(moddir,MainJava.MODID);
			dir.mkdir();
		}
		Configuration config = new Configuration(new File(dir,"config.cfg"));
		config.load();
		radius = config.get("general", "radius", radius).getInt(radius);
		radius_overWorld = config.get("general", "radiusOverworld", 0).getInt(0);
		radius_nether = config.get("general", "radius_nether", radius_nether).getInt(radius_nether);
		radius_end = config.get("general", "radius_end", radius_end).getInt(radius_end);
		default_weight = config.get("general", "default_weight", default_weight).getInt(default_weight);
		validateGeneratedEntries = config.get("general", "validateGeneratedEntries", validateGeneratedEntries).getBoolean(validateGeneratedEntries);
		blankSpawnerWhenBlank = config.get("general","blankSpawnerWhenBlank",true).getBoolean(true);
		config.save();
	}
	
	public static void loadDefinitionsDir(File dir)
	{	
		//parse the config
		def.lines.clear();
		
		for(ILine l : codedDef)
			def.addLine(l);
		
		Configuration config = new Configuration(new File(dir,"config.cfg"));
		config.load();
		String[] list = config.getStringList("dungeons", "definitions", new String[]{}, "");
		for(String s : list)
		{
			String wspaced = JavaUtil.toWhiteSpaced(s);
			if(wspaced.isEmpty() || wspaced.indexOf("#") == 0)
				continue;
			else
			{
				LineArray line = new LineArray(s);
				if(!s.contains("="))
				{
					System.out.println("legacy format found:" + line);
					line.setHead(true);
				}
				def.addLine(line);
				LineArray actual = (LineArray) def.getUpdatedLine(line);
				actual.setHead(line.getBoolean());
			}
		}
		System.out.println(def);
		//save the config definitions that get programatically in here
		list = new String[def.lines.size()];
		int index = 0;
		for(ILine l : def.lines)
		{
			list[index] = l.toString();
			index++;
		}
		config.addCustomCategoryComment("definitions", "define dungeons based on mobid and dimension in this format \"modid:dungeonname <dimensiond-id/\"biomeid\"> = enabled\" dimension is optional without it it will work in any dimension");
		config.get("definitions", "dungeons", new String[]{}).set(list);
		
		fancyConfig = config.get("general","fancyConfig",true).getBoolean(true);//so you don't have to keep going back and forth to debug or to look at the differences
		config.save();
	}

}
