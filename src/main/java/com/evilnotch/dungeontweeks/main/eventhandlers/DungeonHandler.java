package com.evilnotch.dungeontweeks.main.eventhandlers;

import com.evilnotch.dungeontweeks.main.caps.CapSpawnerReg;
import com.evilnotch.dungeontweeks.main.events.EventDungeon;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobNBT;
import com.evilnotch.dungeontweeks.main.world.worldgen.mobs.DungeonMobs;
import com.evilnotch.lib.minecraft.content.capability.primitive.CapBoolean;
import com.evilnotch.lib.minecraft.content.capability.registry.CapRegHandler;
import com.evilnotch.lib.minecraft.util.TileEntityUtil;

import net.minecraft.block.state.IBlockState;
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
		
		DungeonMobNBT entry = DungeonMobs.pickMobSpawner(e.rnd,e.dungeonId,e.world.provider.getDimension(),e.world.getBiome(e.pos).getRegistryName() );
		
		NBTTagCompound data = entry.nbt == null ? new NBTTagCompound() : entry.nbt.copy();
		
		if(!entry.advanced)
		{
			data.setString("id",entry.type.toString());
			nbt.setTag("SpawnData", data);
		}
		else
			nbt = data;
		
		CapBoolean cap = (CapBoolean) CapRegHandler.getCapability(e.tile, CapSpawnerReg.hasScanned);
		cap.value = true;
		IBlockState state = e.world.getBlockState(e.pos);
		TileEntityUtil.setTileNBT(e.world, e.tile, nbt, false);
	}

}