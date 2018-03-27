package com.EvilNotch.dungeontweeks.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.EvilNotch.dungeontweeks.main.Attatchments.CapInterface;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapObj;
import com.EvilNotch.dungeontweeks.main.Attatchments.Storage;
import com.EvilNotch.dungeontweeks.main.EventHandlers.DungeonHandler;
import com.EvilNotch.dungeontweeks.main.EventHandlers.ReplaceGen;
import com.EvilNotch.dungeontweeks.main.EventHandlers.TileEntityExtendedProperties;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon.Type;
import com.EvilNotch.dungeontweeks.main.commands.CmdReload;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.MappingEntry;
import com.EvilNotch.lib.Api.MCPMappings;
import com.EvilNotch.lib.util.JavaUtil;

import net.minecraft.block.Block;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

@Mod(modid = MainJava.MODID,name = "Dungeon Tweeks", version = MainJava.VERSION,acceptableRemoteVersions = "*", dependencies = "required-after:evilnotchlib")
public class MainJava {
	public static final String MODID = "dungeontweaks";
	public static final String VERSION = "1.2.4.7";
	public static String chunkSettings = null;
	
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		Config.loadConfig(e.getModConfigurationDirectory());
		chunkSettings = MCPMappings.getField(ChunkGeneratorOverworld.class, "settings");//cache variables from mcp-api as it's a heavy process
		CapabilityManager.INSTANCE.register(CapInterface.class, new Storage(), CapObj.class);
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
		DungeonMobs.cacheMobs();
		DungeonMobs.printDungeonMobs();
	}
	@Mod.EventHandler
	public void commandRegister(FMLServerStartingEvent e)
	{
		e.registerServerCommand(new CmdReload() );
	}

}
