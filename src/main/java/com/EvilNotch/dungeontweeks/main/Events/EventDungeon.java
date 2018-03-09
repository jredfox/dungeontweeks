package com.EvilNotch.dungeontweeks.main.Events;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventDungeon extends Event{
	
	public TileEntity tile;
	public BlockPos pos;
	public Type type;
	public Random rnd;
	public ResourceLocation loc;
	public World w;
	
	protected EventDungeon(TileEntity tile,BlockPos pos,Type type,Random rnd,ResourceLocation loc,World w)
	{
		this.tile = tile;
		this.pos = pos;
		this.type = type;
		this.rnd = rnd;
		this.loc = loc;
		this.w = w;
	}
	
	public static class Pre extends EventDungeon{
		public Pre(TileEntity tile, BlockPos pos, Type type,Random rnd,ResourceLocation loc,World w) {
			super(tile, pos, type,rnd,loc,w);
		}
		  public boolean isCancelable()
		  {
			  return true;
		  }
	}
	public static class Post extends EventDungeon{

		public Post(TileEntity tile, BlockPos pos, Type type,Random rnd,ResourceLocation loc,World w) {
			super(tile, pos, type,rnd,loc,w);
			this.type = type;
		}
	}
	
	public static enum Type{
		DUNGEON("dungeon"),
	    MINESHAFT("mineshaft"),
	    STRONGHOLD("stronghold"),
	    NETHERFORTRESS("nether_fortress"), 
	    MANSION("mansion"),
	    MODED("blank_");
		
		public final String type;
		
		Type(String str)
		{
			this.type = str;
		}
	}

}
