package com.EvilNotch.dungeontweeks.newAttatch;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface ICapabilityBase {
	
	public void readFromNBT(NBTTagCompound nbt);
	public NBTTagCompound getNBT();
	public boolean hasCapability(NBTTagCompound nbt,EnumFacing side);

}
