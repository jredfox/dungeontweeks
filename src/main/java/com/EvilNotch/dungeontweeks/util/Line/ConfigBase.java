package com.EvilNotch.dungeontweeks.util.Line;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;


public class ConfigBase {
    
    public ArrayList<LineBase> lines; //lines of the file main point
    public final File cfgfile;
    public String header = "";
    public boolean first_launch = false;
    
    //comments and wrappers
    protected ArrayList<Comment> init;
    protected ArrayList<Comment> comments;
    protected char commentStart = '#';
    protected char headerLWrap = '<';
    protected char headerRWrap = '>';
    protected char headerSlash = '/';
    protected char lineSeperator = ':';
    protected char lineQuote = '"';
    public boolean enableComments = true;
    //version read only
    public static final String version = "1.1-build-105";
    
    public ConfigBase(File file)
    {
        this(file,new ArrayList<Comment>() );
    }
    public ConfigBase(File file, ArrayList<Comment> initComments)
    {
        this(file,initComments,"");
    }
    public ConfigBase(File file, ArrayList<Comment> initComments, String header)
    {
        this(file,initComments,header,'#');
    }
    public ConfigBase(File file, ArrayList<Comment> initComments, String header,char commentStart)
    {
        this(file,initComments,header,commentStart,'<','>','/',true,':','"');
    }
    
    public ConfigBase(File file, ArrayList<Comment> initComments, String header,char commentStart,char lhwrap,char rhwrap,char hslash,boolean comments,char lsep,char lquote)
    {
        this.cfgfile = file;
        this.lines = new ArrayList();
        for(Comment c : initComments)
            c.start = commentStart;//fix comments
        this.init = initComments;
        this.header = header;
        this.commentStart  = commentStart;
        this.headerLWrap = lhwrap;
        this.headerRWrap = rhwrap;
        this.headerSlash = hslash;
        this.enableComments = comments;
        this.lineSeperator = lsep;
        this.lineQuote = lquote;
        boolean exsists = file.exists();
        
        if(!exsists)
        {
            try {
                file.createNewFile();
                this.first_launch = true;
                
            } catch (IOException e) {e.printStackTrace();}
            
            this.writeFile(new ArrayList());
        }
        this.readFile();//cache arrays
    }
    public void setInit(ArrayList<Comment> list){this.init = list;}

    public void writeFile(ArrayList<String> list) 
    {
        try {
            //header and init
            FileWriter out = new FileWriter(this.cfgfile);
            for(Comment c : this.init)
                out.write("" + c.start + c.comment + "\r\n");
            if(this.init.size() > 0)
                out.write("\r\n");//create new line if has header
            if(!this.header.equals("")) 
                out.write(this.getWrapper(true) + "\r\n\r\n");
            
            for(String s : list)
                out.write(s + "\r\n");
            
            //end of header
            if(!this.header.equals(""))
                out.write("\r\n" + this.getWrapper(false) + "\r\n");
            out.close();
        } catch (Exception e) {e.printStackTrace();}
    }
    public void readFile()
    {
        this.lines = new ArrayList();
        this.comments = new ArrayList();
        
        try {
            List<String> filelist = Files.readAllLines(this.cfgfile.toPath());
            String wrapperHead = getWrapper(true);
            String wrapperTail = getWrapper(false);
            int index = 0;
            int cindex = 0;
            int actualIndex = 0;
            boolean initPassed = false;
            
            int initSize = this.init.size();
            int headerIndex = -1;
            
            //scan for header
            int count = 0;
            for(String s : filelist)
            {
                String whitespaced = LineBase.toWhiteSpaced(s);
                if(whitespaced.equals(LineBase.toWhiteSpaced(wrapperHead)) && !this.header.equals("") || whitespaced.equals(""))
                {
                    headerIndex = count;
                    break;
                }
                if(LineDynamicLogic.isStringPossibleLine(whitespaced, "" + this.commentStart,this.lineSeperator,this.lineQuote))
                	break;//if is line stop trying to parse the header index
                count++;
            }
            for(String strline : filelist)
            {
                String whitespaced = LineBase.toWhiteSpaced(strline);
                initPassed = actualIndex > headerIndex;
                actualIndex++;//since only used for boolean at beginging no need to copy ten other places
                if(!LineDynamicLogic.isStringPossibleLine(strline,"" + this.commentStart,wrapperHead,wrapperTail,this.lineSeperator,this.lineQuote) )
                {
                    if(!enableComments || whitespaced.equals(""))
                        continue;
                    //comment handling
                    if(whitespaced.indexOf(this.commentStart) == 0)
                    {
                        if(initPassed)
                        	this.comments.add(new Comment(index,strline,false,this.commentStart) );//if not passed header leave comments to init
                        if(!initPassed)
                        {
                        	Comment initcomment = new Comment(strline,this.commentStart);
                        	if(!this.init.contains(initcomment))
                        		this.init.add(initcomment);
                        }
                        cindex++;
                    }
                    
                    continue;
                }
                LineBase line = LineDynamicLogic.getLineFromString(strline,this.lineSeperator,this.lineQuote,this.commentStart);
                lines.add(line);
                //scan for attached comments on lines
                if(enableComments)
                if(strline.contains("" + this.commentStart))
                {
                    int hIndex = strline.indexOf(this.commentStart);
                    if(hIndex >= line.getString().length() )
                        this.comments.add(new Comment(index,strline.substring(hIndex, strline.length()),true,this.commentStart) );
                }
                
                index++;
            }
        } catch (Exception e) {e.printStackTrace();}
        
    }
    
