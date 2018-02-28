package com.EvilNotch.dungeontweeks.main.world.worldgen.mobs;

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
		return this.type.toString();
	}

}
