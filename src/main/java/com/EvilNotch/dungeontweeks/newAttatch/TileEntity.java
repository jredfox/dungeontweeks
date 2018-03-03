package com.EvilNotch.dungeontweeks.newAttatch;

import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class TileEntity {
	public NBTTagCompound cap = new NBTTagCompound();
	
	public void readFromNBT(NBTTagCompound nbt)
	{
		NBTTagCompound forge = nbt.getCompoundTag("ForgeCapabilities");
		
		for(Entry<ResourceLocation, ICapabilityBase> cap : CapabilityRegistry.abilities_tileEntity.entrySet())
		{
			if(CapabilityRegistry.hasAbility(this,cap.getKey() ) )
				cap.getValue().readFromNBT(nbt.getCompoundTag("ForgeCapabilities"));
		}
	}
	public NBTTagCompound getNBT()
	{
		NBTTagCompound nbt = new NBTTagCompound();
		//if you need this to write to nbt use this option don't know how forge does it config?
		nbt.setTag("ForgeCapabilities", new NBTTagCompound());
		for(Entry<ResourceLocation, ICapabilityBase> cap : CapabilityRegistry.abilities_tileEntity.entrySet())
		{
			//do stuffs here yes might requrie my api to set the tags properly
		}
		return nbt;
	}

}
