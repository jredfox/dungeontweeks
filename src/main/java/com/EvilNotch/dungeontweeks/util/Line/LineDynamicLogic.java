package com.EvilNotch.dungeontweeks.util.Line;

import java.util.ArrayList;

public class LineDynamicLogic {
	public ArrayList<LineBase> lineLogic;
	
	public LineDynamicLogic()
	{
		this.lineLogic = new ArrayList<LineBase>();
	}
	/**
	 * Breaks up string into seperate lines and each index is one && || logic Doesn't need to extend line since it's an array of lines
	 * @param s
	 */
	public LineDynamicLogic(String s)
	{
		this.lineLogic = new ArrayList<LineBase>();//Initiates the array
		seperateLines(s);//Separates lines into parts based on || being each index
	}

	/**
	 * Breaks lines up based on && || logic where each index in the array represents one line{Current use is only for silk spawners}
	 */
	private void seperateLines(String str) 
	{

		if(str.contains("\\|\\|") || str.contains("||"))
		{
			String[] parts = str.split("\\|\\|"); // split from the value of ||
			for(String s : parts)
				this.lineLogic.add(getLineFromString(s));
		}
		else
			lineLogic.add(getLineFromString(str));
	}
	
	/**
	 * Returns the line from a string that is a line to be parsed
	 * @param s
	 * @return
	 */
	public static LineBase getLineFromString(String s) 
	{
		if(s.contains("=") && !s.contains(",") && !s.contains("{") && !s.contains("}") && !s.contains("<") && !s.contains(">"))
			return new LineItemStack(s);
		if(!s.contains("=") && s.contains("{") && s.contains("}") && !s.contains(",") ||!s.contains("=") && s.contains("<") && s.contains(">")  && !s.contains(","))
			return new LineItemStackBase(s);
		if(s.contains("=") && s.contains("{") && s.contains("}")  && !s.contains(",") || s.contains("=") && s.contains("<") && s.contains(">") && !s.contains(","))
			return new LineItemStack(s);
		if(s.contains(",") && !s.contains("=") || s.indexOf(",") < s.indexOf("=") )
			return new LineDWNF(s);
		if(s.contains(",") && s.contains("="))
			return new LineDynamic(s);
		else
			return new LineBase(s);
	}
	public static boolean isStringPossibleLine(String strline)
	{
		if(LineBase.toWhiteSpaced(strline).equals("") || LineBase.toWhiteSpaced(strline).indexOf("#") == 0 || LineBase.toWhiteSpaced(strline).indexOf("<") == 0 || !strline.contains("\"") || !strline.contains(":"))
			return false;
		return true;
	}
	public static boolean isPosibleDynamicLogic(String strline)
	{
		boolean isLine = isStringPossibleLine(strline);
		return isLine && strline.contains("\\|\\|") || isLine && strline.contains("||");
	}
	/**
	 * Used for accurate representation of the line
	 * @return
	 */
	public String getString()
	{
		String str = "";
		for(LineBase line : this.lineLogic)
		{
			if(this.lineLogic.size() > 1)
				str +=  line.toString() + " || ";
			else
				str +=  line.toString();
		}
		try{
			if(this.lineLogic.size() > 1)
				str = str.substring(0, str.length()-4);
			}catch(Exception e){e.printStackTrace();}
		return str;
	}
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineDynamicLogic))
			return false;
		ArrayList<LineBase> thislines = this.lineLogic;
		ArrayList<LineBase> objlines = ((LineDynamicLogic)obj).lineLogic;
		if(thislines.size() != objlines.size())
			return false;//If not same length return so no stressfull for loops
		for(int i=0;i<thislines.size();i++)
		{
			if(!thislines.get(i).equals(objlines.get(i)))
				return false;//If lines args are not equal return false else it has to be true
		}
		return true;
	}

	/**
	 * Used for readability 
	 */
	@Override
	public String toString()
	{
		String str = "";
		for(LineBase line : this.lineLogic)
			str += "<" + line.toString() + ">,";
		str = str.substring(0, str.length()-1);
		return str;
	}

}
