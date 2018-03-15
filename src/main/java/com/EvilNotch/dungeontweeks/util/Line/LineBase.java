package com.EvilNotch.dungeontweeks.util.Line;

import java.util.ArrayList;

import net.minecraft.util.ResourceLocation;

public class LineBase
{
	public static final String lineLibraryVersion = "1.107";
	public String modid;
	public String name;
	public static final ArrayList<String> invalidParsingChars = new ArrayList<String>(10);
	public boolean legacyParsed = false;
	
	public LineBase(String s) 
	{
		//invalid chars for line base as = doesn't do anything for the base class
		if(!invalidParsingChars.contains("<"))
			invalidParsingChars.add("<");
		
		if(!invalidParsingChars.contains("{"))
			invalidParsingChars.add("{");
		
		if(!invalidParsingChars.contains("="))
			invalidParsingChars.add("=");
	try{
		  String[] stack = getStrId(s);
		  if(stack != null)
		  {
			  this.modid = stack[0];
			  if(stack.length > 1)
				  this.name = stack[1];
			  else
				  this.name = null;//For simplistic line parsing line is the line no string
		  }
		  else{
			  this.modid = null;
			  this.name = null;
		  }
	  }
		catch(Exception e)
		{
			e.printStackTrace();
			this.modid = null;
			this.name = null;
		}
	}
	
	/**
	 * Doesn't support Quotes in modid:block! Returnes modid[0] name[1]
	 * @param s
	 * @return
	 */
	protected String[] getStrId(String s) 
	{
		String compare = s;
		if(s.contains("="))
			compare = LineBase.toWhiteSpaced(s.split("=")[0]);//ensures it is to the left of the = sign
		
		String strid = null;
		if(compare.contains("\""))
			strid = parseQuotes(s,0);
		else{
			legacyParsed = true;
			return getParts(parseLoosly(compare),":");//old format supports no spacing however is easy to edit and create
		}
		
		return getParts(strid, ":");
	}

	public String parseLoosly(String s) {
		String str = "";
		for(int i=0;i<s.length();i++)
		{
			String charstr = s.substring(i, i+1);
			//check for chars that stop parsing before the = sign
			for(String invalid : invalidParsingChars)
			{
				if(charstr.equals(invalid))
					return str.trim();
			}
			str += charstr;
		}
		return str;
	}
	/**
	 * Separates a string dynamically supports vanilla format
	 * @param s
	 * @param split
	 * @return
	 */
	public static String[] getParts(String s, String split)
	{
		if(split.equals(":"))
		{
			s = s.replaceFirst(":", "\u00A9");
			return s.split("\u00A9");
		}
		return s.split(split);
	}

	/**
	 * Ejects a string that is whitespaced
	 * @param s
	 * @return
	 */
	public static String toWhiteSpaced(String s)
	{
		return s.replaceAll("\\s+", "");
	}
	/**
	 * Returns true if character is a number
	 * @param s
	 * @return
	 */
	public static boolean isCharNum(String s) 
	{
		if(s.equals("0") || s.equals("1") || s.equals("2") || s.equals("3") || s.equals("4")|| s.equals("5")|| s.equals("6")|| s.equals("7")|| s.equals("8")|| s.equals("9"))
			return true;
		return false;
	}
	public static boolean isStringNum(String s)
	{
		String valid = "1234567890.";
		String valid_endings = "bslfdi";//byte,short,long,float,double,int
		int indexdot = 0;
		if(s.indexOf(".") == 0 || s.indexOf(".") == s.length() - 1)
			return false;
		for(int i=0;i<s.length();i++)
		{
			String character = s.substring(i, i+1);
			boolean lastindex = i == s.length() -1;
			if(character.equals("."))
			{
				if(indexdot >= 1)
					return false;
				indexdot++;
			}
			if(!valid.contains(character))
			{
				if(i + 1 < s.length())
					return false;
				if(lastindex)
					return valid_endings.contains(character.toLowerCase());
			}
		}
		return true;
	}
	public static String getStaticIntString(int[] list)
	{
		String s = "";
		for(int i=0;i<list.length;i++)
		{
			if(i<list.length-1)
				s+=list[i] + ",";
			else
				s+=list[i] + "";
		}
		return s;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if(!(obj instanceof LineBase))
			return false;
		LineBase line = (LineBase)obj;
		String line1 = "" + this.modid + ":" + this.name;
		String line2 =  "" + line.modid + ":" + line.name;
		return line1.equals(line2);
	}
	/**
	 * if head != null should line compare head? Used for ConfigBase
	 * @return
	 */
	public boolean equals(Object obj,boolean compareHead)
	{
		return this.equals(obj);
	}
	
	@Override
	public String toString()
	{
		String quote = this.legacyParsed ? "" : "\"";
		return  quote + this.modid + ":" + this.name + quote;	
	}
	/**
	 * Gets lines string for configuration files overridden in LineItemStackBase
	 * @return
	 */
	public String getString()
	{
	    String quote = this.legacyParsed ? "" : "\"";
		return quote + getModPath().toString() + quote;
	}
	public static boolean isDynamicLogic(String s)
	{
		return s.contains("\\|") || s.contains("|");
	}
	public static int findFirstQuote(String s) 
	{
		for(int i=0;i<s.length();i++)
		{
			String str = s.substring(i, i++);
			if(str.equals("\""))
				return i;
		}
		return -1;
	}
	public static String parseQuotes(String s, int index) 
	{
		String strid = "";
		int quote = 0;
		for(int i=index;i<s.length();i++)
		{
			if(quote == 2)
				break; //if end of parsing object stop loop and return getParts(strid,":");
			
			if(s.substring(i,i+1).equals("\""))
				quote++;
			if(!s.substring(i,i+1).equals("\"") && quote > 0)
				strid += s.substring(i, i+1);
		}
		return strid;
	}
	public ResourceLocation getModPath()
    {
		String strname = "";
		String compare = "" + this.name;
		if(!compare.equals("null") )
			strname = ":" + this.name;
		return  new ResourceLocation(this.modid + strname);
    }

}
