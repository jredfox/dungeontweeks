package com.EvilNotch.dungeontweeks.main.EventHandlers;

import com.EvilNotch.dungeontweeks.main.MainJava;
import com.EvilNotch.dungeontweeks.main.Attatchments.CapProvider;
import com.EvilNotch.dungeontweeks.main.commands.CmdReload;
import com.google.common.eventbus.Subscribe;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TileEntityExtendedProperties {
	
	@SubscribeEvent
	public void patchVanilla(AttachCapabilitiesEvent<TileEntity> e)
	{
		e.addCapability(new ResourceLocation(MainJava.MODID + ":" + "hasScanned"), new CapProvider());
	}
	@Subscribe
	public void commandRegister(FMLServerStartingEvent e)
	{
		for(int i=0;i<100;i++)
			System.out.println("Command Reload Registered");
		e.registerServerCommand(new CmdReload() );
	}

}
