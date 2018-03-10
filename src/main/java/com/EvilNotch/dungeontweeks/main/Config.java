package com.EvilNotch.dungeontweeks.main;

import java.io.File;
import java.util.ArrayList;

import com.EvilNotch.dungeontweeks.util.Line.LineBase;
import com.EvilNotch.dungeontweeks.util.Line.LineItemStack;

import net.minecraftforge.common.config.Configuration;


public class Config {
	public static int radius = 0;
	public static int radius_nether = 1;
	public static int radius_end = 0;
	public static File dir = null;
	public static int default_weight = 0;
	public static boolean validateGeneratedEntries = true;
	public static boolean Debug = false;
	public static ArrayList<LineItemStack> cfgdefinitions = new ArrayList();
	public static boolean blankSpawnerWhenBlank = true;
	public static boolean legacyHooks = true;
	
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
		radius_nether = config.get("general", "radius_nether", radius_nether).getInt(radius_nether);
		radius_end = config.get("general", "radius_end", radius_end).getInt(radius_end);
		default_weight = config.get("general", "default_weight", default_weight).getInt(default_weight);
		validateGeneratedEntries = config.get("general", "validateGeneratedEntries", validateGeneratedEntries).getBoolean(validateGeneratedEntries);
		Debug = config.get("general", "debug", false).getBoolean(false);
		blankSpawnerWhenBlank = config.get("general","blankSpawnerWhenBlank",true).getBoolean(true);
		legacyHooks = config.get("general","legacyHooksSupport",true).getBoolean(true);
		config.save();
	}
	public static void loadDefinitionsDir(File dir){
		Configuration config = new Configuration(new File(dir,"config.cfg"));
		config.load();
		String[] list = config.getStringList("definitions", "userdefinitions", new String[]{"\"battletowers:cobblestone\"","\"battletowers:cobblestonemossy\"","\"battletowers:sandstone\"","\"battletowers:ice\"","\"battletowers:smoothstone\"","\"battletowers:netherrack\"","\"battletowers:jungle\"","\"quark:dungeon\" <0>"}, "define dungeons based on mobid and dimension in this format \"modid:dungeonname\" <dimensiondid> dimension is optional without it it will work in any dimension");
		for(String s : list)
		{
			if(LineBase.toWhiteSpaced(s).equals("") || LineBase.toWhiteSpaced(s).indexOf("#") == 0)
				continue;
			else{
				LineItemStack line = new LineItemStack(s);
				if(!line.hasMeta)
				    line.bhead = true;//if doesn't specify meta aka dimension assume any dimension
				if(!cfgdefinitions.contains(line))
					cfgdefinitions.add(line);
			}
		}
		config.save();
	}

}
