package com.EvilNotch.dungeontweeks.main.Events;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventDungeon extends Event{
	
	public TileEntity tile;
	public boolean isVanilla;
	public BlockPos pos;
	public Type type;
	public Random rnd;
	
	protected EventDungeon(TileEntity tile,BlockPos pos,Type type,Random rnd)
	{
		this.tile = tile;
		this.pos = pos;
		isVanilla = tile instanceof TileEntityMobSpawner;
		this.type = type;
		this.rnd = rnd;
	}
	
	public static class Pre extends EventDungeon{
		public Pre(TileEntity tile, BlockPos pos, Type type,Random rnd) {
			super(tile, pos, type,rnd);
		}
		  public boolean isCancelable()
		  {
			  return true;
		  }
	}
	public static class Post extends EventDungeon{

		public Post(TileEntity tile, BlockPos pos, Type type,Random rnd) {
			super(tile, pos, type,rnd);
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
