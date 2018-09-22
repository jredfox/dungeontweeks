package com.evilnotch.dungeontweeks.main.world.worldgen.mobs;

import com.evilnotch.lib.util.JavaUtil;

import net.minecraft.util.ResourceLocation;

public class DungeonLocation extends ResourceLocation{

	public static final String anyDim = "anydim";
	/**
	 * requires input of an integer value
	 */
	public static final String dimension = "dim-";
	/**
	 * requires input of an biome resource location
	 */
	public static final String biome = "biome-";
	
	public String dungeonSubType = anyDim;
	
	public DungeonLocation(ResourceLocation loc)
	{
		super(loc.toString());
	}
	public DungeonLocation(ResourceLocation loc,int dim)
	{
		super(loc.toString());
		this.dungeonSubType = dimension + dim;
	}
	public DungeonLocation(ResourceLocation loc,ResourceLocation biomeId)
	{
		super(loc.toString());
		this.dungeonSubType = biome + biomeId;
	}
	
	public DungeonLocation(String name,String dst) 
	{
		super(name);
		if(!JavaUtil.isStringNullOrEmpty(dst))
			this.dungeonSubType = dst;
	}
    public DungeonLocation(String domain, String path,String dst)
    {
    	super(domain,path);
    	if(!JavaUtil.isStringNullOrEmpty(dst))
			this.dungeonSubType = dst;
    }
    
    @Override
    public String toString()
    {
    	return super.toString() + '#' + this.dungeonSubType;
    }
    @Override
    public boolean equals(Object obj)
    {
    	if(!(obj instanceof DungeonLocation))
    		return false;
    	DungeonLocation loc = (DungeonLocation)obj;
    	return super.equals(obj) && this.dungeonSubType.equals(loc.dungeonSubType);
    }
    @Override
    public int hashCode()
    {
    	return this.toString().hashCode();
    }
    
    public ResourceLocation toResourceLocation()
    {
    	return new ResourceLocation(super.toString());
    }

}
