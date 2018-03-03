package com.EvilNotch.dungeontweeks.newAttatch;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class BasicImplementation implements ICapabilityBase{
	public int i1 = 1000;
	public long l1 = 1000000;
	public boolean aye = false;
	public ResourceLocation id = null;

	/**
	 * this is for basic values any more things can be set by an interface that extends the base and get it from being caseted or get nbt and then modify it
	 */
	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		this.i1 = nbt.getInteger("i1");
		this.l1 = nbt.getLong("l1");
		this.aye = nbt.getBoolean("aye");
		if(nbt.hasKey("objectid"))
			this.id = new ResourceLocation(nbt.getString("objectid"));
	}

	@Override
	public NBTTagCompound getNBT() {
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setInteger("i1", this.i1);
		nbt.setLong("l1", this.l1);
		nbt.setBoolean("aye", this.aye);
		nbt.setString("objectid", this.id.toString());
		return nbt;
	}

	/**
	 * if forge doesn't deny capability per object will allow modders to determine if said object has capability such as creative mode and mana
	 */
	@Override
	public boolean hasCapability(NBTTagCompound nbt,EnumFacing side) {
		return nbt.getInteger("i1") > 1 && nbt.getBoolean("aye");
	}

}
