package com.EvilNotch.dungeontweeks.main.Attatchments;

public class CapObj implements CapInterface{
	public boolean hasScanned = false;
	public CapObj()
	{
		this.hasScanned = false;
	}
	public CapObj(boolean b)
	{
		this.hasScanned = b;
	}
	@Override
	public boolean getScanned() {
		return this.hasScanned;
	}
	@Override
	public boolean setScanned(boolean b) {
		return this.hasScanned = b;
	}

}
