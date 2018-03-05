package com.EvilNotch.dungeontweeks.util.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class ConfigBase {
	
	public ArrayList<LineBase> lines; //If lines
	public ArrayList<LineDynamicLogic> logic; //for config files that use dynamic logic which contains array of lines for one statement
	public final File cfgfile;
	private ArrayList<String> file_lines;
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
			try {
				file.createNewFile();
				this.first_launch = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		if(!exsists)
			this.writeFile(list);
		try{
			this.file_lines = (ArrayList<String>) Files.readAllLines(cfgfile.toPath());
		}catch(Exception e){e.printStackTrace();}
		
		this.readFile();//cache arrays
		
		if(!this.end.equals(""))
			this.file_lines.remove(this.end);
		detleteWhiteSpacedLine();
	}

	private void detleteWhiteSpacedLine()
	{
		boolean whitespaced = false;
		for(int i=0;i<this.file_lines.size();i++)
		{
			String s = this.file_lines.get(i);
			if(whitespaced && LineBase.toWhiteSpaced(s).equals(""))
			{
				this.file_lines.remove(i);
				break;
			}
			if(LineBase.toWhiteSpaced(s).startsWith("<"))
				whitespaced = true;
		}
	}

	public void writeFile(ArrayList<String> list) 
	{
		try {
			FileWriter out = new FileWriter(this.cfgfile);
			for(String s : list)
				out.write(s);
			out.close();
		} catch (Exception e) {e.printStackTrace();}
	}
	public void readFile()
	{
		this.lines = new ArrayList();
		this.logic = new ArrayList();
		this.file_lines = new ArrayList(); //Hotfix for multiple instances of manipulation via ResetConfig and End Tag Instances
		try {
			Scanner scan = new Scanner(this.cfgfile);
			while(scan.hasNextLine())
			{
				String strline = scan.nextLine();
				this.file_lines.add(strline);
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
	 * Use For appending lines
	 * @param str
	 */
	public void appendString(String str)
	{
		this.file_lines.add(str);
	}
	 /** Use For appending lines
	 * @param str
	 */
	public void appendString(String str,int index)
	{
		this.file_lines.add(index,str);
	}
	/**
	 * Use for appending alot of lines ~ call update only on a need to basis
	 * @param list
	 */
	public void appendList(ArrayList<String> list)
	{
		this.file_lines.addAll(list);
	}
	/**
	 * Use for appending alot of lines ~ call update only on a need to basis
	 * @param list
	 */
	public void appendList(ArrayList<String> list, int index)
	{
		this.file_lines.addAll(index,list);
	}
	/**
	 * Set line at index will replace
	 */
	public void setString(String str, int index)
	{
		this.file_lines.set(index,str);
	}
	/**
	 * sets all from array to indexes if not out of bounds
	 * basically just replace all from this line on forward based till it runs out of indexes and then adds
	 */
	public void setList(ArrayList<String> list, int index)
	{
		
		for(int i=0;i<list.size();i++)
		{
			String str = list.get(i);
			boolean flag = false;
			if(index + i < this.file_lines.size() && !flag)
				this.file_lines.set(index+i,str);
			else{
				this.file_lines.add(str);
				flag = true;
			}
		}

	}
	/**
	 * Re-Orders File to be alphabetized
	 */
	public void alphabetize()
	{
		boolean start = false;
		ArrayList<String> list = new ArrayList<String>();
		int index = 0;
		for(int i=0;i<this.file_lines.size();i++)
		{
			String str = this.file_lines.get(i);
			if(LineBase.toWhiteSpaced(str).equals("") || str.indexOf("#") == 0)
				continue;
			list.add(str);
		}
	    Collections.sort(list);
	    this.setList(list, index);
	}
	/**
	 * Deletes First Line instance ~ call update only on a need to basis
	 * @param strline
	 */
	public void deleteLineString(String strline)
	{
		this.file_lines.remove(strline);
	}
	/**
	 * Delete Line ~ call update only on a need to basis
	 * @param strline
	 */
	public void deleteLineString(int index)
	{
		this.file_lines.remove(index);
	}
	/**
	 * Deletes String lines from index 1 to index 2
	 * @param index1
	 * @param index2
	 */
	public void deleteLineString(int index1, int index2)
	{
		 List<String> sub = this.file_lines.subList(index1, index2);
		sub.clear();
	}
	/**
	 * Delete All Instances of string lines
	 * @param list
	 */
	public void deleteAllLinesString(ArrayList<String> list)
	{
		this.file_lines.removeAll(list);
	}
	/**
	 * Returns true if str.equals(str) based on line
	 * Doesn't Compare Line or LineDynamicLogic objects depreciated for Line/Logic Objects!
	 * @param s
	 * @return
	 */
	public boolean containsStrLine(String s)
	{
		return this.file_lines.contains(s);//Compares the line of a string not an actual line object do not use unless your advanced!
	}
	/**
	 * Appends Line To end of file
	 * @param str
	 */
	public void appendLine(LineBase line)
	{
		this.file_lines.add(line.getString());
		this.lines.add(line);
	}
	/**
	 * Appends Line To index of file
	 * @param str
	 */
	public void appendLine(LineBase line,int index)
	{
		this.file_lines.add(index,line.toString());
		this.lines.add(index,line);
	}
	/**
	 * Append List of lines
	 * @param list
	 */
	public void appendLineList(ArrayList<LineBase> list)
	{
		for(LineBase line : list)
		{
			this.file_lines.add(line.toString());
			this.lines.add(line);
		}
		
	}
	/**
	 * Append List of lines at starting index
	 * @param list
	 * @param index
	 */
	public void appendLineList(ArrayList<LineBase> list, int index)
	{
		for(LineBase line : list)
		{
			this.file_lines.add(index,line.toString());
			this.lines.add(index,line);
		}
	}
	/**
	 * Sets line to index
	 * @param line
	 * @param index
	 */
	public void setLine(LineBase line, int index)
	{
		this.file_lines.set(index,line.toString());
		this.lines.set(index,line);
	}
	/**
	 * Take line objects and convert set them to the file
	 * @param list
	 * @param index
	 */
	public void setLineList(ArrayList<LineBase> list, int index)
	{
		ArrayList<String> lineString = new ArrayList<String>();
		for(LineBase line : list)
		{
			lineString.add(line.toString());//Convert to array of strings then set them
			this.lines.add(line);
		}
		
		this.setList(lineString, index);
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
		String[] lines = new String[this.file_lines.size()];
		for(int i=0;i<this.file_lines.size();i++)
			lines[i] = this.file_lines.get(i);  //Copy array
		
		for(int i=0;i<this.file_lines.size();i++)
		{
			String str = this.file_lines.get(i);
			if(LineDynamicLogic.isStringPossibleLine(str))
			{
				LineBase lineobj = LineDynamicLogic.getLineFromString(str);
				if(line.equals(lineobj))
				{
					lines[i] = null;
					if(!deleteAll)
						break; //Delete first instance and stop the loop unless it's delete all
				}
			}
		}
		//Reset ArrayList<String>(); It will remove all null instances
		this.file_lines = new ArrayList<String>();
		for(String strline : lines)
		{
			if(strline != null)
				this.file_lines.add(strline);
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
		for(int i=0;i<this.file_lines.size();i++)
		{
			String str = this.file_lines.get(i);
			if(LineDynamicLogic.isStringPossibleLine(str))
			{
				LineBase lineobj = LineDynamicLogic.getLineFromString(str);
				if(line.equals(lineobj))
				{
					return true;
				}
			}
		}
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
	public void appendLine(LineDynamicLogic line)
	{
		this.file_lines.add(line.getString());
	}
	public void appendLine(LineDynamicLogic line,int index)
	{
		this.file_lines.add(index,line.getString());
	}
	public void appendLineDynamicList(ArrayList<LineDynamicLogic> list)
	{
		ArrayList<String> lines = new ArrayList<String>();
		for(LineDynamicLogic logic : list)
			lines.add(logic.getString());
		this.file_lines.addAll(lines);
	}
	public void appendLineDynamicList(ArrayList<LineDynamicLogic> list, int index)
	{
		ArrayList<String> lines = new ArrayList<String>();
		for(LineDynamicLogic logic : list)
			lines.add(logic.getString());
		
		this.file_lines.addAll(index,lines);
	}
	public void setLine(LineDynamicLogic line, int index)
	{
		this.file_lines.set(index,line.getString());
	}
	public void setLineDynamicList(ArrayList<LineDynamicLogic> list, int index)
	{
		ArrayList<String> lines = new ArrayList<String>();
		for(LineDynamicLogic logic : list)
			lines.add(logic.getString());
		
		this.setList(lines, index);
	}
	public void deleteLineDynamicLogic(LineDynamicLogic line, boolean deleteAll)
	{
		String[] lines = new String[this.file_lines.size()];
		for(int i=0;i<this.file_lines.size();i++)
			lines[i] = this.file_lines.get(i);  //Copy array
		
		for(int i=0;i<this.file_lines.size();i++)
		{
			String str = this.file_lines.get(i);
			if(LineDynamicLogic.isPosibleDynamicLogic(str))
			{
				LineDynamicLogic lineobj = new LineDynamicLogic(str);
				if(line.equals(lineobj))
				{
					lines[i] = null;
					if(!deleteAll)
						break; //Delete first instance and stop the loop unless it's delete all
				}
			}
		}
		//Reset ArrayList<String>(); It will remove all null instances
		this.file_lines = new ArrayList<String>();
		for(String strline : lines)
		{
			if(strline != null)
				this.file_lines.add(strline);
		}
		
	}
	public void deleteLine(LineDynamicLogic line)
	{
		deleteLineDynamicLogic(line,false);
	}
	public void deleteAllDynamicLines(ArrayList<LineDynamicLogic> list)
	{
		for(LineDynamicLogic logic : list)
			deleteLineDynamicLogic(logic,true);
	}
	public boolean containsLine(LineDynamicLogic logic)
	{
		for(int i=0;i<this.file_lines.size();i++)
		{
			String str = this.file_lines.get(i);
			if(LineDynamicLogic.isStringPossibleLine(str))
			{
				LineDynamicLogic lineobj = new LineDynamicLogic(str);
				if(logic.equals(lineobj))
				{
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * Do not manipulate this unless you know what you are doing
	 * @return
	 */
	public ArrayList<String> getFile_Lines() {return this.file_lines;}
	
	/**
	 * Since it's un-optimized to re-write the file every single entry is deleted it will have an array to delete and an array for adding
	 */
	public void updateConfig()
	{
		try {
			if(!this.end.equals(""))
				this.file_lines.add(this.end);
			Files.write(this.cfgfile.toPath(), this.file_lines);
		} catch (IOException e) {e.printStackTrace();}
		this.readFile();
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
