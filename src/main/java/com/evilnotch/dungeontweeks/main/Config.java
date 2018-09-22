package com.evilnotch.dungeontweeks.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.evilnotch.lib.util.JavaUtil;
import com.evilnotch.lib.util.line.LineArray;
import com.evilnotch.lib.util.line.LineMeta;

import net.minecraftforge.common.config.Configuration;


public class Config {
	public static int radius = 0;
	public static int radius_overWorld = 0;
	public static int radius_nether = 1;
	public static int radius_end = 0;
	public static File dir = null;
	public static int default_weight = 0;
	public static boolean validateGeneratedEntries = true;
	public static List<LineArray> definitions = new ArrayList();
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
	public static void loadDefinitionsDir(File dir){
		Configuration config = new Configuration(new File(dir,"config.cfg"));
		config.load();
		String[] list = config.getStringList("definitions", "userdefinitions", new String[]{"minecraft:dungeon","minecraft:mansion","minecraft:mineshaft","minecraft:netherfortress","minecraft:stronghold","battletowers:cobblestone","battletowers:cobblestonemossy","battletowers:sandstone","battletowers:ice","battletowers:smoothstone","battletowers:netherrack","battletowers:jungle","quark:dungeon <0>"}, "define dungeons based on mobid and dimension in this format modid:dungeonname <dimensiondid> dimension is optional without it it will work in any dimension");
		for(String s : list)
		{
			String wspaced = JavaUtil.toWhiteSpaced(s);
			if(wspaced.isEmpty() || wspaced.indexOf("#") == 0)
				continue;
			else
			{
				LineArray line = new LineArray(s);
				line.setHead(true);
			    definitions.add(line);
			}
		}
		fancyConfig = config.get("general","fancyConfig",true).getBoolean(true);//so you don't have to keep going back and forth to debug or to look at the differences
		config.save();
	}

}
