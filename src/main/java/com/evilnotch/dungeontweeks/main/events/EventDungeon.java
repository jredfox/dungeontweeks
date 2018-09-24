package com.evilnotch.dungeontweeks.main.events;

import java.util.Random;

import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonLocation;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventDungeon extends Event{
	
	public TileEntity tile;
	public BlockPos pos;
	public Random rnd;
	public ResourceLocation dungeonId;
	public World world;
	
	public EventDungeon(TileEntity tile,BlockPos pos,Random rnd,ResourceLocation loc,World w)
	{
		if(loc instanceof DungeonLocation)
			loc = ((DungeonLocation)loc).toResourceLocation();
		this.tile = tile;
		this.pos = pos;
		this.rnd = rnd;
		this.dungeonId = loc;
		this.world = w;
	}
	/**
	 * getters
	 */
	public TileEntity getTileEntity(){return this.tile;}
	public BlockPos getPos(){return this.pos;}
	public Random getRandom(){return this.rnd;}
	public ResourceLocation getDungeonId(){return this.dungeonId;}
	public World getWorld(){return this.world;}
	
}
