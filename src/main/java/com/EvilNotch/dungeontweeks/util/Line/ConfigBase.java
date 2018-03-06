package com.EvilNotch.dungeontweeks.util.Line;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Scanner;


public class ConfigBase {
	
	public ArrayList<LineBase> lines; //If lines
	public ArrayList<LineDynamicLogic> logic; //for config files that use dynamic logic which contains array of lines for one statement
	public final File cfgfile;
	private ArrayList<String> init;
	public String end = "";
	public boolean first_launch = false;
	
	public ConfigBase(File file, ArrayList<String> list)
	{
		this.cfgfile = file;
		this.lines = new ArrayList();
		this.logic = new ArrayList();
		this.init = list;
		boolean exsists = file.exists();
		
		if(!exsists)
		{
			try {
				file.createNewFile();
				this.first_launch = true;
				
			} catch (IOException e) {e.printStackTrace();}
			
			this.writeFile(list);
		}
		this.readFile();//cache arrays
	}

	public void writeFile(ArrayList<String> list) 
	{
		try {
			FileWriter out = new FileWriter(this.cfgfile);
			for(String s : list)
				out.write(s + "\r\n");
			out.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	public void readFile()
	{
		this.lines = new ArrayList();
		this.logic = new ArrayList();
		try {
			Scanner scan = new Scanner(this.cfgfile);
			while(scan.hasNextLine())
			{
				String strline = scan.nextLine();
				
				if(!LineDynamicLogic.isStringPossibleLine(strline))
				{
					if(LineBase.toWhiteSpaced(strline).indexOf("</") == 0)
						this.end = LineBase.toWhiteSpaced(strline);
					continue;//If it is Comment/Invalid/Old Parsing format Leave
				}
				if(LineBase.isDynamicLogic(strline))
					logic.add(new LineDynamicLogic(strline));
				else
					lines.add(LineDynamicLogic.getLineFromString(strline));
			}
			scan.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	/**
	 * sets all from array to indexes if not out of bounds
	 * basically just replace all from this line on forward based till it runs out of indexes and then adds
	 */
	public void setCfgList(ArrayList<LineBase> list, int index)
	{
		for(int i=0;i<list.size();i++)
		{
			LineBase str = list.get(i);
			boolean flag = false;
			if(index + i < this.lines.size() && !flag)
				this.lines.set(index+i,str);
			else{
				this.lines.add(str);
				flag = true;
			}
		}
	}
	public void setList(ArrayList<String> list, int index)
	{
		for(int i=0;i<list.size();i++)
		{
			LineBase line = LineDynamicLogic.getLineFromString(list.get(i));
			boolean flag = false;
			if(index + i < this.lines.size() && !flag)
				this.lines.set(index+i,line);
			else{
				this.lines.add(line);
				flag = true;
			}
		}
	}
	
	/**
	 * Re-Orders File to be alphabetized
	 */
	public void alphabetize()
	{
		ArrayList<String> list = getStringLines();
	    Collections.sort(list);
	    this.setList(list, 0);
	}
	/**
	 * Appends Line To end of file
	 * @param str
	 */
	public void appendLine(LineBase line)
	{
		this.lines.add(line);
	}
	/**
	 * Appends Line To index of file
	 * @param str
	 */
	public void appendLine(LineBase line,int index)
	{
		this.lines.add(index,line);
	}
	/**
	 * Append List of lines
	 * @param list
	 */
	public void appendLineList(ArrayList<LineBase> list)
	{
		for(LineBase line : list)
			this.lines.add(line);
	}
	/**
	 * Append List of lines at starting index
	 * @param list
	 * @param index
	 */
	public void appendLineList(ArrayList<LineBase> list, int index)
	{
		for(LineBase line : list)
			this.lines.add(index++,line);
	}
	/**
	 * Sets line to index
	 * @param line
	 * @param index
	 */
	public void setLine(LineBase line, int index)
	{
		this.lines.set(index,line);
	}
	/**
	 * Take line objects and convert set them to the file
	 * @param list
	 * @param index
	 */
	public void setLineList(ArrayList<LineBase> list, int index)
	{
		this.setCfgList(list, index);
	}
	/**
	 * Deletes first instanceof String
	 * @param strline
	 */
	public void deleteLine(LineBase line)
	{
		deleteLineBase(line,false);
	}
	public void deleteLineBase(LineBase line, boolean deleteAll)
	{
		Iterator<LineBase> it = this.lines.iterator();
		while(it.hasNext())
		{
			LineBase compare = it.next();
			if(line.equals(compare,false))
			{
				it.remove();
				if(!deleteAll)
					break;
			}
		}
	}
	/**
	 * Delete all instances of list of lines
	 * @param list
	 */
	public void deleteAllLines(ArrayList<LineBase> list)
	{
		for(LineBase line : list)
			deleteLineBase(line,true);
	}
	/**
	 * Does this config contain this line?
	 * @param line
	 * @return
	 */
	public boolean containsLine(LineBase line)
	{
		for(LineBase compare : this.lines)
			if(line.equals(compare,false))
				return true;
		return false;
	}
	
	/**
	 * Don't Use Unless you want the config defaults people will get pissed
	 */
	public void resetConfig()
	{
		try {
			Files.write(this.cfgfile.toPath(), this.init);
			this.readFile();//makes arrays reset
		} catch (IOException e) {e.printStackTrace();}
	}
	
	/**
	 * Since it's un-optimized to re-write the file every single entry is deleted it will have an array to delete and an array for adding
	 */
	public void updateConfig()
	{
		this.writeFile(getStringLines());
		this.readFile();
	}
	protected ArrayList<String> getStringLines() {
		ArrayList<String> list = new ArrayList();
		for(LineBase line : this.lines)
			list.add(line.getString() );
		return list;
	}

	@Override
	public String toString()
	{
		String s = "[";
		for(LineBase line : this.lines)
			s += "<" + line.toString() + ">";
		for(LineDynamicLogic logic : this.logic)
			s += "<" + logic.getString() + ">";
		return s + "]";
	}

	
}
