package com.EvilNotch.dungeontweeks.util.Line;

import net.minecraft.util.ResourceLocation;

public interface ILine {
	
	public boolean equals(Object obj, boolean compareHead);
	public String getString();
	public ResourceLocation getModPath();
	public void parse(String string, char sep, char q, char... invalid);
	public String getModid();
	public String getName();

}
