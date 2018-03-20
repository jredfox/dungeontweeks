package com.EvilNotch.dungeontweeks.util.Line;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;

import com.EvilNotch.dungeontweeks.util.ICopy;

import net.minecraft.util.ResourceLocation;

public class LineDynamicLogic implements ILine{
    public HashMap<Integer,ArrayList<ILine> > lineLogic = new HashMap();
    protected char seperator;
    protected char quote;
    protected char[] invalid;
    
    public LineDynamicLogic(String s)
    {
    	this(s,':','"','#',',');
    }
    /**
     * Breaks up string into separate lines and each index is one && || logic Doesn't need to extend line since it's an array of lines
     * @param s
     */
    public LineDynamicLogic(String s,char sep,char q,char...invalid)
    {
    	this.seperator = sep;
    	this.quote = q;
    	this.invalid = invalid;
        this.lineLogic = new HashMap();//Initiates the array
        parse(s,sep,q,invalid);
    }
    @Override
	public void parse(String str, char sep, char q, char...invalid) {
        if(str.contains("\\|\\|") || str.contains("||"))
        {
            String[] parts = str.split("\\|\\|"); // split from the value of ||
            int index = 0;
            for(String s : parts)
            {
            	populateLines(s,index,sep,q,invalid);
                index++;
            }
        }
        else
           populateLines(str,0,sep,q,invalid);
	}

    protected void populateLines(String s, int index,char sep, char q, char...invalid) {
        String[] subs = getSubs(s);
        ArrayList<ILine> lines = new ArrayList();
        for(String sub : subs)
        {
            if(!LineBase.toWhiteSpaced(sub).equals(""))
                lines.add(getLine(sub,sep,q,invalid));
        }
        this.lineLogic.put(index,lines);
	}
    /**
     * filters out all , with the copyright symbol if it's not in nbt
     */
    protected String[] getSubs(String line) {
    	String str = "";
    	for(int i=0;i<line.length();i++)
    	{
    		String ch = line.substring(i, i+1);
    		//ignore nbt
    		if(ch.equals("{"))
    		{
    			int rindex = getIndexRBracket(i, line);
    			String nbt = line.substring(i, rindex+1);
    			str += nbt;
    			i = rindex;
    			continue;//since using continue forces and index++ editing the variable it is index desired-1
    		}
    		if(ch.equals(","))
    			ch = "\u00A9";
    		str += ch;
    	}
		return str.split("\u00A9");
	}
	/**
     * Object oriented so people using this library can override with adding new lines easily
     */
	protected ILine getLine(String sub, char sep, char q, char...invalid) {
		return getLineFromString(sub,sep,q,invalid);
	}
	public static ILine getLineFromString(String s) 
    {
    	return getLineFromString(s,':','"','#');
    }
	
