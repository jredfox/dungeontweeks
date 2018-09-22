package com.evilnotch.dungeontweeks.main.eventhandlers;

import com.evilnotch.dungeontweeks.main.caps.CapSpawnerReg;
import com.evilnotch.dungeontweeks.main.events.EventDungeon;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobNBT;
import com.evilnotch.lib.minecraft.content.capabilites.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.content.capabilites.registry.CapRegHandler;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DungeonHandler {
	
	/**
	 * fires whenever a dungeon is detected for replacing
	 */
	@SubscribeEvent
	public void dungeonHandler(EventDungeon.Post e)
	{
		NBTTagCompound nbt = new NBTTagCompound();
		e.tile.writeToNBT(nbt);
		nbt.removeTag("SpawnData");
		nbt.removeTag("SpawnPotentials");
		
		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
		DungeonMobNBT entry = null;//DungeonMobs.pickMobSpawner(e.rnd, e.type,e.loc,e.w.provider.getDimension());
		data.setString("id",entry.type.toString());
		//NBT Support in spawndata
		if(entry.nbt != null)
		{
			for(String s : entry.nbt.getKeySet() )
				data.setTag(s, entry.nbt.getTag(s).copy());
		}
		if(!nbt.hasKey("SpawnData"))
		    nbt.setTag("SpawnData", data);
		e.tile.readFromNBT(nbt);
		CapBoolean cap = (CapBoolean) CapRegHandler.getCapability(e.tile, CapSpawnerReg.hasScanned);
		cap.value = true;
		e.tile.markDirty();
	}

}