package com.EvilNotch.dungeontweeks.main.EventHandlers;

import com.EvilNotch.dungeontweeks.main.Events.EventDungeon;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon.Type;

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
		if(e.type == Type.DUNGEON)
		{
			data.setString("id", "sheep");
			data.setInteger("Color", 1);
		}
		if(e.type == Type.MINESHAFT)
		{
			data.setString("id", "sheep");
			data.setInteger("Color", 2);
		}
		if(e.type == Type.STRONGHOLD)
		{
			data.setString("id", "sheep");
			data.setInteger("Color", 3);
		}
		if(e.type == Type.MANSION)
		{
			data.setString("id", "sheep");
			data.setInteger("Color", 6);
		}
		if(e.type == Type.NETHERFORTRESS)
		{
			data.setString("id", "wither_skeleton");
		}
		
		nbt.removeTag("SpawnPotentials");
		e.tile.readFromNBT(nbt);
		e.tile.markDirty();
	}

}