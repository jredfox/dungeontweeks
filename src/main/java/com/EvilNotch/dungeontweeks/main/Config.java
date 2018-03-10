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
		config.save();
	}
	public static void loadDefinitionsDir(File dir){
		Configuration config = new Configuration(new File(dir,"config.cfg"));
		config.load();
		String[] list = config.getStringList("definitions", "userdefinitions", new String[]{"\"battletowers:cobblestone\" <0> = true","\"battletowers:cobblestonemossy\" <0> = true","\"battletowers:sandstone\" <0> = true","\"battletowers:ice\" <0> = true","\"battletowers:smoothstone\" <0> = true","\"battletowers:netherrack\" <0> = true","\"battletowers:jungle\" <0> = true","\"quark:dungeon\" <0>"}, "define dungeons based on mobid and dimension in this format \"modid:dungeonname\" <dimensiondid> = boolean where boolean is any dimension not nessary to have the = boolean");
		for(String s : list)
		{
			if(LineBase.toWhiteSpaced(s).equals("") || LineBase.toWhiteSpaced(s).indexOf("#") == 0)
				continue;
			else{
				LineItemStack line = new LineItemStack(s);
				if(!cfgdefinitions.contains(line))
					cfgdefinitions.add(line);
			}
		}
		config.save();
	}

}
