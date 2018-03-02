package com.EvilNotch.dungeontweeks.main.Attatchments;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class Storage implements IStorage<CapInterface>{

	@Override
	public NBTBase writeNBT(Capability<CapInterface> capability, CapInterface instance, EnumFacing side) {
		boolean scanned = instance.getScanned();
		byte b = scanned ? (byte)1 : (byte)0;
		return new NBTTagByte(b);
	}

	@Override
	public void readNBT(Capability<CapInterface> capability, CapInterface instance, EnumFacing side, NBTBase nbt) {
		byte b = ((NBTPrimitive) nbt).getByte();
		boolean scan = b == (byte)1 ? true : false;
		instance.setScanned(scan);
	}

}
