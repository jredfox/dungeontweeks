package com.EvilNotch.dungeontweeks.main.Events;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class EventDungeon extends Event{
	
	public TileEntity tile;
	public boolean isVanilla;
	public BlockPos pos;
	public Type type;
	
	protected EventDungeon(TileEntity tile,BlockPos pos,Type type)
	{
		this.tile = tile;
		this.pos = pos;
		isVanilla = tile instanceof TileEntityMobSpawner;
		this.type = type;
	}
	
	public static class Pre extends EventDungeon{
		public Pre(TileEntity tile, BlockPos pos, Type type) {
			super(tile, pos, type);
		}
		  public boolean isCancelable()
		  {
			  return true;
		  }
	}
	public static class Post extends EventDungeon{

		public Post(TileEntity tile, BlockPos pos, Type type) {
			super(tile, pos, type);
			this.type = type;
		}
	}
	
	public static enum Type{
		DUNGEON("dungeon"),
	    MINESHAFT("mineshaft"),
	    STRONGHOLD("stronghold"),
	    NETHERFORTRESS("nether_fortress"),
	    BATTLETOWER("battletowers"),
	    OTHER("other"), 
	    MANSION("mansion");
		
		public final String type;
		
		Type(String str)
		{
			this.type = str;
		}
	}

}
