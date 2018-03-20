package com.EvilNotch.dungeontweeks.util.Line;

import java.util.Comparator;

public class CustomComparitor implements Comparator<ILine>{

	@Override
	public int compare(ILine o1, ILine o2) {
		return o1.getString().compareTo(o2.getString());
	}

}
