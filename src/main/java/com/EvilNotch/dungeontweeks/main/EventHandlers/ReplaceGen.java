package com.EvilNotch.dungeontweeks.main.EventHandlers;

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

public class ReplaceGen {
	
	@SubscribeEvent
	public void dungeonDetectNether(PopulateChunkEvent.Post e)
	{
		World w = e.getWorld();
		if(w.isRemote || !w.provider.isNether())
			return;
		Chunk chunk = w.getChunkFromChunkCoords(e.getChunkX(), e.getChunkZ() );
		
		IChunkGenerator gen = e.getGenerator();
		ArrayList<Chunk> chunks = getRadiusChunks(w,e.getChunkX(),e.getChunkZ(),1);
		for(Chunk c : chunks)
		{
		  Map<BlockPos, TileEntity> map = c.getTileEntityMap();
		  if(map == null)
			return;
//		  System.out.println("C:" + chunks.size());
		  Iterator<Map.Entry<BlockPos, TileEntity>> it = map.entrySet().iterator();
		
		  while(it.hasNext() )
		  {
			  if(true)
				  continue;
			Map.Entry<BlockPos, TileEntity> pair = it.next();
			BlockPos pos = pair.getKey();
			TileEntity tile = pair.getValue();
			if(tile instanceof TileEntityMobSpawner)
				System.out.println("Spawner:" + pos);
			EventDungeon d = new EventDungeon.Post(tile,pos, Type.NETHERFORTRESS);
			MinecraftForge.EVENT_BUS.post(d);
		  }
		}
	}
	/**
	 * Get chunks from center radius
	 */
	public static ArrayList<Chunk> getRadiusChunks(World w, int chunkPosX, int chunkPosZ,int radius) {
		ArrayList<Chunk> chunks = new ArrayList();
		int index = 0;
		  for (int x = chunkPosX - radius; x <= chunkPosX + radius; x++) {
	            for (int z = chunkPosZ - radius; z <= chunkPosZ + radius; z++) {
	            	index++;
	            	if(w.isChunkGeneratedAt(x, z))
	            		chunks.add(w.getChunkFromChunkCoords(x, z));
	            }
		  }
		  if(index != 9)
			  System.out.println("Index != 9");
		return chunks;
	}
	/**
	 * For everything except default dungeon and nether
	 */
	@SubscribeEvent
	public void dungeonDetect(PopulateChunkEvent.Post e)
	{
		World w = e.getWorld();
		if(w.isRemote || true)
			return;
		Chunk chunk = w.getChunkFromChunkCoords(e.getChunkX(), e.getChunkZ() );
		Map<BlockPos, TileEntity> map = chunk.getTileEntityMap();
		IChunkGenerator gen = e.getGenerator();
		if(map == null)
			return;
		Iterator<Map.Entry<BlockPos, TileEntity>> it = map.entrySet().iterator();
		while(it.hasNext() )
		{
			Map.Entry<BlockPos, TileEntity> pair = it.next();
			BlockPos pos = pair.getKey();
			TileEntity tile = pair.getValue();
//			if(tile instanceof TileEntityMobSpawner)
			
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
				netherfortress = true;//gen3.isInsideStructure(w, "Fortress", pos);
			}
			if(!mineshaft && !stronghold && !mansion &&!netherfortress)
				return;
			if(tile instanceof TileEntityMobSpawner)
			{				
				EventDungeon.Type type = mineshaft ? EventDungeon.Type.MINESHAFT : stronghold ? EventDungeon.Type.STRONGHOLD : mansion ? EventDungeon.Type.MANSION : null;
//				System.out.println(type + " " + pos);
				EventDungeon.Post d = new EventDungeon.Post(tile,pos,type);
				MinecraftForge.EVENT_BUS.post(d);
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
		
		/*
		Chunk chunk = w.getChunkFromChunkCoords(e.getChunkX(), e.getChunkZ() );
		Map<BlockPos, TileEntity> map = chunk.getTileEntityMap();
		if(map == null)
			return;
		Iterator<Map.Entry<BlockPos, TileEntity>> it = map.entrySet().iterator();
		while(it.hasNext() )
		{
			Map.Entry<BlockPos, TileEntity> pair = it.next();
			BlockPos pos = pair.getKey();
			TileEntity tile = pair.getValue();
			if(tile instanceof TileEntityChest)
				continue;
			if(e.getGenerator() instanceof ChunkGeneratorOverworld)
			{
				ChunkGeneratorOverworld gen = (ChunkGeneratorOverworld)e.getGenerator();
				if(gen.isInsideStructure(w, "Mineshaft", pos) || gen.isInsideStructure(w, "Stronghold", pos) || gen.isInsideStructure(w, "Mansion", pos))
					return;
			}
			if(e.getGenerator() instanceof ChunkGeneratorHell)
			{
				ChunkGeneratorHell gen = (ChunkGeneratorHell)e.getGenerator();
				if(gen.isInsideStructure(w, "Fortress", pos))
					return;
			}
			System.out.println("VDUNGEON:" + pos);
			
//			if(true)
//				continue;
			if(tile instanceof TileEntityMobSpawner)
			{
				IBlockState old = w.getBlockState(pos);
				NBTTagCompound compound = new NBTTagCompound();
				tile.writeToNBT(compound);
				NBTTagCompound data = new NBTTagCompound();
				data.setString("id", "minecraft:creeper");
				data.setInteger("powered", 100);
				compound.setTag("SpawnData", data);
				compound.removeTag("SpawnPotentials");
				tile.readFromNBT(compound);
				tile.markDirty();
				dungeon = false;
			}
		}*/
	}

}
