package com.EvilNotch.dungeontweeks.main.EventHandlers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.EvilNotch.dungeontweeks.Api.ReflectionUtil;
import com.EvilNotch.dungeontweeks.main.MainJava;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon.Type;
import com.EvilNotch.dungeontweeks.main.world.worldgen.DungeonMain;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkGeneratorHell;
import net.minecraft.world.gen.ChunkGeneratorOverworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate;
import net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

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
		if(c == null)
			c = getPopulatedChunk(w,x,z,true);
		return c;
	}
	public static Chunk getPopulatedChunk(World w, int x, int z,boolean dormant) {
		Chunk c = null;
		if(!dormant)
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
	 * For everything except default dungeon and nether
	 */
	@SubscribeEvent
	public void dungeonDetect(PopulateChunkEvent.Post e)
	{
		World w = e.getWorld();
		if(w.isRemote)
			return;
		IChunkGenerator gen = e.getGenerator();
		ArrayList<Chunk> chunks = getRadiusChunks(w, e.getChunkX(), e.getChunkZ(), w.provider.isNether() ? 1 : 0);
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
			  if(!mineshaft && !stronghold && !mansion)
				  return;
			  if(tile instanceof TileEntityMobSpawner)
			  {				
				  EventDungeon.Type type = mineshaft ? EventDungeon.Type.MINESHAFT : stronghold ? EventDungeon.Type.STRONGHOLD : mansion ? EventDungeon.Type.MANSION : null;
				  EventDungeon.Post d = new EventDungeon.Post(tile,pos,type);
				  MinecraftForge.EVENT_BUS.post(d);
			  }
		   }
	    }
	}
	
	/**
	 * ability for any dimension any biome
	 */
	@SubscribeEvent
	public void dungeonReplace(PopulateChunkEvent.Populate e)
	{	
		if(e.getType() != EventType.DUNGEON)
			return;
		World w = e.getWorld();
		if(w.isRemote)
			return;
		IChunkGenerator gen = e.getGenerator();
		int i = e.getChunkX() * 16;
        int j = e.getChunkZ() * 16;
		BlockPos blockpos = new BlockPos(i, 0, j);
		Random rand = (Random) ReflectionUtil.getObject(gen, ChunkGeneratorOverworld.class, MainJava.isDeObfuscated ? "rand" : null );
		ChunkGeneratorSettings settings = (ChunkGeneratorSettings)ReflectionUtil.getObject(gen, ChunkGeneratorOverworld.class, "settings");
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
