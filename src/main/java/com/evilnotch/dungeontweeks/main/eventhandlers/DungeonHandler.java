package com.evilnotch.dungeontweeks.main.eventhandlers;

import com.evilnotch.dungeontweeks.main.caps.CapSpawnerReg;
import com.evilnotch.dungeontweeks.main.events.EventDungeon;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobNBT;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;
import com.evilnotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapRegHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonHandler {
	
	/**
	 * fires whenever a dungeon is detected for replacing
	 */
	@SubscribeEvent
	public void dungeonHandler(EventDungeon e)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		e.tile.writeToNBT(nbt);
		nbt.removeTag("SpawnPotentials");
		
		DungeonMobNBT entry = DungeonMobs.pickMobSpawner(e.rnd,e.dungeonId,e.world.provider.getDimension(),e.world.getBiome(e.pos).getRegistryName() );
		
		NBTTagCompound data = entry.nbt == null ? new NBTTagCompound() : entry.nbt.copy();
		data.setString("id",entry.type.toString());
		
		nbt.setTag("SpawnData", data);
		e.tile.readFromNBT(nbt);
		CapBoolean cap = (CapBoolean) CapRegHandler.getCapability(e.tile, CapSpawnerReg.hasScanned);
		cap.value = true;
		e.tile.markDirty();
	}

}