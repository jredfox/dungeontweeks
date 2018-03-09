package com.EvilNotch.dungeontweeks.main.EventHandlers;

import com.EvilNotch.dungeontweeks.main.Events.EventDungeon;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobEntry;
import com.EvilNotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
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
		NBTTagCompound data = nbt.getCompoundTag("SpawnData");
		DungeonMobEntry entry = DungeonMobs.pickMobSpawner(e.rnd, e.type,e.loc,e.w.provider.getDimension());
		ResourceLocation loc = entry.type;
		data.setString("id",loc.toString());
		//NBT Support in spawndata
		if(entry.nbt != null)
		{
			for(String s : entry.nbt.getKeySet() )
				data.setTag(s, entry.nbt.getTag(s));
		}
		System.out.println(loc + " " + e.pos);
		nbt.removeTag("SpawnPotentials");
		e.tile.readFromNBT(nbt);
		e.tile.markDirty();
	}

}