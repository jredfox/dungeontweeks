package com.EvilNotch.dungeontweeks.main.Attatchments;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class CapProvider implements ICapabilitySerializable<NBTBase>{
	 @CapabilityInject(CapInterface.class)
	 public static final Capability<CapInterface> MANA_CAP = null;
	 
	 private CapInterface instance = MANA_CAP.getDefaultInstance();

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == MANA_CAP;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		 return capability == MANA_CAP ? MANA_CAP.<T> cast(this.instance) : null;
	}

	@Override
	public NBTBase serializeNBT() {
		return MANA_CAP.getStorage().writeNBT(MANA_CAP, this.instance, null);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		 MANA_CAP.getStorage().readNBT(MANA_CAP, this.instance, null, nbt);
	}

}