    /**
     * Returns the line from a string that is a line to be parsed
     * Doesn't check if line is possible string check that before expecting no exceptions
     */
    public static ILine getLineFromString(String s,char sep,char q, char...invalid) 
    {
    	if(isPosibleDynamicLogic(s, sep, q))
    		return new LineDynamicLogic(s,sep,q,invalid);
    	
        LineItemStack stack = new LineItemStack(s,sep,q,invalid);
        if(stack.getHead() != null)
            return stack;
        if(stack.getHead() == null && stack.NBT != null || stack.getHead() == null && stack.hasMeta)
            return new LineItemStackBase(s,sep,q,invalid);
        if(stack.getHead() == null && stack.NBT == null && !stack.hasMeta)
            return new LineBase(s,sep,q,invalid);
        return null;
    }
    /**
     * returns if string is possible line used by configs thus the customized comment and wrapper
     */
    public static boolean isStringPossibleLine(String strline,String comment,char sep,char q)
    {
        String whitespaced = LineBase.toWhiteSpaced(strline);
        boolean notLine = !whitespaced.contains("" + sep) && !whitespaced.contains("" + q);
        if(notLine || whitespaced.equals("") || whitespaced.indexOf(comment) == 0)
            return false;
        return true;
    }
    public static boolean isStringPossibleLine(String strline,String comment,String wrapperH, String wrapperT,char sep,char q)
    {
        String whitespaced = LineBase.toWhiteSpaced(strline);
        if(whitespaced.equals(LineBase.toWhiteSpaced(wrapperH)) || whitespaced.equals(LineBase.toWhiteSpaced(wrapperT)) )
            return false;
        
        return isStringPossibleLine(strline,comment,sep,q);
    }
    /**
     * legacy support
     */
    public static boolean isStringPossibleLine(String strline)
    {
        return isStringPossibleLine(strline,"#",':','"');
    }
    public static boolean isPosibleDynamicLogic(String strline)
    {
    	return isPosibleDynamicLogic(strline,':','"');
    }
    public static boolean isPosibleDynamicLogic(String strline, char sep, char q)
    {
    	String w = removeNBT(LineBase.toWhiteSpaced(strline));
        boolean isLine = w.contains("" + sep) || w.contains("" + q);
        return isLine && strline.contains("\\|\\|") || isLine && strline.contains("||") || isLine && w.contains(",");
    }
    public static String removeNBT(String line) {
    	if(!line.contains("{"))
    		return line;
    	String str = line;
    	//isnt't looped currently since if more then one nbt then it has to be a possible line dynamic logic
  		int lindex = str.indexOf('{');
   		int rindex = getIndexRBracket(lindex,str);
   		String nbt = str.substring(lindex, rindex+1);
   		str = str.replace(nbt, "");
		return str;
	}
    protected static int getIndexRBracket(int lindex,String str) {
    	int lb = 0;
    	for(int i=lindex;i<str.length();i++)
    	{
    		String ch = str.substring(i, i+1);
    		if(ch.equals("{"))
    			lb++;
    		else if(ch.equals("}"))
    			lb--;
    		str += ch;
    		if(lb == 0)
    			return i;
    	}
		return -1;
	}
	/**
     * Used for display print out all values regardless of null for toString
     */
    public String getString()
    {
        String str = "";
        for(ArrayList<ILine> list : this.lineLogic.values())
        {
            for(ILine line : list)
                str += line.getString() + ", ";
            
            str = str.substring(0, str.length()-2);//get rid of last comment after parsing row
            str += " || ";//add possible next row
        }
        return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
    }
    /**
     * Used for printing out everything
     */
    @Override
    public String toString()
    {
        String str = "";
        for(ArrayList<ILine> list : this.lineLogic.values())
        {
            for(ILine line : list)
                    str += line.toString() + ", ";
            str = str.substring(0, str.length()-2);//get rid of last comment after parsing row
            str += " || ";
        }
        return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
    }
    @Override
    public boolean equals(Object obj)
    {
    	return equals(obj,true);
    }
    public boolean equals(Object obj,boolean compareHeads)
    {
        if(!(obj instanceof LineDynamicLogic))
            return false;
        LineDynamicLogic dynamic = (LineDynamicLogic)obj;
        if(this.lineLogic.size() != dynamic.lineLogic.size() )
            return false;//If not same length return so no stressfull for loops
        
        for(int i=0;i<this.lineLogic.size();i++)
        {
            ArrayList<ILine> lines1 = this.lineLogic.get(i);
            ArrayList<ILine> lines2 = dynamic.lineLogic.get(i);
            if(lines1.size() != lines2.size())
                return false;
            for(int j=0;j<lines1.size();j++)
            {
                ILine line1 = lines1.get(j);
                ILine line2 = lines2.get(j);
                if(!line1.equals(line2,compareHeads))
                    return false;
            }
        }
        return true;
    }
	@Override
	public ResourceLocation	 getModPath() {
		ILine line = getFirstLine();
		if(line != null)
			return line.getModPath();
		
		return null;
	}
	protected ILine getFirstLine() {
		ArrayList<ILine> list = this.lineLogic.get(0);
		if(list == null || list.size() == 0)
			return null;
		return list.get(0);
	}
	@Override
	public String getModid() {
		ILine line = getLineBase();
		if(line != null)
			return line.getModid();
		return null;
	}
	@Override
	public String getName() {
		ILine line = getLineBase();
		if(line != null)
			return line.getName();
		return null;
	}
	@Override
	public ILine getLineBase() {
		return getFirstLine();
	}
	@Override
	public Object getHead() {
		return this.getLineBase().getHead();
	}
	
	public ArrayList<ResourceLocation> getResourceLocationsFirstLogic()
	{
		return getResourceLocationsFirstLogic(true);
	}
	
	public ArrayList<ResourceLocation> getResourceLocationsFirstLogic(boolean grabStringHead)
	{
		return getResourceLocations(0,grabStringHead);
	}
	
	public ArrayList<ResourceLocation> getResourceLocations(int position,boolean grabStringHead)
	{
		ArrayList<ILine> lines = this.lineLogic.get(position);
		ArrayList<ResourceLocation> list = new ArrayList();
		for(ILine line : lines)
		{
			list.add(line.getModPath());
			if(line.getHead() instanceof String && grabStringHead)
				list.add(new ResourceLocation(line.getHead().toString()));
		}
		return list;
	}
	public ICopy copy() {
		try {
			Constructor constructor = this.getClass().getConstructor(String.class,char.class,char.class,char[].class);
			return (ICopy)constructor.newInstance(this.getString(),this.seperator,this.quote,this.invalid);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public int compareTo(Object arg0) {
		ILine line = (ILine)arg0;
		return this.getComparible().compareTo(line.getComparible());
	}
	@Override
	public String getComparible() {
		 String str = "";
	        for(ArrayList<ILine> list : this.lineLogic.values())
	        {
	            for(ILine line : list)
	                str += line.getComparible() + ", ";
	            
	            str = str.substring(0, str.length()-2);//get rid of last comment after parsing row
	            str += " || ";//add possible next row
	        }
	        return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
	}

}
