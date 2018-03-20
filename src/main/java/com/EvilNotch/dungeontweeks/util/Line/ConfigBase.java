package com.EvilNotch.dungeontweeks.util.Line;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.EvilNotch.dungeontweeks.util.JavaUtil;


public class ConfigBase {
    
    public ArrayList<ILine> lines; //warning check before assuming linebase could be linedynamic logic or modded line that uses this interface
    public final File cfgfile;
    public String header = "";
    public boolean first_launch = false;
    public boolean enableComments = true;
    
    //comments and wrappers
    protected ArrayList<Comment> init;
    protected ArrayList<Comment> origin;
    protected ArrayList<Comment> comments;
    protected char commentStart = '#';
    protected char headerLWrap = '<';
    protected char headerRWrap = '>';
    protected char headerSlash = '/';
    protected char lineSeperator = ':';
    protected char lineQuote = '"';
    protected ArrayList<String> lineChecker;//optimized to only keep it as string and not reparse it
    protected ArrayList<Comment> initChecker;
    protected ArrayList<Comment> commentChecker;
    
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
        this.lines = new ArrayList<>();
        for(Comment c : initComments)
            c.start = commentStart;//fix comments
        this.init = initComments;
        this.origin = JavaUtil.copyArrayAndObjects((ArrayList)initComments);
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
            
