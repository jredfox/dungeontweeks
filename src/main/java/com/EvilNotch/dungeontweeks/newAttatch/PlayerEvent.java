package com.EvilNotch.dungeontweeks.newAttatch;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class PlayerEvent {

	/**
	 * change capabilities on player death
	 * @param e
	 */
	@SubscribeEvent
	public void player(PlayerRespawnEvent e)
	{
		ICapabilityMagic base = (ICapabilityMagic) CapabilityRegistry.abilities_tileEntity.get(new ResourceLocation("silkspawners:canSilkSpawner"));
		if(base.getMana() > 0)
			base.setMana(0);
	}
}