    protected String getWrapper(boolean head)
    {
        if(head)
            return this.getLWrap() + this.header + this.headerRWrap;
        else
            return this.getLWrap() + this.headerSlash + this.header + this.headerRWrap;
    }
    protected String getLWrap() {
        return LineBase.toWhiteSpaced("" + this.headerLWrap);
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
            LineBase line = LineDynamicLogic.getLineFromString(list.get(i),this.lineSeperator,this.lineQuote,this.commentStart);
            boolean flag = false;
            if(index + i < this.lines.size() && !flag)
                this.lines.set(index+i,line);
            else{
                this.lines.add(line);
                flag = true;
            }
        }
    }
    public void addLine(LineBase line){
        if(this.containsLine(line))
            return;
        this.appendLine(line);
    }
    public void addLine(LineBase line, int index){
        if(this.containsLine(line))
            return;
        this.appendLine(line,index);
    }
    /**
     * Append List of lines
     * @param list
     */
    public void addLineList(ArrayList<LineBase> list)
    {
        for(LineBase line : list)
            if(!this.containsLine(line))
                this.lines.add(line);
    }
    
    /**
     * Append List of lines at starting index
     */
    public void addLineList(ArrayList<LineBase> list, int index)
    {
        for(LineBase line : list)
            if(!this.containsLine(line))
                this.lines.add(index++,line);
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
            Files.write(this.cfgfile.toPath(), new ArrayList() );
            this.readFile();//makes arrays reset
        } catch (IOException e) {e.printStackTrace();}
    }
    
    /**
     * Re-Orders File to be alphabetized
     */
    public void alphabetize()
    {
        //make comments have nearest lines incase it later gets alphabetized
        if(this.enableComments)
            updateNearestCLines();
        
        ArrayList<String> list = getStringLines();
        Collections.sort(list);
        this.setList(list, 0);
        
        //re-organize comments to new locations
        if(this.enableComments)
        for(Comment c : this.comments)
        {
            int index = 0;
            for(LineBase line : this.lines)
            {
                if(line.equals(c.nearestLine,false) && line != null)
                {
                    c.lineIndex = index;
                    break;
                }
                index++;
            }
        }
    }
    
    protected void updateNearestCLines() {
        for(Comment c : this.comments)
            if(c.lineIndex < this.lines.size() )
                c.nearestLine = this.lines.get(c.lineIndex);
    }

    /**
     * Since it's un-optimized to re-write the file every single entry is deleted/added 
     * it will only save data when this is called
     */
    public void updateConfig()
    {
        ArrayList<String> list = getStringLines();
        //comment handling
        if(this.enableComments)
        {
            try
            {
              reorganizeComments();
              int offset = 0;
              for(Comment c : this.comments)
              {
                  if(!c.isAttactched)
                  {
                      list.add(c.lineIndex + offset,c.start + c.comment);
                      offset++;//only offset it if comment is full line
                  }
                  else
                      list.set(c.lineIndex + offset, list.get(c.lineIndex + offset) + c.start + c.comment);
              }
           }
           catch(Exception e){e.printStackTrace();}
        }
        
        this.writeFile(list);
    }
    public void updateConfig(boolean alphabitize)
    {
        if(alphabitize)
            this.alphabetize();
        this.updateConfig();
    }
    /**
     * organize comments by numeric order
     */
    public void reorganizeComments() {
        ArrayList<Integer> ints = new ArrayList();
        for(Comment c : this.comments)
            ints.add(c.lineIndex);
        
        //sort then clear any dupes
        Collections.sort(ints);
        Set<Integer> ints2 = new HashSet<Integer>(ints);
        ints.clear();
        for(Integer i : ints2)
            ints.add(i);
        
        ArrayList<Comment> com = new ArrayList();
        for(int i=0;i<ints.size();i++)
        {
            int value = ints.get(i);
            for(Comment c : comments)
            {
                if(c.lineIndex == value)
                    com.add(c);
            }
        }
        this.comments = com;
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
        String s = "<ConfigBase>\n";
        for(Comment c : this.comments)
            s += c + "\n";
        for(LineBase line : this.lines)
            s += line.toString() + "\n";
        return s + "</ConfigBase>\n";
    }
    public ArrayList<Comment> getInit(){return this.init;}
    
}
