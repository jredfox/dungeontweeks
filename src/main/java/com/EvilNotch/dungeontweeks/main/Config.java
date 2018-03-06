package com.EvilNotch.dungeontweeks.main;

import java.io.File;

import net.minecraftforge.common.config.Configuration;


public class Config {
	public static int radius = 0;
	public static int radius_nether = 1;
	public static int radius_end = 0;
	public static File dir = null;
	public static int default_weight = 0;
	public static boolean validateGeneratedEntries = true;
	public static boolean Debug = false;
	
	public static void loadConfig(File moddir)
	{
		if(dir == null)
		{
			dir = new File(moddir,"dungeontweeks");
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

}
