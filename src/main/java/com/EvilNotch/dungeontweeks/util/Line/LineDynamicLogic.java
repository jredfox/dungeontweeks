package com.EvilNotch.dungeontweeks.util.Line;

import java.util.ArrayList;
import java.util.HashMap;

public class LineDynamicLogic {
    public HashMap<Integer,ArrayList<LineBase> > lineLogic = new HashMap();
    public LineBase base = null;
    
    public LineDynamicLogic()
    {
        this.lineLogic = new HashMap();
    }
    /**
     * Breaks up string into seperate lines and each index is one && || logic Doesn't need to extend line since it's an array of lines
     * @param s
     */
    public LineDynamicLogic(String s)
    {
        this.lineLogic = new HashMap();//Initiates the array
        
        //instantiate base line if has one
        String[] parts = new String[2];
        if(s.contains("="))
        {
            this.base = new LineBase("\"" + LineBase.parseQuotes(s, 0) + "\"");//instantiate line base if contains it
            parts = s.split("=");
        }
        else
            parts[1] = s;
        //parse all lines
        seperateLines(parts[1]);//Separates lines into parts based on || being each index
    }

    /**
     * Breaks lines up based on && || logic where each index in the array represents one line{Current use is only for silk spawners}
     */
    protected void seperateLines(String str) 
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
                        lines.add(getLineFromString(sub));
                }
                this.lineLogic.put(index,lines);
                index++;
            }
        }
        else{
            ArrayList<LineBase> lines = new ArrayList();
            lines.add(getLineFromString(str));
            lineLogic.put(0,lines);
        }
    }
    /**
     * Returns the line from a string that is a line to be parsed
     * Doesn't check if line is possible string check that before expecting no exceptions
     */
    public static LineBase getLineFromString(String s) 
    {
        LineItemStack stack = new LineItemStack(s);
        if(stack.getHead() != null)
            return stack;
        if(stack.getHead() == null && stack.NBT != null || stack.getHead() == null && stack.meta != -1)
            return new LineItemStackBase(s);
        if(stack.getHead() == null && stack.NBT == null && stack.meta == -1)
            return new LineBase(s);
        return null;
    }
    /**
     * returns if string is possible line used by configs thus the customized comment and wrapper
     */
    public static boolean isStringPossibleLine(String strline,String comment)
    {
        String whitespaced = LineBase.toWhiteSpaced(strline);
        boolean notLine = !whitespaced.contains(":") && !whitespaced.contains("\"");
        if(notLine || whitespaced.equals("") || whitespaced.indexOf(comment) == 0 || whitespaced.indexOf("#") == 0 || whitespaced.indexOf("<") == 0)
            return false;
        return true;
    }
    public static boolean isStringPossibleLine(String strline,String comment,String wrapperH, String wrapperT)
    {
        String whitespaced = LineBase.toWhiteSpaced(strline);
        if(whitespaced.equals(LineBase.toWhiteSpaced(wrapperH)) || whitespaced.equals(LineBase.toWhiteSpaced(wrapperT)) )
            return false;
        
        return isStringPossibleLine(strline,comment);
    }
    /**
     * legacy support
     */
    public static boolean isStringPossibleLine(String strline)
    {
        return isStringPossibleLine(strline,"#");
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
        return str.substring(0, str.length()-4);
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
        return str.substring(0, str.length()-4);
    }

}