            this.writeFile(new ArrayList<>());
        }
        this.readFile();//cache arrays
    }

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
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void readFile()
    {
        this.lines = new ArrayList();
        this.comments = new ArrayList();
        this.init = new ArrayList();
        
        try {
            List<String> filelist = Files.readAllLines(this.cfgfile.toPath());
            String wrapperHead = getWrapper(true);
            String wrapperTail = getWrapper(false);
            int index = 0;
            int cindex = 0;
            int actualIndex = 0;
            boolean initPassed = false;
            
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
                if(isStringPossibleLine(whitespaced, "" + this.commentStart,this.lineSeperator,this.lineQuote))
                	break;//if is line stop trying to parse the header index
                count++;
            }
            for(String strline : filelist)
            {
                String whitespaced = LineBase.toWhiteSpaced(strline);
                initPassed = actualIndex > headerIndex;
                actualIndex++;//since only used for boolean at beginging no need to copy ten other places
                if(whitespaced.equals(""))
                	continue;//optimization
                if(!isStringPossibleLine(strline,"" + this.commentStart,wrapperHead,wrapperTail,this.lineSeperator,this.lineQuote) )
                {
                    //comment handling
                    if(whitespaced.indexOf(this.commentStart) == 0)
                    {
                        if(initPassed)
                        	this.comments.add(new Comment(index,strline,false,this.commentStart) );//if not passed header leave comments to init
                        if(!initPassed)
                        {
                        	Comment initcomment = new Comment(strline,this.commentStart);
                        	if(!this.hasHeaderComment(initcomment))
                        		this.init.add(initcomment);
                        }
                        cindex++;
                    }
                    
                    continue;
                }
                ILine line = this.getLine(strline);
                lines.add(line);
                //scan for attached comments on lines
                if(this.enableComments)
                if(strline.contains("" + this.commentStart))
                {
                    int hIndex = strline.indexOf(this.commentStart);
                    if(hIndex >= line.getString().length() )
                        this.comments.add(new Comment(index,strline.substring(hIndex, strline.length()),true,this.commentStart) );
                }
                
                index++;
            }
        } catch (Exception e) {e.printStackTrace();}
        this.lineChecker = getStringLines();
        if(this.enableComments)
        {
        	this.initChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.init);
        	this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);
        	updateNearestCLines();
        }
        else{
        	this.initChecker = new ArrayList();
        	this.commentChecker = new ArrayList();
        }
    }
    protected boolean isStringPossibleLine(String strline, String comment, String wrapperH, String wrapperT,char sep, char q) {
    	 if(!this.enableComments)
             return false;
		return LineDynamicLogic.isStringPossibleLine(strline, comment, wrapperH, wrapperT, sep, q);
	}
	protected boolean isStringPossibleLine(String strline, String comment, char sep, char q) {
		 if(!this.enableComments)
			 return false;
		return LineDynamicLogic.isStringPossibleLine(strline, comment, sep, q);
	}
	/**
     * Object oriented so people using this library can override with adding new lines easily
     * returns in ConfigBase LineDynamicLogic.getLineFromString(...)
     */
    protected ILine getLine(String strline) {
    	strline = removeComments(strline);
    	return LineDynamicLogic.getLineFromString(strline,this.lineSeperator,this.lineQuote,this.commentStart);
	}
    protected String removeComments(String strline) {
		if(!strline.contains("" + this.commentStart))
			return strline;//optimization
		String str = "";
		boolean insideQuote = false;
		for(int i=0;i<strline.length();i++)
		{
			String s = strline.substring(i, i+1);
			if(s.equals("" + this.lineQuote))
				insideQuote = !insideQuote;//invert the boolean
			if(!insideQuote && s.equals("" + this.commentStart))
				break;//stop parsing if it's a comment that's not inside a quote
			str += s;
		}
		return str;
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
    public void setCfgList(ArrayList<ILine> list, int index)
    {
        for(int i=0;i<list.size();i++)
        {
            ILine str = list.get(i);
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
            ILine line = this.getLine(list.get(i));
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
     * Add a line if and only if It Doesn't exist treats the ConfigBase like a Set[ILine] rather then ArrayList[ILine]
     */
    public void addLine(ILine line){
        if(this.containsLine(line))
            return;
        this.appendLine(line);
    }
    public void addLine(ILine line, int index){
        if(this.containsLine(line))
            return;
        this.appendLine(line,index);
    }
    /**
     * Add a line if and only if It Doesn't exist treats the ConfigBase like a Set[ILine] rather then ArrayList[ILine]
     */
    public void addLine(ILine line,boolean compareBase){
        if(this.containsLine(line,false,compareBase))
            return;
        this.appendLine(line);
    }
    public void addLine(ILine line, int index,boolean compareBase){
        if(this.containsLine(line,false,compareBase))
            return;
        this.appendLine(line,index);
    }
    
    /**
     * Append List of lines if line itself isn't added
     */
    public void addLineList(ArrayList<ILine> list,boolean compareBase)
    {
        for(ILine line : list)
            addLine(line,compareBase);
    }
    
    /**
     * Append List of lines at starting index
     */
    public void addLineList(ArrayList<ILine> list, int index,boolean compareBase)
    {
        for(ILine line : list)
        	addLine(line,index++,compareBase);
    }
    
    public void addLineList(ArrayList<ILine> list)
    {
    	addLineList(list,true);
    }
    public void addLineList(ArrayList<ILine> list, int index)
    {
    	addLineList(list,index,true);
    }
    
    /**
     * Appends Line To end of file
     * @param str
     */
    public void appendLine(ILine line)
    {
        this.lines.add(line);
    }
    /**
     * Appends Line To index of file
     * @param str
     */
    public void appendLine(ILine line,int index)
    {
        this.lines.add(index,line);
    }
    /**
     * Append List of lines
     * @param list
     */
    public void appendLineList(ArrayList<ILine> list)
    {
        for(ILine line : list)
            this.lines.add(line);
    }
    /**
     * Append List of lines at starting index
     * @param list
     * @param index
     */
    public void appendLineList(ArrayList<ILine> list, int index)
    {
        for(ILine line : list)
            this.lines.add(index++,line);
    }
    /**
     * Sets line to index
     * @param line
     * @param index
     */
    public void setLine(ILine line, int index)
    {
        this.lines.set(index,line);
    }
    /**
     * Take line objects and convert set them to the file
     * @param list
     * @param index
     */
    public void setLineList(ArrayList<ILine> list, int index)
    {
        this.setCfgList(list, index);
    }
    /**
     * Deletes first instanceof String
     * @param strline
     */
    public void deleteLine(ILine line)
    {
    	deleteILine(line,false,false,true);
    }
    public void deleteILine(ILine line, boolean deleteAll,boolean compareHead,boolean compareBase)
    {
    	if(compareBase)
    		line = line.getLineBase();
        Iterator<ILine> it = this.lines.iterator();
        while(it.hasNext())
        {
            ILine compare = it.next();
            if(compareBase)
            	compare = compare.getLineBase();
            if(isLineEqual(line,compare,compareHead) )
            {
                it.remove();
                if(!deleteAll)
                    break;
            }
        }
    }
    public boolean isLineEqual(ILine line, ILine compare, boolean compareHead) {
		return line.equals(compare,compareHead) && compare.equals(line, compareHead);
	}
	/**
     * Delete all instances of list of lines
     * @param list
     */
    public void deleteAllLines(ArrayList<ILine> list)
    {
        for(ILine line : list)
        	deleteILine(line,true,false,true);
    }
    public boolean containsLine(ILine line,boolean compareHead,boolean compareBase)
    {
    	if(compareBase)
    		line = line.getLineBase();
    	for(ILine compare : this.lines)
    	{
    		if(compareBase)
    			compare = compare.getLineBase();
    		//let both lines have a rejection because line base isn't going to check for additions of lineitemstackbase etc...
    		if(isLineEqual(line,compare,compareHead))
                return true;//makes it compare the equals at the base level
    	}
        return false;
    }
    /**
     * Does this config contain this line?
     * @param line
     * @return
     */
    public boolean containsLine(ILine line)
    {
        return containsLine(line,false,true);
    }
    
    /**
     * Don't Use Unless you want the config defaults people will get pissed
     */
    public void resetConfig()
    {
        try {
        	setInit(this.origin);
        	writeFile(new ArrayList());
            this.readFile();//makes arrays reset
        } catch(Exception e) {e.printStackTrace();}
    }
    /**
     * shuffle entries on the config
     */
    public void shuffle()
    {
    	if(this.enableComments)
    		this.updateNearestCLines();
    	boolean changeComments = this.comments.equals(this.commentChecker) && this.enableComments;
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	
    	Collections.shuffle(this.lines);
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	updateCommentIndexes(changeComments);
    }
    public void shuffle(int times){
    	if(this.enableComments)
    		this.updateNearestCLines();
    	boolean changeComments = this.comments.equals(this.commentChecker) && this.enableComments;
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	
    	for(int i=0;i<times;i++)
    		Collections.shuffle(this.lines);
    	
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	updateCommentIndexes(changeComments);
    }
    public void shuffle(Random rnd){
    	if(this.enableComments)
    		this.updateNearestCLines();
    	boolean changeComments = this.comments.equals(this.commentChecker) && this.enableComments;
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	
    	Collections.shuffle(this.lines, rnd);
    	
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	updateCommentIndexes(changeComments);
    }
    public void shuffle(Random rnd, int times){
    	if(this.enableComments)
    		this.updateNearestCLines();
    	boolean changeComments = this.comments.equals(this.commentChecker) && this.enableComments;
    	boolean changeLines = this.getStringLines().equals(this.lineChecker);
    	
    	
    	for(int i=0;i<times;i++)
    		Collections.shuffle(this.lines, rnd);
    	
    	if(changeLines)
    		this.lineChecker = this.getStringLines();
    	updateCommentIndexes(changeComments);
    }
    /**
     * Re-Orders File to be alphabetized
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public void alphabetize()
    {
        //make comments have nearest lines incase it later gets alphabetized
        if(this.enableComments)
            updateNearestCLines();
        boolean alphabitizeChecker = this.getStringLines().equals(this.lineChecker);
        boolean alphaComments = this.comments.equals(this.commentChecker) && this.enableComments;
        
        Collections.sort(this.lines);//ILine extends IObject which extends Comparable,ICopy so this is possible
        if(alphabitizeChecker)
        	this.lineChecker = this.getStringLines();//sync changes is called get lines again since lines have been alphabitized
        
       updateCommentIndexes(alphaComments);
    }
    
    protected void updateCommentIndexes(boolean checker) {
    	 //re-organize comments to new locations and sync checkers for updating the config
        if(this.enableComments)
        {
        	for(Comment c : this.comments)
        	{
        		int index = 0;
        		for(ILine line : this.lines)
        		{
        			//enforces higherarchy to deny it's equal to also in case lines change values it compares the bases
        			if(line != null && c.nearestLine != null)
        			if(isLineEqual(line,c.nearestLine,false) )
        			{
        				c.lineIndex = index;
        				break;
        			}
        			index++;
        		}
        	}
        	if(checker)
        		this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);//sync index change since it's compared for equals()
        	updateNearestCLines();//unessary but, for error checking better to have debugged errors then untraceable ones
       	}
	}
	protected void updateNearestCLines() {
        for(Comment c : this.comments)
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
          
           this.initChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.init);
           this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);
        }
        else{
        	this.initChecker.clear();
        	this.commentChecker.clear();//not supported if not enabled
        }
        this.lineChecker = JavaUtil.copyArrays(list);//since is immutable don't worry about them getting modified
        
        this.writeFile(list);
    }
    public void updateConfig(boolean alphabitize)
    {
    	updateConfig(alphabitize,false);
    }
    public void updateConfig(boolean alphabitize,boolean forceUpdate)
    {
    	updateConfig(alphabitize,forceUpdate,false);
    }
    /**
     * update if and only if data has been modified in memory
     */
    public void updateConfig(boolean alphabitize,boolean forceUpdate,boolean msg)
    {   
        //don't update for alphabetizate as it will screw up people using the cfg only do it if the config itself needs updating
        if(forceUpdate || !this.getStringLines().equals(this.lineChecker) || this.enableComments && !this.comments.equals(this.commentChecker) || this.enableComments && !this.init.equals(this.initChecker))
        {
        	if(alphabitize)
                this.alphabetize();
        	if(msg)
        		System.out.print("CFG Updating:" + this.cfgfile + "\n");
        	this.updateConfig();
        }
    }
    /**
     * organize comments by numeric order
     */
    public void reorganizeComments() {
        boolean alphaComment = this.comments.equals(this.commentChecker) && this.enableComments;
        ArrayList<Integer> initialArray = new ArrayList<>();
        //add then sort
        for(Comment c : this.comments)
        	initialArray.add(new Integer(c.lineIndex));
        Set<Integer> set = new HashSet<Integer>(initialArray);
        initialArray.clear();
        for(Integer i : set)
        	initialArray.add(i);
        Collections.sort(initialArray);
        
        System.out.println(initialArray);
        ArrayList<Comment> coms = new ArrayList<>();
        for(Integer i : initialArray)
        {
        	for(Comment c : this.comments)
        		if(c.lineIndex == i)
        			coms.add(c);
        }
        this.comments = coms;
        
    	if(alphaComment)
    		this.commentChecker = JavaUtil.copyArrayAndObjects((ArrayList)this.comments);//sync re-organization
    }
    protected ArrayList<String> getStringLines() {
        ArrayList<String> list = new ArrayList();
        for(ILine line : this.lines)
            list.add(line.getString() );
        return list;
    }
    /**
     * Ignores the index parameter
     */
    public boolean hasHeaderComment(Comment comment){
    	for(Comment c : this.init)
    		if(c.equals(comment,false))
    			return true;
    	return false;
    }
    /**
     * Ignores the index parameter
     */
    public boolean hasComment(Comment comment){
    	for(Comment c : this.comments)
    		if(c.equals(comment,false))
    			return true;
    	return false;
    }
    
    public ArrayList<Comment> getComments(){return this.comments;}
    public ArrayList<Comment> getInit(){return this.init;}
    public ArrayList<Comment> getOrigin(){return this.origin;}
    public void setOrigin(ArrayList<Comment> list){this.origin = list;}
    public void setInit(ArrayList<Comment> list){this.init = list;}

    @Override
    public String toString()
    {
        String s = "<ConfigBase>\n";
        for(Comment c : this.comments)
            s += c + "\n";
        for(ILine line : this.lines)
            s += line.toString() + "\n";
        return s + "</ConfigBase>\n";
    }
    
}
