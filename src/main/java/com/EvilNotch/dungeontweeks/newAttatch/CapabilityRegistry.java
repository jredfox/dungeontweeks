package com.EvilNotch.dungeontweeks.newAttatch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class CapabilityRegistry {
	
	protected static HashMap<ResourceLocation,ICapabilityBase> abilities_tileEntity = new HashMap();
	protected static HashMap<ResourceLocation,Object> abilities = new HashMap();
	
	public void register(ICapabilityBase ability,ResourceLocation id)
	{
		abilities_tileEntity.put(id,ability);
	}
	public ICapabilityBase getAbility(ResourceLocation loc,Object obj)
	{
		if(!hasAbility(obj,loc))
			return null;
		if(obj instanceof TileEntity)
			return abilities_tileEntity.get(loc);
		
		return null;
	}

	public static boolean hasAbility(Object obj,ResourceLocation loc) {
		for(Map.Entry<ResourceLocation,Object> map : abilities.entrySet() )
		{
			ResourceLocation loccompare = map.getKey();
			Object compare = map.getValue();
			if(compare.getClass() == obj.getClass() && loccompare.equals(loc))
				return true;
		}
		return false;
	}

}
