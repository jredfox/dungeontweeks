package com.evilnotch.dungeontweeks.main.caps;

import com.evilnotch.dungeontweeks.main.MainJava;
import com.evilnotch.lib.minecraft.capability.CapContainer;
import com.evilnotch.lib.minecraft.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.capability.registry.CapRegTileEntity;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;

public class CapSpawnerReg extends CapRegTileEntity{

	public static final ResourceLocation hasScanned = new ResourceLocation(MainJava.MODID + ":" + "hasScanned");
	
	@Override
	public void register(TileEntity object, CapContainer c) 
	{
		if(object instanceof TileEntityMobSpawner)
			c.registerCapability(hasScanned, new CapBoolean<TileEntity>(hasScanned.toString()));
	}

}
