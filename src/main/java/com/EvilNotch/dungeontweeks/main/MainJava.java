package com.EvilNotch.dungeontweeks.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.dungeontweeks.Api.MCPMappings;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapInterface;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapObj;
import com.EvilNotch.dungeontweeks.main.Attatchments.Storage;
import com.EvilNotch.dungeontweeks.main.EventHandlers.DungeonHandler;
import com.EvilNotch.dungeontweeks.main.EventHandlers.ReplaceGen;
import com.EvilNotch.dungeontweeks.main.EventHandlers.TileEntityExtendedProperties;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;

import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.math.BlockPos;
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
	public static final String VERSION = "beta 1.0";
	public static boolean isDeObfuscated = false;
	
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		Config.loadConfig(e.getModConfigurationDirectory());
		MCPMappings.CacheMCP(e.getModConfigurationDirectory());
		isDeObfuscated = isDeObfucscated();
		CapabilityManager.INSTANCE.register(CapInterface.class, new Storage(), CapObj.class);
//		for(int i=0;i<10;i++)
//			System.out.println(MCPMappings.getFieldOb(TileEntityPiston.class, "readFromNBT"));
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
    		ReflectionHelper.findField(Block.class, MCPMappings.getFieldOb(Block.class,"blockHardness"));
    		return false;//return false since obfuscated field had no exceptions
    	}
    	catch(Exception e){return true;}
    }

}
