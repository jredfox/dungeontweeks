package com.EvilNotch.dungeontweeks.main.world.worldgen.mobs;

import java.util.ArrayList;
import java.util.Random;

import com.EvilNotch.dungeontweeks.Api.ReflectionUtil;
import com.EvilNotch.dungeontweeks.main.MainJava;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon;
import com.EvilNotch.dungeontweeks.main.Events.EventDungeon.Type;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.common.DungeonHooks.DungeonMob;

public class DungeonMobs {
	public static ArrayList<DungeonMobEntry> mob_dungeon = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_mineshaft = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_stronghold = new ArrayList();
	public static ArrayList<DungeonMobEntry> mob_netherfortress = new ArrayList();
	
	public static void cacheMobs()
	{
		mob_dungeon.clear();
		mob_mineshaft.clear();
		mob_stronghold.clear();
		mob_netherfortress.clear();
		
		//vanilla and mod support
		ArrayList<DungeonMob> forgemobs = (ArrayList<DungeonMob>) ReflectionUtil.getObject(null, DungeonHooks.class, "dungeonMobs");
		if(forgemobs == null)
			for(int i=0;i<20;i++)
				System.out.println("Forge Dungeon Hooks List Empty Wrong Reflection Name");
		else
			for(DungeonMob d : forgemobs)
				mob_dungeon.add(new DungeonMobEntry(d.itemWeight,d.type,null) );//forge hooks compatibility
		
		//sets forge to default list for per entity compatibility detection
		ArrayList default_forge = new ArrayList();
		default_forge.add(new DungeonMob(100, new ResourceLocation("minecraft:blank_dungeon")));
		ReflectionUtil.setObject(null, default_forge, DungeonHooks.class, "dungeonMobs");//empties forges list as it's no longer needed
		
		//vanilla support
		mob_mineshaft.add(new DungeonMobEntry(470,new ResourceLocation("minecraft:cave_spider"), null ) );
		mob_stronghold.add(new DungeonMobEntry(370,new ResourceLocation("minecraft:silverfish"), null ) );
		mob_netherfortress.add(new DungeonMobEntry(470, new ResourceLocation("minecraft:blaze"),null ) );
	}
	
	/**
     * Randomly decides which spawner to use in a dungeon
     */
    public static ResourceLocation pickMobSpawner(Random rand,EventDungeon.Type type)
    {
    	ArrayList<DungeonMobEntry> list = getList(type);
    	if(type == Type.DUNGEON)
    		return WeightedRandom.getRandomItem(rand, list).type;
    	if(type == Type.MINESHAFT)
    		return WeightedRandom.getRandomItem(rand, list).type;
    	if(type == Type.STRONGHOLD)
    		return WeightedRandom.getRandomItem(rand,list).type;
    	if(type == Type.NETHERFORTRESS)
    		return WeightedRandom.getRandomItem(rand, list).type;
    	
    	return null;
    }
    
    public static ArrayList<DungeonMobEntry> getList(Type t){
    	if(t == Type.DUNGEON)
    		return mob_dungeon;
    	if(t == Type.MINESHAFT)
    		return mob_mineshaft;
    	if(t == Type.STRONGHOLD)
    		return mob_stronghold;
    	if(t == Type.NETHERFORTRESS)
    		return mob_netherfortress;
    	return null;
    }
    
    /**
     * Adds a mob to the possible list of creatures the spawner will create.
     * If the mob is already in the spawn list, the rarity will be added to the existing one,
     * causing the mob to be more common.
     *
     * @param name The name of the monster, use the same name used when registering the entity.
     * @param rarity The rarity of selecting this mob over others. Must be greater then 0.
     *        Vanilla Minecraft has the following mobs:
     *        Spider   100
     *        Skeleton 100
     *        Zombie   200
     *        Meaning, Zombies are twice as common as spiders or skeletons.
     * @return The new rarity of the monster,
     */
    public static float addDungeonMob(ResourceLocation name, int rarity,NBTTagCompound nbt,Type type)
    {
        if (rarity <= 0)
            throw new IllegalArgumentException("Rarity must be greater then zero");
        DungeonMobEntry entry = new DungeonMobEntry(rarity,name,nbt);
        
        ArrayList<DungeonMobEntry> list = getList(type);
        for (DungeonMobEntry mob :  list)
        {
            if (entry.equals(mob))
                return mob.itemWeight += rarity;
        }
        list.add(entry);
        return rarity;
    }
    
    public static int removeDungeonMob(ResourceLocation name,NBTTagCompound nbt,Type type)
    {
    	ArrayList<DungeonMobEntry> list = getList(type);
    	DungeonMobEntry e = new DungeonMobEntry(0,name,nbt);
        for (DungeonMobEntry mob : list)
        {
            if (mob.equals(e))
            {
            	list.remove(mob);
            	return mob.itemWeight;
            }
        }
        return 0;
    }

}
