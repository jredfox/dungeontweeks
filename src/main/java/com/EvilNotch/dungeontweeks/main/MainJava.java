package com.EvilNotch.dungeontweeks.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.dungeontweeks.Api.MCPEntry;
import com.EvilNotch.dungeontweeks.Api.MCPMappings;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapInterface;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapObj;
import com.EvilNotch.dungeontweeks.main.Attatchments.Storage;
import com.EvilNotch.dungeontweeks.main.EventHandlers.DungeonHandler;
import com.EvilNotch.dungeontweeks.main.EventHandlers.ReplaceGen;
import com.EvilNotch.dungeontweeks.main.EventHandlers.TileEntityExtendedProperties;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;

import net.minecraft.block.Block;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = MainJava.MODID,name = "Dungeon Tweeks", version = MainJava.VERSION,acceptableRemoteVersions = "*")
public class MainJava {
	public static final String MODID = "dungeontweeks";
	public static final String VERSION = "beta 1.1";
	public static boolean isDeObfuscated = false;
	public static String chunkSettings = null;
	
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		Config.loadConfig(e.getModConfigurationDirectory());
		MCPMappings.CacheMCP(e.getModConfigurationDirectory());
		isDeObfuscated = isDeObfucscated();
		chunkSettings = MCPMappings.getField(ChunkGeneratorOverworld.class, "settings");//cache variables from mcp-api as it's a heavy process
		CapabilityManager.INSTANCE.register(CapInterface.class, new Storage(), CapObj.class);
	}
	
	public Class getClassFromString(String string) {
		string = string.replaceAll("/", ".");
		try{
			Class c = Class.forName(string);
			return c;
		}catch(Throwable t){}
		return null;
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		MinecraftForge.TERRAIN_GEN_BUS.register(new ReplaceGen());
		MinecraftForge.EVENT_BUS.register(new ReplaceGen() );
		MinecraftForge.EVENT_BUS.register(new DungeonHandler() );
		MinecraftForge.EVENT_BUS.register(new TileEntityExtendedProperties() );
		
	}
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
//		DungeonHooks.addDungeonMob(new ResourceLocation("mobspawner:tst"), 100000);
		System.out.println("Dungeon Tweeks Cache Staring:");
		DungeonMobs.cacheMobs();
		System.out.println("Dungeon Tweeks Finished Cache:");
		
		System.out.println("Dungeon:" + DungeonMobs.mob_dungeon);
		System.out.println("MineShaft:" + DungeonMobs.mob_mineshaft);
		System.out.println("Mansion:" + DungeonMobs.mob_mansion);
		System.out.println("Nether Fortress:" + DungeonMobs.mob_netherfortress);
		System.out.println("Stronghold: " + DungeonMobs.mob_stronghold);
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
    		ReflectionHelper.findField(Block.class, MCPMappings.getFieldOb(Block.class,"blockHardness"));
    		return false;//return false since obfuscated field had no exceptions
    	}
    	catch(Exception e){return true;}
    }

}
