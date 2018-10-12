package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DungeonHooks;

public class DungeonMobNBT extends DungeonHooks.DungeonMob{
	
	public NBTTagCompound nbt;
	public boolean advanced = false;
	
	public DungeonMobNBT(ResourceLocation type,int weight) 
	{
		this(type,null,weight);
	}

	public DungeonMobNBT(ResourceLocation type,NBTTagCompound nbt,int weight) 
	{
		super(weight, type);
		this.nbt = nbt;
		if(this.nbt != null)
		{
			this.advanced = nbt.hasKey("SpawnData") || nbt.hasKey("SpawnPotentials");
		}
	}
	
	@Override
	public String toString()
	{
		String s = "";
		if(this.nbt != null)
			s += this.nbt.toString();
		return "\"" + this.type.toString() + " " + this.itemWeight + s + "\"";
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof DungeonMobNBT))
			return false;
		DungeonMobNBT entry = (DungeonMobNBT)obj;
		if(this.nbt == null)
			return entry.nbt == null && this.type.equals(entry.type);
		
		return this.nbt.equals(entry.nbt) && this.type.equals(entry.type);
	}

	public boolean hasNBT() 
	{
		return this.nbt != null;
	}

}
