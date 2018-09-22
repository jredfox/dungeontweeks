package com.evilnotch.dungeontweeks.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.evilnotch.dungeontweeks.main.caps.CapSpawnerReg;
import com.evilnotch.dungeontweeks.main.commands.CmdReload;
import com.evilnotch.dungeontweeks.main.eventhandlers.DungeonHandler;
import com.evilnotch.dungeontweeks.main.eventhandlers.ReplaceGen;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonEntry;
import com.evilnotch.lib.api.MCPMappings;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapRegHandler;
import com.evilnotch.lib.minecraft.registry.GeneralRegistry;
import com.evilnotch.lib.util.JavaUtil;

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
	public static final String VERSION = "1.2.4.9";
	public static String chunkSettings = null;
	
	@Mod.EventHandler
	public void preinit(FMLPreInitializationEvent e)
	{
		Config.loadConfig(e.getModConfigurationDirectory());
		chunkSettings = MCPMappings.getField(ChunkGeneratorOverworld.class, "settings");//cache variables from mcp-api as it's a heavy process
		CapRegHandler.registerRegistry(new CapSpawnerReg());
		GeneralRegistry.registerCommand(new CmdReload() );
		
		//support vanilla nbt is null since I don't need it here but, it is an option
		DungeonMobs.addDungeonMob(DungeonMobs.mansion, new ResourceLocation("minecraft:spider"), 150);
		DungeonMobs.addDungeonMob(DungeonMobs.mineshaft, new ResourceLocation("minecraft:cave_spider"), 150);
		DungeonMobs.addDungeonMob(DungeonMobs.netherfortress, new ResourceLocation("minecraft:blaze"), 150);
		DungeonMobs.addDungeonMob(DungeonMobs.netherfortress, new ResourceLocation("minecraft:wither_skeleton"), 70);
		DungeonMobs.addDungeonMob(DungeonMobs.stronghold, new ResourceLocation("minecraft:silverfish"), 150);
		DungeonMobs.addDungeonMob(DungeonMobs.stronghold, new ResourceLocation("minecraft:silverfish"), 150);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e)
	{
		MinecraftForge.TERRAIN_GEN_BUS.register(new ReplaceGen());
		MinecraftForge.EVENT_BUS.register(new ReplaceGen() );
		MinecraftForge.EVENT_BUS.register(new DungeonHandler() );
	}
	@Mod.EventHandler
	public void post(FMLPostInitializationEvent e)
	{
		DungeonMobs.cacheMobs();
	}

}
