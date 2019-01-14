package com.evilnotch.dungeontweeks.main.eventhandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.evilnotch.dungeontweeks.main.Config;
import com.evilnotch.dungeontweeks.main.MainJava;
import com.evilnotch.dungeontweeks.main.caps.CapSpawnerReg;
import com.evilnotch.dungeontweeks.main.events.EventDungeon;
import com.evilnotch.dungeontweeks.main.world.worldgen.DungeonMain;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;
import com.evilnotch.lib.api.ReflectionUtil;
import com.evilnotch.lib.minecraft.content.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.content.capability.registry.CapRegHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReplaceGen {
	 /**
	 * Get chunks from center radius without loading them dormant chunk support
	 */
    public static ArrayList<Chunk> getRadiusChunks(World w, int chunkPosX, int chunkPosZ,int radius) 
    {
	  ArrayList<Chunk> chunks = new ArrayList();
	  if(radius == 0)
	  {
		 chunks.add(w.getChunkFromChunkCoords(chunkPosX, chunkPosZ));
		 return chunks;
	  }
	  for (int x = chunkPosX - radius; x <= chunkPosX + radius; x++) {
            for (int z = chunkPosZ - radius; z <= chunkPosZ + radius; z++) {
            	if(w.isChunkGeneratedAt(x, z))
            	{
            		Chunk c = getLoadedOrPopulatedChunk(w,x,z);
            		if(c != null)
            			chunks.add(c);
            	}
            }
	  }
	  return chunks;
    }
	public static Chunk getLoadedOrPopulatedChunk(World w, int x, int z)
	{
		Chunk c = w.getChunkProvider().getLoadedChunk(x, z);
		return c;
	}
	public static Chunk getPopulatedChunk(World w, int x, int z,boolean isLoaded) {
		Chunk c = null;
		if(!isLoaded)
			w.getChunkProvider().getLoadedChunk(x, z);
		if(c == null)
		{
			 long pos = ChunkPos.asLong(x, z);
	         c = net.minecraftforge.common.ForgeChunkManager.fetchDormantChunk(pos, w);
	         if(c == null)
	        	 return null;
		}
    	if(c.isPopulated())
    		return c;
    	else
    		return null;
	}
	/**
	 * For everything except default dungeon this includes modded definitions support
	 */
	@SubscribeEvent(priority=EventPriority.LOWEST)
	public void dungeonDetect(PopulateChunkEvent.Post e)
	{
		World w = e.getWorld();
		if(w.isRemote)
			return;
		IChunkGenerator gen = e.getGenerator();
		int radius = Config.radius;
		if(w.provider.getDimension() == -1)
			radius = Config.radius_nether;
		if(w.provider.getDimension() == 1)
			radius = Config.radius_end;
		if(w.provider.getDimension() == 0)
		    radius = Config.radius_overWorld;
		
		ArrayList<Chunk> chunks = getRadiusChunks(w, e.getChunkX(), e.getChunkZ(), radius);
		for(Chunk c : chunks)
		{
		  Map<BlockPos, TileEntity> map = c.getTileEntityMap();
		  if(map == null)
			  return;
		  Iterator<Map.Entry<BlockPos, TileEntity>> it = map.entrySet().iterator();
		  while(it.hasNext() )
		  {
			  Map.Entry<BlockPos, TileEntity> pair = it.next();
			  BlockPos pos = pair.getKey();
			  TileEntity tile = pair.getValue();
			
			  boolean mineshaft = false;
			  boolean stronghold = false;
			  boolean netherfortress = false;
			  boolean mansion = false;//has spawner in secret room here
			
			  if(gen instanceof ChunkGeneratorOverworld)
			  {
			  	  ChunkGeneratorOverworld gen2 = (ChunkGeneratorOverworld)gen;
				  mineshaft = gen2.isInsideStructure(w, "Mineshaft", pos);
				  stronghold = gen2.isInsideStructure(w, "Stronghold", pos);
				  mansion = gen2.isInsideStructure(w, "Mansion", pos);
			  }
			  else if(gen instanceof ChunkGeneratorHell)
			  {
			    ChunkGeneratorHell gen3 = (ChunkGeneratorHell)gen;
			    netherfortress = gen3.isInsideStructure(w, "Fortress", pos);
			  }
			  
			  if(tile instanceof TileEntityMobSpawner)
			  {
				  NBTTagCompound nbt = new NBTTagCompound();
				  tile.writeToNBT(nbt);
				  String name = nbt.getCompoundTag("SpawnData").getString("id");
				  ResourceLocation loc = new ResourceLocation(name);
				  
				  boolean entry = DungeonMobs.containsDungeonEntry(loc,w.provider.getDimension(),w.getBiome(tile.getPos()).getRegistryName());
				  
				  //if entry isn't null it means it's a defined spawner for this dimension fire the event
				  if(!mineshaft && !stronghold && !mansion && !netherfortress && !entry)
					  continue;
				   CapBoolean cap = (CapBoolean) CapRegHandler.getCapability(tile,CapSpawnerReg.hasScanned);
				   boolean scanned = cap.value;
				   if(!scanned)
				   {
					  ResourceLocation type = mineshaft ? DungeonMobs.mineshaft : stronghold ? DungeonMobs.stronghold : mansion ? DungeonMobs.mansion : netherfortress ? DungeonMobs.netherfortress : loc;
					  EventDungeon d = new EventDungeon(tile,e.getRand(),type,e.getWorld());
					  MinecraftForge.EVENT_BUS.post(d);
				   }
			  }
		   }
	    }
	}
	
	/**
	 * replaces vanilla dungeon
	 */
	@SubscribeEvent
	public void dungeonReplace(PopulateChunkEvent.Populate e)
	{	
		if(e.getType() != EventType.DUNGEON)
			return;
		World w = e.getWorld();
		if(w.isRemote || !(e.getGenerator() instanceof ChunkGeneratorOverworld) )
			return;
		IChunkGenerator gen = e.getGenerator();
		int i = e.getChunkX() * 16;
        int j = e.getChunkZ() * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		Random rand = e.getRand();
		ChunkGeneratorSettings settings = (ChunkGeneratorSettings)ReflectionUtil.getObject(gen, ChunkGeneratorOverworld.class, MainJava.chunkSettings );

		for (int j2 = 0; j2 < settings.dungeonChance; ++j2)
        {
            int i3 = rand.nextInt(16) + 8;
            int l3 = rand.nextInt(256);
            int l1 = rand.nextInt(16) + 8;
            (new DungeonMain()).generate(w, rand, blockpos.add(i3, l3, l1));
        }
		e.setResult(Event.Result.DENY);
	}

}
