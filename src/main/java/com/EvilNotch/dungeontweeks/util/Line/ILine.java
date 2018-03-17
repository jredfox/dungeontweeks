package com.EvilNotch.dungeontweeks.util.Line;

import net.minecraft.util.ResourceLocation;

public interface ILine {
	public static final String lineLibraryVersion = "1.2";
	public boolean equals(Object obj, boolean compareHead);
	public String getString();
	public ResourceLocation getModPath();
	public void parse(String string, char sep, char q, char... invalid);
	public String getModid();
	public String getName();
	public ILine getLineBase();//gets the first instanceof of a base for comparsion that implements ILine doesn't have to be LineBase
	public Object getHead();//value associated with the line for example line = value
}
