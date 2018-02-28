package com.EvilNotch.dungeontweeks.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.dungeontweeks.Api.FieldAcess;
import com.EvilNotch.dungeontweeks.main.EventHandlers.DungeonHandler;
import com.EvilNotch.dungeontweeks.main.EventHandlers.ReplaceGen;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;

import net.minecraft.block.Block;
import net.minecraft.network.play.server.SPacketCombatEvent.Event;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = MainJava.MODID,name = "Dungeon Tweeks", version = MainJava.VERSION)
public class MainJava {
	public static final String MODID = "dungeontweeks";
	public static final String VERSION = "1.0";
	public static boolean isDeObfuscated = false;
	
	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		 isDeObfuscated = isDeObfucscated();
//		 FieldAcess.CacheMCP(e.getModConfigurationDirectory());
		 MinecraftForge.TERRAIN_GEN_BUS.register(new ReplaceGen());
		 MinecraftForge.EVENT_BUS.register(new ReplaceGen() );
		 MinecraftForge.EVENT_BUS.register(new DungeonHandler() );
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
