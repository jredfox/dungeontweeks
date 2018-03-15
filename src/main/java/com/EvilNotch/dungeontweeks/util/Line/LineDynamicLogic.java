package com.EvilNotch.dungeontweeks.util.Line;

import java.util.ArrayList;
import java.util.HashMap;

public class LineDynamicLogic {
    public HashMap<Integer,ArrayList<LineBase> > lineLogic = new HashMap();
    public LineBase base = null;
    
    public LineDynamicLogic(String s)
    {
    	this(s,':','"','#');
    }
    /**
     * Breaks up string into seperate lines and each index is one && || logic Doesn't need to extend line since it's an array of lines
     * @param s
     */
    public LineDynamicLogic(String s,char sep,char q,char...invalid)
    {
        this.lineLogic = new HashMap();//Initiates the array
        
        //instantiate base line if has one
        String[] parts = new String[2];
        if(s.contains("="))
        {
        	parts = s.split("=");
            this.base = LineDynamicLogic.getLineFromString(parts[0],sep,q,invalid);
        }
        else
            parts[1] = s;
        //parse all lines
        seperateLines(parts[1],sep,q,invalid);//Separates lines into parts based on || being each index
    }

    /**
     * Breaks lines up based on && || logic where each index in the array represents one line{Current use is only for silk spawners}
     */
    protected void seperateLines(String str,char sep, char q,char...invalid) 
    {
        if(str.contains("\\|\\|") || str.contains("||"))
        {
            String[] parts = str.split("\\|\\|"); // split from the value of ||
            int index = 0;
            for(String s : parts)
            {
                String[] subs = s.split(",");
                ArrayList<LineBase> lines = new ArrayList();
                for(String sub : subs)
                {
                    if(!LineBase.toWhiteSpaced(sub).equals(""))
                        lines.add(getLineFromString(sub,sep,q,invalid));
                }
                this.lineLogic.put(index,lines);
                index++;
            }
        }
        else{
            ArrayList<LineBase> lines = new ArrayList();
            lines.add(getLineFromString(str,sep,q,invalid));
            lineLogic.put(0,lines);
        }
    }
    public static LineBase getLineFromString(String s) 
    {
    	return getLineFromString(s,':','"','#');
    }
    /**
     * Returns the line from a string that is a line to be parsed
     * Doesn't check if line is possible string check that before expecting no exceptions
     */
    public static LineBase getLineFromString(String s,char sep,char q, char...invalid) 
    {
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
        boolean isLine = isStringPossibleLine(strline);
        return isLine && strline.contains("\\|\\|") || isLine && strline.contains("||");
    }
    /**
     * Used for display
     * @return
     */
    public String getString()
    {
        String str = "";
        if(this.base != null)
            str += this.base.getString() + " = ";
        for(ArrayList<LineBase> list : this.lineLogic.values())
        {
            for(LineBase line : list)
                    str += line.getString() + " || ";
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
        str += this.base + " = ";
        for(ArrayList<LineBase> list : this.lineLogic.values())
        {
            for(LineBase line : list)
                    str += line.toString() + " || ";
        }
        return this.lineLogic.size() > 0 ? str = str.substring(0, str.length()-4) : str;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if(!(obj instanceof LineDynamicLogic))
            return false;
        LineDynamicLogic dynamic = (LineDynamicLogic)obj;
        if(this.lineLogic.size() != dynamic.lineLogic.size() )
            return false;//If not same length return so no stressfull for loops
        boolean bbase = true;
        if(this.base == null)
            bbase = dynamic.base == null;
        else if(!this.base.equals(dynamic.base))
            bbase = false;
        
        for(int i=0;i<this.lineLogic.size();i++)
        {
            ArrayList<LineBase> lines1 = this.lineLogic.get(i);
            ArrayList<LineBase> lines2 = dynamic.lineLogic.get(i);
            if(lines1.size() != lines2.size())
                return false;
            for(int j=0;j<lines1.size();j++)
            {
                LineBase line1 = lines1.get(j);
                LineBase line2 = lines2.get(j);
                if(!line1.equals(line2))
                    return false;
            }
        }
        return true && bbase;
    }

}
