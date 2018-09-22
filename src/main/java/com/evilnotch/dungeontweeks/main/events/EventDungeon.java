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
	public ResourceLocation loc;
	public World w;
	
	protected EventDungeon(TileEntity tile,BlockPos pos,Random rnd,ResourceLocation loc,World w)
	{
		if(loc instanceof DungeonLocation)
			loc = ((DungeonLocation)loc).toResourceLocation();
		this.tile = tile;
		this.pos = pos;
		this.rnd = rnd;
		this.loc = loc;
		this.w = w;
	}
	
	public static class Pre extends EventDungeon
	{
		public Pre(TileEntity tile, BlockPos pos,Random rnd,ResourceLocation loc,World w) {
			super(tile, pos,rnd,loc,w);
		}
		@Override
		public boolean isCancelable()
		{
		  return true;
		}
	}
	
	public static class Post extends EventDungeon
	{
		public Post(TileEntity tile, BlockPos pos,Random rnd,ResourceLocation loc,World w) {
			super(tile, pos,rnd,loc,w);
		}
	}
	
}
