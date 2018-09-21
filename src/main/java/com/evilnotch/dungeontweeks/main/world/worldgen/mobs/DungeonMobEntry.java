package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DungeonHooks;

public class DungeonMobEntry extends DungeonHooks.DungeonMob{
	public NBTTagCompound nbt;

	public DungeonMobEntry(int weight, ResourceLocation type,NBTTagCompound nbt) {
		super(weight, type);
		this.nbt = nbt;
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
		if(!(obj instanceof DungeonMobEntry))
			return false;
		DungeonMobEntry entry = (DungeonMobEntry)obj;
		if(this.nbt == null)
			return entry.nbt == null && this.type.equals(entry.type);
		
		return this.nbt.equals(entry.nbt) && this.type.equals(entry.type);
	}

}
