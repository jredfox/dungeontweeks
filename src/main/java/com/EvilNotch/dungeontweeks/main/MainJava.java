package com.EvilNotch.dungeontweeks.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.dungeontweeks.Api.FieldAcess;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapInterface;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapObj;
import com.EvilNotch.dungeontweeks.main.Attatchments.Storage;
import com.EvilNotch.dungeontweeks.main.EventHandlers.DungeonHandler;
import com.EvilNotch.dungeontweeks.main.EventHandlers.ReplaceGen;
import com.EvilNotch.dungeontweeks.main.EventHandlers.TileEntityExtendedProperties;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;

import net.minecraft.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = MainJava.MODID,name = "Dungeon Tweeks", version = MainJava.VERSION)
public class MainJava {
	public static final String MODID = "dungeontweeks";
	public static final String VERSION = "1.0";
	public static boolean isDeObfuscated = false;
	
	@Mod.EventHandler
	public void init(FMLPreInitializationEvent e)
	{
		CapabilityManager.INSTANCE.register(CapInterface.class, new Storage(), CapObj.class);
		isDeObfuscated = isDeObfucscated();
//	 	FieldAcess.CacheMCP(e.getModConfigurationDirectory());
		MinecraftForge.TERRAIN_GEN_BUS.register(new ReplaceGen());
		MinecraftForge.EVENT_BUS.register(new ReplaceGen() );
		MinecraftForge.EVENT_BUS.register(new DungeonHandler() );
		MinecraftForge.EVENT_BUS.register(new TileEntityExtendedProperties() );
//		for(int i=0;i<10;i++)
//			System.out.println("findTTTTTTTTTTTTXXXXXXXXT__.TXT ");
		/*
		 try {
			 DungeonMobEntry a = new DungeonMobEntry(1,new ResourceLocation("creeper"),JsonToNBT.getTagFromJson("{powered:1}"));
			 DungeonMobEntry b = new DungeonMobEntry(1,new ResourceLocation("creeper"),JsonToNBT.getTagFromJson("{powered:1}"));
			System.out.println("a:" + a.equals(b) + " b:" + b.equals(a) );
		} catch (NBTException e1) {
		}*/
	}
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
		DungeonMobs.cacheMobs();
		System.out.println(DungeonMobs.mob_dungeon);
	}
	
	@SuppressWarnings("rawtypes")
	public static void moveFileFromJar(Class clazz,String input,File output,boolean replace) {
		if(output.exists() && !replace)
			return;
		try {
			InputStream inputstream =  clazz.getResourceAsStream(input);
			FileOutputStream outputstream = new FileOutputStream(output);
			output.createNewFile();
			IOUtils.copy(inputstream,outputstream);
			inputstream.close();
			outputstream.close();
		} catch (Exception io) {io.printStackTrace();}
	}
	
	public static boolean isDeObfucscated()
    {
    	try{
    		ReflectionHelper.findField(Block.class, FieldAcess.fieldToOb.get("blockHardness"));
    		return false;
    	}
    	catch(Exception e){return true;}
    }

}
