package com.EvilNotch.dungeontweeks.main.EventHandlers;

import com.EvilNotch.dungeontweeks.main.MainJava;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapProvider;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TileEntityExtendedProperties {
	
	@SubscribeEvent
	public void patchVanilla(AttachCapabilitiesEvent<TileEntity> e)
	{
		e.addCapability(new ResourceLocation(MainJava.MODID + ":" + "hasScanned"), new CapProvider());
	}

}
